 ## SSM（二）-- Shiro 的整合

#### 前言

Apache Shiro 是一个强大且易用的 Java 安全框架, 执行身份验证、授权、密码学和会话管理。使用 Shiro 的易于理解的 API, 您可以快速、轻松地获得任何应用程序, 从最小的移动应用程序到最大的网络和企业应用程序。此篇只是我关于 `SSM` 和 `Shiro` 的整合，如果要详细的介绍 `Shiro` ，请移步到下面的地址，有我详细的介绍：[Shiro 介绍](https://github.com/volewu/JAVA-Learn/blob/master/%E7%AC%94%E8%AE%B0/Shiro/Shiro.md#shiro124)

> 后面还会有关于 `SpringBoot` 和 `Shiro` 的整合

#### 导入相关 jar 包

在原项目的 `pom.xml` 导入 `jar`

```xml
		<shiro.version>1.2.4</shiro.version>

<!-- 添加shiro支持 -->
        <dependency>
            <groupId>org.apache.shiro</groupId>
            <artifactId>shiro-core</artifactId>
            <version>${shiro.version}</version>
        </dependency>

        <dependency>
            <groupId>org.apache.shiro</groupId>
            <artifactId>shiro-web</artifactId>
            <version>${shiro.version}</version>
        </dependency>

        <dependency>
            <groupId>org.apache.shiro</groupId>
            <artifactId>shiro-spring</artifactId>
            <version>${shiro.version}</version>
        </dependency>
```

#### 创建方法

在本次测试中，我使用了三个方式：

1. `getByUserName(String userName)`： 通过用户名查询用户
2. `getRoles(String userName)`：通过用户名查询角色信息
3. `getPermissions(String userName)`：通过用户名查询权限信息

下面是我对应的 `UserMappers.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.vole.dao.UserDao">

    <resultMap type="User" id="UserResult">
        <id column="id" property="id"/>
        <result column="userName" property="userName"/>
        <result column="password" property="password"/>
        <result property="roleId" column="roleId"/>
    </resultMap>

    <select id="getByUserName" parameterType="String" resultMap="UserResult">
        select * from t_user where userName=#{userName}
    </select>

    <select id="getRoles" parameterType="String" resultType="String">
        select r.roleName from t_user u,t_role r where u.roleId=r.id and u.userName=#{userName}
    </select>

    <select id="getPermissions" parameterType="String" resultType="String">
        select p.permissionName from t_user u,t_role r,t_permission p where u.roleId=r.id and p.roleId=r.id and u.userName=#{userName}
    </select>

</mapper>
```

#### 自定义 `MyRealm`

```java
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

```

#### `applicationContext.xml` 配置

这次显示的只是关于 `shiro` 部分配置，完成配置可以在我的 `GitHub` 上

```xml
 <!-- 自定义 Realm -->
    <bean id="myRealm" class="com.vole.realm.MyRealm"/>

    <!-- 安全管理器 -->
    <bean id="securityManager" class="org.apache.shiro.web.mgt.DefaultWebSecurityManager"
          p:realm-ref="myRealm"/>


    <!-- Shiro 过滤器 -->
    <bean id="shiroFilter" class="org.apache.shiro.spring.web.ShiroFilterFactoryBean">
        <!-- Shiro 的核心安全接口, 这个属性是必须的 -->
        <property name="securityManager" ref="securityManager"/>
        <!-- 身份认证失败，则跳转到登录页面的配置 -->
        <property name="loginUrl" value="/login"/>
        <!-- 权限认证失败，则跳转到指定页面 -->
        <property name="unauthorizedUrl" value="/unauthor"/>
        <!-- Shiro 连接约束配置, 即过滤链的定义 -->
        <property name="filterChainDefinitions">
            <value>
                /login=anon
                /admin/**=authc
                /student=roles[teacher]
                /teacher=perms["user:create"]
            </value>
        </property>
    </bean>

    <!-- 保证实现了 Shiro 内部 lifecycle 函数的 bean 执行 -->
    <bean id="lifecycleBeanPostProcessor"
          class="org.apache.shiro.spring.LifecycleBeanPostProcessor"/>

    <!-- 开启 Shiro 注解 -->
    <bean class="org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator"
          depends-on="lifecycleBeanPostProcessor"/>
    <bean class="org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor">
        <property name="securityManager" ref="securityManager"/>
    </bean>

```

`/login=anon`：表示不用用户认证，可以直接访问该请求

`/admin/**=authc`：表示该请求需要用户登入

`/student=roles[teacher]`： 表示该请求需要用户的角色为 `teacher`才能访问

`/teacher=perms["user:create"]`：表示该请求需要用户的权限为 `user:create`才能访问



#### `web.xml` 配置

此处也是部分配置

```xml
 <!-- shiro 过滤器定义 -->
    <filter>
        <filter-name>shiroFilter</filter-name>
        <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
        <init-param>
            <!-- 该值缺省为 false, 表示生命周期由 SpringApplicationContext 管理, 设置为 true 则表示由 ServletContainer 管理 -->
            <param-name>targetFilterLifecycle</param-name>
            <param-value>true</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>shiroFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
```

#### `UserController.java`

```java
package com.vole.controller;

import com.vole.entity.User;
import com.vole.service.UserService;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 编写者： vole
 * Time： 2018/7/24.16:38
 * 内容：User Controller 层
 */
@Controller
public class UserController {

    @Resource
    private UserService userService;

    @RequestMapping("/login")
    public Object login(User user, HttpServletRequest request) {
        Subject subject= SecurityUtils.getSubject();
        UsernamePasswordToken token=new UsernamePasswordToken(user.getUserName(), user.getPassword());
        try{
            subject.login(token);
            Session session=subject.getSession();
            System.out.println("sessionId:"+session.getId());
            System.out.println("sessionHost:"+session.getHost());
            System.out.println("sessionTimeout:"+session.getTimeout());
            session.setAttribute("info", "session的数据");
            return "redirect:/success.jsp";
        }catch(Exception e){
            e.printStackTrace();
            request.setAttribute("user", user);
            request.setAttribute("errorMsg", "用户名或密码错误！");
            return "index";
        }
    }

    @RequestMapping("/unauthor")
    @ResponseBody
    public String getUnauthor() {
        return "权限认证失败";
    }

    @RequestMapping("/admin/a")
    @ResponseBody
    public Object getAdmin() {
        return "Admin";
    }

    @RequestMapping("/student")
    @ResponseBody
    public Object getStudent() {
        return "student";
    }

    @RequestMapping("/teacher")
    @ResponseBody
    public Object getteacher() {
        return "teacher";
    }

    @RequestMapping("/getUser")
    @ResponseBody
    public User getUser() {
        return userService.getByUserName("gakki");
    }

    /**
     * 注销
     * @return 重定向到登入页面
     * @throws Exception e
     */
    @RequestMapping("/logout")
    public String logout() throws Exception {
        SecurityUtils.getSubject().logout();
        return "redirect:/login.jsp";
    }
}

```

#### `shiro` 标签库

```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
${info }
${currentUser.userName}
欢迎你!
<shiro:hasRole name="admin">
	欢迎有admin角色的用户！<shiro:principal/>
</shiro:hasRole>
<shiro:hasPermission name="student:create">
	欢迎有student:create权限的用户！<shiro:principal/>
</shiro:hasPermission>
</body>
</html>
```



#### 测试

下面是我的数据库测试数据：[db_shiro.sql](https://github.com/volewu/JAVA_NOTE-DEMO/blob/master/SSM/SSM-DEMO/db_blog.sql)

![db_shiro](https://github.com/volewu/JAVA_NOTE-DEMO/blob/master/SSM/SSM-DEMO/image/db_shiro.png?raw=true)

在这次测试中我使用的是 `postman` 来请求相关的路径，可以根据 `UserController` 里面的请求逐步测试，然后根据不同的用户和不同的路径，返回不一样的数据。

#### 总结

由于在本文得测试项，我并没做过多的介绍，也许本文只有我能看懂全部，文笔还需努力呀！

项目地址：[SSM](https://github.com/volewu/JAVA_NOTE-DEMO/tree/master/SSM/SSM-DEMO))