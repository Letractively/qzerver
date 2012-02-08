package org.qzerver.model.dao.business;

import com.gainmatrix.lib.business.AbstractBusinessEntity;
import com.gainmatrix.lib.business.BusinessEntityDao;
import com.gainmatrix.lib.business.BusinessId;
import com.gainmatrix.lib.business.BusinessId_;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.*;

public abstract class AbstractBusinessEntityDao<T extends AbstractBusinessEntity<I>,I> implements BusinessEntityDao<T,I> {

    private static final String ENTITY_ID = "id";

    private static final String ENTITY_BUSINESS_ID = "businessId";

    private EntityManager entityManager;

    private Class<T> entityClass;

    public AbstractBusinessEntityDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public T findById(I id) {
        return getEntityManager().find(entityClass, id, LockModeType.NONE);
    }

    @Override
    public Set<T> findByIds(Collection<I> ids) {
        if ((ids == null) || (ids.isEmpty())) {
            return Collections.emptySet();
        }

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<T> root = criteriaQuery.from(entityClass);

        criteriaQuery.where(
                root.get(ENTITY_ID).in(ids)
        );

        TypedQuery<T> typedQuery = entityManager.createQuery(criteriaQuery);

        List<T> resultList = typedQuery.getResultList();
        Set<T> resultSet = new HashSet<T>(resultList);

        return Collections.unmodifiableSet(resultSet);
    }

    @Override
    public T findByBusinessId(BusinessId businessId) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<T> root = criteriaQuery.from(entityClass);

        criteriaQuery.where(
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.<BusinessId>get(ENTITY_BUSINESS_ID).get(BusinessId_.hi), businessId.getHi()),
                        criteriaBuilder.equal(root.<BusinessId>get(ENTITY_BUSINESS_ID).get(BusinessId_.lo), businessId.getLo())
                )
        );

        TypedQuery<T> typedQuery = entityManager.createQuery(criteriaQuery);

        try {
            return typedQuery.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public T lockById(I id) {
        return getEntityManager().find(entityClass, id, LockModeType.PESSIMISTIC_WRITE);
    }

    @Override
    public void save(T entity) {
        EntityManager entityManager = getEntityManager();

        if (! entityManager.contains(entity)) {
            entityManager.persist(entity);
        }
    }

    @Override
    public void deleteById(I id) {
        T entity = findById(id);

        if (entity != null) {
            delete(entity);
        }
    }

    @Override
    public void delete(T entity) {
        getEntityManager().remove(entity);
    }

    @Override
    public void flush() {
        getEntityManager().flush();
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

}
