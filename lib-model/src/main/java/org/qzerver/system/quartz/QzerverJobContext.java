package org.qzerver.system.quartz;

import java.io.Serializable;

public class QzerverJobContext implements Serializable {

    private String name;

    private String group;

    private String planned;

    private String fired;

}
