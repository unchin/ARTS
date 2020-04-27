

# GitLab CI/CD + Docker 实现多环境自动化部署

![70842820_p0.jpg](https://upload-images.jianshu.io/upload_images/11571828-8ae8365e145a66ac.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

> 阅读本文需要的基础知识：
>
> - 熟练掌握Java
> - 掌握了Spring Boot基础知识
> - 掌握 Docker     ->     [Docker 部署 SpringBoot项目](https://juejin.im/post/5e8d8cee6fb9a03c6e642265)
> - 掌握 GitLab CI/CD  ->  [GitLab CI/CD +Docker 实现自动化部署](https://juejin.im/post/5e8d9362e51d45470720c296)

## 介绍

日常项目开发过程中，一般都有多套环境，比如开发、测试和生产。各个环境部署的代码版本不一致，手动一个个来部署效率低且容易出错。如果项目采用了敏捷开发方式，可能每天需要部署几十次。手动方式更加不可行，因此必须要把多环境的部署工作自动化。

## 构建一个简单的 Spring Boot 项目

创建一个最简单的 helloworld 的spring项目，运行起来，访问端口查看是否成功。

添加好项目的 docker 支持。可以在[这篇文章](https://juejin.im/post/5e8d8cee6fb9a03c6e642265))项目的原基础上做修改。

## Spring Boot 配置文件做多环境支持

### Spring Environment 概念简介

任何一个软件项目至少都需经过开发、测试、发布阶段，不同阶段有不同的运行环境，其对应的数据库、运行主机、存储、网络、外部服务也会有所区别，故大多数项目都有多套配置对应多个环境，一般来说有开发环境 (dev)、测试环境 (sit/test)、预生产环境 (pre) 和生产环境 (prd)，有些项目可能还有验证新功能的灰度环境等。

Spring 框架从 3.1 版本以后提供了 Environment 接口，包含两个关键概念 profiles 和 properties。Profile 是 Spring 容器中所定义的 Bean 的逻辑组名称，当指定 Profile 激活时，才会将 Profile 中所对应的 Bean 注册到 Spring 容器中，并把相关能力开放给了开发者；而 properties 代表着一组键值对配置信息，其实现中借助了 ConversionService 实现，具备 String 到 Object 的转换能力。其类图如下：

![Spring Boot多环境配置最佳实践](https://static001.infoq.cn/resource/image/3c/3a/3cc957e0514d07e188c9bff933a9003a.jpg)

### 建立不同环境的配置文件

![Snipaste_2020-04-09_10-57-04.png](https://upload-images.jianshu.io/upload_images/11571828-b0ff5a800d85d034.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### application.yml 设置默认环境

```
spring:
  profiles:
    active: dev
```

## pom 文件的修改

在 `pom.xml` 中添加 profiles名称

```
<profiles>
        <profile>
            <!-- 本地开发环境 -->
            <id>dev</id>
            <properties>
                <profiles.active>dev</profiles.active>
            </properties>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
        </profile>
        <profile>
            <!-- 测试环境 -->
            <id>uat</id>
            <properties>
                <profiles.active>uat</profiles.active>
            </properties>
        </profile>
        <profile>
            <!-- 生产环境 -->
            <id>pro</id>
            <properties>
                <profiles.active>pro</profiles.active>
            </properties>
        </profile>
    </profiles>
```

## 修改 DockerFile 文件

在目录src/main/docker下创建 Dockerfile 文件，Dockerfile 文件用来说明如何来构建镜像。

```
FROM openjdk:11.0.3-jdk-stretch
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' >/etc/timezone
VOLUME /tmp
ADD subject-lib-0.0.1-SNAPSHOT.jar app.jar
# spring.profiles.active指定启动环境
#-c为清除以前启动的数据
ENTRYPOINT ["java","-jar","/app.jar","--spring.profiles.active=${profiles}","-c"]
```

## 修改 .gitlab-ci.yml 文件

```
after_script:
  - docker rmi $( docker images -q -f dangling=true)

stages:
  - build
  - test
  - deploy

job_build:
  stage: build
  script:
    - mvn clean install

job_test:
  stage: test
  script:
    - mvn test


job_deploy_dev:
  stage: deploy
  script:
    - mvn package docker:build -P dev
    - if [ $(docker ps -aq --filter name=spring-boot-docker-multi-environment) ]; then docker rm -f spring-boot-docker-multi-environment-dev;fi
    - docker run -e profiles="dev" -it --name spring-boot-docker-multi-environment-dev -d -p 8081:8081 unchin/spring-boot-docker-multi-environment
  only:
    - dev

job_deploy_uat:
  stage: deploy
  script:
    - mvn package docker:build -P uat
    - if [ $(docker ps -aq --filter name=spring-boot-docker-multi-environment-uat) ]; then docker rm -f spring-boot-docker-multi-environment-uat;fi
    - docker run -e profiles="uat" -it --name spring-boot-docker-multi-environment-uat -d -p 8082:8082 unchin/spring-boot-docker-multi-environment
  only:
    - uat

job_deploy_pro:
  stage: deploy
  script:
    - mvn package docker:build -P pro
    - if [ $(docker ps -aq --filter name=spring-boot-docker-multi-environment-pro) ]; then docker rm -f spring-boot-docker-multi-environment-pro;fi
    - docker run -e profiles="pro" -it --name spring-boot-docker-multi-environment-pro -d -p 8090:8090 unchin/spring-boot-docker-multi-environment
  only:
    - tags
```

## 测试

目前的这个脚本的意思是

1. 项目提交就打包和测试
2. 代码合并到dev，就部署到dev容器中
3. 代码合并到uat，就部署到uat容器中
4. 代码合并到master中，并打上 tag，就部署到 生产环境（pro容器）中

目前是都放在一台服务器中，如果是不同的服务器，此方法不适用。

下面是在gitlab上看到的 Piplines

![Snipaste_2020-04-09_11-16-05.png](https://upload-images.jianshu.io/upload_images/11571828-eda92f70d25b3364.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

## 文献

[9-docker、dockerfile配置springboot多环境启动，亲测](https://www.jianshu.com/p/5ae56ceac81b)

[Git + Docker 多环境自动化部署](https://blog.jaggerwang.net/git-docker-multiple-env-deploy/)

## 使用到的模块

```
spring-boot-examples
└── spring-boot-docker-multi-environment 
复制代码
```

## 项目源码地址

[spring-boot-examples](https://github.com/unchin/spring-boot-examples)

## 交流

在阅读此文时有任何问题，都可评论提出，以改进文章错误或者表达不清晰的地方。

## 公众号

![](https://i.loli.net/2019/07/17/5d2ec26e0db2e80600.png)







