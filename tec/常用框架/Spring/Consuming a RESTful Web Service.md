### From
> https://spring.io/guides/gs/consuming-rest/

### How to do
1. Maven导包，或者用Gradle或者IDE去构建需要的环境
2. 新建一个我们需要用到的引用实体类
   1. 类中使用了 `@JsonIgnoreProperties ` 这个注解，表示忽略掉未绑定任何属性的字段类型
   2. 如果我们需要绑定自定义类型，那变量名就需要是 json 文档中提供的命名。
   3. 如果绑定自定义类型并且命名与 json 文档不一致，那我们就需要使用 `@JsonProperty`注解
3. 上面实体类中的数据类型也需要再建立一个类，内部引用所用
4. 编写应用类，使用 SpringBoot 中的 `RestTemplate`
   1. 由于Jackson JSON处理库位于类路径中，因此 `RestTemplate` 将使用它（通过消息转换器）将传入的 JSON 数据转换为 `Quote` 对象.
   2. 在这里，使用这种方法只能应用 `GET` 请求，要使用其他的几种 HTTP 请求，我们可以换一种方法
5. 优化改写应用程序
   1. 使用 `SpringBootApplication` 注解声明主函数
   2. 将 `RestTemplate` 移动到 `CommandLineRunner` 回调，以便它在启动时由 SpringBoot 执行

### Summary
完成了一个最基础的 REST 客户端，而我们与前端交互时，用的就是 REST 接口规范。