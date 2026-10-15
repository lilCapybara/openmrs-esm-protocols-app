package org.openmrs.module.protocolos.web.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.protocolos.ProtocolosConstants;
import org.openmrs.module.protocolos.api.ProtocoloArchivoStorage;
import org.openmrs.module.protocolos.api.ProtocoloService;
import org.openmrs.module.protocolos.model.Protocolo;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.response.GenericRestException;
import org.openmrs.module.webservices.rest.web.response.IllegalRequestException;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

/**
 * Endpoints REST de protocolos, bajo /openmrs/ws/rest/v1/protocolo
 *
 *   GET    /protocolo               lista (Get Protocolos)
 *   GET    /protocolo/{uuid}        detalle (Get Protocolos)
 *   GET    /protocolo/{uuid}/file   archivo (Get Protocolos)
 *   POST   /protocolo/upload        alta multipart: file, titulo, descripcion (Manage Protocolos)
 *   DELETE /protocolo/{uuid}        borrado logico (Manage Protocolos)
 *
 * Extiende BaseRestController para que los errores (sin permiso, no encontrado, etc.)
 * se devuelvan como JSON con el codigo HTTP correcto.
 */
@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/protocolo")
public class ProtocoloController extends BaseRestController {

    private ProtocoloService service() {
        return Context.getService(ProtocoloService.class);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public SimpleObject listar() {
        List<SimpleObject> results = new ArrayList<>();
        for (Protocolo p : service().getAllProtocolos(false)) {
            results.add(aJson(p));
        }
        return new SimpleObject().add("results", results);
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
    @ResponseBody
    public SimpleObject obtener(@PathVariable("uuid") String uuid) {
        return aJson(buscar(uuid));
    }

    @RequestMapping(value = "/{uuid}/file", method = RequestMethod.GET)
    public void descargar(@PathVariable("uuid") String uuid, HttpServletResponse response) {
        Protocolo p = buscar(uuid);
        File archivo = ProtocoloArchivoStorage.archivoDe(p.getUuid());
        if (!archivo.exists()) {
            throw new ObjectNotFoundException("El archivo del protocolo no existe en el servidor");
        }

        response.setContentType(p.getMimeType());
        response.setContentLengthLong(archivo.length());
        // inline: el navegador lo muestra (PDF/imagen) en vez de forzar la descarga
        response.setHeader("Content-Disposition", "inline; " + nombreParaHeader(p.getNombreArchivo()));

        try (InputStream in = Files.newInputStream(archivo.toPath()); OutputStream out = response.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int leidos;
            while ((leidos = in.read(buffer)) != -1) {
                out.write(buffer, 0, leidos);
            }
        }
        catch (IOException e) {
            throw new GenericRestException("No se pudo leer el archivo del protocolo", e);
        }
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public SimpleObject subir(@RequestParam("file") MultipartFile file, @RequestParam("titulo") String titulo,
                              @RequestParam(value = "descripcion", required = false) String descripcion) {

        if (!Context.hasPrivilege(ProtocolosConstants.PRIV_MANAGE_PROTOCOLOS)) {
            throw new org.openmrs.api.APIAuthenticationException("Se requiere el privilegio " + ProtocolosConstants.PRIV_MANAGE_PROTOCOLOS);
        }

        if (file == null || file.isEmpty()) {
            throw new IllegalRequestException("Falta el archivo");
        }
        if (StringUtils.isBlank(titulo)) {
            throw new IllegalRequestException("Falta el titulo");
        }
        if (file.getSize() > ProtocoloArchivoStorage.TAMANIO_MAXIMO_BYTES) {
            throw new IllegalRequestException("El archivo supera el maximo de 20 MB");
        }
        if (!ProtocoloArchivoStorage.esTipoPermitido(file.getContentType())) {
            throw new IllegalRequestException("Tipo de archivo no permitido: " + file.getContentType()
                    + ". Se aceptan PDF, imagenes (PNG/JPG) y Word.");
        }

        Protocolo p = new Protocolo(); // el uuid se genera al construir el objeto
        p.setTitulo(titulo.trim());
        p.setDescripcion(StringUtils.trimToNull(descripcion));
        p.setNombreArchivo(file.getOriginalFilename());
        p.setMimeType(file.getContentType().toLowerCase());
        p.setTamanioBytes(file.getSize());

        try (InputStream in = file.getInputStream()) {
            ProtocoloArchivoStorage.guardar(p.getUuid(), in);
        }
        catch (IOException e) {
            throw new GenericRestException("No se pudo guardar el archivo", e);
        }

        try {
            p = service().saveProtocolo(p);
        }
        catch (RuntimeException e) {
            // si falla la base, no dejamos el archivo huerfano
            ProtocoloArchivoStorage.borrar(p.getUuid());
            throw e;
        }
        return aJson(p);
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable("uuid") String uuid,
                         @RequestParam(value = "reason", required = false) String reason) {
        Protocolo p = buscar(uuid);
        service().voidProtocolo(p, StringUtils.defaultIfBlank(reason, "Eliminado por el administrador"));
        // El archivo queda en disco: es un borrado logico y se puede restaurar
    }

    // ---------- auxiliares ----------

    private Protocolo buscar(String uuid) {
        Protocolo p = service().getProtocoloByUuid(uuid);
        if (p == null || p.getVoided()) {
            throw new ObjectNotFoundException();
        }
        return p;
    }

    private SimpleObject aJson(Protocolo p) {
        SimpleObject o = new SimpleObject();
        o.add("uuid", p.getUuid());
        o.add("titulo", p.getTitulo());
        o.add("descripcion", p.getDescripcion());
        o.add("nombreArchivo", p.getNombreArchivo());
        o.add("mimeType", p.getMimeType());
        o.add("tamanioBytes", p.getTamanioBytes());
        o.add("fechaCreacion", formatear(p.getDateCreated()));
        o.add("creadoPor", p.getCreator() != null ? p.getCreator().getDisplayString() : null);
        return o;
    }

    private static String formatear(Date fecha) {
        return fecha == null ? null : new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(fecha);
    }

    /** filename para el header, soportando tildes (RFC 5987) */
    private static String nombreParaHeader(String nombre) {
        String seguro = nombre == null ? "protocolo" : nombre.replaceAll("[\"\\\\\\r\\n]", "_");
        String ascii = seguro.replaceAll("[^\\x20-\\x7E]", "_");
        try {
            String codificado = URLEncoder.encode(seguro, "UTF-8").replace("+", "%20");
            return "filename=\"" + ascii + "\"; filename*=UTF-8''" + codificado;
        }
        catch (UnsupportedEncodingException e) {
            return "filename=\"" + ascii + "\"";
        }
    }
}