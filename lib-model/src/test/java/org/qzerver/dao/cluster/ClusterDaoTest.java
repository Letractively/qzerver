package org.qzerver.dao.cluster;

import com.gainmatrix.lib.business.BusinessEntityDao;
import org.junit.Test;
import org.qzerver.base.AbstractModelTest;
import org.qzerver.base.BusinessEntityJpaDaoChecker;
import org.qzerver.model.domain.entities.cluster.ClusterGroup;
import org.qzerver.model.domain.entities.cluster.ClusterNode;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class ClusterDaoTest extends AbstractModelTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Resource
    private BusinessEntityDao businessEntityDao;

    @Test
    public void testClusterGroupDao() throws Exception {
        ClusterGroup clusterGroup = new ClusterGroup();
        clusterGroup.setName("dgfsrgw");
        clusterGroup.setRollingIndex(2);

        BusinessEntityJpaDaoChecker.checkBusinessEntityDao(entityManager, businessEntityDao,
                ClusterGroup.class, clusterGroup);
    }

    @Test
    public void testClusterNodeDao() throws Exception {
        ClusterGroup clusterGroup = new ClusterGroup();
        clusterGroup.setName("dgfsrgw");
        clusterGroup.setRollingIndex(2);
        businessEntityDao.save(clusterGroup);

        ClusterNode clusterNode = new ClusterNode();
        clusterNode.setAddress("www.example.org");
        clusterNode.setEnabled(false);
        clusterNode.setDescription("rwgwrgwr");
        clusterNode.setOrderIndex(2);

        clusterNode.setGroup(clusterGroup);
        clusterGroup.getNodes().add(clusterNode);

        BusinessEntityJpaDaoChecker.checkBusinessEntityDao(entityManager, businessEntityDao,
                ClusterNode.class, clusterNode);
    }

}
