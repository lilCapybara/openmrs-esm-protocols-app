package org.openmrs.module.protocolos.api.db;

import java.util.List;

import org.openmrs.module.protocolos.model.Protocolo;

public interface ProtocoloDao {

	List<Protocolo> getAllProtocolos(boolean includeVoided);

	Protocolo getProtocoloByUuid(String uuid);

	Protocolo saveProtocolo(Protocolo protocolo);
}
