package org.openmrs.module.protocolos;

import org.openmrs.module.BaseModuleActivator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Se ejecuta cuando OpenMRS arranca o detiene el modulo.
 */
public class ProtocolosActivator extends BaseModuleActivator {

	private static final Logger log = LoggerFactory.getLogger(ProtocolosActivator.class);

	@Override
	public void started() {
		log.info("Modulo Protocolos iniciado");
	}

	@Override
	public void stopped() {
		log.info("Modulo Protocolos detenido");
	}
}
