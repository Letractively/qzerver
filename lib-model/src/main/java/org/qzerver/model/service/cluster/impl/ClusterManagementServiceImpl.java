package org.qzerver.model.service.cluster.impl;

import com.gainmatrix.lib.business.exception.MissingEntityException;
import com.gainmatrix.lib.paging.Extraction;
import com.google.common.base.Preconditions;
import org.qzerver.model.dao.cluster.ClusterGroupDao;
import org.qzerver.model.dao.cluster.ClusterNodeDao;
import org.qzerver.model.domain.entities.cluster.ClusterGroup;
import org.qzerver.model.domain.entities.cluster.ClusterNode;
import org.qzerver.model.domain.entities.cluster.ClusterStrategy;
import org.qzerver.model.service.cluster.ClusterManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(propagation = Propagation.REQUIRED)
public class ClusterManagementServiceImpl implements ClusterManagementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterManagementServiceImpl.class);

    private ClusterGroupDao clusterGroupDao;

    private ClusterNodeDao clusterNodeDao;

    @Override
    public List<ClusterGroup> getAllGroups(Extraction extraction) {
        return clusterGroupDao.findAllGroups(extraction);
    }

    @Override
    public ClusterGroup createGroup(String name, ClusterStrategy strategy) {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(strategy);

        ClusterGroup clusterGroup = new ClusterGroup();
        clusterGroup.setName(name);
        clusterGroup.setStrategy(strategy);
        clusterGroup.setRollingIndex(0);

        clusterGroupDao.save(clusterGroup);

        return clusterGroup;
    }

    @Override
    public ClusterGroup modifyGroup(long clusterGroupId, String name, ClusterStrategy strategy) {
        ClusterGroup clusterGroup = clusterGroupDao.lockById(clusterGroupId);
        if (clusterGroup == null) {
            throw new MissingEntityException(ClusterGroup.class, clusterGroupId);
        }

        clusterGroup.setName(name);
        clusterGroup.setStrategy(strategy);
        clusterGroup.setRollingIndex(0);

        return clusterGroup;
    }

    @Override
    public void deleteGroup(long clusterGroupId) {
        clusterGroupDao.deleteById(clusterGroupId);
    }

    @Override
    public ClusterGroup getGroup(long clusterGroupId) {
        return clusterGroupDao.findById(clusterGroupId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int rollGroupIndex(long clusterGroupId) {
        ClusterGroup clusterGroup = clusterGroupDao.lockById(clusterGroupId);
        if (clusterGroup == null) {
            throw new MissingEntityException(ClusterGroup.class, clusterGroupId);
        }

        int currentIndex = clusterGroup.getRollingIndex();

        for (int i=currentIndex + 1, size=clusterGroup.getNodes().size(); i < size; i++) {
            ClusterNode clusterNode = clusterGroup.getNodes().get(i);
            if (clusterNode.isActive()) {
                clusterGroup.setRollingIndex(i);
                return i;
            }
        }

        for (int i=0; i < currentIndex; i++) {
            ClusterNode clusterNode = clusterGroup.getNodes().get(i);
            if (clusterNode.isActive()) {
                clusterGroup.setRollingIndex(i);
                return i;
            }
        }

        return currentIndex;
    }

    @Override
    public ClusterNode createNode(long clusterGroupId, String domain, String comment, boolean activity) {
        ClusterGroup clusterGroup = clusterGroupDao.lockById(clusterGroupId);
        if (clusterGroup == null) {
            throw new MissingEntityException(ClusterGroup.class, clusterGroupId);
        }

        ClusterNode clusterNode = new ClusterNode();
        clusterNode.setActive(activity);
        clusterNode.setDomain(domain);
        clusterNode.setComment(comment);

        clusterNode.setGroup(clusterGroup);
        clusterGroup.getNodes().add(clusterNode);

        return clusterNode;
    }

    @Override
    public void deleteNode(long clusterNodeId) {
        ClusterNode clusterNode = clusterNodeDao.findById(clusterNodeId);
        if (clusterNode == null) {
            throw new MissingEntityException(ClusterNode.class, clusterNodeId);
        }

        ClusterGroup clusterGroup = clusterGroupDao.lockById(clusterNode.getGroup().getId());

        if (clusterGroup.getRollingIndex() > clusterNode.getOrderIndex()) {
            clusterGroup.setRollingIndex(clusterGroup.getRollingIndex() - 1);
        }

        clusterGroup.getNodes().remove(clusterNode);
    }

    @Override
    public ClusterNode modifyNode(long clusterNodeId, String domain, String comment, boolean activity) {
        ClusterNode clusterNode = clusterNodeDao.lockById(clusterNodeId);
        if (clusterNode == null) {
            throw new MissingEntityException(ClusterNode.class, clusterNodeId);
        }

        clusterNode.setDomain(domain);
        clusterNode.setComment(comment);
        clusterNode.setActive(activity);

        return clusterNode;
    }

    @Required
    public void setClusterGroupDao(ClusterGroupDao clusterGroupDao) {
        this.clusterGroupDao = clusterGroupDao;
    }

    @Required
    public void setClusterNodeDao(ClusterNodeDao clusterNodeDao) {
        this.clusterNodeDao = clusterNodeDao;
    }
}
