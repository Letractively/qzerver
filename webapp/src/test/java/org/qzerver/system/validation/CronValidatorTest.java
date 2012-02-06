package org.qzerver.system.validation;

import org.junit.Assert;
import org.junit.Test;
import org.qzerver.base.AbstractContextTest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.annotation.Resource;

public class CronValidatorTest extends AbstractContextTest {

    @Resource
    private Validator beanValidator;

    @Test
    public void testNull() throws Exception {
        TestBean bean = new TestBean();
        bean.setCron(null);

        Errors errors = new BeanPropertyBindingResult(bean, "bean");

        beanValidator.validate(bean, errors);
        Assert.assertEquals(0, errors.getAllErrors().size());
    }

    @Test
    public void testGood1() throws Exception {
        TestBean bean = new TestBean();
        bean.setCron("0 * * * * ?");

        Errors errors = new BeanPropertyBindingResult(bean, "bean");

        beanValidator.validate(bean, errors);
        Assert.assertEquals(0, errors.getAllErrors().size());
    }

    @Test
    public void testGood2() throws Exception {
        TestBean bean = new TestBean();
        bean.setCron("0,2,3 * 1 * * ?");

        Errors errors = new BeanPropertyBindingResult(bean, "bean");

        beanValidator.validate(bean, errors);
        Assert.assertEquals(0, errors.getAllErrors().size());
    }

    @Test
    public void testBad1() throws Exception {
        TestBean bean = new TestBean();
        bean.setCron("gr * 1 * * ?");

        Errors errors = new BeanPropertyBindingResult(bean, "bean");

        beanValidator.validate(bean, errors);
        Assert.assertEquals(1, errors.getAllErrors().size());
    }

    public static class TestBean {

        @Cron
        private String cron;

        public String getCron() {
            return cron;
        }

        public void setCron(String cron) {
            this.cron = cron;
        }
    }
}

