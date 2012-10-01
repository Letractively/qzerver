package org.qzerver.model.service.job.execution.impl;

import com.gainmatrix.lib.business.entity.BusinessEntityDao;
import com.gainmatrix.lib.time.ChronometerUtils;
import com.gainmatrix.lib.time.impl.StubChronometer;
import com.google.common.collect.Iterators;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.qzerver.base.AbstractTransactionalTest;
import org.qzerver.model.dao.job.ScheduleExecutionDao;
import org.qzerver.model.domain.action.ActionResult;
import org.qzerver.model.domain.entities.cluster.ClusterGroup;
import org.qzerver.model.domain.entities.cluster.ClusterNode;
import org.qzerver.model.domain.entities.job.*;
import org.qzerver.model.service.cluster.ClusterManagementService;
import org.qzerver.model.service.job.execution.dto.StartExecutionParameters;
import org.qzerver.model.service.job.management.ScheduleJobManagementService;
import org.qzerver.model.service.job.management.dto.ScheduleJobCreateParameters;
import org.qzerver.model.service.quartz.management.QuartzManagementService;
import org.springframework.validation.Validator;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class ScheduleExecutionManagementServiceImplTest extends AbstractTransactionalTest {

    private ScheduleExecutionManagementServiceImpl scheduleExecutionManagementService;

    private ScheduleGroup scheduleGroup;

    private ScheduleJob scheduleJob;

    private ClusterGroup clusterGroup;

    private ClusterNode clusterNode1;

    private ClusterNode clusterNode2;

    private ClusterNode clusterNode3;

    @Resource
    private QuartzManagementService quartzManagementService;

    @Resource
    private Validator modelBeanValidator;

    @Resource
    private StubChronometer chronometer;

    @Resource
    private ScheduleExecutionDao scheduleExecutionDao;

    @Resource
    private BusinessEntityDao businessEntityDao;

    @Resource
    private ScheduleJobManagementService scheduleJobManagementService;

    @Resource
    private ClusterManagementService clusterManagementService;

    @PersistenceContext
    private EntityManager entityManager;

    @Before
    public void setUp() throws Exception {
        scheduleExecutionManagementService = new ScheduleExecutionManagementServiceImpl();
        scheduleExecutionManagementService.setBeanValidator(modelBeanValidator);
        scheduleExecutionManagementService.setChronometer(chronometer);
        scheduleExecutionManagementService.setNode("test.example.com");
        scheduleExecutionManagementService.setScheduleExecutionDao(scheduleExecutionDao);
        scheduleExecutionManagementService.setClusterManagementService(clusterManagementService);
        scheduleExecutionManagementService.setBusinessEntityDao(businessEntityDao);

        scheduleGroup = scheduleJobManagementService.createGroup("Test group");

        clusterGroup = clusterManagementService.createGroup("Test group");
        clusterNode1 = clusterManagementService.createNode(clusterGroup.getId(), "10.2.0.1", "Node 1", true);
        clusterNode2 = clusterManagementService.createNode(clusterGroup.getId(), "10.2.0.2", "Node 2", true);
        clusterNode3 = clusterManagementService.createNode(clusterGroup.getId(), "10.2.0.3", "Node 3", true);

        ScheduleJobCreateParameters parameters = new ScheduleJobCreateParameters();
        parameters.setName("Test Job");
        parameters.setDescription("Nothing to do");
        parameters.setTimezone("UTC");
        parameters.setCron("0 0 0 * * ?");
        parameters.setEnabled(true);
        parameters.setActionType(ScheduleActionType.LOCAL_COMMAND);
        parameters.setClusterGroupId(clusterGroup.getId());
        parameters.setScheduleGroupId(scheduleGroup.getId());
        parameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);

        scheduleJob = scheduleJobManagementService.createJob(parameters);

        // Disable Quartz job manually to prevent the firing from Quartz
        quartzManagementService.disableJob(scheduleJob.getId());
    }

    @Test
    public void testNormalExecution() throws Exception {
        // Start execution

        StartExecutionParameters startExecutionParameters = new StartExecutionParameters();
        startExecutionParameters.setManual(false);
        startExecutionParameters.setFired(ChronometerUtils.parseMoment("2012-01-02 12:32:12.000 UTC"));
        startExecutionParameters.setScheduled(ChronometerUtils.parseMoment("2012-01-02 12:32:12.000 UTC"));

        ScheduleExecution scheduleExecution =
            scheduleExecutionManagementService.startExecution(scheduleJob.getId(), startExecutionParameters);
        Assert.assertNotNull(scheduleExecution);
        Assert.assertNull(scheduleExecution.getFinished());

        entityManager.flush();
        entityManager.clear();

        ScheduleExecution scheduleExecutionModified =
            scheduleExecutionManagementService.findExecution(scheduleExecution.getId());
        Assert.assertNotNull(scheduleExecutionModified);
        Assert.assertEquals(scheduleExecution, scheduleExecutionModified);
        Assert.assertEquals(3, scheduleExecutionModified.getNodes().size());

        ScheduleExecutionNode scheduleExecutionNode1 = scheduleExecutionModified.getNodes().get(0);
        Assert.assertNotNull(scheduleExecutionNode1);
        Assert.assertEquals(0, scheduleExecutionNode1.getOrderIndex());
        Assert.assertEquals(clusterNode2.getAddress(), scheduleExecutionNode1.getAddress());

        ScheduleExecutionNode scheduleExecutionNode2 = scheduleExecutionModified.getNodes().get(1);
        Assert.assertNotNull(scheduleExecutionNode2);
        Assert.assertEquals(1, scheduleExecutionNode2.getOrderIndex());
        Assert.assertEquals(clusterNode3.getAddress(), scheduleExecutionNode2.getAddress());

        ScheduleExecutionNode scheduleExecutionNode3 = scheduleExecutionModified.getNodes().get(2);
        Assert.assertNotNull(scheduleExecutionNode3);
        Assert.assertEquals(2, scheduleExecutionNode3.getOrderIndex());
        Assert.assertEquals(clusterNode1.getAddress(), scheduleExecutionNode3.getAddress());

        // Search all executions

        List<ScheduleExecution> scheduleExecutions = scheduleExecutionManagementService.findAll(null);
        Assert.assertNotNull(scheduleExecutions);
        Assert.assertTrue(scheduleExecutions.size() > 0);
        Assert.assertTrue(Iterators.contains(scheduleExecutions.iterator(), scheduleExecution));

        // Make some progress

        ScheduleExecutionResult scheduleExecutionResult;

        scheduleExecutionResult = scheduleExecutionManagementService.startExecutionResult(scheduleExecutionNode1.getId());
        Assert.assertNotNull(scheduleExecutionResult);

        scheduleExecutionResult = scheduleExecutionManagementService.finishExecutionResult(scheduleExecutionResult.getId(),
            new ActionResultStub(true));
        Assert.assertNotNull(scheduleExecutionResult);

        scheduleExecutionResult = scheduleExecutionManagementService.startExecutionResult(scheduleExecutionNode2.getId());
        Assert.assertNotNull(scheduleExecutionResult);

        scheduleExecutionResult = scheduleExecutionManagementService.finishExecutionResult(scheduleExecutionResult.getId(),
            new ActionResultStub(true));
        Assert.assertNotNull(scheduleExecutionResult);

        scheduleExecutionResult = scheduleExecutionManagementService.startExecutionResult(scheduleExecutionNode3.getId());
        Assert.assertNotNull(scheduleExecutionResult);

        scheduleExecutionResult = scheduleExecutionManagementService.finishExecutionResult(scheduleExecutionResult.getId(),
            new ActionResultStub(false));
        Assert.assertNotNull(scheduleExecutionResult);

        entityManager.flush();
        entityManager.clear();

        scheduleExecutionModified =
            scheduleExecutionManagementService.findExecution(scheduleExecution.getId());
        Assert.assertNotNull(scheduleExecutionModified);
        Assert.assertEquals(scheduleExecution, scheduleExecutionModified);
        Assert.assertEquals(3, scheduleExecutionModified.getResults().size());

        scheduleExecutionResult = scheduleExecutionModified.getResults().get(0);
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertEquals(0, scheduleExecutionResult.getOrderIndex());
        Assert.assertTrue(scheduleExecutionResult.isSucceed());

        scheduleExecutionResult = scheduleExecutionModified.getResults().get(1);
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertEquals(1, scheduleExecutionResult.getOrderIndex());
        Assert.assertTrue(scheduleExecutionResult.isSucceed());

        scheduleExecutionResult = scheduleExecutionModified.getResults().get(2);
        Assert.assertEquals(2, scheduleExecutionResult.getOrderIndex());
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertFalse(scheduleExecutionResult.isSucceed());

        // Finish execution

        scheduleExecutionModified = scheduleExecutionManagementService.finishExecution(
            scheduleExecution.getId(), ScheduleExecutionStatus.SUCCEED);
        Assert.assertNotNull(scheduleExecutionModified);
        Assert.assertNotNull(scheduleExecutionModified.getFinished());

        entityManager.flush();
        entityManager.clear();

        scheduleExecutionModified =
            scheduleExecutionManagementService.findExecution(scheduleExecution.getId());
        Assert.assertNotNull(scheduleExecutionModified);

    }

    @Test
    public void testCancelExecution() throws Exception {
        // Start execution

        StartExecutionParameters startExecutionParameters = new StartExecutionParameters();
        startExecutionParameters.setManual(false);
        startExecutionParameters.setFired(ChronometerUtils.parseMoment("2012-01-02 12:32:12.000 UTC"));
        startExecutionParameters.setScheduled(ChronometerUtils.parseMoment("2012-01-02 12:32:12.000 UTC"));

        ScheduleExecution scheduleExecution =
            scheduleExecutionManagementService.startExecution(scheduleJob.getId(), startExecutionParameters);
        Assert.assertNotNull(scheduleExecution);
        Assert.assertNull(scheduleExecution.getFinished());

        // Cancel execution

        ScheduleExecution scheduleExecutionModified = scheduleExecutionManagementService.cancelExecution(
            scheduleExecution.getId());
        Assert.assertNotNull(scheduleExecutionModified);
        Assert.assertNull(scheduleExecutionModified.getFinished());
        Assert.assertTrue(scheduleExecutionModified.isCancelled());

        entityManager.flush();
        entityManager.clear();

        scheduleExecutionModified = scheduleExecutionManagementService.findExecution(scheduleExecution.getId());
        Assert.assertNotNull(scheduleExecutionModified);
        Assert.assertNull(scheduleExecutionModified.getFinished());
        Assert.assertTrue(scheduleExecutionModified.isCancelled());

        // Finish execution

        scheduleExecutionModified = scheduleExecutionManagementService.finishExecution(
            scheduleExecution.getId(), ScheduleExecutionStatus.SUCCEED);
        Assert.assertNotNull(scheduleExecutionModified);
        Assert.assertNotNull(scheduleExecutionModified.getFinished());

        entityManager.flush();
        entityManager.clear();

        scheduleExecutionModified =
            scheduleExecutionManagementService.findExecution(scheduleExecution.getId());
        Assert.assertNotNull(scheduleExecutionModified);
        Assert.assertNotNull(scheduleExecutionModified.getFinished());
        Assert.assertFalse(scheduleExecutionModified.isCancelled());
    }

    @Test
    public void testFindFinished() throws Exception {

    }

    @Test
    public void testFindEngaged() throws Exception {

    }

    @Test
    public void testFindByJob() throws Exception {

    }

    private static class ActionResultStub implements ActionResult {

        private boolean succeed;

        private ActionResultStub(boolean succeed) {
            this.succeed = succeed;
        }

        @Override
        public boolean isSucceed() {
            return succeed;
        }

    }


}