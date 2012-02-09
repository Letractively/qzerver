package org.qzerver.model.dao.cluster;

import com.gainmatrix.lib.paging.Extraction;
import org.qzerver.model.domain.entities.cluster.ClusterGroup;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClusterGroupDao {

    /**
     * Load all group sorted by name
     * @param extraction Extraction
     * @return Group list
     */
    List<ClusterGroup> findAllGroups(Extraction extraction);

}
