/*
 * @lc app=leetcode.cn id=9 lang=java
 *
 * [9] 回文数
 */

// @lc code=start
class Solution {
    public boolean isPalindrome(int x) {
         //边界
         if (x < 0 || x % 10 == 0 && x!=0){
            return false;
        }

        int reverseNumber = 0;
        //规律
        while (x > reverseNumber) {
            //方法
            int a = x % 10;
            x = x / 10;
            reverseNumber = reverseNumber * 10 + a;
        }
        if (reverseNumber == x || reverseNumber/10 == x){
            return true;
        }

        //返回值
        return false;
    }
}
// @lc code=end

