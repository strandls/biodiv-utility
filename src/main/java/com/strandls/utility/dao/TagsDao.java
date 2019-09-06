/**
 * 
 */
package com.strandls.utility.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.strandls.utility.pojo.Tags;
import com.strandls.utility.util.AbstractDAO;

/**
 * @author Abhishek Rudra
 *
 */
public class TagsDao extends AbstractDAO<Tags, Long> {

	private final Logger logger = LoggerFactory.getLogger(TagsDao.class);

	/**
	 * @param sessionFactory
	 */
	@Inject
	protected TagsDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public Tags findById(Long id) {
		Session session = sessionFactory.openSession();
		Tags entity = null;
		try {
			entity = session.get(Tags.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return entity;
	}

}
