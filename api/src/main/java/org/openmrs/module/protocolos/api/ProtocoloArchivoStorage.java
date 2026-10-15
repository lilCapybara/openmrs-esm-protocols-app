package org.openmrs.module.protocolos.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.openmrs.util.OpenmrsUtil;

/**
 * Guarda y recupera los archivos de los protocolos en disco.
 * Ubicacion: <application data directory>/protocolos/<uuid del protocolo>
 * En Docker eso es /openmrs/data/protocolos/, que vive en el volumen openmrs-data (persiste).
 */
public final class ProtocoloArchivoStorage {

    public static final String CARPETA = "protocolos";

    /** 20 MB */
    public static final long TAMANIO_MAXIMO_BYTES = 20L * 1024 * 1024;

    public static final Set<String> TIPOS_PERMITIDOS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "application/pdf",
            "image/png",
            "image/jpeg",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document")));

    private ProtocoloArchivoStorage() {
    }

    public static boolean esTipoPermitido(String mimeType) {
        return mimeType != null && TIPOS_PERMITIDOS.contains(mimeType.toLowerCase());
    }

    private static File carpeta() {
        // Crea la carpeta si no existe
        return OpenmrsUtil.getDirectoryInApplicationDataDirectory(CARPETA);
    }

    public static File archivoDe(String uuid) {
        return new File(carpeta(), uuid);
    }

    public static void guardar(String uuid, InputStream contenido) throws IOException {
        Files.copy(contenido, archivoDe(uuid).toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public static void borrar(String uuid) {
        File f = archivoDe(uuid);
        if (f.exists()) {
            f.delete();
        }
    }
}