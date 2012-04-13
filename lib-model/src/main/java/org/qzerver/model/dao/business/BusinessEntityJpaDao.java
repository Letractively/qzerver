package org.qzerver.model.dao.business;

import com.gainmatrix.lib.business.BusinessEntity;
import com.gainmatrix.lib.business.BusinessEntityDao;
import com.gainmatrix.lib.business.BusinessId;
import com.gainmatrix.lib.business.BusinessId_;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.*;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class BusinessEntityJpaDao implements BusinessEntityDao {

    private static final String ENTITY_ID = "id";

    private static final String ENTITY_BUSINESS_ID = "businessId";

    private EntityManager entityManager;

    @Override
    public <T extends BusinessEntity<I>, I> T findById(Class<T> clazz, I id) {
        return entityManager.find(clazz, id, LockModeType.NONE);
    }

    @Override
    public <T extends BusinessEntity<I>, I> Set<T> findByIds(Class<T> clazz, Collection<I> ids) {
        if ((ids == null) || (ids.isEmpty())) {
            return Collections.emptySet();
        }

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(clazz);
        Root<T> root = criteriaQuery.from(clazz);

        criteriaQuery.where(
            root.get(ENTITY_ID).in(ids)
        );

        TypedQuery<T> typedQuery = entityManager.createQuery(criteriaQuery);

        List<T> resultList = typedQuery.getResultList();
        Set<T> resultSet = new HashSet<T>(resultList);

        return Collections.unmodifiableSet(resultSet);
    }

    @Override
    public <T extends BusinessEntity<I>, I> T findByBusinessId(Class<T> clazz, BusinessId businessId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(clazz);
        Root<T> root = criteriaQuery.from(clazz);

        criteriaQuery.where(
            criteriaBuilder.and(
                criteriaBuilder.equal(
                    root.<BusinessId>get(ENTITY_BUSINESS_ID).get(BusinessId_.hi),
                    businessId.getHi()
                ),
                criteriaBuilder.equal(
                    root.<BusinessId>get(ENTITY_BUSINESS_ID).get(BusinessId_.lo),
                    businessId.getLo()
                )
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
    public <T extends BusinessEntity<I>, I> T lockById(Class<T> clazz, I id) {
        return entityManager.find(clazz, id, LockModeType.PESSIMISTIC_WRITE);
    }

    @Override
    public <T extends BusinessEntity<I>, I> void lock(T entity) {
        entityManager.lock(entity, LockModeType.PESSIMISTIC_WRITE);
    }

    @Override
    public <T extends BusinessEntity<I>, I> void save(T entity) {
        if (!entityManager.contains(entity)) {
            entityManager.persist(entity);
        }
    }

    @Override
    public <T extends BusinessEntity<I>, I> void deleteById(Class<T> clazz, I id) {
        T entity = findById(clazz, id);

        if (entity != null) {
            delete(entity);
        }
    }

    @Override
    public <T extends BusinessEntity<I>, I> void delete(T entity) {
        entityManager.remove(entity);
    }

    @Override
    public void flush() {
        entityManager.flush();
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

}
