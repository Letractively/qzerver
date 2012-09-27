package org.qzerver.model.service.job.management.impl;

import com.gainmatrix.lib.business.entity.BusinessEntityDao;
import com.gainmatrix.lib.time.impl.StubChronometer;
import junit.framework.Assert;
import org.easymock.Capture;
import org.easymock.CaptureType;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;
import org.qzerver.base.AbstractModelTest;
import org.qzerver.model.dao.job.ScheduleExecutionDao;
import org.qzerver.model.dao.job.ScheduleJobDao;
import org.qzerver.model.domain.entities.cluster.ClusterGroup;
import org.qzerver.model.domain.entities.cluster.ClusterNode;
import org.qzerver.model.domain.entities.job.*;
import org.qzerver.model.service.job.management.dto.ScheduleJobCreateParameters;
import org.qzerver.model.service.job.management.dto.ScheduleJobModifyParameters;
import org.qzerver.model.service.job.management.dto.ScheduleJobRescheduleParameters;
import org.qzerver.model.service.quartz.management.QuartzManagementService;
import org.springframework.validation.Validator;

import javax.annotation.Resource;

public class ScheduleJobManagementServiceImplTest extends AbstractModelTest {

    private static final String DEFAULT_TIMEZONE = "UTC";

    private IMocksControl control;

    private ScheduleJobManagementServiceImpl scheduleJobManagementService;

    private QuartzManagementService quartzManagementService;

    @Resource
    private Validator modelBeanValidator;

    @Resource
    private BusinessEntityDao businessEntityDao;

    @Resource
    private StubChronometer chronometer;

    @Resource
    private ScheduleExecutionDao scheduleExecutionDao;

    @Resource
    private ScheduleJobDao scheduleJobDao;

    @Before
    public void setUp() throws Exception {
        control = EasyMock.createStrictControl();

        quartzManagementService = control.createMock(QuartzManagementService.class);

        scheduleJobManagementService  = new ScheduleJobManagementServiceImpl();
        scheduleJobManagementService.setQuartzManagementService(quartzManagementService);
        scheduleJobManagementService.setBeanValidator(modelBeanValidator);
        scheduleJobManagementService.setBusinessEntityDao(businessEntityDao);
        scheduleJobManagementService.setChronometer(chronometer);
        scheduleJobManagementService.setScheduleExecutionDao(scheduleExecutionDao);
        scheduleJobManagementService.setScheduleJobDao(scheduleJobDao);
    }

    @Test
    public void testCreateJobWithoutCluster() throws Exception {
        String cron = "0 0 0 * * ?";

        ScheduleGroup scheduleGroup = new ScheduleGroup();
        scheduleGroup.setName("test group");
        businessEntityDao.save(scheduleGroup);

        control.reset();

        quartzManagementService.createJob(
            EasyMock.anyLong(),
            EasyMock.eq(cron),
            EasyMock.eq(DEFAULT_TIMEZONE),
            EasyMock.eq(true)
        );

        control.replay();

        ScheduleJobCreateParameters parameters = new ScheduleJobCreateParameters();
        parameters.setName("Test Job");
        parameters.setDescription("Nothing to do");
        parameters.setTimezone(DEFAULT_TIMEZONE);
        parameters.setCron(cron);
        parameters.setEnabled(true);
        parameters.setActionType(ScheduleActionType.LOCAL_COMMAND);
        parameters.setClusterGroupId(null);
        parameters.setSchedulerGroupId(scheduleGroup.getId());
        parameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);

        ScheduleJob scheduleJob = scheduleJobManagementService.createJob(parameters);
        Assert.assertNotNull(scheduleJob);

        control.verify();

