package org.qzerver.model.domain.action.jmx;

import com.gainmatrix.lib.business.AbstractBusinessEntity;
import org.qzerver.model.domain.business.BusinessModel;

public class JmxAction extends AbstractBusinessEntity<Long> {

    private Long id;

    public JmxAction() {
        super(BusinessModel.VERSION);
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
