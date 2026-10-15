package org.openmrs.module.protocolos.api.db.hibernate;

import java.util.List;

import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.protocolos.api.db.ProtocoloDao;
import org.openmrs.module.protocolos.model.Protocolo;

public class HibernateProtocoloDao implements ProtocoloDao {

	private DbSessionFactory sessionFactory;

	public void setSessionFactory(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Protocolo> getAllProtocolos(boolean includeVoided) {
		String hql = includeVoided ? "from Protocolo p order by p.titulo"
		        : "from Protocolo p where p.voided = false order by p.titulo";
		return sessionFactory.getCurrentSession().createQuery(hql).list();
	}

	@Override
	public Protocolo getProtocoloByUuid(String uuid) {
		return (Protocolo) sessionFactory.getCurrentSession().createQuery("from Protocolo p where p.uuid = :uuid")
		        .setParameter("uuid", uuid).uniqueResult();
	}

	@Override
	public Protocolo saveProtocolo(Protocolo protocolo) {
		sessionFactory.getCurrentSession().saveOrUpdate(protocolo);
		return protocolo;
	}
}