        ScheduleAction action = scheduleJob.getAction();
        Assert.assertNotNull(action);
    }

    @Test
    public void testCreateJobWithCluster() throws Exception {
        String cron = "0 0 0 * * ?";

        ScheduleGroup scheduleGroup = new ScheduleGroup();
        scheduleGroup.setName("test group");
        businessEntityDao.save(scheduleGroup);

        ClusterGroup clusterGroup = new ClusterGroup();
        clusterGroup.setName("Test cluster group");
        businessEntityDao.save(clusterGroup);

        ClusterNode clusterNode1 = new ClusterNode();
        clusterNode1.setAddress("10.2.0.1");
        clusterNode1.setDescription("test node");
        clusterNode1.setEnabled(true);
        clusterNode1.setGroup(clusterGroup);
        clusterGroup.getNodes().add(clusterNode1);

        ClusterNode clusterNode2 = new ClusterNode();
        clusterNode2.setAddress("10.2.0.2");
        clusterNode2.setDescription("test node");
        clusterNode2.setEnabled(true);
        clusterNode2.setGroup(clusterGroup);
        clusterGroup.getNodes().add(clusterNode2);

        control.reset();

        quartzManagementService.createJob(
            EasyMock.anyLong(),
            EasyMock.eq(cron),
            EasyMock.eq(DEFAULT_TIMEZONE),
            EasyMock.eq(true)
        );

        control.replay();

        ScheduleJobCreateParameters parameters = new ScheduleJobCreateParameters();
        parameters.setName("Test Job");
        parameters.setDescription("Nothing to do");
        parameters.setTimezone(DEFAULT_TIMEZONE);
        parameters.setCron(cron);
        parameters.setEnabled(true);
        parameters.setActionType(ScheduleActionType.LOCAL_COMMAND);
        parameters.setClusterGroupId(clusterGroup.getId());
        parameters.setSchedulerGroupId(scheduleGroup.getId());
        parameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);

        ScheduleJob scheduleJob = scheduleJobManagementService.createJob(parameters);
        Assert.assertNotNull(scheduleJob);

        control.verify();

        ScheduleAction action = scheduleJob.getAction();
        Assert.assertNotNull(action);
    }

    @Test
    public void testDeleteJob() throws Exception {
        String cron = "0 0 0 * * ?";

        ScheduleGroup scheduleGroup = new ScheduleGroup();
        scheduleGroup.setName("test group");
        businessEntityDao.save(scheduleGroup);

        ClusterGroup clusterGroup = new ClusterGroup();
        clusterGroup.setName("Test cluster group");
        businessEntityDao.save(clusterGroup);

        ClusterNode clusterNode = new ClusterNode();
        clusterNode.setAddress("10.2.0.1");
        clusterNode.setDescription("test node");
        clusterNode.setEnabled(true);
        clusterNode.setGroup(clusterGroup);
        clusterGroup.getNodes().add(clusterNode);

        Capture<Long> idCapture1 = new Capture<Long>(CaptureType.ALL);
        Capture<Long> idCapture2 = new Capture<Long>(CaptureType.ALL);

        control.reset();

        quartzManagementService.createJob(
            EasyMock.capture(idCapture1),
            EasyMock.eq(cron),
            EasyMock.eq(DEFAULT_TIMEZONE),
            EasyMock.eq(true)
        );

        quartzManagementService.deleteJob(
            EasyMock.capture(idCapture2)
        );

        control.replay();

        ScheduleJobCreateParameters parameters = new ScheduleJobCreateParameters();
        parameters.setName("Test Job");
        parameters.setDescription("Nothing to do");
        parameters.setTimezone(DEFAULT_TIMEZONE);
        parameters.setCron(cron);
        parameters.setEnabled(true);
        parameters.setActionType(ScheduleActionType.LOCAL_COMMAND);
        parameters.setClusterGroupId(clusterGroup.getId());
        parameters.setSchedulerGroupId(scheduleGroup.getId());
        parameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);

        ScheduleJob scheduleJob = scheduleJobManagementService.createJob(parameters);
        Assert.assertNotNull(scheduleJob);

        scheduleJobManagementService.deleteJob(scheduleJob.getId());

        control.verify();

        Assert.assertEquals(scheduleJob.getId(), idCapture1.getValue());
        Assert.assertEquals(scheduleJob.getId(), idCapture2.getValue());

        ScheduleAction action = scheduleJob.getAction();
        Assert.assertNotNull(action);
    }

    @Test
    public void testModifyJob() throws Exception {
        String cron = "0 0 0 * * ?";

        ScheduleGroup scheduleGroup = new ScheduleGroup();
        scheduleGroup.setName("test group");
        businessEntityDao.save(scheduleGroup);

        ClusterGroup clusterGroup = new ClusterGroup();
        clusterGroup.setName("Test cluster group");
        businessEntityDao.save(clusterGroup);

        ClusterNode clusterNode = new ClusterNode();
        clusterNode.setAddress("10.2.0.1");
        clusterNode.setDescription("test node");
        clusterNode.setEnabled(true);
        clusterNode.setGroup(clusterGroup);
        clusterGroup.getNodes().add(clusterNode);

        control.reset();

        quartzManagementService.createJob(
            EasyMock.anyLong(),
            EasyMock.eq(cron),
            EasyMock.eq(DEFAULT_TIMEZONE),
            EasyMock.eq(true)
        );

        control.replay();

        ScheduleJobCreateParameters parameters = new ScheduleJobCreateParameters();
        parameters.setName("Test Job");
        parameters.setDescription("Nothing to do");
        parameters.setTimezone(DEFAULT_TIMEZONE);
        parameters.setCron(cron);
        parameters.setEnabled(true);
        parameters.setActionType(ScheduleActionType.LOCAL_COMMAND);
        parameters.setClusterGroupId(clusterGroup.getId());
        parameters.setSchedulerGroupId(scheduleGroup.getId());
        parameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);

        ScheduleJob scheduleJob = scheduleJobManagementService.createJob(parameters);
        Assert.assertNotNull(scheduleJob);

        ScheduleJobModifyParameters modifyParameters = new ScheduleJobModifyParameters();
        modifyParameters.setName("Name2");
        modifyParameters.setDescription("Description2");

        ScheduleJob scheduleJobModified = scheduleJobManagementService.modifyJob(scheduleJob.getId(), modifyParameters);
        Assert.assertNotNull(scheduleJobModified);
        Assert.assertEquals("Name2", scheduleJobModified.getName());
        Assert.assertEquals("Description2", scheduleJobModified.getDescription());

        control.verify();
    }

    @Test
    public void testToggleJob() throws Exception {
        String cron = "0 0 0 * * ?";

        ScheduleGroup scheduleGroup = new ScheduleGroup();
        scheduleGroup.setName("test group");
        businessEntityDao.save(scheduleGroup);

        ClusterGroup clusterGroup = new ClusterGroup();
        clusterGroup.setName("Test cluster group");
        businessEntityDao.save(clusterGroup);

        ClusterNode clusterNode = new ClusterNode();
        clusterNode.setAddress("10.2.0.1");
        clusterNode.setDescription("test node");
        clusterNode.setEnabled(true);
        clusterNode.setGroup(clusterGroup);
        clusterGroup.getNodes().add(clusterNode);

        Capture<Long> idCapture1 = new Capture<Long>(CaptureType.ALL);
        Capture<Long> idCapture2 = new Capture<Long>(CaptureType.ALL);

        control.reset();

        quartzManagementService.createJob(
            EasyMock.anyLong(),
            EasyMock.eq(cron),
            EasyMock.eq(DEFAULT_TIMEZONE),
            EasyMock.eq(true)
        );

        quartzManagementService.disableJob(
            EasyMock.capture(idCapture1)
        );

        quartzManagementService.enableJob(
            EasyMock.capture(idCapture2),
            EasyMock.eq(cron),
            EasyMock.eq(DEFAULT_TIMEZONE)
        );

        control.replay();

        ScheduleJobCreateParameters parameters = new ScheduleJobCreateParameters();
        parameters.setName("Test Job");
        parameters.setDescription("Nothing to do");
        parameters.setTimezone(DEFAULT_TIMEZONE);
        parameters.setCron(cron);
        parameters.setEnabled(true);
        parameters.setActionType(ScheduleActionType.LOCAL_COMMAND);
        parameters.setClusterGroupId(clusterGroup.getId());
        parameters.setSchedulerGroupId(scheduleGroup.getId());
        parameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);

        ScheduleJob scheduleJob = scheduleJobManagementService.createJob(parameters);
        Assert.assertNotNull(scheduleJob);

        ScheduleJob scheduleJobModified;

        scheduleJobModified = scheduleJobManagementService.disableJob(scheduleJob.getId());
        Assert.assertNotNull(scheduleJobModified);
        Assert.assertFalse(scheduleJobModified.isEnabled());

        scheduleJobModified = scheduleJobManagementService.enableJob(scheduleJob.getId());
        Assert.assertNotNull(scheduleJobModified);
        Assert.assertTrue(scheduleJobModified.isEnabled());

        control.verify();

        Assert.assertEquals(scheduleJobModified.getId(), idCapture1.getValue());
        Assert.assertEquals(scheduleJobModified.getId(), idCapture2.getValue());
    }

    @Test
    public void testRescheduleActiveJob() throws Exception {
        String cron1 = "0 0 0 * * ?";
        String cron2 = "1 0 0 * * ?";

        ScheduleGroup scheduleGroup = new ScheduleGroup();
        scheduleGroup.setName("test group");
        businessEntityDao.save(scheduleGroup);

        ClusterGroup clusterGroup = new ClusterGroup();
        clusterGroup.setName("Test cluster group");
        businessEntityDao.save(clusterGroup);

        ClusterNode clusterNode = new ClusterNode();
        clusterNode.setAddress("10.2.0.1");
        clusterNode.setDescription("test node");
        clusterNode.setEnabled(true);
        clusterNode.setGroup(clusterGroup);
        clusterGroup.getNodes().add(clusterNode);

        Capture<Long> idCapture = new Capture<Long>(CaptureType.ALL);

        control.reset();

        quartzManagementService.createJob(
            EasyMock.anyLong(),
            EasyMock.eq(cron1),
            EasyMock.eq(DEFAULT_TIMEZONE),
            EasyMock.eq(true)
        );

        quartzManagementService.rescheduleJob(
            EasyMock.capture(idCapture),
            EasyMock.eq(cron2),
            EasyMock.eq(DEFAULT_TIMEZONE)
        );

        control.replay();

        ScheduleJobCreateParameters parameters = new ScheduleJobCreateParameters();
        parameters.setName("Test Job");
        parameters.setDescription("Nothing to do");
        parameters.setTimezone(DEFAULT_TIMEZONE);
        parameters.setCron(cron1);
        parameters.setEnabled(true);
        parameters.setActionType(ScheduleActionType.LOCAL_COMMAND);
        parameters.setClusterGroupId(clusterGroup.getId());
        parameters.setSchedulerGroupId(scheduleGroup.getId());
        parameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);

        ScheduleJob scheduleJob = scheduleJobManagementService.createJob(parameters);
        Assert.assertNotNull(scheduleJob);

        ScheduleJobRescheduleParameters rescheduleParameters = new ScheduleJobRescheduleParameters();
        rescheduleParameters.setCron(cron2);
        rescheduleParameters.setTimezone(DEFAULT_TIMEZONE);

        ScheduleJob scheduleJobModified;

        scheduleJobModified = scheduleJobManagementService.rescheduleJob(scheduleJob.getId(), rescheduleParameters);
        Assert.assertNotNull(scheduleJobModified);
        Assert.assertTrue(scheduleJobModified.isEnabled());

        control.verify();

        Assert.assertEquals(scheduleJobModified.getId(), idCapture.getValue());
    }

    @Test
    public void testReschedulePassiveJob() throws Exception {
        String cron1 = "0 0 0 * * ?";
        String cron2 = "1 0 0 * * ?";

        ScheduleGroup scheduleGroup = new ScheduleGroup();
        scheduleGroup.setName("test group");
        businessEntityDao.save(scheduleGroup);

        ClusterGroup clusterGroup = new ClusterGroup();
        clusterGroup.setName("Test cluster group");
        businessEntityDao.save(clusterGroup);

        ClusterNode clusterNode = new ClusterNode();
        clusterNode.setAddress("10.2.0.1");
        clusterNode.setDescription("test node");
        clusterNode.setEnabled(true);
        clusterNode.setGroup(clusterGroup);
        clusterGroup.getNodes().add(clusterNode);

        Capture<Long> idCapture = new Capture<Long>(CaptureType.ALL);

        control.reset();

        quartzManagementService.createJob(
            EasyMock.anyLong(),
            EasyMock.eq(cron1),
            EasyMock.eq(DEFAULT_TIMEZONE),
            EasyMock.eq(true)
        );

        quartzManagementService.disableJob(
            EasyMock.capture(idCapture)
        );

        control.replay();

        ScheduleJobCreateParameters parameters = new ScheduleJobCreateParameters();
        parameters.setName("Test Job");
        parameters.setDescription("Nothing to do");
        parameters.setTimezone(DEFAULT_TIMEZONE);
        parameters.setCron(cron1);
        parameters.setEnabled(true);
        parameters.setActionType(ScheduleActionType.LOCAL_COMMAND);
        parameters.setClusterGroupId(clusterGroup.getId());
        parameters.setSchedulerGroupId(scheduleGroup.getId());
        parameters.setStrategy(ScheduleExecutionStrategy.CIRCULAR);

        ScheduleJob scheduleJob = scheduleJobManagementService.createJob(parameters);
        Assert.assertNotNull(scheduleJob);

        ScheduleJob scheduleJobModified;

        scheduleJobModified = scheduleJobManagementService.disableJob(scheduleJob.getId());
        Assert.assertNotNull(scheduleJobModified);
        Assert.assertFalse(scheduleJobModified.isEnabled());

        ScheduleJobRescheduleParameters rescheduleParameters = new ScheduleJobRescheduleParameters();
        rescheduleParameters.setCron(cron2);
        rescheduleParameters.setTimezone(DEFAULT_TIMEZONE);

        scheduleJobModified = scheduleJobManagementService.rescheduleJob(scheduleJob.getId(), rescheduleParameters);
        Assert.assertNotNull(scheduleJobModified);
        Assert.assertFalse(scheduleJobModified.isEnabled());

        control.verify();

        Assert.assertEquals(scheduleJobModified.getId(), idCapture.getValue());
    }

}
