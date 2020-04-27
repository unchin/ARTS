# HAP框架搭建以及demo运行
> 开源地址 
> http://eco.hand-china.com/doc/hap/latest/

本文主要是记录官方文档中没有提到或者是没有特意提醒的点，主要搭建流程，以官方文档为主。

### 新建数据库
首先是mysql的配置文件，**不是最好修改，而是一定要修改，** 因为你不知道你的mysql版本中设置的默认选项是什么，我因为之前没有修改，liquibase拉过来的数据就是有问题的数据，所以这一步一定要做。

第二个要注意点就是修改配置文件要注意自己使用的mysql的版本，去对应修改。

查询版本号：`SELECT VERSION();`

修改后，在系统的服务中重启mysql。

查看是否修改成功：`
show variables like 'character_set%';`

[mysql修改配置文件相关链接](https://www.cnblogs.com/xiaoliying/p/7434971.html)

### 数据源配置
修改tomcat安装目录中的文件

### 修改父项目依赖版本
注意是父项目，也就是HbiParent的pom文件修改
最新版是3.5.4版本，我们拉下来的是3.5.3版本，所以这里需要修改一下。

```
 <groupId>hbi</groupId>
    <artifactId>HbiParent</artifactId>
    <packaging>pom</packaging>
    <version>1.0</version>
    <properties>
        <!--<hap.version>2.1.4-Release</hap.version>-->
        <hap.version>3.5.4-RELEASE</hap.version>   //修改这里
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
        <org.slf4j-version>1.7.21</org.slf4j-version>
        <log4j.version>2.4.1</log4j.version>
        <disruptor.version>3.2.0</disruptor.version>
        <skipLiquibaseRun>true</skipLiquibaseRun>
        <groovy.version>2.4.1</groovy.version>
        <gmaven.version>1.5</gmaven.version>
        <skipTests>true</skipTests>
    </properties>
```

### tomcat配置
在idea中要配置deployment为war文件，才能成功运行

### 运行环境
先把环境跑起来看看有没有问题，没有问题再来进行下一步

### demo
hap环境中有一个代码自动生成器，我用过这个，也自己写过一个，两个都可以。
然后自己写demo之前需要先建表，但是建表的时候有一个很重要，**就是who字段不能少，这个非常重要**，不然后面会报错。

这是我的建表语句：
```
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for my_test01
-- ----------------------------
DROP TABLE IF EXISTS `my_test01`;
CREATE TABLE `my_test01`  (
  `MY_ID` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `MY_NAME` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `MY_AGE` int(12) NULL DEFAULT NULL,
  `MY_BIRTHDAY` timestamp(0) NULL DEFAULT NULL,
  `OBJECT_VERSION_NUMBER` bigint(20) NULL DEFAULT 1,
  `REQUEST_ID` bigint(20) NULL DEFAULT -1,
  `PROGRAM_ID` bigint(20) NULL DEFAULT -1,
  `CREATED_BY` bigint(20) NULL DEFAULT -1,
  `CREATION_DATE` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  `LAST_UPDATED_BY` bigint(20) NULL DEFAULT -1,
  `LAST_UPDATE_DATE` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  `LAST_UPDATE_LOGIN` bigint(20) NULL DEFAULT -1,
  PRIMARY KEY (`MY_ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of my_test01
-- ----------------------------
INSERT INTO `my_test01` VALUES ('1', '小明', 20, '2016-10-13 00:00:00', 1, -1, -1, -1, '2018-11-22 18:37:24', -1, '2018-11-22 18:37:24', -1);
INSERT INTO `my_test01` VALUES ('2', '静静', 30, '2016-09-08 00:00:00', 1, -1, -1, -1, '2018-11-22 18:37:24', -1, '2018-11-22 18:37:24', -1);
INSERT INTO `my_test01` VALUES ('3', '傻龙', 20, '2016-10-20 00:00:00', 1, -1, -1, -1, '2018-11-22 18:37:24', -1, '2018-11-22 18:37:24', -1);
INSERT INTO `my_test01` VALUES ('4', '大壮', 40, '2016-10-01 00:00:00', 1, -1, -1, -1, '2018-11-22 18:37:24', -1, '2018-11-22 18:37:24', -1);

SET FOREIGN_KEY_CHECKS = 1;

```

表建好后直接上代码，然后跑起来，这个是代码结构。

![1542961918(1).jpg](https://i.loli.net/2018/11/23/5bf7bb1c3261e.jpg)

**在写完demo的代码后，不要多此一举去修改applicationContext-beans配置文件，因为框架本身就自己会去扫这些bean，我之前就是改了这个文件，导致程序无法跑起来。**

---

最后demo结果截图

![1542962146(1).jpg](https://i.loli.net/2018/11/23/5bf7bbf48da3c.jpg)