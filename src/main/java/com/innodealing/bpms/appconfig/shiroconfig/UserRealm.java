package com.innodealing.bpms.appconfig.shiroconfig;

import com.innodealing.bpms.model.Role;
import com.innodealing.bpms.model.User;
import com.innodealing.bpms.service.PermissionService;
import com.innodealing.bpms.service.RoleService;
import com.innodealing.bpms.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cas.CasRealm;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
public class UserRealm extends CasRealm {

    private static final Logger logger = LoggerFactory.getLogger(UserRealm.class);

    private PermissionService permissionService;
    private RoleService roleService;

    public UserRealm(PermissionService permissionService, RoleService roleService){
        this.permissionService = permissionService;
        this.roleService = roleService;
    }
    /**
     * 获取授权信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String tid = (String) principals.getPrimaryPrincipal();
        Set<String> userPermissions = new HashSet();
        Set<String> userRoles = new HashSet();
        if (null != tid) {
            userPermissions  = permissionService.getPermissionsByUserId(tid);
            userRoles = roleService.findByUserId(tid);
        } else {
            throw new AuthorizationException();
        }
        //为当前用户设置角色和权限
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.addRoles(userRoles);
        authorizationInfo.addStringPermissions(userPermissions);
        logger.info("###【获取权限成功】[SessionId] => {}", SecurityUtils.getSubject().getSession().getId());
        return authorizationInfo;
    }

}
