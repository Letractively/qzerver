package org.qzerver.web.attribute.freemarker;

import com.gainmatrix.lib.web.attribute.AttributePublisher;
import freemarker.ext.beans.BeansWrapper;

import javax.servlet.http.HttpServletRequest;

public class FreemarkerAttributePublisher implements AttributePublisher {

    public static final String ATTRIBUTE_STATICS_NAME = "freemarker_static";

    public static final String ATTRIBUTE_ENUMS_NAME = "freemarker_enum";

    @Override
    public void publish(HttpServletRequest request) {
        request.setAttribute(ATTRIBUTE_STATICS_NAME, BeansWrapper.getDefaultInstance().getStaticModels());
        request.setAttribute(ATTRIBUTE_ENUMS_NAME, BeansWrapper.getDefaultInstance().getEnumModels());
    }

}
