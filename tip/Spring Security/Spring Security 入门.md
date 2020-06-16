[TOC]

# Spring Security 入门

![59025825_p0_master1200.jpg](https://i.loli.net/2020/04/01/xPpWseF2vqKuBto.jpg)

> 阅读本文需要的基础知识：
>
> - 熟练掌握Java
> - 掌握了Spring Boot基础知识

## 介绍



### 背景

Spring Security 最早不叫 Spring Security ，叫 Acegi Security，叫 Acegi Security 并不是说它和 Spring 就没有关系了，它依然是为 Spring 框架提供安全支持的。事实上，Java 领域的框架，很少有框架能够脱离 Spring 框架独立存在。

Acegi Security 基于 Spring，可以帮助我们为项目建立丰富的角色与权限管理，但是最广为人诟病的则是它臃肿繁琐的配置，这一问题最终也遗传给了 Spring Security。

在 Acegi Security 时代，网上流传一句话：“每当有人要使用 Acegi Security，就会有一个精灵死去。”足见 Acegi Security 的配置是多么可怕。

当 Acegi Security 投入 Spring 怀抱之后，先把这个名字改了，这就是大家所见到的 Spring Security 了，然后配置也得到了极大的简化。

但是和 Shiro 相比，人们对 Spring Security 的评价依然中重量级、配置繁琐。

直到有一天 Spring Boot 像谜一般出现在江湖边缘，彻底颠覆了 JavaEE 的世界。一人得道鸡犬升天，Spring Security 也因此飞上枝头变凤凰。

---



### 选型推荐

Spring Security是一个能够为基于Spring的企业应用系统提供声明式的安全访问控制解决方案的安全框架。

它提供了一组可以在Spring应用上下文中配置的Bean，充分利用了Spring IoC，DI（控制反转Inversion of Control ,DI:Dependency Injection 依赖注入）和AOP（面向切面编程）功能，为应用系统提供声明式的安全访问控制功能，减少了为企业系统安全控制编写大量重复代码的工作。

相对于 Shiro，在 SSM/SSH 中整合 Spring Security 都是比较麻烦的操作，所以，Spring Security 虽然功能比 Shiro 强大，但是使用反而没有 Shiro 多（Shiro 虽然功能没有 Spring Security 多，但是对于大部分项目而言，Shiro 也够用了）。

自从有了 Spring Boot 之后，Spring Boot 对于 Spring Security 提供了 自动化配置方案，可以零配置使用 Spring Security。

因此，一般来说，常见的安全管理技术栈的组合是这样的：

- SSM + Shiro
- Spring Boot/Spring Cloud + Spring Security

**注意，这只是一个推荐的组合而已，如果单纯从技术上来说，无论怎么组合，都是可以运行的。**

---



### Spring Security的相关结构

这里可以参考Spring Security的官方介绍文档：[spring-security-architecture](http://spring.io/guides/topicals/spring-security-architecture/)
 简单的来说：

- Spring Security是一个单一的`Filter`，其具体的类型是`FilterChainProxy`，其是作为`@Bean`在`ApplicationContext`中配置的。

- 从容器的角度来看，Spring Security是一个单一的Filter,但是在其中有很多额外的Filter,每一个都扮演着他们各自的角色，如下图所示：

  ![An AuthenticationManager hierarchy using ProviderManager](https://user-gold-cdn.xitu.io/2018/9/29/16623e483466e413?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

- Spring Security的身份验证，主要由`AuthenticationManager`这个接口完成，其验证的主要方法是`authenticate()`

```
public interface AuthenticationManager {   
  Authentication authenticate(Authentication authentication)   
    throws AuthenticationException;   
}
复制代码
```

- 该方法可以完成三件事：
  - 如果它可以验证输入代表一个有效的主体，就返回一个`Authentication`（通常包含 `authenticated=true`）
  - 如果它可以验证输入代表一个无效的主体，就throw一个`AuthenticationException`
  - 如果它不能决断，就返回`null`
- 最常用的`AuthicationManager`的实现是`ProviderManager`，它将其委托给`AuthticationProvider`这个实例，`AuthenticationProvider`和`AuthenticationManager`有一点像，但是含有一些额外的方法，来允许调用者来查询是否支持该`Authenticaion`形式。

```
public interface AuthenticationProvider {   
	Authentication authenticate(Authentication authentication)   
			throws AuthenticationException;   
   
	boolean supports(Class<?> authentication);   
}
复制代码
```

`supports()`方法中的`Class`参数是`Class`,它只会询问其是否支持传递给`authenticate()`方法。

- 在同一个程序中，一个`ProviderManager`通过委托一系列的`AuthenticaitonProviders`，以此来支支持多个不同的认证机制，如果`ProviderManager`无法识别一个特定的`Authentication`实例类型，则会跳过它。

- 很多时候，一个程序含有多个资源保护逻辑组，每一个组都有他们独有的`AuthenticationManager`，通常他们共享父级，那么父级就成为了了一个`"global"资源`，作为所有`provider`的后背。

  ![An AuthenticationManager hierarchy using ProviderManager](https://user-gold-cdn.xitu.io/2018/9/29/16623e4ddc43208e?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

  

- Spring Security提供了一些配置帮助我们快速的开启验证功能，最常用的就是`AuthenticationManagerBuiler`，它在内存（in-memory）、JDBC、LDAP或者个人定制的`UserDetailService`这些领域都很擅长。

---



## 实战

### 1.项目创建

在 Spring Boot 中使用 Spring Security 非常容易，引入依赖即可：

![img](http://www.javaboy.org/images/boot/25-1.png)

pom.xml 中的 Spring Security 依赖：

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

只要加入依赖，项目的所有接口都会被自动保护起来。

### 2.初次体验

我们创建一个 HelloController:

```
@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}
```

访问 `/hello` ，需要登录之后才能访问。

![img](http://www.javaboy.org/images/boot/25-2.png)

当用户从浏览器发送请求访问 `/hello` 接口时，服务端会返回 `302` 响应码，让客户端重定向到 `/login` 页面，用户在 `/login` 页面登录，登陆成功之后，就会自动跳转到 `/hello` 接口。

另外，也可以使用 `POSTMAN` 来发送请求，使用 `POSTMAN` 发送请求时，可以将用户信息放在请求头中（这样可以避免重定向到登录页面）：

![img](http://www.javaboy.org/images/boot/25-3.png)

通过以上两种不同的登录方式，可以看出，Spring Security 支持两种不同的认证方式：

- 可以通过 form 表单来认证
- 可以通过 HttpBasic 来认证

### 3.用户名配置

默认情况下，登录的用户名是 `user` ，密码则是项目启动时随机生成的字符串，可以从启动的控制台日志中看到默认密码：

![img](http://www.javaboy.org/images/boot/25-4.png)

这个随机生成的密码，每次启动时都会变。对登录的用户名/密码进行配置，有三种不同的方式：

- 在 application.properties 中进行配置
- 通过 Java 代码配置在内存中
- 通过 Java 从数据库中加载

前两种比较简单，第三种代码量略大。

#### 3.1 配置文件配置用户名/密码

可以直接在 application.yml文件中配置用户的基本信息：

```
spring:
  security:
    user:
      name: unchin
      password: 777
```

配置完成后，重启项目，就可以使用这里配置的用户名/密码登录了。

#### 3.2 Java 配置类配置用户名/密码

也可以在 Java 代码中配置用户名密码，首先需要我们创建一个 Spring Security 的配置类，集成自 WebSecurityConfigurerAdapter 类，如下：

```
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.inMemoryAuthentication()
                .withUser("unchin")
                .password(passwordEncoder().encode("123"))
                .roles("admin")
            .and()
                .withUser("dylan")
                .password(passwordEncoder().encode("123"))
                .roles("user");
    }
}
```

这里我们在 configure 方法中配置了两个用户，用户的密码都是加密之后的字符串(明文是 123)，从 Spring5 开始，强制要求密码要加密，如果非不想加密，可以使用一个过期的 PasswordEncoder 的实例 NoOpPasswordEncoder，但是不建议这么做，毕竟不安全。

Spring Security 中提供了 BCryptPasswordEncoder 密码编码工具，可以非常方便的实现密码的加密加盐，相同明文加密出来的结果总是不同，这样就不需要用户去额外保存`盐`的字段了，这一点比 Shiro 要方便很多。

#### 3.3 通过 Java 从数据库中加载

##### 3.3.1 导入依赖

##### 3.3.2 创建数据库

##### 3.3.3 配置数据库连接

##### 3.3.4 创建实体、dao、service和controller

##### 3.3.5 配置security

- ### UserDetailsService

- ### WebSecurityConfig

##### 3.3.6 项目结构截图



### 4.登录配置

对于登录接口，登录成功后的响应，登录失败后的响应，我们都可以在 WebSecurityConfigurerAdapter 的实现类中进行配置。例如下面这样：

```
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()//开启登录配置
                //表示访问 /hello 这个接口，需要具备 admin 这个角色
                .antMatchers("/hello").hasRole("admin")
                .anyRequest().authenticated()//表示剩余的其他接口，登录之后就能访问
                .and()
                .formLogin()
                //登录处理接口
                .loginProcessingUrl("/doLogin")
                //定义登录时，用户名的 key，默认为 username
                .usernameParameter("uname")
                //定义登录时，用户密码的 key，默认为 password
                .passwordParameter("passwd")
                //登录成功的处理器
                .successHandler((req,resp,exception) -> {
                        resp.setContentType("application/json;charset=utf-8");
                        PrintWriter out = resp.getWriter();
                        out.write("success");
                        out.flush();
                })
                .failureHandler((req,resp,exception) ->  {
                        resp.setContentType("application/json;charset=utf-8");
                        PrintWriter out = resp.getWriter();
                        out.write("fail");
                        out.flush();
                })
                .permitAll()//和表单登录相关的接口统统都直接通过
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler((req,resp,exception) ->  {
                        resp.setContentType("application/json;charset=utf-8");
                        PrintWriter out = resp.getWriter();
                        out.write("logout success");
                        out.flush();
                })
                .permitAll()
                .and()
                .httpBasic()
                .and()
                .csrf().disable();
    }
}
```

我们可以在 successHandler 方法中，配置登录成功的回调，如果是前后端分离开发的话，登录成功后返回 JSON 即可，同理，failureHandler 方法中配置登录失败的回调，logoutSuccessHandler 中则配置注销成功的回调。

### 5.忽略拦截

如果某一个请求地址不需要拦截的话，有两种方式实现：

- 设置该地址匿名访问
- 直接过滤掉该地址，即该地址不走 Spring Security 过滤器链

推荐使用第二种方案，配置如下：

```
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/vercode");
    }
}
```

Spring Security 另外一个强大之处就是它可以结合 OAuth2 ，玩出更多的花样出来，这些我们在后面的文章中再和大家细细介绍。

---



## 使用到的模块

```
spring-boot-examples
└── spring-boot-security 
复制代码
```

## 项目源码地址

[spring-boot-examples](https://github.com/unchin/spring-boot-examples)

## 参考文献

[SpringBoot集成Spring Security（1）——入门程序](https://blog.csdn.net/yuanlaijike/article/details/80249235)

[3.动态处理角色和资源的关系]([https://github.com/lenve/vhr/wiki/3.%E5%8A%A8%E6%80%81%E5%A4%84%E7%90%86%E8%A7%92%E8%89%B2%E5%92%8C%E8%B5%84%E6%BA%90%E7%9A%84%E5%85%B3%E7%B3%BB](https://github.com/lenve/vhr/wiki/3.动态处理角色和资源的关系))

[Spring Security系列一 权限控制基本功能实现](https://www.ktanx.com/blog/p/4600)

[在Spring Boot中使用Spring Security实现权限控制](https://blog.csdn.net/u012702547/article/details/54319508)

[Spring Cloud Security：Oauth2实现单点登录](https://juejin.im/post/5dc95a675188256e040db43f#heading-6)

[Spring Cloud Security：Oauth2结合JWT使用](https://juejin.im/post/5dc2bec6f265da4d4f65bebe)

[社区 Spring Security 从入门到进阶系列教程](http://www.spring4all.com/article/428)

## 交流

在阅读此文时有任何问题，都可评论提出，以改进文章错误或者表达不清晰的地方。

## 公众号

![](https://i.loli.net/2019/07/17/5d2ec26e0db2e80600.png)