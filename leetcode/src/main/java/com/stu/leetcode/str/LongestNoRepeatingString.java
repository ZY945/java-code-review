package com.stu.leetcode.str;

import java.util.HashMap;

/**
 * https://leetcode.cn/problems/longest-substring-without-repeating-characters/description/?envType=study-plan-v2&envId=top-100-liked
 * 3. 无重复字符的最长子串
 */
public class LongestNoRepeatingString {
    public static void main(String[] args) {
    }


    public int lengthOfLongestSubstring(String s) {
        if (s.length() == 1) return 1;
        HashMap<Character, Integer> map = new HashMap<>();
        int res = 0;
        int lastIndex = 0;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (map.containsKey(ch)) {
                lastIndex = Math.max(lastIndex, map.get(ch) + 1);
            }
            map.put(ch, i);
            res = Math.max(res, i - lastIndex + 1);
        }

        return res;
    }
}
