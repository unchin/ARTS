HashMap 是使用频率最高的类型之一，同时也是面试经常被问到的问题之一，这是因为 HashMap 的知识点有很多，同时它又属于 Java 基础知识的一部分，因此在面试中经常被问到。

本课时的面试题是，HashMap 底层是如何实现的？在 JDK 1.8 中它都做了哪些优化？

## 典型回答

在 JDK 1.7 中 HashMap 是以数组加链表的形式组成的，JDK 1.8 之后新增了红黑树的组成结构，当链表大于 8 并且容量大于 64 时，链表结构会转换成红黑树结构，它的组成结构如下图所示：

![img](https://s0.lgstatic.com/i/image3/M01/73/D9/Cgq2xl5rDYmAM-0hAABv6sMsyOQ867.png)

数组中的元素我们称之为哈希桶，它的定义如下：

	static class Node<K,V> implements Map.Entry<K,V> {
	final int hash;
	final K key;
	V value;
	Node<K,V> next;
	
	Node(int hash, K key, V value, Node<K,V> next) {
	    this.hash = hash;
	    this.key = key;
	    this.value = value;
	    this.next = next;
	}
	
	public final K getKey()        { return key; }
	public final V getValue()      { return value; }
	public final String toString() { return key + "=" + value; }
	
	public final int hashCode() {
	    return Objects.hashCode(key) ^ Objects.hashCode(value);
	}
	
	public final V setValue(V newValue) {
	    V oldValue = value;
	    value = newValue;
	    return oldValue;
	}
	
	public final boolean equals(Object o) {
	    if (o == this)
	        return true;
	    if (o instanceof Map.Entry) {
	        Map.Entry<?,?> e = (Map.Entry<?,?>)o;
	        if (Objects.equals(key, e.getKey()) &&
	            Objects.equals(value, e.getValue()))
	            return true;
	    }
	    return false;
	}
	}
可以看出每个哈希桶中包含了四个字段：hash、key、value、next，其中 next 表示链表的下一个节点。

JDK 1.8 之所以添加红黑树是因为一旦链表过长，会严重影响 HashMap 的性能，而红黑树具有快速增删改查的特点，这样就可以有效的解决链表过长时操作比较慢的问题。

## 考点分析

### 1.HashMap 源码分析

> 声明：本系列课程在未做特殊说明的情况下，都是以目前主流的 JDK 版本 1.8 为例来进行源码分析的。

HashMap 源码中包含了以下几个属性：

```
// HashMap 初始化长度
static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16

// HashMap 最大长度
static final int MAXIMUM_CAPACITY = 1 << 30; // 1073741824

// 默认的加载因子 (扩容因子)
static final float DEFAULT_LOAD_FACTOR = 0.75f;

// 当链表长度大于此值且容量大于 64 时
static final int TREEIFY_THRESHOLD = 8;

// 转换链表的临界值，当元素小于此值时，会将红黑树结构转换成链表结构
static final int UNTREEIFY_THRESHOLD = 6;

// 最小树容量
static final int MIN_TREEIFY_CAPACITY =

```

