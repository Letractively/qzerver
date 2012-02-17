package org.qzerver.model.service.cluster.impl;

import com.gainmatrix.lib.business.BusinessEntityDao;
import com.gainmatrix.lib.business.exception.AbstractServiceException;
import com.gainmatrix.lib.business.exception.MissingEntityException;
import com.gainmatrix.lib.paging.Extraction;
import com.google.common.base.Preconditions;
import org.qzerver.model.dao.cluster.ClusterGroupDao;
import org.qzerver.model.dao.job.ScheduleJobDao;
import org.qzerver.model.domain.entities.cluster.ClusterGroup;
import org.qzerver.model.domain.entities.cluster.ClusterNode;
import org.qzerver.model.domain.entities.job.ScheduleJob;
import org.qzerver.model.service.cluster.ClusterManagementService;
import org.qzerver.model.service.cluster.exception.ClusterGroupUsed;
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

    private ScheduleJobDao scheduleJobDao;

    protected BusinessEntityDao businessEntityDao;

    @Override
    @Transactional(readOnly = true)
    public List<ClusterGroup> getAllGroups(Extraction extraction) {
        return clusterGroupDao.findAllGroups(extraction);
    }

    @Override
    public ClusterGroup createGroup(String name) {
        Preconditions.checkNotNull(name);

        ClusterGroup clusterGroup = new ClusterGroup();
        clusterGroup.setName(name);
        clusterGroup.setRollingIndex(0);

        businessEntityDao.save(clusterGroup);

        return clusterGroup;
    }

    @Override
    public ClusterGroup modifyGroup(long clusterGroupId, String name) {
        Preconditions.checkNotNull(name);

        ClusterGroup clusterGroup = businessEntityDao.lockById(ClusterGroup.class, clusterGroupId);
        if (clusterGroup == null) {
            throw new MissingEntityException(ClusterGroup.class, clusterGroupId);
        }

        clusterGroup.setName(name);

        return clusterGroup;
    }

    @Override
    public void deleteGroup(long clusterGroupId) throws AbstractServiceException {
        List<ScheduleJob> jobs = scheduleJobDao.findAllByClusterGroup(clusterGroupId);
        if ((jobs != null) && (jobs.size() > 0)) {
            throw new ClusterGroupUsed();
        }

        businessEntityDao.deleteById(ScheduleJob.class, clusterGroupId);
    }

    @Override
    @Transactional(readOnly = true)
    public ClusterGroup getGroup(long clusterGroupId) {
        ClusterGroup clusterGroup = businessEntityDao.findById(ClusterGroup.class, clusterGroupId);
        clusterGroup.getNodes().size();
        return clusterGroup;
    }

    @Override
    public int rollGroupIndex(long clusterGroupId) {
        ClusterGroup clusterGroup = businessEntityDao.lockById(ClusterGroup.class, clusterGroupId);
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
        ClusterGroup clusterGroup = businessEntityDao.lockById(ClusterGroup.class, clusterGroupId);
        if (clusterGroup == null) {
            throw new MissingEntityException(ClusterGroup.class, clusterGroupId);
        }

        ClusterNode clusterNode = new ClusterNode();
        clusterNode.setActive(activity);
        clusterNode.setDomain(domain);
        clusterNode.setDescription(comment);

        clusterNode.setGroup(clusterGroup);
        clusterGroup.getNodes().add(clusterNode);

        return clusterNode;
    }

    @Override
    public void deleteNode(long clusterNodeId) {
        ClusterNode clusterNode = businessEntityDao.findById(ClusterNode.class, clusterNodeId);
        if (clusterNode == null) {
            throw new MissingEntityException(ClusterNode.class, clusterNodeId);
        }

        ClusterGroup clusterGroup = clusterNode.getGroup();
        businessEntityDao.lock(clusterGroup);

        if (clusterGroup.getRollingIndex() > clusterNode.getOrderIndex()) {
            clusterGroup.setRollingIndex(clusterGroup.getRollingIndex() - 1);
        }

        clusterGroup.getNodes().remove(clusterNode);
        businessEntityDao.delete(clusterNode);
    }

    @Override
    public ClusterNode modifyNode(long clusterNodeId, String domain, String comment, boolean activity) {
        ClusterNode clusterNode = businessEntityDao.lockById(ClusterNode.class, clusterNodeId);
        if (clusterNode == null) {
            throw new MissingEntityException(ClusterNode.class, clusterNodeId);
        }

        clusterNode.setDomain(domain);
        clusterNode.setDescription(comment);
        clusterNode.setActive(activity);

        return clusterNode;
    }

    @Required
    public void setClusterGroupDao(ClusterGroupDao clusterGroupDao) {
        this.clusterGroupDao = clusterGroupDao;
    }

    @Required
    public void setScheduleJobDao(ScheduleJobDao scheduleJobDao) {
        this.scheduleJobDao = scheduleJobDao;
    }

    @Required
    public void setBusinessEntityDao(BusinessEntityDao businessEntityDao) {
        this.businessEntityDao = businessEntityDao;
    }
}
