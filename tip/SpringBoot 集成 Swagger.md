## 场景与定义
没有API文档工具之前，大家都是手写API文档的（维护起来相当困难），在什么地方书写的都有，有在confluence上写的，有在对应的项目目录下readme.md上写的，每个公司都有每个公司的玩法，无所谓好坏。

Swagger 是一个规范和完整的框架，用于生成、描述、调用和可视化 RESTful 风格的 Web 服务。总体目标是使客户端和文件系统作为服务器以同样的速度来更新。文件的方法，参数和模型紧密集成到服务器端的代码，允许API来始终保持同步。Swagger 让部署管理和使用功能强大的API简单。

## 操作步骤
### 配置pom.xml
```
<!-- Swagger -->
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger2</artifactId>
            <version>2.9.2</version>
        </dependency>
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger-ui</artifactId>
            <version>2.9.2</version>
        </dependency>
        <dependency>
            <groupId>com.github.caspar-chen</groupId>
            <artifactId>swagger-ui-layer</artifactId>
            <version>1.1.3</version>
        </dependency>
```
这里没有使用swagger 默认的ui，用一个第三方ui 替换。

### 添加Swagger配置类
忽略（因为其实不配置也是可以用的）

如果需要自定义一些东西，就需要修改配置文件。

### 启动类
```
@SpringBootApplication
@EnableSwagger2
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```
在启动类中添加注解

### 配置controller

```
@RestController
@RequestMapping(value = "/hello")
@Api(value = "/hello",tags = "DEMO")
public class HelloController {

    @Resource
    HelloService helloService;


    @GetMapping("/hello")
    @ApiOperation(value = "你好")
    public String say(){
        return "Hello Steven!";
    }
```
- 在每个controller类中加上注解 `@Api(tags = "这里写这一类接口作用")`
- 在每个接口上加上注解 ` @ApiOperation(value = "这里写这一个接口的作用")`
- 如果需要写每一个参数的描述，加上注解 ` @ApiImplicitParams`

### Test
接口完成后访问路径为 http://localhost:8080/docs.html#/ （端口号默认8080）