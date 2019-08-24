package com.chenyin.sentinel.token.server.apollo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ctrip.framework.apollo.openapi.client.ApolloOpenApiClient;
import com.ctrip.framework.apollo.openapi.dto.NamespaceReleaseDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenItemDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenNamespaceDTO;
import com.chenyin.sentinel.token.server.entity.ClusterGroupEntity;
import com.chenyin.sentinel.token.server.util.ApolloConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * @author: chenyin
 * @date: 2019-08-22 19:28
 */
@Component
public class ApolloClusterConfigManager{
    private static final Logger log = LoggerFactory.getLogger(ApolloClusterConfigManager.class);
    private static String projectEnv;

    @Value("${spring.profiles.active}")
    public void setProjectEnv(String env) {
        projectEnv = env;
    }

    /**
     * 成为master的tokenServer修改不同规则namespace中的集群配置
     *
     * @param ip
     * @param port
     */
    public static void changeMasterTokenServerAddress(String ip, Integer port) {
        ApolloOpenApiClient apolloOpenApiClient = ApolloOpenApiClientHolder.getApolloOpenApiClient();
        //查询sentinel规则下所有的nameSpace
        String sentinelAppId = ApolloConfigUtil.getSentinelAppId();
        List<OpenNamespaceDTO> namespaceDTOList = apolloOpenApiClient.getNamespaces(sentinelAppId, projectEnv,
                "default");
        //遍历nameSpace查找集群配置所在item，修改业务应用限流规则中的token server配置 的ip、port
        for (OpenNamespaceDTO openNamespaceDTO : namespaceDTOList) {
            String nameSpaceName = openNamespaceDTO.getNamespaceName();
            //token server所在nameSpace不作处理
            if (ApolloConfigUtil.getTokenServerNamespaceName().equals(nameSpaceName)) {
                continue;
            }
            List<OpenItemDTO> itemDTOList = openNamespaceDTO.getItems();
            if (itemDTOList == null || itemDTOList.isEmpty()) {
                continue;
            }
            //找到配置了集群限流的item
            Optional<OpenItemDTO> clusterConfigItem =
                    itemDTOList.stream().filter(t -> ApolloConfigUtil.getTokenServerClusterMapDataId().equals(t.getKey())).findAny();
            if (!clusterConfigItem.isPresent()) {
                continue;
            }

            publishMasterTokenServerAddress(clusterConfigItem.get(), openNamespaceDTO.getNamespaceName(), ip, port);


        }
    }

    /**
     * 将master TokenServer的配置写入不同规则namespace的集群配置中
     *
     * @param openItemDTO 集群规则所在的item
     * @param appName     不同规则所在的namespace名字，即为appName
     * @param ip          tokenServer ip
     * @param port        tokenServer port
     */
    private static void publishMasterTokenServerAddress(OpenItemDTO openItemDTO, String appName, String ip,
                                                        Integer port) {
        String sentinelAppId = ApolloConfigUtil.getSentinelAppId();
        String value = openItemDTO.getValue();
        String clusterName = "default";
        if (StringUtils.isEmpty(value)) {
            return;
        }
        try {
            List<ClusterGroupEntity> groupList = JSON.parseObject(value, new TypeReference<List<ClusterGroupEntity>>() {
            });

            if (groupList == null || groupList.isEmpty()) {
                return;
            }
            boolean needToChangeServerAddress = false;

            for (ClusterGroupEntity clusterGroupEntity : groupList) {
                //规则中的tokenServer地址与当前相等，不做处理
                if (clusterGroupEntity.getIp().equals(ip) && clusterGroupEntity.getPort().equals(port)) {
                    continue;
                }

                clusterGroupEntity.setIp(ip);
                clusterGroupEntity.setPort(port);
                clusterGroupEntity.setMachineId(ip);
                needToChangeServerAddress = true;
            }

            if (!needToChangeServerAddress) {
                return;
            }
            openItemDTO.setValue(JSON.toJSONString(groupList));
            ApolloOpenApiClientHolder.getApolloOpenApiClient().createOrUpdateItem(sentinelAppId, projectEnv,
                    clusterName, appName, openItemDTO);
            // Release configuration
            NamespaceReleaseDTO namespaceReleaseDTO = new NamespaceReleaseDTO();
            namespaceReleaseDTO.setEmergencyPublish(true);
            namespaceReleaseDTO.setReleasedBy(ApolloConfigUtil.getApolloMasterName());
            namespaceReleaseDTO.setReleaseTitle("Modify Token Server Config ");
            ApolloOpenApiClientHolder.getApolloOpenApiClient().publishNamespace(sentinelAppId, projectEnv,
                    "default",
                    appName, namespaceReleaseDTO);
            log.info("Token Server 地址修改成功，appName:" + appName);

        } catch (Exception e) {
            e.printStackTrace();
            log.info("Token Server 地址修改失败，appName:" + appName);
        }

    }
}
