## Consuming a RESTful Web Service

### From
> https://spring.io/guides/gs/consuming-rest/

### How to do
1. Maven导包，或者用Gradle或者IDE去构建需要的环境
2. 新建一个我们需要用到的引用实体类
   1. 类中使用了 `@JsonIgnoreProperties ` 这个注解，表示忽略掉未绑定任何属性的字段类型
   2. 如果我们需要绑定自定义类型，那变量名就需要是 json 文档中提供的命名。
   3. 如果绑定自定义类型并且命名与 json 文档不一致，那我们就需要使用 `@JsonProperty`注解

