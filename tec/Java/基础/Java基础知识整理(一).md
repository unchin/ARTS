## java数组的初始化

- 静态初始化：直接分配数据，不需要提前开辟数组空间

- 动态初始化：需要提前开辟数组空间（需要new）

## 字符串String的比较
- ==：双等于号是比较的地址

- equals：比较的是内容

## 字符串常用方法
1. 字符串长度：length()方法
2. 字符串转换数组：toCharArray()
3. 从字符串中取出指定位置的字符：charAt()
4. 字符串与byte数组的转换：getBytes（）
5. 过滤字符串中存在的字符：indexOf()
6. 去掉字符串的前后空格：trim()
7. 从字符串中取出子字符串：subString()
8. 大小写转换：toLowerCase() toUpperCase()
9. 判断字符串的开头结尾字符：endWith() startWith()
10. 替换String字符串中的一个字符：replace()

## 常见异常
1. 数组越界异常：ArrayIndexOutOfBoundsException
2. 数字格式化异常：NumberFormatException
3. 算数异常：ArithmeticException
4. 空指针异常：NullPointerException

## 面向对象是三大特征
1. 封装性：对外部不可见
2. 继承：扩展类的功能
3. 多态性：方法的重载

## 构造方法
- 格式：
      访问修饰符 类名称（）{
        程序语句
      }
- 注意点：
      （1）构造方法必须与类名一致
      （2）构造方法没有返回值
1. 构造方法主要是为类中的属性初始化
2. 每个类在实例化之后都会调用构造方法，如果没有构造方法，程序在编译的时候会创建一个无参的什么都不做的构造方法。
## 引用传递
- string类型的引用传递是不可改变的

## static关键字
1. 使用static声明全局属性
2. 使用static申明方法，直接通过类名调用
3. 使用static方法的时候，只能访问static声明的属性和方法，而非static声明的属性和方法是不能访问的


