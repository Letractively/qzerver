package org.qzerver.model.service.job.executor.impl;

import com.gainmatrix.lib.time.ChronometerUtils;
import com.gainmatrix.lib.time.impl.StubChronometer;
import com.google.common.collect.Lists;
import junit.framework.Assert;
import org.easymock.*;
import org.junit.Before;
import org.junit.Test;
import org.qzerver.base.AbstractModelTest;
import org.qzerver.model.agent.action.ActionAgent;
import org.qzerver.model.domain.action.ActionResult;
import org.qzerver.model.domain.entities.cluster.ClusterGroup;
import org.qzerver.model.domain.entities.cluster.ClusterNode;
import org.qzerver.model.domain.entities.job.*;
import org.qzerver.model.service.cluster.ClusterManagementService;
import org.qzerver.model.service.job.execution.ScheduleExecutionManagementService;
import org.qzerver.model.service.job.executor.dto.AutomaticJobExecutionParameters;
import org.qzerver.model.service.job.executor.dto.ManualJobExecutionParameters;
import org.qzerver.model.service.job.management.ScheduleJobManagementService;
import org.qzerver.model.service.job.management.dto.ScheduleJobCreateParameters;
import org.qzerver.model.service.mail.MailService;
import org.qzerver.model.service.quartz.management.QuartzManagementService;
import org.springframework.validation.Validator;

import javax.annotation.Resource;
import java.util.concurrent.Callable;

public class ScheduleJobExecutorServiceImplTest extends AbstractModelTest {

    private IMocksControl control;

    private MailService mailService;

    private ScheduleJobExecutorServiceImpl scheduleJobExecutorService;

    private ActionAgent actionAgent;

    private ScheduleGroup scheduleGroup;

    private ScheduleJob scheduleJob;

    private ClusterGroup clusterGroup;

    private ClusterNode clusterNode1;

    private ClusterNode clusterNode2;

    private ClusterNode clusterNode3;

    @Resource
    private ScheduleJobManagementService scheduleJobManagementService;

    @Resource
    private ClusterManagementService clusterManagementService;

    @Resource
    private QuartzManagementService quartzManagementService;

    @Resource
    private ScheduleExecutionManagementService scheduleExecutionManagementService;

    @Resource
    private Validator modelBeanValidator;

    @Resource
    private StubChronometer chronometer;

    @Before
    public void setUp() throws Exception {
        control = EasyMock.createStrictControl();

        mailService = control.createMock(MailService.class);
        actionAgent = control.createMock(ActionAgent.class);

        scheduleJobExecutorService = new ScheduleJobExecutorServiceImpl();
        scheduleJobExecutorService.setBeanValidator(modelBeanValidator);
        scheduleJobExecutorService.setChronometer(chronometer);
        scheduleJobExecutorService.setMailService(mailService);
        scheduleJobExecutorService.setExecutionManagementService(scheduleExecutionManagementService);
        scheduleJobExecutorService.setActionAgent(actionAgent);

        scheduleGroup = scheduleJobManagementService.createGroup("Test group");

        clusterGroup = clusterManagementService.createGroup("Test group");
        clusterNode1 = clusterManagementService.createNode(clusterGroup.getId(), "10.2.0.1", "Node 1", true);
        clusterNode2 = clusterManagementService.createNode(clusterGroup.getId(), "10.2.0.2", "Node 2", true);
        clusterNode3 = clusterManagementService.createNode(clusterGroup.getId(), "10.2.0.3", "Node 3", false);
    }

    @Test
    public void testNormalAutomaticSingleExecution() throws Exception {
        ScheduleJobCreateParameters jobCreateParameters = new ScheduleJobCreateParameters();
        jobCreateParameters.setName("Test Job");
        jobCreateParameters.setDescription("Nothing to do");
        jobCreateParameters.setTimezone("UTC");
        jobCreateParameters.setCron("0 0 0 * * ?");
        jobCreateParameters.setEnabled(true);
        jobCreateParameters.setAllNodes(false);
        jobCreateParameters.setClusterGroupId(clusterGroup.getId());
        jobCreateParameters.setScheduleGroupId(scheduleGroup.getId());
        jobCreateParameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);

        scheduleJob = scheduleJobManagementService.createJob(jobCreateParameters);
        quartzManagementService.disableJob(scheduleJob.getId());

        Capture<ScheduleAction> scheduleActionCapture = new ScheduleActionCapture();

        control.reset();

        EasyMock.expect(actionAgent.executeAction(
            EasyMock.anyLong(),
            EasyMock.capture(scheduleActionCapture),
            EasyMock.eq(clusterNode2.getAddress())
        )).andReturn(new ActionResultStub(true));

        control.replay();

        AutomaticJobExecutionParameters jobExecutionParameters = new AutomaticJobExecutionParameters();
        jobExecutionParameters.setScheduled(ChronometerUtils.parseMoment("2012-02-20 10:00:00.000 UTC"));
        jobExecutionParameters.setFired(ChronometerUtils.parseMoment("2012-02-20 10:00:00.231 UTC"));

        ScheduleExecution scheduleExecution = scheduleJobExecutorService.executeAutomaticJob(scheduleJob.getId(),
            jobExecutionParameters);
        Assert.assertNotNull(scheduleExecution);

        control.verify();

        ScheduleExecutionNode scheduleExecutionNode;
        ScheduleExecutionResult scheduleExecutionResult;

        scheduleExecution = scheduleExecutionManagementService.findExecution(scheduleExecution.getId());
        Assert.assertEquals(ScheduleExecutionStatus.SUCCEED, scheduleExecution.getStatus());
        Assert.assertEquals(2, scheduleExecution.getNodes().size());
        Assert.assertEquals(1, scheduleExecution.getResults().size());

        scheduleExecutionNode = scheduleExecution.getNodes().get(0);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode2.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertTrue(scheduleExecutionResult.isSucceed());

