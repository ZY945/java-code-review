package com.stu.leetcode.listnode;

import java.util.Arrays;

class ListNode {
    int val;
    ListNode next;

    ListNode(int x) {
        val = x;
        next = null;
    }

    public static ListNode initNode(int[] arr){
        System.out.println(Arrays.toString(arr));
        // [1, 2, 3, 3, 4, 4, 5]
        //1 2 3 4 5
        // 修复下

        if (arr == null || arr.length == 0) {
            return null;
        }
        ListNode head = new ListNode(arr[0]);
        ListNode current = head;
        for (int i = 1; i < arr.length; i++) {
            current.next = new ListNode(arr[i]);
            current = current.next;
        }
        return head;
    }
}