/**
 * 
 */
package com.strandls.utility.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.strandls.utility.pojo.GallerySlider;
import com.strandls.utility.util.AbstractDAO;

/**
 * @author Abhishek Rudra
 *
 */
public class GallerySliderDao extends AbstractDAO<GallerySlider, Long> {

	private final Logger logger = LoggerFactory.getLogger(GallerySliderDao.class);

	/**
	 * @param sessionFactory
	 */
	@Inject
	protected GallerySliderDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public GallerySlider findById(Long id) {
		GallerySlider result = null;
		Session session = sessionFactory.openSession();
		try {
			result = session.get(GallerySlider.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<GallerySlider> getAllGallerySliderInfo(Long userGroupId) {
		List<GallerySlider> result = null;
		String qry = "";
		if (userGroupId != null) {
			qry = "from  GallerySlider where ugId = :ugId";
		} else {
			qry = "from  GallerySlider where ugId is NULL";
		}
		Session session = sessionFactory.openSession();
		try {
			Query<GallerySlider> query = session.createQuery(qry);
			if (userGroupId != null)
				query.setParameter("ugId", userGroupId);
			result = query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public String getDesc(Long userGroupId) {
		String qry = "select strip_tags(description) from user_group where id = " + userGroupId;
		Session session = sessionFactory.openSession();
		String desc = "";
		try {
			Query<String> query = session.createNativeQuery(qry);
			desc = query.getSingleResult();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return desc;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Long> getObservation(Long observationId) {
		String qry = "SELECT author_id, repr_image_id FROM public.observation where id = " + observationId;
		Session session = sessionFactory.openSession();
		Map<String, Long> result = new HashMap<String, Long>();
		Object[] list = null;
		try {
			Query<Object[]> query = session.createNativeQuery(qry);
			list = query.getSingleResult();
			if (list != null) {
				result.put("authorId", Long.parseLong(list[0].toString()));
				result.put("reprImage", Long.parseLong(list[1].toString()));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

}
