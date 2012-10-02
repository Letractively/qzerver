package org.qzerver.dao.job;

import com.gainmatrix.lib.business.entity.BusinessEntityDao;
import com.gainmatrix.lib.jpa.BusinessEntityJpaDaoChecker;
import com.gainmatrix.lib.time.ChronometerUtils;
import org.junit.Test;
import org.qzerver.base.AbstractTransactionalTest;
import org.qzerver.model.domain.entities.job.*;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;

public class JobDaoTest extends AbstractTransactionalTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Resource
    private BusinessEntityDao businessEntityDao;

    @Test
    public void testScheduleGroupDao() throws Exception {
        ScheduleGroup scheduleGroup = new ScheduleGroup();
        scheduleGroup.setName("dgfsrgw");

        BusinessEntityJpaDaoChecker.checkBusinessEntityDao(entityManager, businessEntityDao,
            ScheduleGroup.class, scheduleGroup);
    }

    public void testScheduleActionDao() throws Exception {
        ScheduleAction scheduleAction = new ScheduleAction();
        scheduleAction.setArchived(true);
        scheduleAction.setCreated(new Date(300000000000L));
        scheduleAction.setType("none");
        scheduleAction.setDefinition("<xml></xml>".getBytes());

        BusinessEntityJpaDaoChecker.checkBusinessEntityDao(entityManager, businessEntityDao,
                ScheduleAction.class, scheduleAction);
    }

    @Test
    public void testScheduleJobDao() throws Exception {
        ScheduleGroup scheduleGroup = new ScheduleGroup();
        scheduleGroup.setName("dgfsrgw");
        businessEntityDao.save(scheduleGroup);

        ScheduleAction scheduleAction = new ScheduleAction();
        scheduleAction.setArchived(true);
        scheduleAction.setCreated(new Date(300000000000L));
        scheduleAction.setType("none");
        scheduleAction.setDefinition("<xml></xml>".getBytes());
        businessEntityDao.save(scheduleAction);

        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setName("dgfsrgw");
        scheduleJob.setDescription("wgwrgwg");
        scheduleJob.setCron("0 * * * * ?");
        scheduleJob.setTimezone("UTC");
        scheduleJob.setEnabled(true);
        scheduleJob.setStandby(false);
        scheduleJob.setAllNodes(true);
        scheduleJob.setStrategy(ScheduleExecutionStrategy.CIRCULAR);
        scheduleJob.setTimeout(1000);
        scheduleJob.setTrials(1);
        scheduleJob.setAction(scheduleAction);
        scheduleJob.setGroup(scheduleGroup);
        scheduleGroup.getJobs().add(scheduleJob);

        BusinessEntityJpaDaoChecker.checkBusinessEntityDao(entityManager, businessEntityDao,
                ScheduleGroup.class, scheduleGroup);
    }

    @Test
    public void testScheduleExecutionDao() throws Exception {
        ScheduleGroup scheduleGroup = new ScheduleGroup();
        scheduleGroup.setName("dgfsrgw");
        businessEntityDao.save(scheduleGroup);

        ScheduleAction scheduleAction = new ScheduleAction();
        scheduleAction.setArchived(true);
        scheduleAction.setCreated(ChronometerUtils.parseMoment("2011-01-01 12:00:00.000 UTC"));
        scheduleAction.setType("none");
        scheduleAction.setDefinition("<xml></xml>".getBytes());
        businessEntityDao.save(scheduleAction);

        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setName("dgfsrgw");
        scheduleJob.setDescription("wgwrgwg");
        scheduleJob.setCron("0 * * * * ?");
        scheduleJob.setTimezone("UTC");
        scheduleJob.setEnabled(true);
        scheduleJob.setStandby(false);
        scheduleJob.setAllNodes(true);
        scheduleJob.setStrategy(ScheduleExecutionStrategy.CIRCULAR);
        scheduleJob.setTimeout(1000);
        scheduleJob.setTrials(1);
        scheduleJob.setAction(scheduleAction);
        scheduleJob.setGroup(scheduleGroup);
        scheduleGroup.getJobs().add(scheduleJob);
        businessEntityDao.save(scheduleJob);

        ScheduleExecution scheduleExecution = new ScheduleExecution();
        scheduleExecution.setStrategy(ScheduleExecutionStrategy.CIRCULAR);
        scheduleExecution.setName("rgwrgr");
        scheduleExecution.setCron("0 * * * * ?");
        scheduleExecution.setForced(true);
        scheduleExecution.setHostname("rgwrgwr");
        scheduleExecution.setFired(ChronometerUtils.parseMoment("2011-01-01 12:00:01.000 UTC"));
        scheduleExecution.setScheduled(ChronometerUtils.parseMoment("2011-01-01 12:00:00.000 UTC"));
        scheduleExecution.setStarted(ChronometerUtils.parseMoment("2011-01-01 12:00:02.000 UTC"));
        scheduleExecution.setFinished(ChronometerUtils.parseMoment("2011-01-01 12:00:03.000 UTC"));
        scheduleExecution.setStatus(ScheduleExecutionStatus.SUCCEED);
        scheduleExecution.setAllNodes(true);
        scheduleExecution.setCancelled(false);
        scheduleExecution.setAction(scheduleAction);
        scheduleExecution.setJob(scheduleJob);

        BusinessEntityJpaDaoChecker.checkBusinessEntityDao(entityManager, businessEntityDao,
                ScheduleExecution.class, scheduleExecution);
    }
}
