package org.qzerver.model.domain.business;

import com.gainmatrix.lib.business.BusinessModelVersionUtils;

public final class BusinessModel {

    /**
     * Current business model version
     * @see com.gainmatrix.lib.business.BusinessModelVersionUtils#parseBusinessModelVersion(java.lang.Class)
     */

    public static final int VERSION = BusinessModelVersionUtils.parseBusinessModelVersion(BusinessModel.class);

    private BusinessModel() {
    }

}
