# hap-book-demo 

## 写在前面

这个demo主要记录排错过程，都是低级错误，有点愧疚。

https://github.com/guoyongqin/hap-todo-service-parent/tree/master/hap-book-service

> 项目需求：
> 1. 完成基本CRUD（增删改查）
> 2. 完成多表关联查询

## 记录排查错误过程(调Bug)

### 1. spring boot 报错 Unable to start embedded container;

https://blog.csdn.net/fhhffchffy/article/details/50377934

运行时候报错，查资料后发现是在主函数`BookServiceApplication`页面没有添加
`@EnableEurekaClient `
`@SpringBootApplication `
这两个注解

```
@EnableEurekaClient
@SpringBootApplication
public class BookServiceApplication {
    public static void main(String[] args){
        SpringApplication.run(BookServiceApplication.class,args);
    }
}

```

### 2. Spring Boot排查 Cannot determine embedded database driver class for database type NONE

https://blog.csdn.net/hengyunabc/article/details/78762097

文中的方法没有解决问题,但是确实可以提供思路
最终是 
1. 配置文件`bootstrap.yml`中的`MyBatis`中`classpath`路径与文件路径不匹配。

```
server:
  port: 8051
mybatis:
  mapperLocations: classpath*:/mapper/*.xml
  configuration:
    mapUnderscoreToCamelCase: true
```

2. 在application-default.yml文件中修改了数据库配置代码的缩进距离

```
spring:
  datasource:
    url: jdbc:mysql://rm-2zeah762u44mddngavo.mysql.rds.aliyuncs.com/hap_demo_service_todo?useUnicode=true&characterEncoding=utf-8&useSSL=false
    username: ********
    password: ********
  eureka:
    client:
      serviceUrl:
        defaultZone: http://localhost:8000/eureka
swagger:
    oauthUrl: http://localhost:8080/oauth/oauth/authorize
```


### 3. create方法实现失败

在swagger中新增数据失败，提示`id.isDelete`

通过debug查错，发现是`TAuthorServiceImpl`实现类的问题,在create方法中使用了`getId()`的方法，然而`id`字段在数据库中是自增的，所以`getId()`取出的是null空值，所以会引起空指针异常。

```
@Override
    public TAuthor create(TAuthor tAuthor) {
        tAuthor.setAuthorUuid(UUID.randomUUID().toString());
        tAuthor.setProcessingStatus("created");
        tAuthor.setReferenceSource("gyq");
        if (findById(tAuthor.getId()) != null) {  //这里取出的是null
            throw new HapException("error.author.authorIdExist");
        }
        if (insert(tAuthor) != 1) {
            throw new HapException("error.author.insertNotOne");

        }
        return selectByPrimaryKey(tAuthor.getId());
    }
```

将该方法删除，就可以正常运行了。

```
//删除的内容
if (findById(tAuthor.getId()) != null) {
            throw new HapException("error.author.authorIdExist");
        }
```

### 4. OrderController中orderService报错

>今天发现其实这个问题是经常出现的，我的这个解决方式并不是问题的根源，详细的看链接，这个是别人整理的：
https://blog.csdn.net/gaoshan12345678910/article/details/80973908


报错信息：


```
2018-08-09 18:18:44.129 ERROR [hap-book2-service,,,] 4860 --- [           main] o.s.b.d.LoggingFailureAnalysisReporter   : 

***************************
APPLICATION FAILED TO START
***************************

Description:

Field orderService in com.hand.hap.cloud.book.controller.OrderController required a bean of type 'com.hand.hap.cloud.book.service.OrderService' that could not be found.


Action:

Consider defining a bean of type 'com.hand.hap.cloud.book.service.OrderService' in your configuration.


Process finished with exit code 1
````


定位代码中：


```

    @Autowired
    private OrderService orderService;


```



查错之后发现是实现类中没有添加注解，添加之后就好了：



```
@Service
@Transactional(rollbackFor = HapException.class)
```


## 总结：
1. 在编写代码时候要注意文件路径匹配问题。
2. 有些格式的文件代码，缩进要求严格，需要特别注意。
3. 自增的字段，用`getId()`方法只能获取到null
4. 注解的使用很重要，一旦缺失，就容易出现错误
