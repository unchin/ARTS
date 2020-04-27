<!-- TOC -->

- [IDEA 插件](#idea-%E6%8F%92%E4%BB%B6)
  - [说明](#%E8%AF%B4%E6%98%8E)
  - [必备](#%E5%BF%85%E5%A4%87)
    - [1. String Maniplation](#1-string-maniplation)
    - [2. Maven Helper](#2-maven-helper)
    - [3. Lombok](#3-lombok)
    - [4. JRebel for Intellij](#4-jrebel-for-intellij)
    - [5. HighlightBracketPair](#5-highlightbracketpair)
    - [6. GsonFormat](#6-gsonformat)
    - [7. Alibaba Java Coding Guidelines](#7-alibaba-java-coding-guidelines)
    - [8. Free Mybatis pligin](#8-free-mybatis-pligin)
    - [9. Grep Console](#9-grep-console)
  - [选配](#%E9%80%89%E9%85%8D)
    - [1. Rainbow Brackets](#1-rainbow-brackets)
    - [2. Power Mode 2](#2-power-mode-2)
    - [3. Mybatislog Plugin](#3-mybatislog-plugin)
    - [4. Key Promoter X](#4-key-promoter-x)
    - [5. Background Image Plus](#5-background-image-plus)
    - [6. .ignore](#6-ignore)
    - [7. CodeGlance](#7-codeglance)

<!-- /TOC -->

# IDEA 插件

## 说明

网上idea插件推荐确实很多很多，这个是自己用的习惯的，推荐出来。

## 必备

### 1. String Maniplation

强大的字符串转换工具。使用快捷键，Alt+m。

*  切换样式（camelCase, hyphen-lowercase, HYPHEN-UPPERCASE, snake_case, SCREAMING_SNAKE_CASE, dot.case, words lowercase, Words Capitalized, PascalCase）
*  转换为SCREAMING_SNAKE_CASE (或转换为camelCase)
*  转换为 snake_case (或转换为camelCase)
*  转换为dot.case (或转换为camelCase)
*  转换为hyphen-case (或转换为camelCase)
*  转换为hyphen-case (或转换为snake_case)
*  转换为camelCase (或转换为Words)
*  转换为camelCase (或转换为lowercase words)
*  转换为PascalCase (或转换为camelCase)
*  选定文本大写
*  样式反转

### 2. Maven Helper

idea 中解决maven 包冲突的问题，找到冲突的jar右键 Exclude即可解决。

### 3. Lombok

Java语言，每次写实体类的时候都需要写一大堆的setter，getter，如果bean中的属性一旦有修改、删除或增加时，需要重新生成或删除get/set等方法，给代码维护增加负担，这也是Java被诟病的一种原因。

Lombok则为我们解决了这些问题，使用了lombok的注解(@Setter,@Getter,@ToString,@@RequiredArgsConstructor,@EqualsAndHashCode或@Data)之后，就不需要编写或生成get/set等方法，很大程度上减少了代码量，而且减少了代码维护的负担。

依赖

```
<dependency>
<groupId>org.projectlombok</groupId>
<artifactId>lombok</artifactId>
<version>1.16.18</version>
<scope>provided</scope>
</dependency>
```
然后就在实体类前加上 @Data 注解就好了。

### 4. JRebel for Intellij

JRebel是一种热部署生产力工具，修改代码后不用重新启动程序，所有的更改便可以生效。它跳过了Java开发中常见的重建、重新启动和重新部署周期。

这个插件的使用方法就比较复杂了，需要你去谷歌一下顺便把激活方法也查到。

**注意，这个虽然麻烦，但是确实是非常有用的插件**

### 5. HighlightBracketPair

自动化高亮显示光标所在代码块对应的括号，可以定制颜色和形状，我个人觉得这个在看他人代码的时候那是相当的有帮助。

### 6. GsonFormat

可根据json数据快速生成java实体类。

自定义个javaBean(无任何内容，就一个空的类)，复制你要解析的Json，然后alt+insert弹出如下界面或者使用快捷键 Alt+S，在里面粘贴刚刚复制的Json，点击OK即可。

### 7. Alibaba Java Coding Guidelines

阿里巴巴代码规范检查插件

### 8. Free Mybatis pligin

mybatis 插件，这种插件其实很多，但是有几个比较有名的，都是需要收费的，这个免费。我们开发中使用mybatis时时长需要通过mapper接口查找对应的xml中的sql语句，该插件方便了我们的操作。

安装完成重启IDEA之后，我们会看到code左侧或多出一列绿色的箭头，点击箭头我们就可以直接定位到xml相应文件的位置。

### 9. Grep Console

自定义日志颜色，idea 控制台可以彩色显示各种级别的 log，安装完成后，在 console 中右键就能打开。

并且可以设置不同的日志级别的显示样式。

可以直接根据关键字搜索你想要的，搜索条件是支持正则表达式的。

## 选配

### 1. Rainbow Brackets

彩色显示所有括号，这个就只是美化啦，不过在看括号的层次的时候，也是很有帮助的。

### 2. Power Mode 2

根据Atom的插件activate-power-mode的效果移植到IDEA上，不过这个插件需要设置，把屏幕抖动关了，不然真的看到脑瓜疼。具体可以参考我的设置。

![image.png](https://upload-images.jianshu.io/upload_images/11571828-a096ef9a9e0eba3e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 3. Mybatislog Plugin

Mybatis现在是java中操作数据库的首选，在开发的时候，我们都会把Mybatis的脚本直接输出在console中，但是默认的情况下，输出的脚本不是一个可以直接执行的。

如果我们想直接执行，还需要在手动转化一下。

MyBatis Log Plugin 这款插件是直接将Mybatis执行的sql脚本显示出来，无需处理，可以直接复制出来执行的，如图：

![image.png](https://upload-images.jianshu.io/upload_images/11571828-a841df6d0f3efa4c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

执行程序后，我们可以很清晰的看到我们执行了哪些sql脚本，而且脚本可以执行拿出来运行。

### 4. Key Promoter X

IntelliJ IDEA的快捷键提示插件，会统计你鼠标点击某个功能的次数，提示你应该用什么快捷键，帮助记忆快捷键，等熟悉了之后可以关闭掉这个插件。

### 5. Background Image Plus

idea背景修改插件，让你的idea与众不同，可以设置自己喜欢的图片作为code背景。

安装成功之后重启，菜单栏的VIew标签>点击Set Background Image(没安装插件是没有这个标签的)，在弹框中路由选择到本地图片，点击OK即可。

### 6. .ignore

git提交时过滤掉不需要提交的文件，很方便，有些本地文件是不需要提交到Git上的。

![image.png](https://upload-images.jianshu.io/upload_images/11571828-6ad2e47da1b0c0ca.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 7. CodeGlance

类似SublimeText的Mini Map插件。