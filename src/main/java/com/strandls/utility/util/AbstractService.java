package com.strandls.utility.util;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.utility.service.impl.UtilityServiceImpl;

public abstract class AbstractService<T> {
	private static final Logger logger = LoggerFactory.getLogger(AbstractService.class);
	public Class<T> entityClass;
	protected AbstractDAO<T, Long> dao;

	@SuppressWarnings("unchecked")
	public AbstractService(AbstractDAO<T, Long> dao) {
		System.out.println("\nAbstractService constructor");
		this.dao = dao;
		entityClass = ((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
	}

	public T save(T entity) {
		try {
			this.dao.save(entity);
			return entity;
		} catch (RuntimeException re) {
			logger.error(re.getMessage());
			throw re;
		}
	}

	public T update(T entity) {
		try {
			this.dao.update(entity);
			return entity;
		} catch (RuntimeException re) {
			logger.error(re.getMessage());
			throw re;
		}

	}

	public T delete(Long id) {
		try {
			T entity = (T) this.dao.findById(id);
			this.dao.delete(entity);
			return entity;
		} catch (RuntimeException re) {
			logger.error(re.getMessage());
			throw re;
		}
	}

	public T findById(Long id) {
		try {
			T entity = (T) this.dao.findById(id);
			return entity;
		} catch (RuntimeException re) {
			logger.error(re.getMessage());
			throw re;
		}
	}

	public List<T> findAll(int limit, int offset) {
		try {
			List<T> entities = this.dao.findAll(limit, offset);
			return entities;
		} catch (RuntimeException re) {
			logger.error(re.getMessage());
			throw re;
		}
	}

	public List<T> findAll() {

		try {
			List<T> entities = this.dao.findAll();
			return entities;
		} catch (RuntimeException re) {
			logger.error(re.getMessage());
			throw re;
		}
	}

}
