# 基于curator、apollo实现的sentinel token server集群实现

## 如何启动？

1. 修改ApolloConfigUtil.APOLLO_PORTAL_URL、APOLLO_TOKEN、APOLLO_MASTER_NAME为自己的Apollo配置

2. 修改application-rd.properties中的apollo.meta及zk连接地址

3. 启动MainApplication


## 实现关键点

1. 不同业务应用(使用apollo中appId当做sentinel中projectName，即控制台左侧名称)

2. 所有业务应用限流规则存储在同一个appId=sentinel的Apollo项目中，不同业务的限流规则存储在不同namespace中，tokenServer单独分配一个namespace(名称为token-server)
如图所示，其中sentinel-test所在namespace为模拟的业务应用appId

![在这里插入图片描述](https://img-blog.csdnimg.cn/2019082415093138.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2hvc2Fvcw==,size_16,color_FFFFFF,t_70)

3. 业务应用根据自己的nameSpace中的token-server-cluster-map对应的cluster配置，来设置连接的token-server地址

4. 独立部署的token-server根据 apollo中token-server命名空间下的token-server-namespace-set来设置监听需要配置集群限流规则的nameSpace

5. token Server抢主成功后，修改不同业务应用nameSpace中token-server-cluster-map对应的cluster配置，将其token Server的ip、端口改为当前机器的ip、端口

6. 因为token Server抢主成功后，apollo配置修改存在失败的可能性，TokenServerBootstrap启动线程，定时自检并推送修改

7、客户端通过监听apollo cluster配置的修改，来达到token server挂掉后的重新连接


## 不足之处

1. Master挂掉到重新选举出一个Master的期间，会有一小段时间，业务应用无法达到集群限流的效果，退化为本地单机限流

2. Master抢主成功后，会将所有业务应用的token Server地址指向自己，如果能按业务区分不同的tokenServer Master会更好
