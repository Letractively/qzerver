package org.qzerver.model.service.cluster;

import com.gainmatrix.lib.business.exception.AbstractServiceException;
import com.gainmatrix.lib.paging.Extraction;
import org.qzerver.model.domain.entities.cluster.ClusterGroup;
import org.qzerver.model.domain.entities.cluster.ClusterNode;
import org.qzerver.model.domain.entities.cluster.ClusterStrategy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ClusterManagementService {

    /**
     * Get all cluster groups
     * @param extraction Extraction
     * @return List of cluster groups
     */
    List<ClusterGroup> getAllGroups(Extraction extraction);

    /**
     * Get cluster group
     * @param clusterGroupId Cluster group identifier
     * @return Cluster group
     */
    ClusterGroup getGroup(long clusterGroupId);

    /**
     * Modify group
     * @param clusterGroupId Cluster group identifier
     * @param name Name
     * @param strategy Node selection strategy
     * @return Cluster group
     */
    ClusterGroup modifyGroup(long clusterGroupId, String name, ClusterStrategy strategy);

    /**
     * Create new cluster group
     * @param name Name
     * @param strategy Node selection strategy
     * @return Cluster group
     */
    ClusterGroup createGroup(String name, ClusterStrategy strategy);

    /**
     * Roll cluster node index
     * @param clusterGroupId Cluster group identify
     * @return New index position
     */
    int rollGroupIndex(long clusterGroupId);

    /**
     * Delete cluster group
     * @param clusterGroupId Cluster group identifier
     * @throws org.qzerver.model.service.cluster.exception.ClusterGroupUsed If cluster group is used and can't be deleted
     */
    void deleteGroup(long clusterGroupId) throws AbstractServiceException;

    /**
     * Create new node in the specified group
     * @param clusterGroupId Cluster group identifier
     * @param domain Domain
     * @param comment Comment
     * @param activity Is node active
     * @return Cluster node
     */
    ClusterNode createNode(long clusterGroupId, String domain, String comment, boolean activity);

    /**
     * Modify cluster node
     * @param clusterNodeId Cluster node identifier
     * @param domain Domain
     * @param comment Comment
     * @param activity Is node active
     * @return Cluster node
     */
    ClusterNode modifyNode(long clusterNodeId, String domain, String comment, boolean activity);

    /**
     * Delete cluster node
     * @param clusterNodeId Cluster node identifier
     */
    void deleteNode(long clusterNodeId);

}
