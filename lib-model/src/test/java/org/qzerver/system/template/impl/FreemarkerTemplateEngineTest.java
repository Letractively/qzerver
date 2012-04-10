package org.qzerver.system.template.impl;

import com.google.common.collect.ImmutableMap;
import junit.framework.Assert;
import org.junit.Test;
import org.qzerver.base.AbstractModelTest;
import org.qzerver.system.template.TemplateEngine;

import javax.annotation.Resource;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class FreemarkerTemplateEngineTest extends AbstractModelTest {

    @Resource
    private TemplateEngine mailFreemarkerTemplateEngine;

    @Test
    public void testTemplate() throws Exception {
        Map<String,Object> model = ImmutableMap.<String,Object>builder()
                .build();

        String result = mailFreemarkerTemplateEngine.template("job-failed", model, Locale.ENGLISH, TimeZone.getTimeZone("UTC"));
        Assert.assertNotNull(result);
    }

}
