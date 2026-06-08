package com.ape.apeframework.config;

import com.ape.apecommon.constant.Constants;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apecommon.utils.JwtUtil;
import com.ape.apeframework.custom.JwtToken;
import com.ape.apesystem.domain.ApeUser;
import com.ape.apesystem.service.ApeRoleMenuService;
import com.ape.apesystem.service.ApeUserRoleService;
import com.ape.apesystem.service.ApeUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ShiroRealm extends AuthorizingRealm {

    @Autowired
    private ApeUserService apeUserService;

    @Autowired
    private ApeUserRoleService apeUserRoleService;

    @Autowired
    private ApeRoleMenuService apeRoleMenuService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        //根据用户名从自己的数据库中获取role和permission信息
        ApeUser apeUser = null;
        String loginAccount = null;
        if (principals != null) {
            apeUser = (ApeUser) principals.getPrimaryPrincipal();
            loginAccount = apeUser.getLoginAccount();
        }
        // 设置用户拥有的角色集合，比如“admin,test”
        Set<String> roleSet = apeUserRoleService.getUserRolesSet(loginAccount);
        simpleAuthorizationInfo.setRoles(roleSet);
        for (String role : roleSet) {
            // 设置用户拥有的权限集合，比如“sys:role:add,sys:user:add”
            Set<String> menuSet = apeRoleMenuService.getRoleMenusSet(role);
            simpleAuthorizationInfo.addStringPermissions(menuSet);
        }
        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String accessToken = (String) token.getPrincipal();
        if (accessToken == null) {
            throw new AuthenticationException(ResultCode.COMMON_NO_TOKEN.getMessage());
        }
        // 校验token有效性
        ApeUser tokenEntity = this.checkUserTokenIsEffect(accessToken);
        return new SimpleAuthenticationInfo(tokenEntity, accessToken, getName());
    }

    public ApeUser checkUserTokenIsEffect(String token) throws AuthenticationException {
        // 解密获得username，用于和数据库进行对比
        String userId = JwtUtil.getUserId(token);
        if (userId == null) {
            throw new AuthenticationException(ResultCode.COMMON_TOKEN_ILLEGAL.getMessage());
        }

        // 查询用户信息
        ApeUser loginUser = apeUserService.getById(userId);
        if (loginUser == null) {
            throw new UnknownAccountException(ResultCode.COMMON_USER_NOT_EXIST.getMessage());
        }
        // 判断用户状态
        if (loginUser.getStatus() != 0) {
            throw new LockedAccountException(ResultCode.COMMON_ACCOUNT_LOCKED.getMessage());
        }
        // 校验token是否超时失效 & 或者账号密码是否错误
        if (!jwtTokenRefresh(token, userId, loginUser.getPassword())) {
            throw new IncorrectCredentialsException(ResultCode.COMMON_TOKEN_FAILURE.getMessage());
        }
        return loginUser;
    }

    public boolean jwtTokenRefresh(String token, String userId, String password) {
        //如果缓存中的token为空，直接返回失效异常
        String cacheToken = stringRedisTemplate.opsForValue().get(Constants.PREFIX_USER_TOKEN + userId);
        if (!StringUtils.isBlank(cacheToken)) {
            // 校验token有效性
            if (!JwtUtil.verify(cacheToken, userId, password)) {
                JwtUtil.sign(userId, password);
            }
            return true;
        }
        return false;
    }

    @Override
    public void clearCache(PrincipalCollection principals) {
        super.clearCache(principals);
    }

}
