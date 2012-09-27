package org.qzerver.model.service.job.executor.impl;

import com.gainmatrix.lib.time.impl.StubChronometer;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;
import org.qzerver.base.AbstractModelTest;
import org.qzerver.model.agent.action.ActionAgent;
import org.qzerver.model.service.job.execution.ScheduleExecutionManagementService;
import org.qzerver.model.service.mail.MailService;
import org.springframework.validation.Validator;

import javax.annotation.Resource;

public class ScheduleJobExecutorServiceImplTest extends AbstractModelTest {

    private IMocksControl control;

    private MailService mailService;

    private ScheduleJobExecutorServiceImpl scheduleJobExecutorService;

    private ActionAgent actionAgent;

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
    }

    @Test
    public void testNormalExecution() throws Exception {

    }
}
