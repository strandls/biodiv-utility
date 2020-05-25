package com.strandls.utility.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import com.strandls.utility.pojo.PortalStats;

public class PortalStatsDao {

	private final Logger logger = LoggerFactory.getLogger(PortalStatsDao.class);

	@Inject
	private SessionFactory sessionFactory;

	@SuppressWarnings("unchecked")
	public PortalStats fetchPortalStats() {
		PortalStats stats = new PortalStats();
		Session session = sessionFactory.openSession();

		String obvQry = "select count(*) from observation  where is_deleted = false";
		String docQry = "select count(*) from document";
		String ugQry = "select count(*) from user_group  where is_deleted= false";
		String speciesQry = "SELECT count(*) FROM public.species where is_deleted = false";
		String discussionQry = "select count(*) from discussion where is_deleted= false";
		String actUserQry = "select count(*) from suser where account_expired = false and account_locked = false and enabled = true";
		try {
			Query<Object> obvquery = session.createNativeQuery(obvQry);
			Query<Object> docQuery = session.createNativeQuery(docQry);
			Query<Object> ugQuery = session.createNativeQuery(ugQry);
			Query<Object> speciesQuery = session.createNativeQuery(speciesQry);
			Query<Object> disQuery = session.createNativeQuery(discussionQry);
			Query<Object> actUserQuery = session.createNativeQuery(actUserQry);

			stats.setObservation(Long.parseLong(obvquery.getSingleResult().toString()));
			stats.setDocuments(Long.parseLong(docQuery.getSingleResult().toString()));
			stats.setUserGroups(Long.parseLong(ugQuery.getSingleResult().toString()));
			stats.setSpecies(Long.parseLong(speciesQuery.getSingleResult().toString()));
			stats.setDiscussions(Long.parseLong(disQuery.getSingleResult().toString()));
			stats.setActiveUser(Long.parseLong(actUserQuery.getSingleResult().toString()));
			stats.setMaps(203L);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}

		return stats;
	}

}
