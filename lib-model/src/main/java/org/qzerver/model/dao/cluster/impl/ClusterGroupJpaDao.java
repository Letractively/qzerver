package org.qzerver.model.dao.cluster.impl;

import com.gainmatrix.lib.paging.Extraction;
import org.qzerver.model.dao.business.AbstractBusinessEntityDao;
import org.qzerver.model.dao.cluster.ClusterGroupDao;
import org.qzerver.model.domain.entities.cluster.ClusterGroup;
import org.qzerver.model.domain.entities.cluster.ClusterGroup_;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Transactional(propagation = Propagation.MANDATORY)
public class ClusterGroupJpaDao extends AbstractBusinessEntityDao<ClusterGroup, Long> implements ClusterGroupDao {

    public ClusterGroupJpaDao() {
        super(ClusterGroup.class);
    }

    @Override
    public List<ClusterGroup> findAllGroups(Extraction extraction) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<ClusterGroup> criteriaQuery = criteriaBuilder.createQuery(ClusterGroup.class);
        Root<ClusterGroup> root = criteriaQuery.from(ClusterGroup.class);

        criteriaQuery.orderBy(
                criteriaBuilder.asc(root.get(ClusterGroup_.name))
        );

        TypedQuery<ClusterGroup> typedQuery = getEntityManager().createQuery(criteriaQuery);
        if (extraction != null) {
            typedQuery.setFirstResult(extraction.getOffset());
            typedQuery.setMaxResults(extraction.getCount());
        }

        return typedQuery.getResultList();
    }

}
