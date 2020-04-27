

[TOC]



# Docker 部署 SpringBoot项目

![70290459_p01.jpg](https://i.loli.net/2020/04/08/2FOHRu4X9j7Tx5v.jpg)



> 阅读本文需要的基础知识：
>
> - 熟练掌握Java
> - 掌握了Spring Boot基础知识，并懂得构建一个 SpringBoot 项目

## 介绍

Docker 技术发展为微服务落地提供了更加便利的环境，使用 Docker 部署 Spring Boot 其实非常简单，这篇文章我们就来简单学习下。

## 构建一个简单的 Spring Boot 项目

创建一个最简单的 helloworld 的spring项目，运行起来，访问端口查看是否成功。

## 项目添加 Docker 支持

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


在目录src/main/docker下创建 Dockerfile 文件，Dockerfile 文件用来说明如何来构建镜像。

```
FROM openjdk:11.0.3-jdk-stretch
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' >/etc/timezone
VOLUME /tmp
ADD subject-lib-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
```


这个 Dockerfile 文件很简单，构建 Jdk 基础环境，添加 Spring Boot Jar 到镜像中，简单解释一下:

- FROM ，表示使用 Jdk11 环境 为基础镜像，如果镜像不是本地的会从 DockerHub 进行下载
- VOLUME ，VOLUME 指向了一个`/tmp`的目录，由于 Spring Boot 使用内置的Tomcat容器，Tomcat 默认使用`/tmp`作为工作目录。这个命令的效果是：在宿主机的`/var/lib/docker`目录下创建一个临时文件并把它链接到容器中的`/tmp`目录
- ADD ，拷贝文件并且重命名
- ENTRYPOINT ，为了缩短 Tomcat 的启动时间，添加`java.security.egd`的系统属性指向`/dev/urandom`作为 ENTRYPOINT


> 注意：上面的这个`ADD`中的项目名（hello-docker）和版本号（0.0.1-SNAPSHOT
> ）一定要与 pom 文件中的一致


## 构建 Docker 环境

在 Windows 搭建 Docker 环境很麻烦，但是目前这边所能提供到的服务器环境只有 Windows，所以即使是麻烦也要折腾。如果是有 Linux 条件的小伙伴，可以直接跳到安装 docker 这一步。

在Windows上有三种方式安装 docker：

- 一种是使用 docker tools，它的原理其实就是在 Windows上安装一个虚拟机，里面跑 linux，然后跑docker。
- 一种是把 Windows 升级到 win10或者win server 2019或者以上
- 最后一种是使用虚拟机安装 linux，然后再装 docker

这些方法我都试过了，第一种方法的 docker版本比较旧，并且 docker tool已经被遗弃，后期要是出现问题，确实比较麻烦。
第二种方法是官方的推荐模式，不过在我用一段时间后，发现出现了 bug，去网上查资料几乎没有 Windows版本的docker解决方案，导致搁浅进度。

所以，最后决定，用第三种方案。

### 安装虚拟机

安装vmware并激活

#### 网络设置 

首先是网络设置，可以先`ip addr` 试一试，一般来说，现在是没有网卡和网络 ip 的。

在 CentOS 6 中， 网络设置及静态IP配置在 `/etc/sysconfig/network-scripts/ifcfg-eth0` 文件中配置，CentOS 7 网卡命令规则变化，命名规则根据系统固件和硬件来命名为 `ifcfg-en*` 类型，只有新的命名规则找不到的情况下才使用类似 eth0 这样的样式（系统之所以做出这样的改变肯定是有利于兼容硬件，感兴趣的可以自己深入了解，这里不做展开，我的电脑网卡是 `/etc/sysconfig/network-scripts/ifcfg-enp33`） ，使用 `vi /etc/sysconfig/network-scripts/ifcfg-enp33` 打开这个文件(最小安装默认只有 vi 而没有 vim )，修改如下内容：

```
BOOTPROTO=dhcp #保持默认的dhcp，dhcp会在联网之后自动获取到IP，dhcp获取的ip地址可能会因为网络重连发生变化 
ONBOOT=yes #开启自动启用网络连接 
DNS1=8.8.8.8 #第一个dns服务器，可设置多个，不指定也可以 
# 8.8.8.8 是Google提供的一个免费DNS服务器ip
```

`:wq` 保存退出之后，`service network restart `重启，网络已经启用了；

### xshell连接

1. `ip addr`找到IP，输入账号密码就可连接了。
2. 安装文件传输功能

yum 安装。

```
yum install  lrzsz -y
```

检查是否安装成功。

```
rpm -qa |grep lrzsz
```

上传文件的执行命令：

```
rz
```

就会打开本地选择文件对话框，选择文件，确定就可以上传到当前所在目录。

如果覆盖原文件，执行：

```
rz   -y
```

下载文件，执行：

```
sz
```

弹出选择本地保存文件对话框。

> 这里也可以选择 Xftp 来实现文件传输，这是一个相当强大的软件。

### 安装JDK

```
yum -y install java-1.8.0-openjdk*
```

配置环境变量 打开` vim /etc/profile` 添加一下内容

```
export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.242.b08-0.el7_7.x86_64 
export PATH=$PATH:$JAVA_HOME/bin 
```

修改完成之后，使其生效`

```
source /etc/profile`
```

输入`java -version` 返回版本信息则安装正常。

### 安装MAVEN

下载：http://mirrors.shu.edu.cn/apache/maven/maven-3/3.5.2/binaries/apache-maven-3.5.2-bin.tar.gz

```
## 解压tar vxf apache-maven-3.6.3-bin.tar.gz
## 移动mv apache-maven-3.6.3 /usr/local/maven3
```

创建软连接

```
ln -s apache-maven-3.6.3 maven
```

修改环境变量， 在`/etc/profile中`添加以下几行

```
MAVEN_HOME=/usr/local/maven
export MAVEN_HOME
export PATH=${PATH}:${MAVEN_HOME}/bin
```

记得执行`source /etc/profile`使环境变量生效。

输入`mvn -version` 返回版本信息则安装正常。这样整个构建环境就配置完成了。

## 安装 docker

安装

```
yum install docker
```

安装完成后，使用下面的命令来启动 docker 服务，并将其设置为开机启动：

```
systemctl  start docker.service
systemctl  enable docker.service
```

使用Docker 中国加速器，这一步很重要

```
vi  /etc/docker/daemon.json

#添加后：{
    "registry-mirrors": ["https://registry.docker-cn.com"],
    "live-restore": true
    }
```

重新启动

```
docker systemctl restart docker
```

输入`docker version` 返回版本信息则安装正常。


## 使用 Docker 部署 Spring Boot 项目 

项目拷贝服务器中，进入项目路径下进行打包测试。

```
#打包
mvn package
```

接下来我们使用 DockerFile 构建镜像。

```
mvn package docker:build
```

第一次构建可能有点慢，当看到以下内容的时候表明构建成功：

```
...
Step 1 : FROM openjdk:8-jdk-alpine
 ---> 224765a6bdbe
Step 2 : VOLUME /tmp
 ---> Using cache
 ---> b4e86cc8654e
Step 3 : ADD spring-boot-docker-1.0.jar app.jar
 ---> a20fe75963ab
Removing intermediate container 593ee5e1ea51
Step 4 : ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom -jar /app.jar
 ---> Running in 85d558a10cd4
 ---> 7102f08b5e95
Removing intermediate container 85d558a10cd4
Successfully built 7102f08b5e95
[INFO] Built springboot/spring-boot-docker
[INFO] ------------------------------------------------------------------------[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------[INFO] Total time: 54.346 s
[INFO] Finished at: 2020-03-03T16:20:15+08:00
[INFO] Final Memory: 42M/182M
[INFO] ------------------------------------------------------------------------
```

使用docker images命令查看构建好的镜像：

```
docker images
REPOSITORY                      TAG                 IMAGE ID            CREATED             SIZE
springboot/spring-boot-docker   latest              99ce9468da74        6 seconds ago       117.5 MB
```

springboot/spring-boot-docker 就是我们构建好的镜像，下一步就是运行该镜像

```
docker run -p 8080:8080 -t springboot/spring-boot-docker
```

启动完成之后我们使用docker ps查看正在运行的镜像：

```
docker ps
CONTAINER ID        IMAGE                           COMMAND                  CREATED             STATUS              PORTS                    NAMES
049570da86a9        springboot/spring-boot-docker   "java -Djava.security"   30 seconds ago      Up 27 seconds       0.0.0.0:8080->8080/tcp   determined_mahavira
```

可以看到我们构建的容器正在在运行，访问浏览器：`http://192.168.0.x:8080/`,返回Hello Docker!

说明使用 Docker 部署 Spring Boot 项目成功！


以上这个是最基本的 dockerfile 部署的方法，在掌握了这个方法之后，还有其他部署方法：

1. [别用 Dockerfile 部署 Spring Boot ，利用 dockerhub云部署](https://mp.weixin.qq.com/s?__biz=MzI1NDY0MTkzNQ==&mid=2247486891&idx=1&sn=99fd793e3f1ac05898aceec052336120&chksm=e9c35fcbdeb4d6dde353c49929ed64157e7c83f737166192cef134ac5e8883d53d8326a38e78&mpshare=1&scene=1&srcid=&sharer_sharetime=1573131989225&sharer_shareid=a0bb45905ea4078e53ce8303a91a1eab#rd)

2. [使用Jenkins一键打包部署SpringBoot应用](https://mp.weixin.qq.com/s?__biz=MzU1Nzg4NjgyMw==&mid=2247484270&idx=1&sn=92bc35f7568e061059e58af919e75bde&chksm=fc2fbf66cb5836703713c6da2258704fe3a1adc5f643e0150c545236ec3d6c87385dbc5c2e4a&scene=0&xtrack=1&key=cd46a5853d370dc7eff9079f8bc5f8d7df9f9e38ec88b95f6232aa663db45ef6e28dd25f63754b22071b4715fa7871a066109594ad5d2f7f5a235b924105bf51270b9c4333267a5168da39f1176de23d&ascene=1&uin=MTQwMDgyNjc4MQ%3D%3D&devicetype=Windows+10&version=62070158&lang=zh_CN&exportkey=AfYo5g36H4LpR%2FWFUtmAGm0%3D&pass_ticket=HaGajqRlyct9GSmKgMOR1qQbx%2B7dhCz6Oo4YGekdHf8oT0bTHc%2BA544QtA1mKEVh)

## 遇到的问题

1. [Linux下安装maven, mvn -v报错: JAVA_HOME should point to a JDK not a JRE](https://www.cnblogs.com/dotama/p/10974641.html)

2. [Xshell连接不上虚拟机的问题和解决办法](https://blog.csdn.net/fengasdfgh/article/details/60135290?depth_1-utm_source=distribute.pc_relevant.none-task&utm_source=distribute.pc_relevant.none-task)

3. [构建项目失败解决方案](https://blog.csdn.net/fragrant_no1/article/details/84326155?depth_1-utm_source=distribute.pc_relevant.none-task&utm_source=distribute.pc_relevant.none-task)

## 使用到的模块

```
spring-boot-examples
└── spring-boot-docker 
复制代码
```

## 项目源码地址

[spring-boot-examples](https://github.com/unchin/spring-boot-examples)

## 交流

在阅读此文时有任何问题，都可评论提出，以改进文章错误或者表达不清晰的地方。

## 公众号

![](https://i.loli.net/2019/07/17/5d2ec26e0db2e80600.png)