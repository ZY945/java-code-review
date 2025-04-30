package com.stu.leetcode.listnode;

import java.util.HashSet;

public class DeleteDuplicates {


    public static ListNode deleteDuplicates(ListNode head) {
        HashSet<Integer> set = new HashSet<>();
        ListNode res = new ListNode(-1);
        res.next=head;
        ListNode cur = res;
        while (cur.next != null) {
            if(!set.add(cur.next.val)){
                cur.next=cur.next.next;
            }else {
                cur=cur.next;
            }
        }
        return res.next;
    }

    public static void main(String[] args) {
        ListNode head = ListNode.initNode(new int[]{1,2,3,3,4,4,5});

        ListNode reversedHead = deleteDuplicates(head);
        ListNode current = head;
        while (current != null) {
            System.out.print(current.val + " ");
            current = current.next;
        }
    }
}
