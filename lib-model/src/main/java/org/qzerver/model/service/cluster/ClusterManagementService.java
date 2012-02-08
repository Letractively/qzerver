package org.qzerver.model.service.cluster;

import com.gainmatrix.lib.paging.Extraction;
import org.qzerver.model.domain.entities.cluster.ClusterGroup;
import org.qzerver.model.domain.entities.cluster.ClusterNode;
import org.qzerver.model.domain.entities.cluster.ClusterStrategy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ClusterManagementService {

    List<ClusterGroup> getAllGroups(Extraction extraction);

    ClusterGroup getGroup(long clusterGroupId);

    ClusterGroup modifyGroup(long clusterGroupId, String name, ClusterStrategy strategy);

    ClusterGroup createGroup(String name, ClusterStrategy strategy);

    ClusterGroup updateGroupIndex(long clusterGroupId);

    void deleteGroup(long clusterGroupId);

    ClusterNode createNode(long clusterGroupId, String domain, String comment, boolean activity);

    ClusterNode modifyNode(long clusterNodeId, String domain, String comment, boolean activity);

    void deleteNode(long clusterNodeId);

}
