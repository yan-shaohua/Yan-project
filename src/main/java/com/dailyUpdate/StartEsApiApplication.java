package com.dailyUpdate;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableMethodCache(basePackages="com.unknownproject.service") //开启 Cache注解
@EnableCreateCacheAnnotation   //启用createCache注解
public class StartEsApiApplication implements WebMvcConfigurer {

	private static final Logger logger = LogManager.getLogger(StartEsApiApplication.class);

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(StartEsApiApplication.class, args);

		String[] activeProfiles = context.getEnvironment().getActiveProfiles();
		for (String profile : activeProfiles) {
			logger.info("程序启动使用profile:{}",profile);
		}
	}




}
