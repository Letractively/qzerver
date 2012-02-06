package org.qzerver;

import org.junit.Assert;
import org.junit.Test;
import org.qzerver.base.AbstractContextTest;
import org.qzerver.model.domain.job.SchedulerJob;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class JpaTest extends AbstractContextTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void test1() throws Exception {
        SchedulerJob job = new SchedulerJob();
        job.setName("qwrgqwrgwrg");
        job.setDescription("wrgqwrqwgqwrgrqw");
        entityManager.persist(job);

        Assert.assertNotNull(job.getId());

        entityManager.flush();
        entityManager.clear();

        SchedulerJob jobLoaded = entityManager.find(SchedulerJob.class, job.getId());
        Assert.assertNotNull(jobLoaded);
    }
}
