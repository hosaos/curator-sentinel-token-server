package com.chenyin.sentinel.token.server.parser;

import com.alibaba.csp.sentinel.cluster.server.config.ClusterServerConfigManager;
import com.alibaba.csp.sentinel.cluster.server.config.ServerFlowConfig;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.util.HostNameUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.chenyin.sentinel.token.server.entity.ClusterGroupEntity;

import java.util.List;

/**
 * @author: chenyin
 * @date: 2019-08-12 14:53
 */
public class ClusterServerFlowConfigParser implements Converter<String, ServerFlowConfig> {
    @Override
    public ServerFlowConfig convert(String source) {
        if (source == null) {
            return null;
        }
        RecordLog.info("[ClusterServerFlowConfigParser] Get data: " + source);
        List<ClusterGroupEntity> groupList = JSON.parseObject(source, new TypeReference<List<ClusterGroupEntity>>() {
        });
        if (groupList == null || groupList.isEmpty()) {
            return null;
        }
        return extractServerFlowConfig(groupList);
    }

    private ServerFlowConfig extractServerFlowConfig(List<ClusterGroupEntity> groupList) {
        for (ClusterGroupEntity group : groupList) {
            if (HostNameUtil.getIp().equals(group.getIp())) {
                return new ServerFlowConfig()
                        .setExceedCount(ClusterServerConfigManager.getExceedCount())
                        .setIntervalMs(ClusterServerConfigManager.getIntervalMs())
                        .setMaxAllowedQps(group.getMaxAllowedQps())
                        .setMaxOccupyRatio(ClusterServerConfigManager.getMaxOccupyRatio())
                        .setSampleCount(ClusterServerConfigManager.getSampleCount());
            }
        }
        return null;
    }
}
