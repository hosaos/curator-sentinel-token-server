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
package com.chenyin.sentinel.token.server.entity;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Eric Zhao
 * @since 1.4.1
 */
public class ClusterGroupEntity {

    private String machineId;
    private String ip;
    private Integer port;
    private Set<String> clientSet = new HashSet<>();

    private Double maxAllowedQps;

    public Double getMaxAllowedQps() {
        return maxAllowedQps;
    }

    public void setMaxAllowedQps(Double maxAllowedQps) {
        this.maxAllowedQps = maxAllowedQps;
    }

    public String getMachineId() {
        return machineId;
    }

    public ClusterGroupEntity setMachineId(String machineId) {
        this.machineId = machineId;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public ClusterGroupEntity setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public Integer getPort() {
        return port;
    }

    public ClusterGroupEntity setPort(Integer port) {
        this.port = port;
        return this;
    }

    public Set<String> getClientSet() {
        return clientSet;
    }

    public void setClientSet(Set<String> clientSet) {
        this.clientSet = clientSet;
    }

    @Override
    public String toString() {
        return "ClusterGroupEntity{" +
            "machineId='" + machineId + '\'' +
            ", ip='" + ip + '\'' +
            ", port=" + port +
            '}';
    }
}
