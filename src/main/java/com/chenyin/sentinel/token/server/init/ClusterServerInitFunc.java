/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chenyin.sentinel.token.server.init;

import com.alibaba.csp.sentinel.cluster.flow.rule.ClusterFlowRuleManager;
import com.alibaba.csp.sentinel.cluster.flow.rule.ClusterParamFlowRuleManager;
import com.alibaba.csp.sentinel.cluster.server.config.ClusterServerConfigManager;
import com.alibaba.csp.sentinel.cluster.server.config.ServerFlowConfig;
import com.alibaba.csp.sentinel.cluster.server.config.ServerTransportConfig;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.apollo.ApolloDataSource;
import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.chenyin.sentinel.token.server.parser.ClusterServerFlowConfigParser;
import com.chenyin.sentinel.token.server.parser.TokenServerTransportConfigParser;
import com.chenyin.sentinel.token.server.util.ApolloConfigUtil;

import java.util.List;
import java.util.Set;

/**
 * @author chenyin
 */
public class ClusterServerInitFunc implements InitFunc {
    private String defaultRules = "[]";

    @Override
    public void init() throws Exception {
        String tokenServerNamespace = ApolloConfigUtil.getTokenServerNamespaceName();
        //监听特定namespace下的集群限流规则
        initPropertySupplier();
        // 设置tokenServer管辖的作用域(即管理哪些应用)
        initTokenServerNameSpaces(tokenServerNamespace);

        // Server transport configuration data source.
        initServerTransportConfig(tokenServerNamespace);
        // 初始化最大qps
        initServerFlowConfig(tokenServerNamespace);

    }

    private void initPropertySupplier() {
        // Register cluster flow rule property supplier which creates data source by namespace.
        ClusterFlowRuleManager.setPropertySupplier(namespace -> {
            ReadableDataSource<String, List<FlowRule>> ds = new ApolloDataSource<>(namespace,
                    ApolloConfigUtil.getFlowDataId(), defaultRules, source -> JSON.parseObject(source,
                    new TypeReference<List<FlowRule>>() {
                    }));
            return ds.getProperty();
        });

        // Register cluster parameter flow rule property supplier.
        ClusterParamFlowRuleManager.setPropertySupplier(namespace -> {
            ReadableDataSource<String, List<ParamFlowRule>> ds = new ApolloDataSource<>(namespace,
                    ApolloConfigUtil.getParamFlowDataId(), defaultRules, source -> JSON.parseObject(source,
                    new TypeReference<List<ParamFlowRule>>() {
                    }));

            return ds.getProperty();
        });
    }

    private void initTokenServerNameSpaces(String tokenServerNamespace) {
        ReadableDataSource<String, Set<String>> namespaceDs = new ApolloDataSource<>(tokenServerNamespace,
                ApolloConfigUtil.getTokenServerNamespaceDataId(), defaultRules, source -> JSON.parseObject(source,
                new TypeReference<Set<String>>() {
                }));

        ClusterServerConfigManager.registerNamespaceSetProperty(namespaceDs.getProperty());
    }

    private void initServerTransportConfig(String tokenServerNamespace) {
        ReadableDataSource<String, ServerTransportConfig> serverTransportDs =
                new ApolloDataSource<>(tokenServerNamespace,
                        ApolloConfigUtil.getTokenServerClusterMapDataId(), defaultRules,
                        new TokenServerTransportConfigParser());
        ClusterServerConfigManager.registerServerTransportProperty(serverTransportDs.getProperty());

    }

    private void initServerFlowConfig(String tokenServerNamespace) {
        ClusterServerFlowConfigParser serverFlowConfigParser = new ClusterServerFlowConfigParser();
        ReadableDataSource<String, ServerFlowConfig> serverFlowConfigDs = new ApolloDataSource<>(tokenServerNamespace,
                ApolloConfigUtil.getTokenServerClusterMapDataId(), defaultRules, s -> {
            ServerFlowConfig config = serverFlowConfigParser.convert(s);
            if (config != null) {
                ClusterServerConfigManager.loadGlobalFlowConfig(config);
            }
            return config;
        });
    }

}
