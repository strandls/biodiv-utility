/**
 * 
 */
package com.strandls.utility.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.strandls.utility.pojo.Flag;
import com.strandls.utility.util.AbstractDAO;

/**
 * @author Abhishek Rudra
 *
 */
public class FlagDao extends AbstractDAO<Flag, Long> {

	private static final Logger logger = LoggerFactory.getLogger(FlagDao.class);

	/**
	 * @param sessionFactory
	 */
	@Inject
	protected FlagDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public Flag findById(Long id) {
		Session session = sessionFactory.openSession();
		Flag entity = null;
		try {
			entity = session.get(Flag.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return entity;
	}

	@SuppressWarnings("unchecked")
	public Flag findByObjectId(String objType, Long id) {
		String qry = "from Flag f where f.objectType = :objType and f.objectId = :id";
		Session session = sessionFactory.openSession();
		Query<Flag> query = session.createQuery(qry);
		query.setParameter("objType", objType);
		query.setParameter("id", id);

		Flag flag = query.getSingleResult();
		return flag;

	}

	@SuppressWarnings("unchecked")
	public List<Flag> findByUserId(Long id) {

		String qry = "from Flag f where f.authorId = :id";

		Session session = sessionFactory.openSession();
		Query<Flag> query = session.createQuery(qry);
		query.setParameter("id", id);

		List<Flag> result = query.getResultList();

		return result;

	}

}
