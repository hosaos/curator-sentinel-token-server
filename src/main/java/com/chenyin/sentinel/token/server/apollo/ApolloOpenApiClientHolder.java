package com.chenyin.sentinel.token.server.apollo;

import com.ctrip.framework.apollo.openapi.client.ApolloOpenApiClient;
import com.chenyin.sentinel.token.server.util.ApolloConfigUtil;

/**
 * @author: chenyin
 * @date: 2019-08-22 19:22
 */
public class ApolloOpenApiClientHolder {
    private static final ApolloOpenApiClient APOLLO_OPEN_API_CLIENT = ApolloOpenApiClient.newBuilder()
            .withPortalUrl(ApolloConfigUtil.getApolloPortalUrl())
            .withToken(ApolloConfigUtil.getApolloToken())
            .build();

    public static ApolloOpenApiClient getApolloOpenApiClient() {
        return APOLLO_OPEN_API_CLIENT;
    }
}
