package org.openmrs.module.protocolos.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.openmrs.BaseOpenmrsData;

/**
 * Un protocolo institucional: metadata + referencia al archivo adjunto.
 * El archivo en si se guarda en disco (paso 5), nombrado con el uuid del protocolo.
 * BaseOpenmrsData aporta uuid, creator, dateCreated, changedBy, dateChanged y los campos de voided.
 */
@Entity
@Table(name = "protocolos_protocolo")
public class Protocolo extends BaseOpenmrsData {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "protocolo_id")
	private Integer protocoloId;

	@Column(name = "titulo", nullable = false, length = 255)
	private String titulo;

	@Column(name = "descripcion", length = 1000)
	private String descripcion;

	/** Nombre original del archivo subido, ej. "Protocolo caidas.pdf" */
	@Column(name = "nombre_archivo", nullable = false, length = 255)
	private String nombreArchivo;

	/** Tipo del archivo, ej. "application/pdf" */
	@Column(name = "mime_type", nullable = false, length = 100)
	private String mimeType;

	@Column(name = "tamanio_bytes")
	private Long tamanioBytes;

	@Override
	public Integer getId() {
		return protocoloId;
	}

	@Override
	public void setId(Integer id) {
		this.protocoloId = id;
	}

	public Integer getProtocoloId() {
		return protocoloId;
	}

	public void setProtocoloId(Integer protocoloId) {
		this.protocoloId = protocoloId;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getNombreArchivo() {
		return nombreArchivo;
	}

	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public Long getTamanioBytes() {
		return tamanioBytes;
	}

	public void setTamanioBytes(Long tamanioBytes) {
		this.tamanioBytes = tamanioBytes;
	}
}
