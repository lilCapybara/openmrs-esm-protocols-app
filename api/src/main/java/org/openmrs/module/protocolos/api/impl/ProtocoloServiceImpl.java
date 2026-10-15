package org.openmrs.module.protocolos.api.impl;

import java.util.List;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.protocolos.api.ProtocoloService;
import org.openmrs.module.protocolos.api.db.ProtocoloDao;
import org.openmrs.module.protocolos.model.Protocolo;

public class ProtocoloServiceImpl extends BaseOpenmrsService implements ProtocoloService {

	private ProtocoloDao dao;

	public void setDao(ProtocoloDao dao) {
		this.dao = dao;
	}

	@Override
	public List<Protocolo> getAllProtocolos(boolean includeVoided) {
		return dao.getAllProtocolos(includeVoided);
	}

	@Override
	public Protocolo getProtocoloByUuid(String uuid) {
		return dao.getProtocoloByUuid(uuid);
	}

	@Override
	public Protocolo saveProtocolo(Protocolo protocolo) {
		return dao.saveProtocolo(protocolo);
	}

	@Override
	public Protocolo voidProtocolo(Protocolo protocolo, String reason) {
		// Los campos voided/voidedBy/dateVoided/voidReason los completa OpenMRS (RequiredDataAdvice)
		return dao.saveProtocolo(protocolo);
	}

	@Override
	public Protocolo unvoidProtocolo(Protocolo protocolo) {
		return dao.saveProtocolo(protocolo);
	}
}
