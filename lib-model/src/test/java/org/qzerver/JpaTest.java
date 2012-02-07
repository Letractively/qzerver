package org.qzerver;

import org.junit.Assert;
import org.junit.Test;
import org.qzerver.base.AbstractModelTest;
import org.qzerver.model.domain.entities.job.ScheduleActionType;
import org.qzerver.model.domain.entities.job.ScheduleGroup;
import org.qzerver.model.domain.entities.job.ScheduleJob;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class JpaTest extends AbstractModelTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void test1() throws Exception {
        ScheduleGroup group = new ScheduleGroup();
        group.setName("test");

        ScheduleJob job = new ScheduleJob();
        job.setName("qwrgqwrgwrg");
        job.setDescription("wrgqwrqwgqwrgrqw");
        job.setCron("0 0 * * * ?");
        job.setEnabled(true);
        job.setStandby(false);
        job.setActionType(ScheduleActionType.HTTP);
        job.setActionDefinition("<xml></xml");

        group.getJobs().add(job);
        job.setGroup(group);

        entityManager.persist(job);

        Assert.assertNotNull(job.getId());

        entityManager.flush();
        entityManager.clear();

        ScheduleJob jobLoaded = entityManager.find(ScheduleJob.class, job.getId());
        Assert.assertNotNull(jobLoaded);
    }
}
