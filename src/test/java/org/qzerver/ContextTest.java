package org.qzerver;

import com.gainmatrix.lib.time.Chronometer;
import org.junit.Test;
import org.qzerver.base.AbstractContextTest;

import javax.annotation.Resource;

public class ContextTest extends AbstractContextTest {

    @Resource
    private Chronometer chronometer;

    @Test
    public void test() throws Exception {
        System.out.println(chronometer.getCurrentMoment());
    }
}
