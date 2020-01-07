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

	@SuppressWarnings("unchecked")
	public List<Tags> fetchNameByLike(String phrase) {
		Session session = sessionFactory.openSession();
		List<Tags> result = null;

		String qry = "SELECT id, version, strip_tags(name) FROM public.tags " + "where name like '" + phrase
				+ "%' order by char_length(name) asc limit 10";

		try {
			Query<Tags> query = session.createNativeQuery(qry);
			result = query.getResultList();

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}

		return result;

	}

}
