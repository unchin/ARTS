<!-- TOC -->

- [场景](#场景)
- [解决方案](#解决方案)
- [定义：什么是 oauth2](#定义什么是-oauth2)
- [oauth2的四种授权方式](#oauth2的四种授权方式)
- [oauth角色](#oauth角色)
- [客户端和服务端](#客户端和服务端)
- [举例](#举例)
- [实现](#实现)
    - [客户端](#客户端)
        - [登记](#登记)
        - [项目结构](#项目结构)
        - [pom添加依赖](#pom添加依赖)
        - [yml配置文件](#yml配置文件)
        - [AuthCodeController获取code](#authcodecontroller获取code)
        - [AccessTokenController服务端回调](#accesstokencontroller服务端回调)
        - [GetUserInfoController客户端根据access_token获取用户信息](#getuserinfocontroller客户端根据access_token获取用户信息)
        - [测试](#测试)
    - [服务端](#服务端)
        - [用户表](#用户表)
        - [客户端信息client表](#客户端信息client表)
        - [Todo](#todo)
- [源码](#源码)
- [参考链接](#参考链接)

<!-- /TOC -->

## 场景
在登录模块中，需要集成第三方登录，比如微信登录，微博登录等

## 解决方案
oauth2协议

## 定义：什么是 oauth2
OAuth2.0 是一个开放标准，允许用户让第三方应用访问该用户在某一网站上存储的私密的资源（如照片，视频，联系人列表），而不需要将用户名和密码提供给第三方应用。

OAuth允许用户提供一个令牌，而不是用户名和密码来访问他们存放在特定服务提供者的数据。每一个令牌授权一个特定的网站在特定的时段内访问特定的资源。

简单说，OAuth就是一种授权机制。数据的所有者（用户）告诉系统，同意授权第三方应用进入系统，获取这些数据。系统从而产生一个短期的进入令牌（token），用来代替密码，供第三方应用使用。

有关抽象定义还不清晰的话，可以参考阮一峰的这篇文章 [OAuth 2.0 的一个简单解释](http://www.ruanyifeng.com/blog/2019/04/oauth_design.html)

## oauth2的四种授权方式
> 授权码（authorization-code）
> 
> 隐藏式（implicit）
> 
> 密码式（password）
> 
> 客户端凭证（client credentials）


OAuth 2.0 的标准是 RFC 6749 文件。它规定了四种获得令牌的流程。你可以选择最适合自己的那一种，向第三方应用颁发令牌。

1 授权码（authorization code）方式，指的是第三方应用先申请一个授权码，然后再用该码获取令牌。

2 有些 Web 应用是纯前端应用，没有后端。必须将令牌储存在前端。这种方式没有授权码这个中间步骤，所以称为（授权码）"隐藏式"（implicit）。

3 如果你高度信任某个应用，RFC 6749也允许用户把用户名和密码，直接告诉该应用。该应用就使用你的密码，申请令牌，这种方式称为"密码式"（password）。

4 凭证式（client credentials），适用于没有前端的命令行应用，即在命令行下请求令牌。

## oauth角色
资源拥有者(resource owner)：比如你的信息是属于你的。你就是资源的拥有者。 

资源服务器（resource server）：存储受保护资源，客户端通过access token请求资源，资源服务器响应受保护资源给客户端。

授权服务器（authorization server）：成功验证资源拥有者并获取授权之后，授权服务器颁发授权令牌（Access Token）给客户端。 

客户端（client）：第三方应用，其本身不存储资源，而是资源拥有者授权通过后，使用它的授权（授权令牌）访问受保护资源，然后客户端把相应的数据展示出来/提交到服务器。“客户端”术语不代表任何特定实现（如应用运行在一台服务器、桌面、手机或其他设备）。

## 客户端和服务端
如果是我们自己开发的一个应用，需要调用微信第三方登录。那我们的应用就是客户端，微信提供服务端。

我们也可以自己提供第三方服务，供给别的应用调用，这样子我们就是服务端。

## 举例
第三方登录，实质就是 OAuth授权。

用户想要登录 A网站，A网站让用户提供第三方网站的数据，证明自己的身份。获取第三方网站的身份数据，就需要 OAuth 授权。

A网站就是客户端，Github就是服务端。

举例来说，A网站允许 GitHub登录，背后就是下面的流程。

1. A 网站让用户跳转到 GitHub。
2. GitHub要求用户登录，然后询问"A网站要求获得 xx权限，你是否同意？"
3. 用户同意，GitHub就会重定向回 A网站，同时发回一个授权码。
4. A网站使用授权码，向 GitHub请求令牌。
5. GitHub返回令牌.
6. A 网站使用令牌，向 GitHub请求用户数据。

## 实现
### 客户端
#### 登记
代码例子中，我自己开发了一个 web应用，需要调用微博的oauth2认证接口，首先需要在微博端进行应用登记。

登记成功后我们会拿到一个客户端编号（clientId）和一个客户端密钥（clientSecret），在代码的配置文件中能用上。

以下为部分第三方登录网站的提供的接口登记信息：

Facebook：
https://developers.facebook.com/docs/facebook-login/web

Twitter：
https://developer.twitter.com/en/docs/twitter-for-websites/log-in-with-twitter/login-in-with-twitter

微博：
https://open.weibo.com/wiki/Connect/login

微信：
https://open.we4ixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419316505&token=&lang=zh_CN


#### 项目结构
![](https://i.loli.net/2019/08/05/2qrp5cDymSZN8Fs.png)

#### pom添加依赖
```
<dependency>
    <groupId>org.apache.oltu.oauth2</groupId>
    <artifactId>org.apache.oltu.oauth2.client</artifactId>
    <version>1.0.2</version>
</dependency>
```
#### yml配置文件
```
server:
  port: 8080

mybatis:
  mapperLocations: classpath*:/mapper/*.xml
  configuration:
    mapUnderscoreToCamelCase: true

spring:
  application:
    name: spring-boot-mybatis
  datasource:
    url: jdbc:mysql://localhost:3306/test?useUnicode:true&characterEncoding:UTF-8&serverTimezone:GMT
    username: root
    password: 123456

logging:
  level:
    com:
      steven:
        shiro: DEBUG


#获取token的链接
accessTokenUrl: https://api.weibo.com/oauth2/access_token
#获取code的链接
authorizeUrl: https://api.weibo.com/oauth2/authorize
#登记完后第三方服务返回给我们的客户端编号（每个应用注册的都不同）
clientId: 20022147382121
#登记完后第三方服务返回给我们的客户端密钥（每个应用注册的都不同）
clientSecret: 1c183be503847bf86ce3c033ce7ab05d21231231
#回调接口地址
redirectUrl: http://127.0.0.1:8080/getResourcesFromWeibo
#授权方式
response_type: code
#本地应用需要访问的接口
userInfoUrl: /oauth-client/getUserInfo

```

#### AuthCodeController获取code
```
package com.steven.oauth2client.controller;

import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 1、拼接url然后访问，获取code
 * 2、服务端检查成功,然后会回调到 另一个接口 /oauth-client/callbackCode
 * @author StevenGuo
 * @date 10:13 2019/7/31
 **/
@RestController
@RequestMapping("/oauth-client")
public class AuthCodeController {
    @Value("${clientId}")
    private String clientId;

    @Value("${authorizeUrl}")
    private String authorizeUrl;

    @Value("${redirectUrl}")
    private String redirectUrl;

    @Value("${response_type}")
    private String response_type;

    @RequestMapping("/getCode")
    public String getCode() throws OAuthSystemException {
        String requestUrl = null;

            //配置请求参数，构建oauthd的请求。设置请求服务地址（authorizeUrl）、clientId、response_type、redirectUrl
            OAuthClientRequest accessTokenRequest = OAuthClientRequest.authorizationLocation(authorizeUrl)
                    .setResponseType(response_type)
                    .setClientId(clientId)
                    .setRedirectURI(redirectUrl)
                    .buildQueryMessage();

            requestUrl = accessTokenRequest.getLocationUri();

        System.out.println("==> 客户端重定向到服务端获取auth_code： "+requestUrl);
        return "redirect:"+requestUrl ;
    }
}

```

#### AccessTokenController服务端回调
```
package com.steven.oauth2client.controller;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 1、服务端回调,传回code值
 * 2、根据code值，调用服务端服务,根据code获取access_token
 * 3、拿到access_token重定向到客户端的服务 /oauth-client/getUserInfo 在该服务中 再调用服务端获取用户信息
 * @author StevenGuo
 * @date 10:16 2019/7/31
 **/
@RestController
public class AccessTokenController {
    @Value("${clientId}")
    private String clientId;

    @Value("${clientSecret}")
    private String clientSecret;

    @Value("${accessTokenUrl}")
    private String accessTokenUrl;

    @Value("${redirectUrl}")
    private String redirectUrl;

    @Value("${response_type}")
    private String response_type;


    //接受客户端返回的code，提交申请access token的请求
    @RequestMapping("/getResourcesFromWeibo")
    public Object toLogin(HttpServletRequest request) throws OAuthProblemException, OAuthSystemException {

        String code = request.getParameter("code");

        System.out.println("==> 服务端回调，获取的code：" + code);

        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());


        OAuthClientRequest accessTokenRequest = OAuthClientRequest
                .tokenLocation(accessTokenUrl)
                .setGrantType(GrantType.AUTHORIZATION_CODE)
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setCode(code)
                .setRedirectURI(redirectUrl)
                .buildQueryMessage();

        //去服务端请求access token，并返回响应
        OAuthAccessTokenResponse oAuthResponse = oAuthClient.accessToken(accessTokenRequest, OAuth.HttpMethod.POST);
        //获取服务端返回过来的access token
        String accessToken = oAuthResponse.getAccessToken();
        //查看access token是否过期
        Long expiresIn = oAuthResponse.getExpiresIn();
        System.out.println("==> 客户端根据 code值 " + code + " 到服务端获取的access_token为：" + accessToken + " 过期时间为：" + expiresIn);

        System.out.println("==> 拿到access_token然后重定向到 客户端 /oauth-client/getUserInfo服务,传过去accessToken");

        return "redirect:/oauth-client/getUserInfo?accessToken=" + accessToken;
    }

}

```

#### GetUserInfoController客户端根据access_token获取用户信息
```
package com.steven.oauth2client.controller;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * 通过access_token获取用户信息
 * @author StevenGuo
 * @date 9:02 2019/8/1
 **/
@RestController
@RequestMapping("/oauth-client")
public class GetUserInfoController {
    @Value("${userInfoUrl}")
    private String userInfoUrl;


    //接受服务端传回来的access token，由此token去请求服务端的资源（用户信息等）
    @RequestMapping("/getUserInfo")
    @ResponseBody
    public String accessToken(String accessToken) {

        OAuthClient oAuthClient =new OAuthClient(new URLConnectionClient());
        try {
            OAuthClientRequest userInfoRequest =new OAuthBearerClientRequest(userInfoUrl)
                    .setAccessToken(accessToken).buildQueryMessage();

            OAuthResourceResponse resourceResponse =oAuthClient.resource(userInfoRequest, OAuth.HttpMethod.GET, OAuthResourceResponse.class);
            String body = resourceResponse.getBody();
            System.out.println("==> 客户端通过accessToken："+accessToken +"  从服务端获取用户信息为："+body);
            return body;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

}

```

#### 测试
1. 首先访问客户端http://localhost:9080/oauth-client/getCode 会重定向到服务端让你输入账号密码授权 
2. 输入用户名进行登录并授权； 
3. 如果登录成功，服务端会重定向到客户端，即之前客户端提供的地址http://127.0.0.1:8080/getResourcesFromWeibo？code=98872aeb79889bc27be46da76a204aa3，并带着 auth code过去； 
4. 方法内部拿到 code之后 会调用服务端获取access_token然后重定向到客户端的获取用户信息方法 
5. 获取用户信息方法内调用服务端 并传过去 access_token获取用户名,然后展示到页面 

![](https://i.loli.net/2019/08/05/Gn1QtKjHBlTwrDv.png)

到此客户端基本流程结束。还有其他的扩展，以后再加。

### 服务端
我们提供服务，让别的应用可以通过我们的用户信息登入。

首先我们需要两张表，一张用户表，一张客户端信息表。表中的数据就自己造一些吧。
根据表生成相关的实体类。

#### 用户表
```
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT '' COMMENT '用户名',
  `password` varchar(256) DEFAULT NULL COMMENT '登录密码',
  `name` varchar(256) DEFAULT NULL COMMENT '用户真实姓名',
  `id_card_num` varchar(256) DEFAULT NULL COMMENT '用户身份证号',
  `state` char(1) DEFAULT '0' COMMENT '用户状态：0:正常状态,1：用户被锁定',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `username` (`username`) USING BTREE,
  UNIQUE KEY `id_card_num` (`id_card_num`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8
```

#### 客户端信息client表
```
DROP TABLE IF EXISTS `oauth2_client`;
CREATE TABLE `oauth2_client` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `client_name` varchar(100) DEFAULT NULL COMMENT '客戶端名稱',
  `client_id` varchar(100) DEFAULT NULL COMMENT '客戶端ID',
  `client_secret` varchar(100) DEFAULT NULL COMMENT '客户端安全key',
  PRIMARY KEY (`id`),
  KEY `idx_oauth2_client_client_id` (`client_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
```

#### Todo
在提供 token的时候，只暂时选用了 Cache的方式，其实应该使用 Redis做缓存更好，所以这里代码还没有完善。Github中服务端的代码也还不能跑起来，后面再补。


## 源码
GitHub

客户端： https://github.com/unqin/spring-boot-examples/tree/master/spring-boot-examples/spring-boot-oauth2-client

服务端： https://github.com/unqin/spring-boot-examples/tree/master/spring-boot-examples/spring-boot-oauth2-service

## 参考链接
1. OAuth 2.0 的一个简单解释 
   http://www.ruanyifeng.com/blog/2019/04/oauth_design.html

2.  OAuth2集成——《跟我学Shiro》
   https://jinnianshilongnian.iteye.com/blog/2038646

3. shiro 整合oauth2.0 服务端 和 客户端实现
   https://blog.csdn.net/qq_34021712/article/details/80510774