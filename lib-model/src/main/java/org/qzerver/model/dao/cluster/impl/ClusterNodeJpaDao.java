package org.qzerver.model.dao.cluster.impl;

import org.qzerver.model.dao.business.AbstractBusinessEntityDao;
import org.qzerver.model.dao.cluster.ClusterNodeDao;
import org.qzerver.model.domain.entities.cluster.ClusterNode;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.MANDATORY)
public class ClusterNodeJpaDao extends AbstractBusinessEntityDao<ClusterNode, Long> implements ClusterNodeDao {

    public ClusterNodeJpaDao() {
        super(ClusterNode.class);
    }

}
