# Spring Boot 整合 Shiro-登录认证和权限管理

<!-- TOC -->

- [Spring Boot 整合 Shiro-登录认证和权限管理](#spring-boot-%e6%95%b4%e5%90%88-shiro-%e7%99%bb%e5%bd%95%e8%ae%a4%e8%af%81%e5%92%8c%e6%9d%83%e9%99%90%e7%ae%a1%e7%90%86)
  - [Apache Shiro](#apache-shiro)
  - [实现方案](#%e5%ae%9e%e7%8e%b0%e6%96%b9%e6%a1%88)
    - [pom包依赖](#pom%e5%8c%85%e4%be%9d%e8%b5%96)
    - [RBAC](#rbac)
    - [Shiro配置](#shiro%e9%85%8d%e7%bd%ae)
      - [ShiroConfig](#shiroconfig)
      - [登录认证实现](#%e7%99%bb%e5%bd%95%e8%ae%a4%e8%af%81%e5%ae%9e%e7%8e%b0)
      - [链接权限的实现](#%e9%93%be%e6%8e%a5%e6%9d%83%e9%99%90%e7%9a%84%e5%ae%9e%e7%8e%b0)
      - [登录实现](#%e7%99%bb%e5%bd%95%e5%ae%9e%e7%8e%b0)
      - [前后端分离](#%e5%89%8d%e5%90%8e%e7%ab%af%e5%88%86%e7%a6%bb)
      - [源码](#%e6%ba%90%e7%a0%81)
      - [参考链接](#%e5%8f%82%e8%80%83%e9%93%be%e6%8e%a5)

<!-- /TOC -->

## Apache Shiro
What is Apache Shiro?
Apache Shiro 是一个功能强大、灵活的，开源的安全框架。它可以干净利落地处理身份验证、授权、企业会话管理和加密。

Apache Shiro 的首要目标是易于使用和理解。安全通常很复杂，甚至让人感到很痛苦，但是 Shiro 却不是这样子的。一个好的安全框架应该屏蔽复杂性，向外暴露简单、直观的 API，来简化开发人员实现应用程序安全所花费的时间和精力。

Shiro 能做什么呢？

- 验证用户身份
- 用户访问权限控制，比如：1、判断用户是否分配了一定的安全角色。2、判断用户是否被授予完成某个操作的权限
- 在非 Web 或 EJB 容器的环境下可以任意使用 Session API
- 可以响应认证、访问控制，或者 Session 生命周期中发生的事件
- 可将一个或以上用户安全数据源数据组合成一个复合的用户 “view”(视图)
- 支持单点登录(SSO)功能
- 支持提供“Remember Me”服务，获取用户关联信息而无需登录...
等等——都集成到一个有凝聚力的易于使用的 API。

Shiro 致力在所有应用环境下实现上述功能，小到命令行应用程序，大到企业应用中，而且不需要借助第三方框架、容器、应用服务器等。当然 Shiro 的目的是尽量的融入到这样的应用环境中去，但也可以在它们之外的任何环境下开箱即用。

## 实现方案
### pom包依赖
```
        <dependency>
            <groupId>org.apache.shiro</groupId>
            <artifactId>shiro-web</artifactId>
            <version>1.4.0</version>
        </dependency>
        <dependency>
            <groupId>org.apache.shiro</groupId>
            <artifactId>shiro-spring</artifactId>
            <version>1.4.0</version>
        </dependency>
```

### RBAC
RBAC 是基于角色的访问控制（Role-Based Access Control ）在 RBAC 中，权限与角色相关联，用户通过成为适当角色的成员而得到这些角色的权限。这就极大地简化了权限的管理。这样管理都是层级相互依赖的，权限赋予给角色，而把角色又赋予用户，这样的权限设计清楚，管理起来方便。

用户信息
```
CREATE TABLE `user_info` (
  `uid` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `username` varchar(60) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '账号',
  `name` varchar(60) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '名称（昵称或者真实姓名，不同系统不同定义）',
  `password` varchar(60) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '密码',
  `salt` varchar(60) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '加密密码的盐',
  `state` varchar(11) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '用户状态,0:创建未认证（比如没有激活，没有输入验证码等等）--等待验证的用户 , 1:正常状态,2：用户被锁定.',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `operate_date` datetime DEFAULT NULL COMMENT '操作时间',
  `creator` varchar(40) DEFAULT NULL COMMENT '创建用户',
  `operator` varchar(40) DEFAULT NULL COMMENT '操作用户',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='用户信息表';

```

角色信息
```
CREATE TABLE `sys_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `role` varchar(60) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '角色标识程序中判断使用,如"admin",这个是唯一的:',
  `description` varchar(60) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '角色描述,UI界面显示使用',
  `available` varchar(11) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '是否可用,如果不可用将不会添加给用户',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `operate_date` datetime DEFAULT NULL COMMENT '操作时间',
  `creator` varchar(40) DEFAULT NULL COMMENT '创建用户',
  `operator` varchar(40) DEFAULT NULL COMMENT '操作用户',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `role` (`role`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='角色表';

```

权限信息
```
CREATE TABLE `sys_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(60) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '名称',
  `resource_type` varchar(60) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '资源类型，[menu|button]',
  `url` varchar(60) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '资源路径',
  `permission` varchar(60) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '权限字符串,menu例子：role:*，button例子：role:create,role:update,role:delete,role:view',
  `parent_id` varchar(60) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '父编号',
  `parent_ids` varchar(60) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '父编号列表',
  `available` varchar(11) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '是否可用',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `operate_date` datetime DEFAULT NULL COMMENT '操作时间',
  `creator` varchar(40) DEFAULT NULL COMMENT '创建用户',
  `operator` varchar(40) DEFAULT NULL COMMENT '操作用户',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='权限表';

```

用户角色表
```
CREATE TABLE `sys_user_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `uid` int(11) NOT NULL COMMENT '用户编号',
  `role_id` int(11) NOT NULL COMMENT '角色编号',
  PRIMARY KEY (`id`),
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='用户角色表';

```

角色权限表
```
CREATE TABLE `sys_role_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `permission_id` int(11) NOT NULL COMMENT '角色权限',
  `role_id` int(11) NOT NULL COMMENT '角色编号',
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='角色权限表';

```

user_info（用户信息表）、sys_role（角色表）、sys_permission（权限表）、sys_user_role（用户角色表）、sys_role_permission（角色权限表）五张表，为了方便测试我们给这五张表插入一些初始化数据：

```
INSERT INTO `user_info` (`uid`,`username`,`name`,`password`,`salt`,`state`) VALUES ('1', 'admin', '管理员', 'd3c59d25033dbf980d29554025c23a75', '8d78869f470951332959580424d4bf4f', 0);
INSERT INTO `sys_permission` (`id`,`available`,`name`,`parent_id`,`parent_ids`,`permission`,`resource_type`,`url`) VALUES (1,0,'用户管理',0,'0/','userInfo:view','menu','userInfo/userList');
INSERT INTO `sys_permission` (`id`,`available`,`name`,`parent_id`,`parent_ids`,`permission`,`resource_type`,`url`) VALUES (2,0,'用户添加',1,'0/1','userInfo:add','button','userInfo/userAdd');
INSERT INTO `sys_permission` (`id`,`available`,`name`,`parent_id`,`parent_ids`,`permission`,`resource_type`,`url`) VALUES (3,0,'用户删除',1,'0/1','userInfo:del','button','userInfo/userDel');
INSERT INTO `sys_role` (`id`,`available`,`description`,`role`) VALUES (1,0,'管理员','admin');
INSERT INTO `sys_role` (`id`,`available`,`description`,`role`) VALUES (2,0,'VIP会员','vip');
INSERT INTO `sys_role` (`id`,`available`,`description`,`role`) VALUES (3,1,'test','test');
INSERT INTO `sys_role_permission` VALUES ('1', '1');
INSERT INTO `sys_role_permission` (`permission_id`,`role_id`) VALUES (1,1);
INSERT INTO `sys_role_permission` (`permission_id`,`role_id`) VALUES (2,1);
INSERT INTO `sys_role_permission` (`permission_id`,`role_id`) VALUES (3,2);
INSERT INTO `sys_user_role` (`role_id`,`uid`) VALUES (1,1)
```
### Shiro配置

首先要配置的是 ShiroConfig 类，Apache Shiro 核心通过 Filter 来实现，就好像 SpringMvc 通过 DispachServlet 来主控制一样。 既然是使用 Filter 一般也就能猜到，是通过 URL 规则来进行过滤和权限校验，所以我们需要定义一系列关于 URL 的规则和访问权限。

#### ShiroConfig
```

@Configuration
public class ShiroConfig {
	@Bean
	public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
    ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager());

        //拦截器.
        Map<String,String> filterChainDefinitionMap = new LinkedHashMap<>();

        // 配置不会被拦截的链接 顺序判断
        //<!-- anon:所有url都可以匿名访问-->
        filterChainDefinitionMap.put("/docs.html", "anon");
        filterChainDefinitionMap.put("/user/portalUser/login", "anon");

        //配置退出 过滤器,其中的具体的退出代码Shiro已经替我们实现了
        filterChainDefinitionMap.put("/logout", "logout");

        //<!-- 过滤链定义，从上向下顺序执行，一般将/**放在最为下边 -->
        //<!-- authc:所有url都必须认证通过才可以访问-->
        /*
         * anon：匿名用户可访问
         * authc：认证用户可访问
         * user：使用rememberMe可访问
         * perms：对应权限可访问
         * role：对应角色权限可访问
         **/
        filterChainDefinitionMap.put("/thinktankExpert/**", "anon");

        // 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
        shiroFilterFactoryBean.setLoginUrl("/admin/test");

        // 登录成功后要跳转的链接
        shiroFilterFactoryBean.setSuccessUrl("/docs.html");

        // 未授权界面
        shiroFilterFactoryBean.setUnauthorizedUrl("/403");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        return shiroFilterFactoryBean;
	}

	@Bean
	public MyShiroRealm myShiroRealm(){
		MyShiroRealm myShiroRealm = new MyShiroRealm();
		return myShiroRealm;
	}


	@Bean
	public SecurityManager securityManager(){
		DefaultWebSecurityManager securityManager =  new DefaultWebSecurityManager();
		securityManager.setRealm(myShiroRealm());
		return securityManager;
	}

    /**
    * 开启shiro aop注解支持.
    *
    * @param securityManager
    * @return
    */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
    
}
```

Filter Chain 定义说明：
- 1、一个URL可以配置多个 Filter，使用逗号分隔
- 2、当设置多个过滤器时，全部验证通过，才视为通过
- 3、部分过滤器可指定参数，如 perms，roles

- anon:所有 url 都都可以匿名访问
- authc: 需要认证才能进行访问
- user:配置记住我或认证通过可以访问


#### 登录认证实现
在认证、授权内部实现机制中都有提到，最终处理都将交给Real进行处理。因为在 Shiro 中，最终是通过 Realm 来获取应用程序中的用户、角色及权限信息的。通常情况下，在 Realm 中会直接从我们的数据源中获取 Shiro 需要的验证信息。可以说，Realm 是专用于安全框架的 DAO. Shiro 的认证过程最终会交由 Realm 执行，这时会调用 Realm 的getAuthenticationInfo(token)方法。

该方法主要执行以下操作:

- 1、检查提交的进行认证的令牌信息
- 2、根据令牌信息从数据源(通常为数据库)中获取用户信息
- 3、对用户信息进行匹配验证。
- 4、验证通过将返回一个封装了用户信息的AuthenticationInfo实例。
- 5、验证失败则抛出AuthenticationException异常信息。
而在我们的应用程序中要做的就是自定义一个 Realm 类，继承AuthorizingRealm 抽象类，重载 doGetAuthenticationInfo()，重写获取用户信息的方法。

doGetAuthenticationInfo 的重写

```
@Override
protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
        throws AuthenticationException {
    System.out.println("MyShiroRealm.doGetAuthenticationInfo()");
    //获取用户的输入的账号.
    String username = (String)token.getPrincipal();
    System.out.println(token.getCredentials());
    //通过username从数据库中查找 User对象
    //实际项目中，这里可以根据实际情况做缓存，如果不做，Shiro自己也是有时间间隔机制，2分钟内不会重复执行该方法
    UserInfo userInfo = userInfoService.findByUsername(username);
    System.out.println("----->>userInfo="+userInfo);
    if(userInfo == null){
        return null;
    }
    SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
            userInfo, //用户名
            userInfo.getPassword(), //密码
            ByteSource.Util.bytes(userInfo.getCredentialsSalt()),//salt=username+salt
            getName()  //realm name
    );
    return authenticationInfo;
}
```
#### 链接权限的实现

Shiro 的权限授权是通过继承AuthorizingRealm抽象类，重载doGetAuthorizationInfo();当访问到页面的时候，链接配置了相应的权限或者 Shiro 标签才会执行此方法否则不会执行，所以如果只是简单的身份认证没有权限的控制的话，那么这个方法可以不进行实现，直接返回 null 即可。在这个方法中主要是使用类：SimpleAuthorizationInfo进行角色的添加和权限的添加。

```
@Override
protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
    System.out.println("权限配置-->MyShiroRealm.doGetAuthorizationInfo()");
    SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
    UserInfo userInfo  = (UserInfo)principals.getPrimaryPrincipal();
    for(SysRole role:userInfo.getRoleList()){
        authorizationInfo.addRole(role.getRole());
        for(SysPermission p:role.getPermissions()){
            authorizationInfo.addStringPermission(p.getPermission());
        }
    }
    return authorizationInfo;

```

当然也可以添加 set 集合：roles 是从数据库查询的当前用户的角色，stringPermissions 是从数据库查询的当前用户对应的权限
```
authorizationInfo.setRoles(roles);
authorizationInfo.setStringPermissions(stringPermissions);
```

就是说如果在shiro配置文件中添加了filterChainDefinitionMap.put(“/add”, “perms[权限添加]”);就说明访问/add这个链接必须要有“权限添加”这个权限才可以访问，如果在shiro配置文件中添加了filterChainDefinitionMap.put(“/add”, “roles[100002]，perms[权限添加]”);就说明访问/add这个链接必须要有“权限添加”这个权限和具有“100002”这个角色才可以访问。

#### 登录实现

登录过程其实只是处理异常的相关信息，具体的登录验证交给 Shiro 来处理。

```
@RequestMapping("/login")
public String login(HttpServletRequest request, Map<String, Object> map) throws Exception{
    System.out.println("HomeController.login()");
    // 登录失败从request中获取shiro处理的异常信息。
    // shiroLoginFailure:就是shiro异常类的全类名.
    String exception = (String) request.getAttribute("shiroLoginFailure");
    System.out.println("exception=" + exception);
    String msg = "";
    if (exception != null) {
        if (UnknownAccountException.class.getName().equals(exception)) {
            System.out.println("UnknownAccountException -- > 账号不存在：");
            msg = "UnknownAccountException -- > 账号不存在：";
        } else if (IncorrectCredentialsException.class.getName().equals(exception)) {
            System.out.println("IncorrectCredentialsException -- > 密码不正确：");
            msg = "IncorrectCredentialsException -- > 密码不正确：";
        } else if ("kaptchaValidateFailed".equals(exception)) {
            System.out.println("kaptchaValidateFailed -- > 验证码错误");
            msg = "kaptchaValidateFailed -- > 验证码错误";
        } else {
            msg = "else >> "+exception;
            System.out.println("else -- >" + exception);
        }
    }
    map.put("msg", msg);
    // 此方法不处理登录成功,由shiro进行处理
    return "/login";
}
```


#### 前后端分离
由于现在前后端分离是开发趋势，我们在上面的方法中也没把前后端分离出来，所以接下来我们试试如何将前后端拆开来实现shiro。

在配置文件ShiroConfig中,如果是权限不足，默认是跳转到某个页面中。

```
// 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
shiroFilterFactoryBean.setLoginUrl("/admin/test");

// 登录成功后要跳转的链接
shiroFilterFactoryBean.setSuccessUrl("/docs.html");

// 未授权界面
shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorized");****
```
但是现在页面都是由前端来控制展示，所以，在没有权限的时候，我们只需要返回不同的状态码给前端就可以了。

如果是登录认证，那我们先自定义一个过滤器,继承shiro的登录认证过滤器：

```
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class MyAuthenticationFilter  extends FormAuthenticationFilter {
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response)
            throws Exception {
        WebUtils.toHttp(response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }
}
```
然后在配置文件中将这个过滤器放在配置map中
```
Map<String, Filter> filterMap = shiroFilterFactoryBean.getFilters();
        filterMap.put("authc", new MyAuthenticationFilter());
```

同样的如果需要自定义权限的过滤器,则继承自shiro的权限过滤器。

```
import org.apache.shiro.web.filter.authz.RolesAuthorizationFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MyPermissionFilter extends RolesAuthorizationFilter {

    @Override
    public boolean onAccessDenied(ServletRequest request, ServletResponse response, Object mappedValue) throws IOException {
        WebUtils.toHttp(response).sendError(HttpServletResponse.SC_FORBIDDEN);
        return false;
    }
}
```
再将这个过滤器配置到配置文件中就好了。

这是完整的配置文件 ShiroConfig

```
package org.gdjz.config;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.gdjz.shiro.Filter.MyAuthenticationFilter;
import org.gdjz.shiro.Filter.MyPermissionFilter;
import org.gdjz.shiro.MyRealm;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * shiro 配置
 *
 * @author StevenGuo
 * @date 17:12 2019/6/14
 **/
@Configuration
public class ShiroConfig {

    @Bean
    HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("MD5");
        hashedCredentialsMatcher.setHashIterations(1);
        return hashedCredentialsMatcher;
    }

    @Bean
    MyRealm myRealm() {
        MyRealm myRealm = new MyRealm();
        myRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return myRealm;
    }

    @Bean
    DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        manager.setRealm(myRealm());
        return manager;
    }

    @Bean
    ShiroFilterFactoryBean shiroFilterFactoryBean() {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager());

        Map<String, Filter> filterMap = shiroFilterFactoryBean.getFilters();
        filterMap.put("roles", new MyPermissionFilter());
        filterMap.put("authc", new MyAuthenticationFilter());

        //拦截器.
        Map<String,String> filterChainDefinitionMap = new LinkedHashMap<>();

        // 配置不会被拦截的链接 顺序判断
        //<!-- anon:所有url都可以匿名访问-->
        filterChainDefinitionMap.put("/docs.html", "anon");
        filterChainDefinitionMap.put("/back/user/backUser/login", "anon");
        filterChainDefinitionMap.put("/back/user/backUser/getVerifyCode", "anon");

        //配置退出 过滤器,其中的具体的退出代码Shiro已经替我们实现了
        filterChainDefinitionMap.put("/back/user/backUser/logout", "logout");

        //<!-- 过滤链定义，从上向下顺序执行，一般将/**放在最为下边 -->
        //<!-- authc:所有url都必须认证通过才可以访问-->
        /*
         * anon：匿名用户可访问
         * authc：认证用户可访问
         * user：使用rememberMe可访问
         * perms：对应权限可访问
         * roles：对应角色权限可访问
         **/

        filterChainDefinitionMap.put("/back/overview/**", "roles[overview_role]");

        filterChainDefinitionMap.put("/back/**", "authc");

        // 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
//        shiroFilterFactoryBean.setLoginUrl("/admin/test");

        // 登录成功后要跳转的链接
//        shiroFilterFactoryBean.setSuccessUrl("/docs.html");

        // 未授权界面
//        shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorized");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        return shiroFilterFactoryBean;
    }

    /**
     * 开启shiro aop注解支持.
     *
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    @Bean(name = "simpleMappingExceptionResolver")
    public SimpleMappingExceptionResolver
    createSimpleMappingExceptionResolver() {
        SimpleMappingExceptionResolver r = new SimpleMappingExceptionResolver();
        Properties mappings = new Properties();
        mappings.setProperty("DatabaseException", "databaseError");
        mappings.setProperty("UnauthorizedException", "403");
        r.setExceptionMappings(mappings);
        r.setDefaultErrorView("error");
        r.setExceptionAttribute("ex");
        return r;
    }

}

```

#### 源码
在配置权限的过滤器时，重写的是shiro权限过滤器里面的方法，后来在看shiro官网的源码时，才知道，如果是失败，就直接使用onAccessDenied方法就可以了。具体看参考链接第二条。

#### 参考链接

1. extends RolesAuthorizationFilter
https://www.cnblogs.com/javaxiaoxin/p/7424078.html

2. onAccessDenied官网源码解释 返回true和返回false的不同
http://shiro.apache.org/static/1.4.1/apidocs/org/apache/shiro/web/filter/authz/RolesAuthorizationFilter.html