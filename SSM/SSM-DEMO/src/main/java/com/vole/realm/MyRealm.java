package com.vole.realm;

import com.vole.entity.User;
import com.vole.service.UserService;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import javax.annotation.Resource;

/**
 * 编写者： vole
 * Time： 2018/7/27.16:27
 * 内容：MyRealm
 */
public class MyRealm extends AuthorizingRealm {

    @Resource
    private UserService userService;

    /**
     * 为当限前登录的用户授予角色和权
     */
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String userName = (String) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.setRoles(userService.getRoles(userName));
        authorizationInfo.setStringPermissions(userService.getPermissions(userName));
        return authorizationInfo;
    }

    /**
     * 验证当前登录的用户
     */
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String userName = (String) token.getPrincipal();
        User user = userService.getByUserName(userName);
        if (user != null) {
            // 把当前用户信息存到 session 中
            SecurityUtils.getSubject().getSession().setAttribute("currentUser", user);
            return new SimpleAuthenticationInfo(user.getUserName(), user.getPassword(), "xxx");
        } else
            return null;
    }
}
