## Spring几个特殊的注解
`@Service`用于标注业务层组件，
`@Controller`用于标注控制层组件（如struts中的action），
`@Repository`用于标注数据访问(持久层)组件，即DAO组件，
`@Component`泛指组件，当组件不好归类的时候，我们可以使用这个注解进行标注。

如果 Web 应用程序采用了经典的三层分层结构的话，最好在持久层、业务层和控制层分别采用 @Repository、@Service 和 @Controller 对分层中的类进行注释，而用 @Component 对那些比较中立的类进行注释。 

在一个稍大的项目中，通常会有上百个组件，如果这些组件采用 xml 的 bean 定义来配置，显然会增加配置文件的体积，查找以及维护起来也不太方便。 Spring2.5为我们引入了组件自动扫描机制，他可以在类路径底下寻找标注了@Component , @Service , @Controller , @Repository 注解的类，并把这些类纳入进 spring 容器中管理。它的作用和在 xml 文件中使用 bean 节点配置组件时一样的。

**首先，用注解来向Spring容器注册Bean，要使用自动扫描机制，需要在applicationContext.xml中注册:**

    <context:component-scan base-package=”pagkage1[,pagkage2,…,pagkageN]”/>
如：在base-package指明一个包，指自动扫描该包下面的所有注解

    <context:component-scan base-package="cn.gacl.java"/>
表明`cn.gacl.java`包及其子包中，如果某个类的头上带有特定的注解`@Component/@Repository/@Service/@Controller`，就会将这个对象作为Bean注册进Spring容器。也可以在`<context:component-scan base-package=” ”/>`中指定多个包，如：

    <context:component-scan base-package="cn.gacl.dao.impl,cn.gacl.service.impl,cn.gacl.action"/>
多个包用逗号隔开。

**PS** 如果使用一些框架，则框架内已经封装好了配置信息，所以不需要再重新配置。

### @Component
@Component 是所有受 Spring 管理组件的通用形式，@Component 注解可以放在类的头上，但是 @Component 不是很推荐使用。

### @Controller
@Controller对应表现层的Bean，也就是Action，例如：

    @Controller
    @Scope("prototype")
    public class UserAction extends BaseAction<User>{
    ……
    }
使用 @Controller 注解标识 UserAction 之后，就表示要把 UserAction交给Spring 容器管理，在 Spring 容器中会存在一个名字为 "userAction" 的 action ，这个名字是根据 UserAction 类名来取的。

**注意：**如果 @Controller 不指定其 value ，则默认的 bean 名字为这个类的类名首字母小写，如果指定 value `@Controller(value="UserAction")` 或者`@Controller("UserAction")`，则使用 value 作为 bean 的名字。

**PS** 这里的 UserAction 还使用了 @Scope 注解，`@Scope("prototype")`表示将 Action 的范围声明为原型，可以利用容器的 `scope="prototype"` 来保证每一个请求有一个单独的Action来处理，避免 struts 中 Action 的线程安全问题。
spring 默认 scope 是单例模式 `(scope="singleton")` ，这样只会创建一个 Action 对象，每次访问都是同一 Action 对象，数据不安全，struts2 是要求每次次访问都对应不同的Action，`scope="prototype"` 可以保证当有请求的时候都创建一个Action对象

### @ Service
@Service对应的是业务层Bean，例如：

    @Service("userService")
    public class UserServiceImpl implements UserService {
    ………
    }
`@Service("userService")`注解是告诉 Spring ，当 Spring 要创建 UserServiceImpl 的的实例时，bean 的名字必须叫做 "userService" ，这样当 Action 需要使用 UserServiceImpl 的实例时,就可以由 Spring 创建好的 "userService" ，然后注入给 Action ;在 Action 只需要声明一个名字叫 “userService” 的变量来接收由 Spring 注入的 "userService" 即可，具体代码如下：

    // 注入userService
    @Resource(name = "userService")
    private UserService userService;
**注意：**在 Action 声明的 “userService” 变量的类型**必须是 “UserServiceImpl” 或者是其父类 “UserService”** ，否则由于类型不一致而无法注入。
由于 Action 中的声明的 “userService” 变量使用了 @Resource 注解去标注，并且指明了其 name = "userService" ，这就等于告诉 Spring ，说我 Action 要实例化一个 “userService” ，你 Spring 快点帮我实例化好，然后给我，当 Spring 看到 userService 变量上的 @Resource 的注解时，根据其指明的 name 属性可以知道，Action 中需要用到一个 UserServiceImpl 的实例，此时 Spring 就会把自己创建好的名字叫做 "userService" 的 UserServiceImpl 的实例注入给 Action 中的 “userService” 变量，帮助 Action 完成 userService 的实例化，这样在 Action 中就不用通过` “UserService userService = new UserServiceImpl();”` 这种最原始的方式去实例化 userService 了。

如果没有 Spring ，那么当 Action 需要使用 UserServiceImpl 时，必须通过`“UserService userService = new UserServiceImpl();”`主动去创建实例对象，但使用了 Spring 之后， Action 要使用 UserServiceImpl 时，就不用主动去创建 UserServiceImpl 的实例了，创建 UserServiceImpl 实例已经交给 Spring 来做了， Spring 把创建好的 UserServiceImpl 实例给 Action ， Action 拿到就可以直接用了。

Action 由原来的主动创建 UserServiceImpl 实例后就可以马上使用，变成了被动等待由 Spring 创建好 UserServiceImpl 实例之后再注入给 Action ， Action 才能够使用。**这说明 Action 对 “UserServiceImpl” 类的“控制权”已经被“反转”了，**原来主动权在自己手上，自己要使用 “UserServiceImpl” 类的实例，自己主动去 new 一个出来马上就可以使用了，但现在自己不能主动去 new“UserServiceImpl” 类的实例， new“UserServiceImpl” 类的实例的权力已经被 Spring 拿走了，只有 Spring 才能够 new“UserServiceImpl” 类的实例，而 Action 只能等 Spring 创建好 “UserServiceImpl” 类的实例后，再“恳求” Spring 把创建好的 “UserServiceImpl” 类的实例给他，这样他才能够使用 “UserServiceImpl” ，**这就是 Spring 核心思想“控制反转”，也叫“依赖注入”。**

**“依赖注入”**也很好理解， Action 需要使用 UserServiceImpl 干活，那么就是对 UserServiceImpl 产生了依赖， Spring 把 Acion 需要依赖的 UserServiceImpl 注入(也就是“给”)给 Action ，这就是所谓的“依赖注入”。**对 Action 而言， Action 依赖什么东西，就请求 Spring 注入给他，对 Spring 而言， Action 需要什么， Spring 就主动注入给他。**

### @ Repository
@Repository 对应数据访问层 Bean ，例如：

     @Repository(value="userDao")
     public class UserDaoImpl extends BaseDaoImpl<User> {
     ………
     }
`@Repository(value="userDao")`注解是告诉 Spring ，让 Spring 创建一个名字叫 “userDao” 的 UserDaoImpl 实例。

当 Service 需要使用 Spring 创建的名字叫 “userDao” 的 UserDaoImpl 实例时，就可以使用 `@Resource(name = "userDao") `注解告诉 Spring ，Spring 把创建好的 userDao 注入给 Service 即可。

    // 注入userDao，从数据库中根据用户Id取出指定用户时需要用到
     @Resource(name = "userDao")
     private BaseDao<User> userDao;
