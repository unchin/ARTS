# Java复制数组的四种方法
---

**System.arraycopy > 使用clone方法 > Array.copyOf > for 循环逐一复制**
<br/>
### 1. System.arraycopy方法
    
                public static void arraycopy(Object src, int srcPos,Object dest,int destPos,int length)  

**API中的解释：**从指定源数组中复制一个数组，复制从指定的位置开始，到目标数组的指定位置结束。从src引用的源数组到dest引用的目标数组，数组组件的一个子序列被复制下来。被复制的组件的编号等于length参数。源数组中位置在srcPos到srcPos+length-1之间的组件被分别复制到目标数组中的destPos到destPos+length-1位置

                 public class test{
            public static void main(String[] args) {

                int[] a1={1,2,3,4,5,6,7};
                int[] a2={8,9,10,11,12,13};

                System.arraycopy(a1, 1, a2, 2, 2);
                System.out.print("copy后结果：");
                for(int i=0;i<a2.length;i++){
                    System.out.print(a2[i]+" ");
                }
            }
        }
        
                copy后结果：8 9 2 3 12 13 
### 2.Clone方法

clone翻译就是复制， 在Java语言中， clone方法被对象调用，所以会复制对象。所谓的复制对象，首先要分配一个和源对象同样大小的空间，在这个空间中创建一个新的对象。

- 关于Clone的深入研究见链接
- https://blog.csdn.net/zjkc050818/article/details/76098354

<br />

# 面向对象的五个基本原则
---

- **单一职责原则（Single-Resposibility Principle）**：一个类，最好只做一件事，只有一个引起它的变化。单一职责原则可以看做是低耦合、高内聚在面向对象原则上的引申，将职责定义为引起变化的原因，以提高内聚性来减少引起变化的原因。 
- **开放封闭原则（Open-Closed principle）**：软件实体应该是可扩展的，而不可修改的。也就是，对扩展开放，对修改封闭的。 
- **Liskov替换原则（Liskov-Substituion Principle）**：子类必须能够替换其基类。这一思想体现为对继承机制的约束规范，只有子类能够替换基类时，才能保证系统在运行期内识别子类，这是保证继承复用的基础。 
- **依赖倒置原则（Dependecy-Inversion Principle）**：依赖于抽象。具体而言就是高层模块不依赖于底层模块，二者都同依赖于抽象；抽象不依赖于具体，具体依赖于抽象。 
- **接口隔离原则（Interface-Segregation Principle）**：使用多个小的专门的接口，而不要使用一个大的总接口

*助记：*

s( Single-Resposibility Principle ): 单一职责原则

o( Open-Closed principle ): 开放封闭原则

l( Liskov-Substituion Principle ): 里氏原则

i( Interface-Segregation Principle ): 接口隔离原则

d( Dependecy-Inversion Principle ): 依赖倒置原则

一个单词：立方体(solid),很好记!!!

<br />

# String与正则表达式
---

1. 有时候有这样的需求：对字符串需要匹配，查找，替换等操作.
2. 什么是正则表达式？是一个有规律的，有特殊意义的一串字符，通常用来匹配，查找等操作。
3. 正则表达式**常用符号：**

        *：任意字符；

        [abc]：表示abc中任意一个字母

        [^abc]:除了abc之外的任意字符

        [0-9]:表示0-9的任意一个数字

        [a-z]:表示a-z中任意一个字母

        [0-9a-zA-Z_]:表示这些字符中任意一个 

        [a-z&&[^abc]]:表示a-z中除了abc之外的任意一个字母

        [/d]:表示0-9中任意一个数字 

        [/D]:表示非数字中任意一个字符 

        [/w]:表示单词字符中任意一个相当于[0-9a-zA-Z_]字母数字下划线

        [/W:表示除了单词字符中任意一个

        \s:表示[\n\t\r\f],空白

        \S:非空白

        [X]*:匹配0个以上的X

        [X]?:匹配0个或1个X

        [X]+:匹配1个以上的X

        [X]{n}:匹配n个X

        [X]{n,}:匹配n个以上的X

        [X]{n,m}:匹配n到m个

        "^" :从头开始检查字符串是否匹配正则表达式

        “&”：检查字符串的结尾是否匹配正则表达式