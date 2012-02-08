package org.qzerver;

import org.junit.Assert;
import org.junit.Test;
import org.qzerver.base.AbstractModelTest;
import org.qzerver.model.domain.entities.job.ScheduleAction;
import org.qzerver.model.domain.entities.job.ScheduleActionType;
import org.qzerver.model.domain.entities.job.ScheduleGroup;
import org.qzerver.model.domain.entities.job.ScheduleJob;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;

public class JpaTest extends AbstractModelTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void test1() throws Exception {
        ScheduleGroup group = new ScheduleGroup();
        group.setName("test");

        ScheduleAction action = new ScheduleAction();
        action.setType(ScheduleActionType.NOP);
        action.setArchived(false);
        action.setDefinition("<xml/>");
        action.setClusterGroup(null);
        action.setCreated(new Date(1213232323L));

        ScheduleJob job = new ScheduleJob();
        job.setName("qwrgqwrgwrg");
        job.setDescription("wrgqwrqwgqwrgrqw");
        job.setCron("0 0 * * * ?");
        job.setEnabled(true);
        job.setStandby(false);
        job.setAction(action);

        group.getJobs().add(job);
        job.setGroup(group);

        entityManager.persist(job);

        Assert.assertNotNull(job.getId());

        entityManager.flush();
        entityManager.clear();

        ScheduleJob jobLoaded = entityManager.find(ScheduleJob.class, job.getId());
        Assert.assertNotNull(jobLoaded);

        action = jobLoaded.getAction();
        action.getVersion();
        Assert.assertEquals("<xml/>", action.getDefinition());
    }
}
