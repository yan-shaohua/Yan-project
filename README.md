# Yan-project

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
