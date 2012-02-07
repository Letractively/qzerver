package org.qzerver.model.service.cluster;

import com.gainmatrix.lib.paging.Extraction;
import org.qzerver.model.domain.entities.cluster.ClusterGroup;
import org.qzerver.model.domain.entities.cluster.ClusterNode;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ClusterManagementService {

    List<ClusterGroup> getAllGroups(Extraction extraction);

    ClusterGroup getGroup(long clusterGroupId);

    ClusterGroup modifyGroup(long clusterGroupId, String name);

    ClusterGroup createGroup(String name);

    void deleteGroup(long clusterGroupId);

    ClusterNode createNode(long clusterGroupId, String domain, String comment);

    ClusterNode modifyNode(long clusterNodeId, String domain, String comment);

    void deleteNode(long clusterNodeId);

}
