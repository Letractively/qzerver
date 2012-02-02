package org.qzerver.model.domain.business;

import com.gainmatrix.lib.business.BusinessModelVersionUtils;

public interface BusinessModel {

    /**
     * Current business model version
     * @see com.gainmatrix.lib.business.BusinessModelVersionUtils#parseBusinessModelVersion(java.lang.Class)
     */

    int VERSION = BusinessModelVersionUtils.parseBusinessModelVersion(BusinessModel.class);

}
