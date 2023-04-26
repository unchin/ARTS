# Java Spring各种注解的理解

我们在了解注解的时候，不要求每个都记住，但是要保有印象，在需要的时候能提取出来，知道有这么一个东西，再查找相关资料，这样在工作中能提高效率。

Spring中的注解大概可以分为两大类：

1）spring的bean容器相关的注解，或者说bean工厂相关的注解；
2）springmvc相关的注解。

spring的bean容器相关的注解，先后有：@Required， @Autowired, @PostConstruct, @PreDestory，
还有Spring3.0 开始支持的 JSR-330 标准 javax.inject.* 中的注解：@Inject, @Named, @Qualifier, @Provider, @Scope, @Singleton.

springmvc 相关的注解有：@Controller, @RequestMapping, @RequestParam， @ResponseBody 等等。

要理解Spring中的注解，先要理解Java中的注解。

1. Java中的注解

 Java中1.5中开始引入注解，我们最熟悉的应该是：@Override, 它的定义如下：

        /**
         * Indicates that a method declaration is intended to override a
         * method declaration in a supertype. If a method is annotated with
         * this annotation type compilers are required to generate an error
         * message unless at least one of the following conditions hold:
         * The method does override or implement a method declared in a
         * supertype.
         * The method has a signature that is override-equivalent to that of
         * any public method declared in Object.
         *
         * @author  Peter von der Ah&eacute;
         * @author  Joshua Bloch
         * @jls 9.6.1.4 @Override
         * @since 1.5
         */
        @Target(ElementType.METHOD)
        @Retention(RetentionPolicy.SOURCE)
        public @interface Override {
        }

从注释，我们可以看出，@Override 的作用是，**提示编译器，使用 @Override 注解的方法必须要是 override 父类或者 java.lang.Object 中的一个同名方法。**我们看到 @Override 的定义中使用到了 @Target, @Retention，它们就是**所谓的“元注解”——就是定义注解的注解，或者说注解注解的注解**。我们看下@Retention


        /**
         * Indicates how long annotations with the annotated type are to
         * be retained.  If no Retention annotation is present on
         * an annotation type declaration, the retention policy defaults to
         * RetentionPolicy.CLASS.
         */
        @Documented
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.ANNOTATION_TYPE)
        public @interface Retention {
            /**
             * Returns the retention policy.
             * @return the retention policy
             */
            RetentionPolicy value();
        }

@Retention用于提示注解被保留多长时间，有三种取值：


```
public enum RetentionPolicy {
    /**
     * Annotations are to be discarded by the compiler.
     */
    SOURCE,
    /**
     * Annotations are to be recorded in the class file by the compiler
     * but need not be retained by the VM at run time.  This is the default
     * behavior.
     */
    CLASS,
    /**
     * Annotations are to be recorded in the class file by the compiler and
     * retained by the VM at run time, so they may be read reflectively.
     *
     * @see java.lang.reflect.AnnotatedElement
     */
    RUNTIME
}
```

>RetentionPolicy.SOURCE 保留在源码级别，被编译器抛弃(@Override就是此类)； RetentionPolicy.CLASS被编译器保留在编译后的类文件级别，但是被虚拟机丢弃；
RetentionPolicy.RUNTIME保留至运行时，可以被反射读取。

再看 @Target:

```
package java.lang.annotation;

/**
 * Indicates the contexts in which an annotation type is applicable. The
 * declaration contexts and type contexts in which an annotation type may be
 * applicable are specified in JLS 9.6.4.1, and denoted in source code by enum
 * constants of java.lang.annotation.ElementType
 * @since 1.5
 * @jls 9.6.4.1 @Target
 * @jls 9.7.4 Where Annotations May Appear
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Target {
    /**
     * Returns an array of the kinds of elements an annotation type
     * can be applied to.
     * @return an array of the kinds of elements an annotation type
     * can be applied to
     */
    ElementType[] value();
}
```
 @Target用于提示该注解使用的地方，取值有：

```
public enum ElementType {
    /** Class, interface (including annotation type), or enum declaration */
    TYPE,
    /** Field declaration (includes enum constants) */
    FIELD,
    /** Method declaration */
    METHOD,
    /** Formal parameter declaration */
    PARAMETER,
    /** Constructor declaration */
    CONSTRUCTOR,
    /** Local variable declaration */
    LOCAL_VARIABLE,
    /** Annotation type declaration */
    ANNOTATION_TYPE,
    /** Package declaration */
    PACKAGE,
    /**
     * Type parameter declaration
     * @since 1.8
     */
    TYPE_PARAMETER,
    /**
     * Use of a type
     * @since 1.8
     */
    TYPE_USE
}
```
分别表示该注解可以被使用的地方：
>1)类,接口，注解，enum; 
2)属性域；
3）方法；
4）参数；
5）构造函数；
6）局部变量；
7）注解类型；
8）包

所以：
```
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Override {
}
```
表示 @Override 只能使用在方法上，保留在源码级别，被编译器处理，然后抛弃掉。

还有一个经常使用的元注解 @Documented ：

```
/**
 * Indicates that annotations with a type are to be documented by javadoc
 * and similar tools by default.  This type should be used to annotate the
 * declarations of types whose annotations affect the use of annotated
 * elements by their clients.  If a type declaration is annotated with
 * Documented, its annotations become part of the public API
 * of the annotated elements.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Documented {
}
```
表示注解是否能被 javadoc 处理并保留在文档中。
