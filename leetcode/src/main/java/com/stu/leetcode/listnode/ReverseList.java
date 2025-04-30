package com.stu.leetcode.listnode;

import static com.stu.leetcode.listnode.ListNode.initNode;

class ReverseList {


    public ListNode reverseList(ListNode head) {

        if (head == null) return null;
        ListNode pre = null;
        ListNode cur = head;

        while (cur != null) {
            ListNode temp = cur.next;
            cur.next = pre;
            pre = cur;
            cur = temp;
        }
        return pre;
    }

    public static void main(String[] args) {
        ListNode head = ListNode.initNode(new int[]{1,2,3,4,5});
        ReverseList reverseList = new ReverseList();
        ListNode reversedHead = reverseList.reverseList(head);
        ListNode current = reversedHead;
        while (current != null) {
            System.out.print(current.val + " ");
            current = current.next;
        }

    }
}