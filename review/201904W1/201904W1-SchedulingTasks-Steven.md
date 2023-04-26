## Spring Scheduling Tasks

### From
> https://spring.io/guides/gs/scheduling-tasks/

### How to do
1. Maven导包，或者用Gradle或者IDE去构建需要的环境
2. 新建一个定时任务类
   1. 类中使用 `@Scheduled` 注解
   2. 例子中的 `fixedRate` 属性是指每次调用方法开始时到下一次开始时的时间，我们也可以使用其他的属性，比如 `fixedDelay` 指从完成调度到下一次调度之间的时间。
3. 运行定时任务
   1. `@EnableScheduling` 确保创建定时任务

### Summary
这个是一个很简单很基础的一个教程，相当于一个hello world，不过，通过简单的技术点，去加固自己的英文文档阅读能力，自认为有那么一点点用。
