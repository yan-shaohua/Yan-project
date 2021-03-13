<<<<<<< HEAD
# Yan-project
=======

###项目结构
    java
    |——com.
        |——annotation   自定义注解
        |       |—  xxx
        |——batch    批处理
        |    |—  xxxJob   线程池
        |    |—  task   任务
        |          |—  xxxTask
        |——config    配置中心
        |     |—  xxxConfig
        |——controller
        |     |—  xxxController
        |——service
        |     |—  xxxService
        |     |—  impl
        |          |—  xxxRepositoryImpl
        |——dao
        |   |—  xxxDatabasesName  --  父接口
        |   |—  xxxRepository  --  子接口
        |——entity
        |     |—  DatabaseName
        |——utils
        |     |—  xxxUtils  --  字典类
    resources
        |——  application.properties
        |——  application-dev.properties
        |——  application-test.properties
        |——  application-prod.properties
        |——  log4j2.xml
        |——  redisson.yml  
        

    ElasticSearch
        解析数据：localhost:9090/parse/java
        查询数据：localhost:9090/searchHighlight/java


    jetcache缓存
        1.添加jetcache依赖
            <dependency>
                <groupId>com.alicp.jetcache</groupId>
                <artifactId>jetcache-starter-redis-lettuce</artifactId>
                <version>2.5.14</version>
            </dependency>

        2.配置扫描包：
            @EnableMethodCache(basePackages="com.unknownproject.service") //开启 Cache注解
            @EnableCreateCacheAnnotation   //启用createCache注解

        3.在方法上添加缓存
            @Cached(name = SEARCH_HIGHLIGHT,
                    expire = 300,
                    cacheType = CacheType.LOCAL,    //缓存到本地
                    key = "args[0] + args[1] + args[2] ")

    Aop切面缓存  
        自定义注解 @CustomizedRedisCache  
            @CustomizedRedisCache(cacheName = "模块名", key = " '项目名:模块名:方法名:' + #参数1 +‘-’+ #参数2")
