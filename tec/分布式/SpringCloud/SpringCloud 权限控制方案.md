# SpringCloud 权限控制方案

## 背景

从传统的单体应用转型Spring Cloud，Spring Cloud下的微服务权限怎么管？怎么设计比较合理？从大层面讲叫服务权限，往小处拆分，分别为三块：用户认证、用户权限、服务校验。

## 用户认证

传统的单体应用可能习惯了session的存在，而到了Spring cloud的微服务化后，session虽然可以采取分布式会话来解决，但终究不是上上策。开始有人推行Spring Cloud Security结合很好的OAuth2，后面为了优化OAuth 2中Access Token的存储问题，提高后端服务的可用性和扩展性，有了更好Token验证方式JWT（JSON Web Token）。这里要强调一点的是，OAuth2和JWT这两个根本没有可比性，是两个完全不同的东西。 OAuth2是一种授权框架，而JWT是一种认证协议。



## 1 技术栈

| 技术            | 说明           |
| --------------- | -------------- |
| Spring Cloud    | 微服务框架     |
| Spring Boot     | 容器+MVC框架   |
| Spring Security | 认证和授权框架 |
| Redis           | 分布式缓存     |
| Oauth2          | 权限控制协议   |
| JWT             | JWT登录支持    |

## 2 架构图

![spring cloud权限控制方案.png](https://i.loli.net/2020/03/23/TrybZaAEM5exC9H.png)

## 3 流程图

![img](http://www.javaboy.org/images/sb/20-3.png)

步骤翻译：

1. 应用程序或客户端向授权服务器请求授权
2. 获取到授权后，授权服务器会向应用程序返回访问令牌
3. 应用程序使用访问令牌来访问受保护资源（如API）

因为JWT签发的token中已经包含了用户的身份信息，并且每次请求都会携带，这样服务的就无需保存用户信息，甚至无需去数据库查询，这样就完全符合了RESTful的无状态规范。

![img](https://img-blog.csdnimg.cn/2019032813562876.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDUxNjMwNQ==,size_16,color_FFFFFF,t_70)

![img](https://img-blog.csdn.net/20161226141519671?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvY29kZV9fY29kZQ==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

## 4 详细描述

1. 通过单点登录实现系统账户登录统一化
2. 通过Oauth2实现用户与服务的授权
3. RBAC设计理念设计用户权限表

## 5 相关文献

[Spring Cloud Security：Oauth2使用入门](https://juejin.im/post/5dc013bae51d456e817cec30)

[Spring Cloud Security：Oauth2结合JWT使用](https://juejin.im/post/5dc2bec6f265da4d4f65bebe)

[手把手带你入门 Spring Security](http://springboot.javaboy.org/2019/0725/springboot-springsecurity)

[Spring Cloud Security：Oauth2实现单点登录](https://juejin.im/post/5dc95a675188256e040db43f)

[使用Redis+AOP优化权限管理功能，这波操作贼爽！](https://mp.weixin.qq.com/s?__biz=MzU1Nzg4NjgyMw==&mid=2247484623&idx=1&sn=4e79cd5887c2abf805eee6be8dc1d5af&chksm=fc2fb8c7cb5831d1a8bcb25dc758edbcacc8814a947d6ec8f8263a409b59054ace218c95d9f8&scene=126&sessionid=1584927399&key=2c4881e20dc9fe0ff6959fa4d58bfff3d8ff37bd31f86544596f12664341de841eda0a067ce5c1f6dd10856c43baeb72da42152fe479bf030108491ddebb41a3bd32fcd4de7a04288aa3b81b11de5471&ascene=1&uin=MTQwMDgyNjc4MQ%3D%3D&devicetype=Windows+10&version=62080079&lang=zh_CN&exportkey=AQz%2Bw6eIJBimnMNlToeIxvk%3D&pass_ticket=diBN2pgtkHx5oR1t0PVXlTH%2Bwrr1PQfInG9Kg%2B%2BtryOvsyx1GqnhzPId9U2J%2FiRe)

[[mall整合SpringSecurity和JWT实现认证和授权（一）](http://www.macrozheng.com/#/architect/mall_arch_04?id=mall整合springsecurity和jwt实现认证和授权（一）)

[Spring Cloud下微服务权限方案](https://zhuanlan.zhihu.com/p/29345083)

[Spring Boot 整合 OAuth2，松哥手把手教你！](https://mp.weixin.qq.com/s/1rVPzJGCtDZKvMoA4BYzIA)



