package com.dailyUpdate.aspect;
import com.dailyUpdate.annotation.CustomizedRedisCache;
import com.dailyUpdate.conf.ApplicationContextProvider;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.NullValue;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 切面
 * AspectJProxyFactory可以通过解析 @Aspect 标注的类来生成代理aop代理对象
 */
@Aspect
@Component
public class CustomizedRedisCacheAspect {
    /**
     * 用于 spEL 表达式解析
     */
    private SpelExpressionParser parser = new SpelExpressionParser();

    /**
     * 用于获取方法参数定义名字
     */
    private DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    private boolean allowNullValues = false;


    /**
     * 命名切点
     * 定义切点：在带有 @CustomizedRedisCache 的注解时都可以使用
     */
    @Pointcut(value = "@annotation(com.unknownproject.annotation.CustomizedRedisCache)")
    public void redisCache(){
    }


    /**
     * 环绕增强，对带注解 @CustomizedRedisCache 的方法进行切面，并获取到注解的属性值
     */
    @Around(value = "redisCache() && @annotation(cache)" , argNames = "joinPoint,cache")   // @annotation(cache) 参数跟下面 cache 对应
    public Object aroundCacheable(ProceedingJoinPoint joinPoint, CustomizedRedisCache cache) throws Throwable {

        try {
            String key = "";

            if (!cache.key().contains("#")) {
                //注解中的值非spEL表达式，直接解析
                key = cache.key();
            } else {
                //注解的值为spEL
                String spEL = cache.key();
                //使用  方法解析注解的实际值
                key = generateKeyBySpEL(spEL, joinPoint);
            }

            RedissonClient redissonClient = ApplicationContextProvider.getBean(RedissonClient.class);
            RBucket<Object> rBucket = redissonClient.getBucket(key, CustomRedissonCode.INSTANCE);
            Object value = rBucket.get();
            if (value == null) {
                //key不存在
                value = joinPoint.proceed();
                if (!allowNullValues && value == null) {
                    //不缓存null
                    return null;
                }
                value = toStoreValue(value);
                rBucket.set(value);
                rBucket.expire(cache.expireTime(), TimeUnit.SECONDS);
            }
            return fromStoreValue(value);

        } catch (Exception e) {
            Exception e2 = new Exception("查询不到缓存异常");
            e.printStackTrace();
            e2.printStackTrace();

        }
        return null;
    }


    public String generateKeyBySpEL(String spElString,ProceedingJoinPoint joinPoint){

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        //获取所有的参数
        String[] parameterNames = nameDiscoverer.getParameterNames(methodSignature.getMethod());
        Expression expression = parser.parseExpression(spElString);
        EvaluationContext context = new StandardEvaluationContext();
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            context.setVariable(parameterNames[i],args[i]);
        }
        return expression.getValue(context).toString();
    }


    protected Object fromStoreValue(Object storeValue){
        if(storeValue instanceof NullValue){
            return null;
        }
        return storeValue;
    }

    protected Object toStoreValue(Object userValue){
        if(userValue == null){
            return NullValue.INSTANCE;
        }
        return userValue;
    }



}
