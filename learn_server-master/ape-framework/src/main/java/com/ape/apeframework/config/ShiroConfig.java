package com.ape.apeframework.config;

import com.ape.apeframework.filter.JwtFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);

        Map<String, String> filterMap = new LinkedHashMap<>();
        filterMap.put("/login", "anon");
        filterMap.put("/login/register", "anon");
        filterMap.put("/classification/getApeClassificationList", "anon");
        filterMap.put("/school/getApeSchoolList", "anon");
        filterMap.put("/major/getApeMajorList", "anon");
        filterMap.put("/user/setUserAvatar/**", "anon");
        filterMap.put("/common/**", "anon");
        filterMap.put("/img/**", "anon");
        filterMap.put("/video/**", "anon");
        filterMap.put("/file/**", "anon");
        //=======防止api文档被过滤掉
        filterMap.put("/**", "jwt");
        //jwt过滤
        Map<String, Filter> filters = new HashMap<>();
        filters.put("jwt", new JwtFilter());
        shiroFilter.setFilters(filters);
        shiroFilter.setFilterChainDefinitionMap(filterMap);

        return shiroFilter;
    }

    @Bean("securityManager")
    public DefaultWebSecurityManager securityManager(ShiroRealm shiroRealm, RedisProperties redisProperties) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(shiroRealm);
        //关闭shiro自带的session存放token功能
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        securityManager.setSubjectDAO(subjectDAO);
        //使用redis设置自定义缓存token
        securityManager.setCacheManager(redisCacheManager(redisProperties));
        return securityManager;
    }

    public RedisCacheManager redisCacheManager(RedisProperties redisProperties) {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager(redisProperties));
        //redis中针对不同用户缓存(此处的id需要对应user实体中的userId字段,用于唯一标识)
        redisCacheManager.setPrincipalIdFieldName("id");
        //用户权限信息缓存时间
        redisCacheManager.setExpire(200000);
        return redisCacheManager;
    }

    @Bean
    public RedisManager redisManager(RedisProperties redisProperties) {
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(redisProperties.getHost());
        redisManager.setPort(redisProperties.getPort());
        redisManager.setTimeout(0);
        if (!StringUtils.isEmpty(redisProperties.getPassword())) {
            redisManager.setPassword(redisProperties.getPassword());
        }
        return redisManager;
        //TODO 集群支持后续补充
    }

    @Bean("lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

}
