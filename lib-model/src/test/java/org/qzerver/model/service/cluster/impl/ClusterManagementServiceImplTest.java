package org.qzerver.model.service.cluster.impl;

import com.gainmatrix.lib.business.entity.BusinessEntityDao;
import com.google.common.collect.Iterators;
import junit.framework.Assert;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;
import org.qzerver.base.AbstractModelTest;
import org.qzerver.model.dao.cluster.ClusterGroupDao;
import org.qzerver.model.dao.job.ScheduleJobDao;
import org.qzerver.model.domain.entities.cluster.ClusterGroup;
import org.qzerver.model.domain.entities.cluster.ClusterNode;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class ClusterManagementServiceImplTest extends AbstractModelTest {

    private IMocksControl control;

    private ClusterManagementServiceImpl clusterManagementService;

    @Resource
    private BusinessEntityDao businessEntityDao;

    @Resource
    private ClusterGroupDao clusterGroupDao;

    @Resource
    private ScheduleJobDao scheduleJobDao;

    @PersistenceContext
    private EntityManager entityManager;

    @Before
    public void setUp() throws Exception {
        control = EasyMock.createStrictControl();

        clusterManagementService = new ClusterManagementServiceImpl();
        clusterManagementService.setBusinessEntityDao(businessEntityDao);
        clusterManagementService.setClusterGroupDao(clusterGroupDao);
        clusterManagementService.setScheduleJobDao(scheduleJobDao);
    }

    @Test
    public void testSimpleScenario() throws Exception {
        ClusterGroup clusterGroup = clusterManagementService.createGroup("Test group");
        Assert.assertNotNull(clusterGroup);
        Assert.assertEquals("Test group", clusterGroup.getName());
        Assert.assertEquals(0, clusterGroup.getNodes().size());
        Assert.assertEquals(0, clusterGroup.getRollingIndex());

        entityManager.flush();
        entityManager.clear();

        List<ClusterGroup> allClusterGroups = clusterManagementService.findAllGroups(null);
        Assert.assertNotNull(allClusterGroups);
        Assert.assertTrue(allClusterGroups.size() > 0);
        Assert.assertTrue(Iterators.contains(allClusterGroups.iterator(), clusterGroup));

        // Modify group

        ClusterGroup clusterGroupModified = clusterManagementService.modifyGroup(clusterGroup.getId(), "Test group 2");
        Assert.assertNotNull(clusterGroupModified);
        Assert.assertEquals("Test group 2", clusterGroupModified.getName());

        entityManager.flush();
        entityManager.clear();

        clusterGroupModified = clusterManagementService.findGroup(clusterGroup.getId());
        Assert.assertNotNull(clusterGroupModified);
        Assert.assertEquals("Test group 2", clusterGroupModified.getName());
        Assert.assertEquals(0, clusterGroupModified.getNodes().size());

        // Create nodes

        ClusterNode clusterNode1 = clusterManagementService.createNode(clusterGroup.getId(),
            "10.2.0.1", "Node 1", true);
        Assert.assertNotNull(clusterNode1);

        ClusterNode clusterNode2 = clusterManagementService.createNode(clusterGroup.getId(),
            "10.2.0.2", "Node 2", true);
        Assert.assertNotNull(clusterNode2);

        ClusterNode clusterNode3 = clusterManagementService.createNode(clusterGroup.getId(),
            "10.2.0.3", "Node 3", false);
        Assert.assertNotNull(clusterNode3);

        entityManager.flush();
        entityManager.clear();

        clusterGroupModified = clusterManagementService.findGroup(clusterGroup.getId());
        Assert.assertNotNull(clusterGroupModified);
        Assert.assertEquals(3, clusterGroupModified.getNodes().size());
        Assert.assertEquals(clusterNode1, clusterGroupModified.getNodes().get(0));
        Assert.assertEquals(clusterNode2, clusterGroupModified.getNodes().get(1));
        Assert.assertEquals(clusterNode3, clusterGroupModified.getNodes().get(2));

        // Modify nodes

        ClusterNode clusterNodeModified;

        clusterNodeModified = clusterManagementService.modifyNode(clusterNode2.getId(),
            "10.2.0.2", "Node 2", false);
        Assert.assertNotNull(clusterNodeModified);
        Assert.assertEquals(false, clusterNodeModified.isEnabled());

        clusterNodeModified = clusterManagementService.modifyNode(clusterNode3.getId(),
            "10.2.0.3", "Node 3", true);
        Assert.assertNotNull(clusterNodeModified);
        Assert.assertEquals(true, clusterNodeModified.isEnabled());

        entityManager.flush();
        entityManager.clear();

        clusterGroupModified = clusterManagementService.findGroup(clusterGroup.getId());
        Assert.assertNotNull(clusterGroupModified);
        Assert.assertEquals(3, clusterGroupModified.getNodes().size());

        clusterNodeModified = clusterGroupModified.getNodes().get(0);
        Assert.assertNotNull(clusterNodeModified);
        Assert.assertEquals(0, clusterNodeModified.getOrderIndex());
        Assert.assertEquals("10.2.0.1", clusterNodeModified.getAddress());
        Assert.assertEquals("Node 1", clusterNodeModified.getDescription());
        Assert.assertEquals(true, clusterNodeModified.isEnabled());

        clusterNodeModified = clusterGroupModified.getNodes().get(1);
        Assert.assertNotNull(clusterNodeModified);
        Assert.assertEquals(1, clusterNodeModified.getOrderIndex());
        Assert.assertEquals("10.2.0.2", clusterNodeModified.getAddress());
        Assert.assertEquals("Node 2", clusterNodeModified.getDescription());
        Assert.assertEquals(false, clusterNodeModified.isEnabled());

        clusterNodeModified = clusterGroupModified.getNodes().get(2);
        Assert.assertNotNull(clusterNodeModified);
        Assert.assertEquals(2, clusterNodeModified.getOrderIndex());
        Assert.assertEquals("10.2.0.3", clusterNodeModified.getAddress());
        Assert.assertEquals("Node 3", clusterNodeModified.getDescription());
        Assert.assertEquals(true, clusterNodeModified.isEnabled());

        // Delete node

        clusterManagementService.deleteNode(clusterNode2.getId());

        entityManager.flush();
        entityManager.clear();

        clusterGroupModified = clusterManagementService.findGroup(clusterGroup.getId());
        Assert.assertNotNull(clusterGroupModified);
        Assert.assertEquals(2, clusterGroupModified.getNodes().size());

        clusterNodeModified = clusterGroupModified.getNodes().get(0);
        Assert.assertNotNull(clusterNodeModified);
        Assert.assertEquals(0, clusterNodeModified.getOrderIndex());
        Assert.assertEquals("10.2.0.1", clusterNodeModified.getAddress());
        Assert.assertEquals("Node 1", clusterNodeModified.getDescription());
        Assert.assertEquals(true, clusterNodeModified.isEnabled());

        clusterNodeModified = clusterGroupModified.getNodes().get(1);
        Assert.assertNotNull(clusterNodeModified);
        Assert.assertEquals(1, clusterNodeModified.getOrderIndex());
        Assert.assertEquals("10.2.0.3", clusterNodeModified.getAddress());
        Assert.assertEquals("Node 3", clusterNodeModified.getDescription());
        Assert.assertEquals(true, clusterNodeModified.isEnabled());

        // Delete group

        clusterManagementService.deleteGroup(clusterGroup.getId());

        entityManager.flush();
        entityManager.clear();

        clusterGroupModified = clusterManagementService.findGroup(clusterGroup.getId());
        Assert.assertNull(clusterGroupModified);
    }

}
