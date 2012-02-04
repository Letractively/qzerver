package org.qzerver.web.render.attribute;

import com.gainmatrix.lib.web.attribute.AttributePublisher;
import freemarker.ext.beans.BeansWrapper;

import javax.servlet.http.HttpServletRequest;

public class FreemarkerAttributePublisher implements AttributePublisher {

    private static final String ATTRIBUTE_STATICS = "freemarker_static";

    private static final String ATTRIBUTE_ENUMS = "freemarker_enum";

    @Override
    public void publish(HttpServletRequest request) {
        request.setAttribute(ATTRIBUTE_STATICS, BeansWrapper.getDefaultInstance().getStaticModels());
        request.setAttribute(ATTRIBUTE_ENUMS, BeansWrapper.getDefaultInstance().getEnumModels());
    }

}
