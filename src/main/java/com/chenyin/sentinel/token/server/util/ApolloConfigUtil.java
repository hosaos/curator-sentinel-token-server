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
package com.chenyin.sentinel.token.server.util;


/**
 * @author chenyin
 */
public final class ApolloConfigUtil {
    /**
     * 流控规则id后缀
     */
    public static final String FLOW_DATA_ID = "sentinel-flow-rules";
    /**
     * 热点规则id后缀
     */
    public static final String PARAM_FLOW_DATA_ID = "sentinel-param-flow-rules";

    /**
     * token-server-cluster-map
     */
    public static final String TOKEN_SERVER_CLUSTER_MAP_DATA_ID = "token-server-cluster-map";

    /**
     * token-server-namespace-set
     */
    public static final String TOKEN_SERVER_NAMESPACE_DATA_ID = "token-server-namespace-set";


    public static final String TOKEN_SERVER_NAMESPACE_NAME = "token-server";

    /**
     * sentinel规则所在appId
     */
    public static final String SENTINEL_APP_ID = "sentinel";

    /**
     * apollo管理员名称
     */
    public static final String APOLLO_MASTER_NAME = "shenyi";

    /**
     * apollo portal url
     */
    public static final String APOLLO_PORTAL_URL = "http://apollo.ywwl.com";

    /**
     * apollo 第三方应用token
     */
    public static final String APOLLO_TOKEN = "423670ef5234331eaebae5a1ed9c379c00f3cef6";

    private ApolloConfigUtil() {
    }

    public static String getFlowDataId() {
        return FLOW_DATA_ID;
    }


    public static String getParamFlowDataId() {
        return PARAM_FLOW_DATA_ID;
    }


    public static String getSentinelAppId() {
        return SENTINEL_APP_ID;
    }

    public static String getTokenServerClusterMapDataId() {
        return TOKEN_SERVER_CLUSTER_MAP_DATA_ID;
    }


    public static String getTokenServerNamespaceDataId() {
        return TOKEN_SERVER_NAMESPACE_DATA_ID;
    }

    public static String getTokenServerNamespaceName() {
        return TOKEN_SERVER_NAMESPACE_NAME;
    }

    public static String getApolloMasterName() {
        return APOLLO_MASTER_NAME;
    }

    public static String getApolloPortalUrl() {
        return APOLLO_PORTAL_URL;
    }

    public static String getApolloToken() {
        return APOLLO_TOKEN;
    }
}
