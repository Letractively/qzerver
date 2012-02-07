package org.qzerver;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.qzerver.base.AbstractModelTest;
import org.qzerver.model.domain.job.ScheduleJob;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class JpaTest extends AbstractModelTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @Ignore
    public void test1() throws Exception {
        ScheduleJob job = new ScheduleJob();
        job.setName("qwrgqwrgwrg");
        job.setDescription("wrgqwrqwgqwrgrqw");
        job.setCron("0 0 * * * ?");
        entityManager.persist(job);

        Assert.assertNotNull(job.getId());

        entityManager.flush();
        entityManager.clear();

        ScheduleJob jobLoaded = entityManager.find(ScheduleJob.class, job.getId());
        Assert.assertNotNull(jobLoaded);
    }
}
