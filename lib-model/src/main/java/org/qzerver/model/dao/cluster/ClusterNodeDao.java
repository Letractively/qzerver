package org.qzerver.model.dao.cluster;

import com.gainmatrix.lib.business.BusinessEntityDao;
import org.qzerver.model.domain.entities.cluster.ClusterNode;
import org.springframework.stereotype.Repository;

@Repository
public interface ClusterNodeDao extends BusinessEntityDao<ClusterNode, Long> {
}
