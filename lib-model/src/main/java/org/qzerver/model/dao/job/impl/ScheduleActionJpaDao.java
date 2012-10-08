package org.qzerver.model.dao.job.impl;

import org.qzerver.model.dao.job.ScheduleActionDao;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Transactional(propagation = Propagation.MANDATORY)
public class ScheduleActionJpaDao implements ScheduleActionDao {

    private EntityManager entityManager;

    @Override
    public void removeOrphanedActions() {
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
