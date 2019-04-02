## Leetcode #7 

### 整数反转 Reverse Integer

### 描述：

```
Given a 32-bit signed integer, reverse digits of an integer.

Example 1:

Input: 123
Output: 321
Example 2:

Input: -123
Output: -321
Example 3:

Input: 120
Output: 21

Note:
Assume we are dealing with an environment which could only store integers within the 32-bit signed integer range: [−2^31,  2^31 − 1]. For the purpose of this problem, assume that your function returns 0 when the reversed integer overflows.
```

> 给出一个 32 位的有符号整数，你需要将这个整数中每位上的数字进行反转。
> 
> 假设我们的环境只能存储得下 32 位的有符号整数，则其数值范围为 [−2^31,  2^31 − 1]。请根据这个假设，如果反转后整数溢出那么就返回 0。
 

### 解决方案：
#### 思路：
我们可以一次构建反转整数的一位数字。在这样做的时候，我们可以预先检查向原整数附加另一位数字是否会导致溢出。

#### 算法：

反转整数的方法可以与反转字符串进行类比。

重复“弹出” x 的最后一位数字，并将它“推入”到 rev 的后面。最后，rev 将与 x 相反。

题目要求环境只能用数学方法。

要在没有辅助堆栈 / 数组的帮助下 “弹出” 和 “推入” 数字，使用数学方法。

```
//pop operation:
pop = x % 10;
x /= 10;

//push operation:
temp = rev * 10 + pop;
rev = temp;
```

但是，这种方法很危险，因为当 temp=rev*10+pop 时会导致溢出。

幸运的是，事先检查这个语句是否会导致溢出很容易。

我们先假设 rev 是正数。

如果 temp=rev⋅10+pop 导致溢出，那么一定有 rrev*10 >= INTMAX。

如果 rev*10 > INTMAX，那么 temp=rev⋅10+pop 一定会溢出。

如果 rev*10 == INTMAX，那么只要 pop>7，temp=rev⋅10+pop 就会溢出。

当 rev 为负时可以应用类似的逻辑。



```
class Solution {
    public int reverse(int x) {
        int rev = 0;
        while (x != 0) {
            int pop = x % 10;
            x /= 10;
            if (rev > Integer.MAX_VALUE/10 || (rev == Integer.MAX_VALUE / 10 && pop > 7)) return 0;
            if (rev < Integer.MIN_VALUE/10 || (rev == Integer.MIN_VALUE / 10 && pop < -8)) return 0;
            rev = rev * 10 + pop;
        }
        return rev;
    }
}
```

**复杂度分析：**

时间复杂度：O(log(x))，x 中大约有 log10(x)位数字。

空间复杂度：O(1)。