        scheduleExecutionNode = scheduleExecution.getNodes().get(1);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode1.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNull(scheduleExecutionResult);
    }

    @Test
    public void testNormalManualSingleExecution() throws Exception {
        ScheduleJobCreateParameters jobCreateParameters = new ScheduleJobCreateParameters();
        jobCreateParameters.setName("Test Job");
        jobCreateParameters.setDescription("Nothing to do");
        jobCreateParameters.setTimezone("UTC");
        jobCreateParameters.setCron("0 0 0 * * ?");
        jobCreateParameters.setEnabled(true);
        jobCreateParameters.setAllNodes(false);
        jobCreateParameters.setClusterGroupId(clusterGroup.getId());
        jobCreateParameters.setScheduleGroupId(scheduleGroup.getId());
        jobCreateParameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);

        scheduleJob = scheduleJobManagementService.createJob(jobCreateParameters);
        quartzManagementService.disableJob(scheduleJob.getId());

        Capture<ScheduleAction> scheduleActionCapture = new ScheduleActionCapture();

        control.reset();

        EasyMock.expect(actionAgent.executeAction(
            EasyMock.anyLong(),
            EasyMock.capture(scheduleActionCapture),
            EasyMock.eq(clusterNode2.getAddress())
        )).andReturn(new ActionResultStub(true));

        control.replay();

        ManualJobExecutionParameters jobExecutionParameters = new ManualJobExecutionParameters();
        jobExecutionParameters.setComment("Test comment");

        ScheduleExecution scheduleExecution = scheduleJobExecutorService.executeManualJob(scheduleJob.getId(),
            jobExecutionParameters);
        Assert.assertNotNull(scheduleExecution);

        control.verify();

        ScheduleExecutionNode scheduleExecutionNode;
        ScheduleExecutionResult scheduleExecutionResult;

        scheduleExecution = scheduleExecutionManagementService.findExecution(scheduleExecution.getId());
        Assert.assertEquals(ScheduleExecutionStatus.SUCCEED, scheduleExecution.getStatus());
        Assert.assertEquals("Test comment", scheduleExecution.getComment());
        Assert.assertEquals(2, scheduleExecution.getNodes().size());
        Assert.assertEquals(1, scheduleExecution.getResults().size());

        scheduleExecutionNode = scheduleExecution.getNodes().get(0);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode2.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertTrue(scheduleExecutionResult.isSucceed());

        scheduleExecutionNode = scheduleExecution.getNodes().get(1);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode1.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNull(scheduleExecutionResult);
    }

    @Test
    public void testNormalManualWithAddressSingleExecution() throws Exception {
        ScheduleJobCreateParameters jobCreateParameters = new ScheduleJobCreateParameters();
        jobCreateParameters.setName("Test Job");
        jobCreateParameters.setDescription("Nothing to do");
        jobCreateParameters.setTimezone("UTC");
        jobCreateParameters.setCron("0 0 0 * * ?");
        jobCreateParameters.setEnabled(true);
        jobCreateParameters.setAllNodes(false);
        jobCreateParameters.setClusterGroupId(clusterGroup.getId());
        jobCreateParameters.setScheduleGroupId(scheduleGroup.getId());
        jobCreateParameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);

        scheduleJob = scheduleJobManagementService.createJob(jobCreateParameters);
        quartzManagementService.disableJob(scheduleJob.getId());

        Capture<ScheduleAction> scheduleActionCapture = new ScheduleActionCapture();

        control.reset();

        EasyMock.expect(actionAgent.executeAction(
            EasyMock.anyLong(),
            EasyMock.capture(scheduleActionCapture),
            EasyMock.eq("192.168.1.1")
        )).andReturn(new ActionResultStub(true));

        control.replay();

        ManualJobExecutionParameters jobExecutionParameters = new ManualJobExecutionParameters();
        jobExecutionParameters.setComment("Test comment");
        jobExecutionParameters.setAddresses(Lists.newArrayList("192.168.1.1", "192.168.1.2"));

        ScheduleExecution scheduleExecution = scheduleJobExecutorService.executeManualJob(scheduleJob.getId(),
            jobExecutionParameters);
        Assert.assertNotNull(scheduleExecution);

        control.verify();

        ScheduleExecutionNode scheduleExecutionNode;
        ScheduleExecutionResult scheduleExecutionResult;

        scheduleExecution = scheduleExecutionManagementService.findExecution(scheduleExecution.getId());
        Assert.assertEquals(ScheduleExecutionStatus.SUCCEED, scheduleExecution.getStatus());
        Assert.assertEquals("Test comment", scheduleExecution.getComment());
        Assert.assertEquals(2, scheduleExecution.getNodes().size());
        Assert.assertEquals(1, scheduleExecution.getResults().size());

        scheduleExecutionNode = scheduleExecution.getNodes().get(0);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals("192.168.1.1", scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertTrue(scheduleExecutionResult.isSucceed());

        scheduleExecutionNode = scheduleExecution.getNodes().get(1);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals("192.168.1.2", scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNull(scheduleExecutionResult);
    }

    @Test
    public void testSucceedTimeoutAutomaticSingleExecution1() throws Exception {
        ScheduleJobCreateParameters jobCreateParameters = new ScheduleJobCreateParameters();
        jobCreateParameters.setName("Test Job");
        jobCreateParameters.setDescription("Nothing to do");
        jobCreateParameters.setTimezone("UTC");
        jobCreateParameters.setCron("0 0 0 * * ?");
        jobCreateParameters.setEnabled(true);
        jobCreateParameters.setTimeout(1000);
        jobCreateParameters.setAllNodes(false);
        jobCreateParameters.setClusterGroupId(clusterGroup.getId());
        jobCreateParameters.setScheduleGroupId(scheduleGroup.getId());
        jobCreateParameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);

        scheduleJob = scheduleJobManagementService.createJob(jobCreateParameters);
        quartzManagementService.disableJob(scheduleJob.getId());

        Capture<ScheduleAction> scheduleAction1Capture = new ScheduleActionCapture();
        ActionResultStubAnswer actionResultStub1Answer = new ActionResultStubAnswer(false, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                chronometer.shiftTick(900);
                return null;
            }
        });

        Capture<ScheduleAction> scheduleAction2Capture = new ScheduleActionCapture();
        ActionResultStubAnswer actionResultStub2Answer = new ActionResultStubAnswer(true, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                chronometer.shiftTick(200);
                return null;
            }
        });

        control.reset();

        EasyMock.expect(actionAgent.executeAction(
            EasyMock.anyLong(),
            EasyMock.capture(scheduleAction1Capture),
            EasyMock.eq(clusterNode2.getAddress())
        )).andAnswer(actionResultStub1Answer);

        EasyMock.expect(actionAgent.executeAction(
            EasyMock.anyLong(),
            EasyMock.capture(scheduleAction2Capture),
            EasyMock.eq(clusterNode1.getAddress())
        )).andAnswer(actionResultStub2Answer);

        control.replay();

        AutomaticJobExecutionParameters jobExecutionParameters = new AutomaticJobExecutionParameters();
        jobExecutionParameters.setScheduled(ChronometerUtils.parseMoment("2012-02-20 10:00:00.000 UTC"));
        jobExecutionParameters.setFired(ChronometerUtils.parseMoment("2012-02-20 10:00:00.231 UTC"));

        ScheduleExecution scheduleExecution = scheduleJobExecutorService.executeAutomaticJob(scheduleJob.getId(),
            jobExecutionParameters);
        Assert.assertNotNull(scheduleExecution);

        control.verify();

        ScheduleExecutionNode scheduleExecutionNode;
        ScheduleExecutionResult scheduleExecutionResult;

        scheduleExecution = scheduleExecutionManagementService.findExecution(scheduleExecution.getId());
        Assert.assertEquals(ScheduleExecutionStatus.SUCCEED, scheduleExecution.getStatus());
        Assert.assertEquals(2, scheduleExecution.getNodes().size());
        Assert.assertEquals(2, scheduleExecution.getResults().size());

        scheduleExecutionNode = scheduleExecution.getNodes().get(0);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode2.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertFalse(scheduleExecutionResult.isSucceed());

        scheduleExecutionNode = scheduleExecution.getNodes().get(1);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode1.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertTrue(scheduleExecutionResult.isSucceed());
    }

    @Test
    public void testSucceedTimeoutAutomaticSingleExecution2() throws Exception {
        ScheduleJobCreateParameters jobCreateParameters = new ScheduleJobCreateParameters();
        jobCreateParameters.setName("Test Job");
        jobCreateParameters.setDescription("Nothing to do");
        jobCreateParameters.setTimezone("UTC");
        jobCreateParameters.setCron("0 0 0 * * ?");
        jobCreateParameters.setEnabled(true);
        jobCreateParameters.setTimeout(1000);
        jobCreateParameters.setAllNodes(false);
        jobCreateParameters.setClusterGroupId(clusterGroup.getId());
        jobCreateParameters.setScheduleGroupId(scheduleGroup.getId());
        jobCreateParameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);

        scheduleJob = scheduleJobManagementService.createJob(jobCreateParameters);
        quartzManagementService.disableJob(scheduleJob.getId());

        Capture<ScheduleAction> scheduleActionCapture = new ScheduleActionCapture();
        ActionResultStubAnswer actionResultStubAnswer = new ActionResultStubAnswer(true, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                chronometer.shiftTick(1500);
                return null;
            }
        });

        control.reset();

        EasyMock.expect(actionAgent.executeAction(
            EasyMock.anyLong(),
            EasyMock.capture(scheduleActionCapture),
            EasyMock.eq(clusterNode2.getAddress())
        )).andAnswer(actionResultStubAnswer);

        control.replay();

        AutomaticJobExecutionParameters jobExecutionParameters = new AutomaticJobExecutionParameters();
        jobExecutionParameters.setScheduled(ChronometerUtils.parseMoment("2012-02-20 10:00:00.000 UTC"));
        jobExecutionParameters.setFired(ChronometerUtils.parseMoment("2012-02-20 10:00:00.231 UTC"));

        ScheduleExecution scheduleExecution = scheduleJobExecutorService.executeAutomaticJob(scheduleJob.getId(),
            jobExecutionParameters);
        Assert.assertNotNull(scheduleExecution);

        control.verify();

        ScheduleExecutionNode scheduleExecutionNode;
        ScheduleExecutionResult scheduleExecutionResult;

        scheduleExecution = scheduleExecutionManagementService.findExecution(scheduleExecution.getId());
        Assert.assertEquals(ScheduleExecutionStatus.SUCCEED, scheduleExecution.getStatus());
        Assert.assertEquals(2, scheduleExecution.getNodes().size());
        Assert.assertEquals(1, scheduleExecution.getResults().size());

        scheduleExecutionNode = scheduleExecution.getNodes().get(0);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode2.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertTrue(scheduleExecutionResult.isSucceed());

        scheduleExecutionNode = scheduleExecution.getNodes().get(1);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode1.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNull(scheduleExecutionResult);
    }

    @Test
    public void testFailedTimeoutAutomaticSingleExecution() throws Exception {
        ScheduleJobCreateParameters jobCreateParameters = new ScheduleJobCreateParameters();
        jobCreateParameters.setName("Test Job");
        jobCreateParameters.setDescription("Nothing to do");
        jobCreateParameters.setTimezone("UTC");
        jobCreateParameters.setCron("0 0 0 * * ?");
        jobCreateParameters.setEnabled(true);
        jobCreateParameters.setTimeout(1000);
        jobCreateParameters.setAllNodes(false);
        jobCreateParameters.setClusterGroupId(clusterGroup.getId());
        jobCreateParameters.setScheduleGroupId(scheduleGroup.getId());
        jobCreateParameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);

        scheduleJob = scheduleJobManagementService.createJob(jobCreateParameters);
        quartzManagementService.disableJob(scheduleJob.getId());

        Capture<ScheduleAction> scheduleActionCapture = new ScheduleActionCapture();
        ActionResultStubAnswer actionResultStubAnswer = new ActionResultStubAnswer(false, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                chronometer.shiftTick(1100);
                return null;
            }
        });

        control.reset();

        EasyMock.expect(actionAgent.executeAction(
            EasyMock.anyLong(),
            EasyMock.capture(scheduleActionCapture),
            EasyMock.eq(clusterNode2.getAddress())
        )).andAnswer(actionResultStubAnswer);

        mailService.notifyJobExecutionFailed(
            EasyMock.<ScheduleExecution>anyObject()
        );

        control.replay();

        AutomaticJobExecutionParameters jobExecutionParameters = new AutomaticJobExecutionParameters();
        jobExecutionParameters.setScheduled(ChronometerUtils.parseMoment("2012-02-20 10:00:00.000 UTC"));
        jobExecutionParameters.setFired(ChronometerUtils.parseMoment("2012-02-20 10:00:00.231 UTC"));

        ScheduleExecution scheduleExecution = scheduleJobExecutorService.executeAutomaticJob(scheduleJob.getId(),
            jobExecutionParameters);
        Assert.assertNotNull(scheduleExecution);

        control.verify();

        ScheduleExecutionNode scheduleExecutionNode;
        ScheduleExecutionResult scheduleExecutionResult;

        scheduleExecution = scheduleExecutionManagementService.findExecution(scheduleExecution.getId());
        Assert.assertEquals(ScheduleExecutionStatus.TIMEOUT, scheduleExecution.getStatus());
        Assert.assertEquals(2, scheduleExecution.getNodes().size());
        Assert.assertEquals(1, scheduleExecution.getResults().size());

        scheduleExecutionNode = scheduleExecution.getNodes().get(0);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode2.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertFalse(scheduleExecutionResult.isSucceed());

        scheduleExecutionNode = scheduleExecution.getNodes().get(1);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode1.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNull(scheduleExecutionResult);
    }

    @Test
    public void testCancelledTimeoutAutomaticSingleExecution() throws Exception {
        ScheduleJobCreateParameters jobCreateParameters = new ScheduleJobCreateParameters();
        jobCreateParameters.setName("Test Job");
        jobCreateParameters.setDescription("Nothing to do");
        jobCreateParameters.setTimezone("UTC");
        jobCreateParameters.setCron("0 0 0 * * ?");
        jobCreateParameters.setEnabled(true);
        jobCreateParameters.setAllNodes(false);
        jobCreateParameters.setClusterGroupId(clusterGroup.getId());
        jobCreateParameters.setScheduleGroupId(scheduleGroup.getId());
        jobCreateParameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);

        scheduleJob = scheduleJobManagementService.createJob(jobCreateParameters);
        quartzManagementService.disableJob(scheduleJob.getId());

        final Capture<Long> scheduleExecutionCapture = new Capture<Long>();
        final Capture<ScheduleAction> scheduleActionCapture = new ScheduleActionCapture();
        final ActionResultStubAnswer actionResultStubAnswer = new ActionResultStubAnswer(false, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                scheduleExecutionManagementService.cancelExecution(scheduleExecutionCapture.getValue());
                return null;
            }
        });

        control.reset();

        EasyMock.expect(actionAgent.executeAction(
            EasyMock.capture(scheduleExecutionCapture),
            EasyMock.capture(scheduleActionCapture),
            EasyMock.eq(clusterNode2.getAddress())
        )).andAnswer(actionResultStubAnswer);

        mailService.notifyJobExecutionFailed(
            EasyMock.<ScheduleExecution>anyObject()
        );

        control.replay();

        AutomaticJobExecutionParameters jobExecutionParameters = new AutomaticJobExecutionParameters();
        jobExecutionParameters.setScheduled(ChronometerUtils.parseMoment("2012-02-20 10:00:00.000 UTC"));
        jobExecutionParameters.setFired(ChronometerUtils.parseMoment("2012-02-20 10:00:00.231 UTC"));

        ScheduleExecution scheduleExecution = scheduleJobExecutorService.executeAutomaticJob(scheduleJob.getId(),
            jobExecutionParameters);
        Assert.assertNotNull(scheduleExecution);

        control.verify();

        ScheduleExecutionNode scheduleExecutionNode;
        ScheduleExecutionResult scheduleExecutionResult;

        scheduleExecution = scheduleExecutionManagementService.findExecution(scheduleExecution.getId());
        Assert.assertEquals(ScheduleExecutionStatus.CANCELED, scheduleExecution.getStatus());
        Assert.assertEquals(2, scheduleExecution.getNodes().size());
        Assert.assertEquals(1, scheduleExecution.getResults().size());

        scheduleExecutionNode = scheduleExecution.getNodes().get(0);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode2.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertFalse(scheduleExecutionResult.isSucceed());

        scheduleExecutionNode = scheduleExecution.getNodes().get(1);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode1.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNull(scheduleExecutionResult);
    }

    @Test
    public void testOneFailedAutomaticSingleExecution() throws Exception {
        ScheduleJobCreateParameters jobCreateParameters = new ScheduleJobCreateParameters();
        jobCreateParameters.setName("Test Job");
        jobCreateParameters.setDescription("Nothing to do");
        jobCreateParameters.setTimezone("UTC");
        jobCreateParameters.setCron("0 0 0 * * ?");
        jobCreateParameters.setEnabled(true);
        jobCreateParameters.setAllNodes(false);
        jobCreateParameters.setClusterGroupId(clusterGroup.getId());
        jobCreateParameters.setScheduleGroupId(scheduleGroup.getId());
        jobCreateParameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);

        scheduleJob = scheduleJobManagementService.createJob(jobCreateParameters);
        quartzManagementService.disableJob(scheduleJob.getId());

        Capture<ScheduleAction> scheduleAction1Capture = new ScheduleActionCapture();
        Capture<ScheduleAction> scheduleAction2Capture = new ScheduleActionCapture();

        control.reset();

        EasyMock.expect(actionAgent.executeAction(
            EasyMock.anyLong(),
            EasyMock.capture(scheduleAction1Capture),
            EasyMock.eq(clusterNode2.getAddress())
        )).andReturn(new ActionResultStub(false));

        EasyMock.expect(actionAgent.executeAction(
            EasyMock.anyLong(),
            EasyMock.capture(scheduleAction2Capture),
            EasyMock.eq(clusterNode1.getAddress())
        )).andReturn(new ActionResultStub(true));

        control.replay();

        AutomaticJobExecutionParameters jobExecutionParameters = new AutomaticJobExecutionParameters();
        jobExecutionParameters.setScheduled(ChronometerUtils.parseMoment("2012-02-20 10:00:00.000 UTC"));
        jobExecutionParameters.setFired(ChronometerUtils.parseMoment("2012-02-20 10:00:00.231 UTC"));

        ScheduleExecution scheduleExecution = scheduleJobExecutorService.executeAutomaticJob(scheduleJob.getId(),
            jobExecutionParameters);
        Assert.assertNotNull(scheduleExecution);

        control.verify();

        ScheduleExecutionNode scheduleExecutionNode;
        ScheduleExecutionResult scheduleExecutionResult;

        scheduleExecution = scheduleExecutionManagementService.findExecution(scheduleExecution.getId());
        Assert.assertEquals(ScheduleExecutionStatus.SUCCEED, scheduleExecution.getStatus());
        Assert.assertEquals(2, scheduleExecution.getNodes().size());
        Assert.assertEquals(2, scheduleExecution.getResults().size());

        scheduleExecutionNode = scheduleExecution.getNodes().get(0);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode2.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertFalse(scheduleExecutionResult.isSucceed());

        scheduleExecutionNode = scheduleExecution.getNodes().get(1);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode1.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertTrue(scheduleExecutionResult.isSucceed());
    }

    @Test
    public void testAllFailedAutomaticSingleExecution() throws Exception {
        ScheduleJobCreateParameters jobCreateParameters = new ScheduleJobCreateParameters();
        jobCreateParameters.setName("Test Job");
        jobCreateParameters.setDescription("Nothing to do");
        jobCreateParameters.setTimezone("UTC");
        jobCreateParameters.setCron("0 0 0 * * ?");
        jobCreateParameters.setEnabled(true);
        jobCreateParameters.setAllNodes(false);
        jobCreateParameters.setClusterGroupId(clusterGroup.getId());
        jobCreateParameters.setScheduleGroupId(scheduleGroup.getId());
        jobCreateParameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);

        scheduleJob = scheduleJobManagementService.createJob(jobCreateParameters);
        quartzManagementService.disableJob(scheduleJob.getId());

        Capture<ScheduleAction> scheduleAction1Capture = new ScheduleActionCapture();
        Capture<ScheduleAction> scheduleAction2Capture = new ScheduleActionCapture();

        control.reset();

        EasyMock.expect(actionAgent.executeAction(
            EasyMock.anyLong(),
            EasyMock.capture(scheduleAction1Capture),
            EasyMock.eq(clusterNode2.getAddress())
        )).andReturn(new ActionResultStub(false));

        EasyMock.expect(actionAgent.executeAction(
            EasyMock.anyLong(),
            EasyMock.capture(scheduleAction2Capture),
            EasyMock.eq(clusterNode1.getAddress())
        )).andReturn(new ActionResultStub(false));

        mailService.notifyJobExecutionFailed(
            EasyMock.<ScheduleExecution>anyObject()
        );

        control.replay();

        AutomaticJobExecutionParameters jobExecutionParameters = new AutomaticJobExecutionParameters();
        jobExecutionParameters.setScheduled(ChronometerUtils.parseMoment("2012-02-20 10:00:00.000 UTC"));
        jobExecutionParameters.setFired(ChronometerUtils.parseMoment("2012-02-20 10:00:00.231 UTC"));

        ScheduleExecution scheduleExecution = scheduleJobExecutorService.executeAutomaticJob(scheduleJob.getId(),
            jobExecutionParameters);
        Assert.assertNotNull(scheduleExecution);

        control.verify();

        ScheduleExecutionNode scheduleExecutionNode;
        ScheduleExecutionResult scheduleExecutionResult;

        scheduleExecution = scheduleExecutionManagementService.findExecution(scheduleExecution.getId());
        Assert.assertEquals(ScheduleExecutionStatus.FAILED, scheduleExecution.getStatus());
        Assert.assertEquals(2, scheduleExecution.getNodes().size());
        Assert.assertEquals(2, scheduleExecution.getResults().size());

        scheduleExecutionNode = scheduleExecution.getNodes().get(0);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode2.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertFalse(scheduleExecutionResult.isSucceed());

        scheduleExecutionNode = scheduleExecution.getNodes().get(1);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode1.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertFalse(scheduleExecutionResult.isSucceed());
    }

    @Test
    public void testExceptionAutomaticSingleExecution() throws Exception {
        ScheduleJobCreateParameters jobCreateParameters = new ScheduleJobCreateParameters();
        jobCreateParameters.setName("Test Job");
        jobCreateParameters.setDescription("Nothing to do");
        jobCreateParameters.setTimezone("UTC");
        jobCreateParameters.setCron("0 0 0 * * ?");
        jobCreateParameters.setEnabled(true);
        jobCreateParameters.setAllNodes(false);
        jobCreateParameters.setClusterGroupId(clusterGroup.getId());
        jobCreateParameters.setScheduleGroupId(scheduleGroup.getId());
        jobCreateParameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);

        scheduleJob = scheduleJobManagementService.createJob(jobCreateParameters);
        quartzManagementService.disableJob(scheduleJob.getId());

        Capture<ScheduleAction> scheduleActionCapture = new ScheduleActionCapture();

        control.reset();

        EasyMock.expect(actionAgent.executeAction(
            EasyMock.anyLong(),
            EasyMock.capture(scheduleActionCapture),
            EasyMock.eq(clusterNode2.getAddress())
        )).andThrow(new IllegalStateException("Test exception"));

        mailService.notifyJobExecutionFailed(
            EasyMock.<ScheduleExecution>anyObject()
        );

        control.replay();

        AutomaticJobExecutionParameters jobExecutionParameters = new AutomaticJobExecutionParameters();
        jobExecutionParameters.setScheduled(ChronometerUtils.parseMoment("2012-02-20 10:00:00.000 UTC"));
        jobExecutionParameters.setFired(ChronometerUtils.parseMoment("2012-02-20 10:00:00.231 UTC"));

        ScheduleExecution scheduleExecution = scheduleJobExecutorService.executeAutomaticJob(scheduleJob.getId(),
            jobExecutionParameters);
        Assert.assertNotNull(scheduleExecution);

        control.verify();

        ScheduleExecutionNode scheduleExecutionNode;
        ScheduleExecutionResult scheduleExecutionResult;

        scheduleExecution = scheduleExecutionManagementService.findExecution(scheduleExecution.getId());
        Assert.assertEquals(ScheduleExecutionStatus.EXCEPTION, scheduleExecution.getStatus());
        Assert.assertEquals(2, scheduleExecution.getNodes().size());
        Assert.assertEquals(1, scheduleExecution.getResults().size());

        scheduleExecutionNode = scheduleExecution.getNodes().get(0);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode2.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertFalse(scheduleExecutionResult.isSucceed());

        scheduleExecutionNode = scheduleExecution.getNodes().get(1);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode1.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNull(scheduleExecutionResult);
    }

    @Test
    public void testLimitedAutomaticSingleExecution() throws Exception {
        ScheduleJobCreateParameters jobCreateParameters = new ScheduleJobCreateParameters();
        jobCreateParameters.setName("Test Job");
        jobCreateParameters.setDescription("Nothing to do");
        jobCreateParameters.setTimezone("UTC");
        jobCreateParameters.setCron("0 0 0 * * ?");
        jobCreateParameters.setEnabled(true);
        jobCreateParameters.setAllNodes(false);
        jobCreateParameters.setTrials(1);
        jobCreateParameters.setClusterGroupId(clusterGroup.getId());
        jobCreateParameters.setScheduleGroupId(scheduleGroup.getId());
        jobCreateParameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);

        scheduleJob = scheduleJobManagementService.createJob(jobCreateParameters);
        quartzManagementService.disableJob(scheduleJob.getId());

        Capture<ScheduleAction> scheduleActionCapture = new ScheduleActionCapture();

        control.reset();

        EasyMock.expect(actionAgent.executeAction(
            EasyMock.anyLong(),
            EasyMock.capture(scheduleActionCapture),
            EasyMock.eq(clusterNode2.getAddress())
        )).andReturn(new ActionResultStub(false));

        mailService.notifyJobExecutionFailed(
            EasyMock.<ScheduleExecution>anyObject()
        );

        control.replay();

        AutomaticJobExecutionParameters jobExecutionParameters = new AutomaticJobExecutionParameters();
        jobExecutionParameters.setScheduled(ChronometerUtils.parseMoment("2012-02-20 10:00:00.000 UTC"));
        jobExecutionParameters.setFired(ChronometerUtils.parseMoment("2012-02-20 10:00:00.231 UTC"));

        ScheduleExecution scheduleExecution = scheduleJobExecutorService.executeAutomaticJob(scheduleJob.getId(),
            jobExecutionParameters);
        Assert.assertNotNull(scheduleExecution);

        control.verify();

        ScheduleExecutionNode scheduleExecutionNode;
        ScheduleExecutionResult scheduleExecutionResult;

        scheduleExecution = scheduleExecutionManagementService.findExecution(scheduleExecution.getId());
        Assert.assertEquals(ScheduleExecutionStatus.FAILED, scheduleExecution.getStatus());
        Assert.assertEquals(1, scheduleExecution.getNodes().size());
        Assert.assertEquals(1, scheduleExecution.getResults().size());

        scheduleExecutionNode =scheduleExecution.getNodes().get(0);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode2.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertFalse(scheduleExecutionResult.isSucceed());
    }

    @Test
    public void testNormalAutomaticAllExecution() throws Exception {
        ScheduleJobCreateParameters jobCreateParameters = new ScheduleJobCreateParameters();
        jobCreateParameters.setName("Test Job");
        jobCreateParameters.setDescription("Nothing to do");
        jobCreateParameters.setTimezone("UTC");
        jobCreateParameters.setCron("0 0 0 * * ?");
        jobCreateParameters.setEnabled(true);
        jobCreateParameters.setAllNodes(true);
        jobCreateParameters.setClusterGroupId(clusterGroup.getId());
        jobCreateParameters.setScheduleGroupId(scheduleGroup.getId());
        jobCreateParameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);

        scheduleJob = scheduleJobManagementService.createJob(jobCreateParameters);
        quartzManagementService.disableJob(scheduleJob.getId());

        Capture<ScheduleAction> scheduleAction1Capture = new ScheduleActionCapture();
        Capture<ScheduleAction> scheduleAction2Capture = new ScheduleActionCapture();

        control.reset();

        EasyMock.expect(actionAgent.executeAction(
            EasyMock.anyLong(),
            EasyMock.capture(scheduleAction1Capture),
            EasyMock.eq(clusterNode2.getAddress())
        )).andReturn(new ActionResultStub(true));

        EasyMock.expect(actionAgent.executeAction(
            EasyMock.anyLong(),
            EasyMock.capture(scheduleAction2Capture),
            EasyMock.eq(clusterNode1.getAddress())
        )).andReturn(new ActionResultStub(true));

        control.replay();

        AutomaticJobExecutionParameters jobExecutionParameters = new AutomaticJobExecutionParameters();
        jobExecutionParameters.setScheduled(ChronometerUtils.parseMoment("2012-02-20 10:00:00.000 UTC"));
        jobExecutionParameters.setFired(ChronometerUtils.parseMoment("2012-02-20 10:00:00.231 UTC"));

        ScheduleExecution scheduleExecution = scheduleJobExecutorService.executeAutomaticJob(scheduleJob.getId(),
            jobExecutionParameters);
        Assert.assertNotNull(scheduleExecution);

        control.verify();

        ScheduleExecutionNode scheduleExecutionNode;
        ScheduleExecutionResult scheduleExecutionResult;

        scheduleExecution = scheduleExecutionManagementService.findExecution(scheduleExecution.getId());
        Assert.assertEquals(ScheduleExecutionStatus.SUCCEED, scheduleExecution.getStatus());
        Assert.assertEquals(2, scheduleExecution.getNodes().size());
        Assert.assertEquals(2, scheduleExecution.getResults().size());

        scheduleExecutionNode =scheduleExecution.getNodes().get(0);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode2.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertTrue(scheduleExecutionResult.isSucceed());

        scheduleExecutionNode =scheduleExecution.getNodes().get(1);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode1.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertTrue(scheduleExecutionResult.isSucceed());
    }

    @Test
    public void testLimitedAutomaticAllExecution() throws Exception {
        ScheduleJobCreateParameters jobCreateParameters = new ScheduleJobCreateParameters();
        jobCreateParameters.setName("Test Job");
        jobCreateParameters.setDescription("Nothing to do");
        jobCreateParameters.setTimezone("UTC");
        jobCreateParameters.setCron("0 0 0 * * ?");
        jobCreateParameters.setEnabled(true);
        jobCreateParameters.setAllNodes(true);
        jobCreateParameters.setTrials(1);
        jobCreateParameters.setClusterGroupId(clusterGroup.getId());
        jobCreateParameters.setScheduleGroupId(scheduleGroup.getId());
        jobCreateParameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);

        scheduleJob = scheduleJobManagementService.createJob(jobCreateParameters);
        quartzManagementService.disableJob(scheduleJob.getId());

        Capture<ScheduleAction> scheduleActionCapture = new ScheduleActionCapture();

        control.reset();

        EasyMock.expect(actionAgent.executeAction(
            EasyMock.anyLong(),
            EasyMock.capture(scheduleActionCapture),
            EasyMock.eq(clusterNode2.getAddress())
        )).andReturn(new ActionResultStub(true));

        control.replay();

        AutomaticJobExecutionParameters jobExecutionParameters = new AutomaticJobExecutionParameters();
        jobExecutionParameters.setScheduled(ChronometerUtils.parseMoment("2012-02-20 10:00:00.000 UTC"));
        jobExecutionParameters.setFired(ChronometerUtils.parseMoment("2012-02-20 10:00:00.231 UTC"));

        ScheduleExecution scheduleExecution = scheduleJobExecutorService.executeAutomaticJob(scheduleJob.getId(),
            jobExecutionParameters);
        Assert.assertNotNull(scheduleExecution);

        control.verify();

        ScheduleExecutionNode scheduleExecutionNode;
        ScheduleExecutionResult scheduleExecutionResult;

        scheduleExecution = scheduleExecutionManagementService.findExecution(scheduleExecution.getId());
        Assert.assertEquals(ScheduleExecutionStatus.SUCCEED, scheduleExecution.getStatus());
        Assert.assertEquals(1, scheduleExecution.getNodes().size());
        Assert.assertEquals(1, scheduleExecution.getResults().size());

        scheduleExecutionNode =scheduleExecution.getNodes().get(0);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode2.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertTrue(scheduleExecutionResult.isSucceed());
    }

    @Test
    public void testOneFailedAutomaticAllExecution() throws Exception {
        ScheduleJobCreateParameters jobCreateParameters = new ScheduleJobCreateParameters();
        jobCreateParameters.setName("Test Job");
        jobCreateParameters.setDescription("Nothing to do");
        jobCreateParameters.setTimezone("UTC");
        jobCreateParameters.setCron("0 0 0 * * ?");
        jobCreateParameters.setEnabled(true);
        jobCreateParameters.setAllNodes(true);
        jobCreateParameters.setClusterGroupId(clusterGroup.getId());
        jobCreateParameters.setScheduleGroupId(scheduleGroup.getId());
        jobCreateParameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);

        scheduleJob = scheduleJobManagementService.createJob(jobCreateParameters);
        quartzManagementService.disableJob(scheduleJob.getId());

        Capture<ScheduleAction> scheduleAction1Capture = new ScheduleActionCapture();
        Capture<ScheduleAction> scheduleAction2Capture = new ScheduleActionCapture();

        control.reset();

        EasyMock.expect(actionAgent.executeAction(
            EasyMock.anyLong(),
            EasyMock.capture(scheduleAction1Capture),
            EasyMock.eq(clusterNode2.getAddress())
        )).andReturn(new ActionResultStub(false));

        EasyMock.expect(actionAgent.executeAction(
            EasyMock.anyLong(),
            EasyMock.capture(scheduleAction2Capture),
            EasyMock.eq(clusterNode1.getAddress())
        )).andReturn(new ActionResultStub(true));

        mailService.notifyJobExecutionFailed(
            EasyMock.<ScheduleExecution>anyObject()
        );

        control.replay();

        AutomaticJobExecutionParameters jobExecutionParameters = new AutomaticJobExecutionParameters();
        jobExecutionParameters.setScheduled(ChronometerUtils.parseMoment("2012-02-20 10:00:00.000 UTC"));
        jobExecutionParameters.setFired(ChronometerUtils.parseMoment("2012-02-20 10:00:00.231 UTC"));

        ScheduleExecution scheduleExecution = scheduleJobExecutorService.executeAutomaticJob(scheduleJob.getId(),
            jobExecutionParameters);
        Assert.assertNotNull(scheduleExecution);

        control.verify();

        ScheduleExecutionNode scheduleExecutionNode;
        ScheduleExecutionResult scheduleExecutionResult;

        scheduleExecution = scheduleExecutionManagementService.findExecution(scheduleExecution.getId());
        Assert.assertEquals(ScheduleExecutionStatus.FAILED, scheduleExecution.getStatus());
        Assert.assertEquals(2, scheduleExecution.getNodes().size());
        Assert.assertEquals(2, scheduleExecution.getResults().size());

        scheduleExecutionNode =scheduleExecution.getNodes().get(0);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode2.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertFalse(scheduleExecutionResult.isSucceed());

        scheduleExecutionNode =scheduleExecution.getNodes().get(1);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode1.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertTrue(scheduleExecutionResult.isSucceed());
    }

    @Test
    public void testAllFailedAutomaticAllExecution() throws Exception {
        ScheduleJobCreateParameters jobCreateParameters = new ScheduleJobCreateParameters();
        jobCreateParameters.setName("Test Job");
        jobCreateParameters.setDescription("Nothing to do");
        jobCreateParameters.setTimezone("UTC");
        jobCreateParameters.setCron("0 0 0 * * ?");
        jobCreateParameters.setEnabled(true);
        jobCreateParameters.setAllNodes(true);
        jobCreateParameters.setClusterGroupId(clusterGroup.getId());
        jobCreateParameters.setScheduleGroupId(scheduleGroup.getId());
        jobCreateParameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);

        scheduleJob = scheduleJobManagementService.createJob(jobCreateParameters);
        quartzManagementService.disableJob(scheduleJob.getId());

        Capture<ScheduleAction> scheduleAction1Capture = new ScheduleActionCapture();
        Capture<ScheduleAction> scheduleAction2Capture = new ScheduleActionCapture();

        control.reset();

        EasyMock.expect(actionAgent.executeAction(
            EasyMock.anyLong(),
            EasyMock.capture(scheduleAction1Capture),
            EasyMock.eq(clusterNode2.getAddress())
        )).andReturn(new ActionResultStub(false));

        EasyMock.expect(actionAgent.executeAction(
            EasyMock.anyLong(),
            EasyMock.capture(scheduleAction2Capture),
            EasyMock.eq(clusterNode1.getAddress())
        )).andReturn(new ActionResultStub(false));

        mailService.notifyJobExecutionFailed(
            EasyMock.<ScheduleExecution>anyObject()
        );

        control.replay();

        AutomaticJobExecutionParameters jobExecutionParameters = new AutomaticJobExecutionParameters();
        jobExecutionParameters.setScheduled(ChronometerUtils.parseMoment("2012-02-20 10:00:00.000 UTC"));
        jobExecutionParameters.setFired(ChronometerUtils.parseMoment("2012-02-20 10:00:00.231 UTC"));

        ScheduleExecution scheduleExecution = scheduleJobExecutorService.executeAutomaticJob(scheduleJob.getId(),
            jobExecutionParameters);
        Assert.assertNotNull(scheduleExecution);

        control.verify();

        ScheduleExecutionNode scheduleExecutionNode;
        ScheduleExecutionResult scheduleExecutionResult;

        scheduleExecution = scheduleExecutionManagementService.findExecution(scheduleExecution.getId());
        Assert.assertEquals(ScheduleExecutionStatus.FAILED, scheduleExecution.getStatus());
        Assert.assertEquals(2, scheduleExecution.getNodes().size());
        Assert.assertEquals(2, scheduleExecution.getResults().size());

        scheduleExecutionNode =scheduleExecution.getNodes().get(0);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode2.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertFalse(scheduleExecutionResult.isSucceed());

        scheduleExecutionNode =scheduleExecution.getNodes().get(1);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode1.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertFalse(scheduleExecutionResult.isSucceed());
    }

    @Test
    public void testExceptionAutomaticAllExecution() throws Exception {
        ScheduleJobCreateParameters jobCreateParameters = new ScheduleJobCreateParameters();
        jobCreateParameters.setName("Test Job");
        jobCreateParameters.setDescription("Nothing to do");
        jobCreateParameters.setTimezone("UTC");
        jobCreateParameters.setCron("0 0 0 * * ?");
        jobCreateParameters.setEnabled(true);
        jobCreateParameters.setAllNodes(true);
        jobCreateParameters.setClusterGroupId(clusterGroup.getId());
        jobCreateParameters.setScheduleGroupId(scheduleGroup.getId());
        jobCreateParameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);

        scheduleJob = scheduleJobManagementService.createJob(jobCreateParameters);
        quartzManagementService.disableJob(scheduleJob.getId());

        Capture<ScheduleAction> scheduleActionCapture = new ScheduleActionCapture();

        control.reset();

        EasyMock.expect(actionAgent.executeAction(
            EasyMock.anyLong(),
            EasyMock.capture(scheduleActionCapture),
            EasyMock.eq(clusterNode2.getAddress())
        )).andThrow(new IllegalStateException("Test exception"));

        mailService.notifyJobExecutionFailed(
            EasyMock.<ScheduleExecution>anyObject()
        );

        control.replay();

        AutomaticJobExecutionParameters jobExecutionParameters = new AutomaticJobExecutionParameters();
        jobExecutionParameters.setScheduled(ChronometerUtils.parseMoment("2012-02-20 10:00:00.000 UTC"));
        jobExecutionParameters.setFired(ChronometerUtils.parseMoment("2012-02-20 10:00:00.231 UTC"));

        ScheduleExecution scheduleExecution = scheduleJobExecutorService.executeAutomaticJob(scheduleJob.getId(),
            jobExecutionParameters);
        Assert.assertNotNull(scheduleExecution);

        control.verify();

        ScheduleExecutionNode scheduleExecutionNode;
        ScheduleExecutionResult scheduleExecutionResult;

        scheduleExecution = scheduleExecutionManagementService.findExecution(scheduleExecution.getId());
        Assert.assertEquals(ScheduleExecutionStatus.EXCEPTION, scheduleExecution.getStatus());
        Assert.assertEquals(2, scheduleExecution.getNodes().size());
        Assert.assertEquals(1, scheduleExecution.getResults().size());

        scheduleExecutionNode =scheduleExecution.getNodes().get(0);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode2.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertFalse(scheduleExecutionResult.isSucceed());

        scheduleExecutionNode =scheduleExecution.getNodes().get(1);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode1.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNull(scheduleExecutionResult);
    }

    @Test
    public void testFailedTimeoutAutomaticAllExecution() throws Exception {
        ScheduleJobCreateParameters jobCreateParameters = new ScheduleJobCreateParameters();
        jobCreateParameters.setName("Test Job");
        jobCreateParameters.setDescription("Nothing to do");
        jobCreateParameters.setTimezone("UTC");
        jobCreateParameters.setCron("0 0 0 * * ?");
        jobCreateParameters.setEnabled(true);
        jobCreateParameters.setTimeout(1000);
        jobCreateParameters.setAllNodes(true);
        jobCreateParameters.setClusterGroupId(clusterGroup.getId());
        jobCreateParameters.setScheduleGroupId(scheduleGroup.getId());
        jobCreateParameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);

        scheduleJob = scheduleJobManagementService.createJob(jobCreateParameters);
        quartzManagementService.disableJob(scheduleJob.getId());

        Capture<ScheduleAction> scheduleActionCapture = new ScheduleActionCapture();
        ActionResultStubAnswer actionResultStubAnswer = new ActionResultStubAnswer(false, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                chronometer.shiftTick(1100);
                return null;
            }
        });

        control.reset();

        EasyMock.expect(actionAgent.executeAction(
            EasyMock.anyLong(),
            EasyMock.capture(scheduleActionCapture),
            EasyMock.eq(clusterNode2.getAddress())
        )).andAnswer(actionResultStubAnswer);

        mailService.notifyJobExecutionFailed(
            EasyMock.<ScheduleExecution>anyObject()
        );

        control.replay();

        AutomaticJobExecutionParameters jobExecutionParameters = new AutomaticJobExecutionParameters();
        jobExecutionParameters.setScheduled(ChronometerUtils.parseMoment("2012-02-20 10:00:00.000 UTC"));
        jobExecutionParameters.setFired(ChronometerUtils.parseMoment("2012-02-20 10:00:00.231 UTC"));

        ScheduleExecution scheduleExecution = scheduleJobExecutorService.executeAutomaticJob(scheduleJob.getId(),
            jobExecutionParameters);
        Assert.assertNotNull(scheduleExecution);

        control.verify();

        ScheduleExecutionNode scheduleExecutionNode;
        ScheduleExecutionResult scheduleExecutionResult;

        scheduleExecution = scheduleExecutionManagementService.findExecution(scheduleExecution.getId());
        Assert.assertEquals(ScheduleExecutionStatus.TIMEOUT, scheduleExecution.getStatus());
        Assert.assertEquals(2, scheduleExecution.getNodes().size());
        Assert.assertEquals(1, scheduleExecution.getResults().size());

        scheduleExecutionNode = scheduleExecution.getNodes().get(0);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode2.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertFalse(scheduleExecutionResult.isSucceed());

        scheduleExecutionNode = scheduleExecution.getNodes().get(1);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode1.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNull(scheduleExecutionResult);
    }

    @Test
    public void testCancelledTimeoutAutomaticAllExecution() throws Exception {
        ScheduleJobCreateParameters jobCreateParameters = new ScheduleJobCreateParameters();
        jobCreateParameters.setName("Test Job");
        jobCreateParameters.setDescription("Nothing to do");
        jobCreateParameters.setTimezone("UTC");
        jobCreateParameters.setCron("0 0 0 * * ?");
        jobCreateParameters.setEnabled(true);
        jobCreateParameters.setTimeout(1000);
        jobCreateParameters.setAllNodes(true);
        jobCreateParameters.setClusterGroupId(clusterGroup.getId());
        jobCreateParameters.setScheduleGroupId(scheduleGroup.getId());
        jobCreateParameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);

        scheduleJob = scheduleJobManagementService.createJob(jobCreateParameters);
        quartzManagementService.disableJob(scheduleJob.getId());

        final Capture<Long> scheduleExecutionCapture = new Capture<Long>();
        final Capture<ScheduleAction> scheduleActionCapture = new ScheduleActionCapture();
        final ActionResultStubAnswer actionResultStubAnswer = new ActionResultStubAnswer(false, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                scheduleExecutionManagementService.cancelExecution(scheduleExecutionCapture.getValue());
                return null;
            }
        });

        control.reset();

        EasyMock.expect(actionAgent.executeAction(
            EasyMock.capture(scheduleExecutionCapture),
            EasyMock.capture(scheduleActionCapture),
            EasyMock.eq(clusterNode2.getAddress())
        )).andAnswer(actionResultStubAnswer);

        mailService.notifyJobExecutionFailed(
            EasyMock.<ScheduleExecution>anyObject()
        );

        control.replay();

        AutomaticJobExecutionParameters jobExecutionParameters = new AutomaticJobExecutionParameters();
        jobExecutionParameters.setScheduled(ChronometerUtils.parseMoment("2012-02-20 10:00:00.000 UTC"));
        jobExecutionParameters.setFired(ChronometerUtils.parseMoment("2012-02-20 10:00:00.231 UTC"));

        ScheduleExecution scheduleExecution = scheduleJobExecutorService.executeAutomaticJob(scheduleJob.getId(),
            jobExecutionParameters);
        Assert.assertNotNull(scheduleExecution);

        control.verify();

        ScheduleExecutionNode scheduleExecutionNode;
        ScheduleExecutionResult scheduleExecutionResult;

        scheduleExecution = scheduleExecutionManagementService.findExecution(scheduleExecution.getId());
        Assert.assertEquals(ScheduleExecutionStatus.CANCELED, scheduleExecution.getStatus());
        Assert.assertEquals(2, scheduleExecution.getNodes().size());
        Assert.assertEquals(1, scheduleExecution.getResults().size());

        scheduleExecutionNode = scheduleExecution.getNodes().get(0);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode2.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNotNull(scheduleExecutionResult);
        Assert.assertFalse(scheduleExecutionResult.isSucceed());

        scheduleExecutionNode = scheduleExecution.getNodes().get(1);
        Assert.assertNotNull(scheduleExecutionNode);
        Assert.assertEquals(clusterNode1.getAddress(), scheduleExecutionNode.getAddress());
        scheduleExecutionResult = scheduleExecutionNode.getResult();
        Assert.assertNull(scheduleExecutionResult);
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

    private static class ActionResultStubAnswer implements IAnswer<ActionResult> {

        private boolean succeed;

        private Callable callable;

        private ActionResultStubAnswer(boolean succeed, Callable callable) {
            this.callable = callable;
            this.succeed = succeed;
        }

        @Override
        public ActionResult answer() throws Throwable {
            callable.call();
            return new ActionResultStub(succeed);
        }
    }

    private static class ScheduleActionCapture extends Capture<ScheduleAction> {

        private ScheduleActionCapture() {
            super(CaptureType.FIRST);
        }

        @Override
        public String toString() {
            // return nothing due to org.hibernate.LazyInitializationException on detached object's toString()
            return "<object>";
        }
    }

}
