package org.openmrs.module.protocolos.api;

import java.util.List;

import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.protocolos.ProtocolosConstants;
import org.openmrs.module.protocolos.model.Protocolo;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de protocolos. Se obtiene con Context.getService(ProtocoloService.class).
 * Los @Authorized hacen que OpenMRS rechace la llamada si el usuario no tiene el privilegio:
 * esta es la seguridad real, independiente de lo que muestre el frontend.
 */
@Transactional
public interface ProtocoloService extends OpenmrsService {

	@Authorized(ProtocolosConstants.PRIV_GET_PROTOCOLOS)
	@Transactional(readOnly = true)
	List<Protocolo> getAllProtocolos(boolean includeVoided);

	@Authorized(ProtocolosConstants.PRIV_GET_PROTOCOLOS)
	@Transactional(readOnly = true)
	Protocolo getProtocoloByUuid(String uuid);

	/** Crea o actualiza. OpenMRS completa uuid, creator y dateCreated automaticamente. */
	@Authorized(ProtocolosConstants.PRIV_MANAGE_PROTOCOLOS)
	Protocolo saveProtocolo(Protocolo protocolo);

	/** Borrado logico. OpenMRS completa voided, voidedBy y dateVoided automaticamente. */
	@Authorized(ProtocolosConstants.PRIV_MANAGE_PROTOCOLOS)
	Protocolo voidProtocolo(Protocolo protocolo, String reason);

	@Authorized(ProtocolosConstants.PRIV_MANAGE_PROTOCOLOS)
	Protocolo unvoidProtocolo(Protocolo protocolo);
}
