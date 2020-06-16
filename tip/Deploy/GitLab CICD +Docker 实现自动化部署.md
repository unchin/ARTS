

[TOC]

#  GitLab CI/CD +Docker 实现自动化部署

![70775347_p0.jpg](https://upload-images.jianshu.io/upload_images/11571828-74ebb995af57597e.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

## 背景

在网上查了很多资料，很大一部分资料，只是写了一部分，或者是个例的文档，不具备普适性。

本文做一个各种杂乱文章的整合和补充。

资料顺序：

1. 官方文档
2. 各种博客

> 官方文档中是说有三种方法可以实现gitlab ci 和 docker 的配置，本文写的是第一种使用 shell的方式，这种也是最简单的一种方式。

## 简介

gitlab-ci全称是gitlab continuous integration的意思，也就是持续集成。中心思想是当每一次push到gitlab的时候，都会触发一次脚本执行，然后脚本的内容包括了测试，编译，部署等一系列自定义的内容。本文就是利用gitlab-ci的持续集成来实现自动部署。相比之前[webhook的自动部署](https://www.jianshu.com/p/00bc0323e83f)还是强大以及方便了许多。

## 原理

自动部署涉及了若干个角色，主要介绍如下

- GitLab-CI
  这个是一套配合GitLab使用的持续集成系统，是GitLab自带的，也就是你装GitLab的那台服务器上就带有的。无需多考虑。.gitlab-ci.yml的脚本解析就由它来负责。
- GitLab-Runner
  这个是脚本执行的承载者，.gitlab-ci.yml的script部分的运行就是由runner来负责的。GitLab-CI浏览过项目里的.gitlab-ci.yml文件之后，根据里面的规则，分配到各个Runner来运行相应的脚本script。这些脚本有的是测试项目用的，有的是部署用的。
- .gitlab-ci.yml
  这个是在git项目的根目录下的一个文件，记录了一系列的阶段和执行规则。GitLab-CI在push后会解析它，根据里面的内容调用runner来运行。

## 1 准备工作

### 1.1 gitlab环境

这个可以搭建私有gitlab空间，或者为了方便，可以使用官方的托管仓库。

gitlab 仓库搭好后，gitlab ci 就自动安装好了。（老版本不适用）

### 1.2 装有`docker`和`gitlab-runner`环境的云服务器

- [安装docker](https://blog.csdn.net/amethystcity/article/details/104676878)

- 安装GitLab-Runner

  官网有详细的安装步骤，链接如下：

  [https://docs.gitlab.com/runner/install/linux-repository.html](https://link.jianshu.com/?t=https://docs.gitlab.com/runner/install/linux-repository.html)

  需要执行如下命令，在centOS上安装gitlab-ci-multi-runner

```bash
$ curl -L https://packages.gitlab.com/install/repositories/runner/gitlab-ci-multi-runner/script.rpm.sh | sudo bash
$ yum install gitlab-ci-multi-runner
```

这样就装好了gitlab-ci-multi-runner

### 1.3 项目代码

写个helloworld就可。

在 `pom.xml` 中添加 Docker 镜像名称，属性中添加好镜像名字的前缀

```
<properties>
    <java.version>1.8</java.version>
    <docker.image.prefix>springboot</docker.image.prefix>
</properties>
```

plugins 中添加 Docker 构建插件：

```
<!-- Docker maven plugin -->
<plugin>
    <groupId>com.spotify</groupId>
    <artifactId>docker-maven-plugin</artifactId>
    <version>1.0.0</version>
    <configuration>
        <imageName>${docker.image.prefix}/${project.artifactId}</imageName>
        <dockerDirectory>src/main/docker</dockerDirectory>
        <resources>
            <resource>
                <targetPath>/</targetPath>
                <directory>${project.build.directory}</directory>
                <include>${project.build.finalName}.jar</include>
            </resource>
        </resources>
    </configuration>
</plugin>
```

### 1.4`Dockerfile`文件

首先是项目要添加docker支持，然后添加好Dockerfile文件，这个文件根据项目的不同可能略有不同，这个是springboot项目（hello-docker）的Dockerfile：

在目录src/main/docker下创建 Dockerfile 文件，Dockerfile 文件用来说明如何来构建镜像。

```
FROM openjdk:11.0.3-jdk-stretch
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' >/etc/timezone
VOLUME /tmp
ADD subject-lib-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
```


### 1.5. `.gitlab-ci.yml`文件

gitlab提供了一个很方便的配置工具，就是.gitlab-ci.yml， 将该文件放入到project的根目录下即可。

以 Java springboot 项目为例，要配置一个生成 docke r镜像的 job， 每次对 project 打 tag 的时候就会触发 job 执行，生成一个镜像，镜像的 version 是 tag的name. 

.gitlab-ci.yml的内容如下：

```
# 脚本执行之后删除该镜像（为了不与下次镜像打包冲突）
after_script:
  - docker rmi $( docker images -q -f dangling=true)

# 定义几个流程（job），这里是定义了三个自动化流程
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

job_deploy:
  stage: deploy
  script:
    # 通过Dockerfile生成镜像
    - mvn package docker:build
    # 删除已经在运行的容器
    - if [ $(docker ps -aq --filter name=hello-docker) ]; then docker rm -f hello-docker;fi
    # 通过镜像启动容器，并把本机端口映射到容器端口
    - docker run -d -p 8070:8070 --name hello-docker gyq/hello-docker
  only:
    - master
```

**only** 规定了只有git tags操作和triggers才能触发任务

**triggers**可以在gitlab的project->settings->CI/DI Pipelines下添加，添加之后会生成一个token，使用这个token就可以随时触发一次job了，在测试的时候非常有用

**tags** 用来筛选符合条件的runners

默认的script运行的位置是project的根目录，runner会先将整个project取下来，然后在project根目录下运行job.

更多.gitlab-ci.yml文件的用法可以参考[官方的配置文档](https://docs.gitlab.com/ee/ci/yaml/README.html)

## 2 环境配置

### 2.1 配置权限 

在runner上运行任务的时候使用的是gitlab-runner账户，该账户没有root权限，如果想使用更高的权限可以对gitlab-runner账户进行提权。

这里因为只有docker命令需要更高的权限，可以使用创建docker用户组的方式解决。在runner上使用root账户执行如下命令：

```undefined
usermod -aG docker gitlab-runner
```

验证是否`gitlab-runner`有权访问Docker：

```
sudo -u gitlab-runner -H docker info
```

当然也可以直接切换到 gitlab-runner 用户中，查看 docker命令有没有权限

> 权限这个问题很重要，请务必设置

### 2.2 为项目注册执行部署任务的Runner服务器

向gitlab-CI注册这个runner，不然gitlab-CI在push事件到来的时候怎么知道要调用谁呢？这里也可以发现和webhook方式的区别，webhook方式是我们主动配置了一个连接给gitlab；gitlab-runner只要注册一下就好了。

那么我们就注册一下

```bash
$ gitlab-ci-multi-runner register
#引导会让你输入gitlab的url，输入自己的url，例如http://gitlab.example.com/
#引导会让你输入token，去相应的项目下找到token，例如ase12c235qazd32
#引导会让你输入tag，一个项目可能有多个runner，是根据tag来区别runner的，输入若干个就好了，比如web,hook,deploy
#引导会问你是否无tag也触发自动部署，我这里为了方便测试，选择的true
#引导会问你是否锁定此runner仅为此项目锁定，我这里选的默认否
#引导会让你输入executor，这个是要用什么方式来执行脚本，输入shell就好了。
```

![img](https://upload-images.jianshu.io/upload_images/3447958-72eac6f3b0f5be4b.jpg?imageMogr2/auto-orient/strip|imageView2/2/w/1200/format/webp)

然后就注册好了，在gitlab中相应的位置就可以看到你注册好的runner信息。

## 3 提交更新，自动部署

### 3.1. 提交代码到git master分支

runner注册成功后，通过git命令提交更新到master分支，只要master分支有修改，都会执行Job的任务。

### 3.2. 等待Job任务完成

在gitlab页面可以看到job的状态，以及日志

### 3.3 测试结果

查看端口，检查代码的更新情况

## 参考文档

[[后端]gitlab之gitlab-ci自动部署](https://www.jianshu.com/p/df433633816b)

[GitLab-CI与GitLab-Runner](https://www.jianshu.com/p/2b43151fb92e)

[[如何在gitlab持续集成中使用docker](https://q.cnblogs.com/q/94648/)]

[使用Gitlab CI/DI 自动生成docker镜像](https://www.jianshu.com/p/c4a9dfdbbc87)

[GitLab+Docker快速搭建CI/CD自动化部署](https://www.jianshu.com/p/c398509f8861)

[[Docker启动Get Permission Denied](https://www.cnblogs.com/informatics/p/8276172.html)]

[linux添加用户或给用户添加root权限](https://blog.csdn.net/yajie_china/article/details/80636783)

## 交流

在阅读此文时有任何问题，都可评论提出，以改进文章错误或者表达不清晰的地方。

## 公众号

![](https://i.loli.net/2019/07/17/5d2ec26e0db2e80600.png)

