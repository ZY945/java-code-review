package com.stu.leetcode.listnode;

class ReverseBetween {


    public static ListNode reverseBetween(ListNode head, int left, int right) {
        // 初始化dummyNode节点
        ListNode dummyNode = new ListNode(-1);
        dummyNode.next = head;


        // 获取反转的起始节点
        ListNode pre = dummyNode;
        for (int i = 0; i < left - 1; i++) {
            pre=pre.next;
        }

        ListNode cur = pre.next;

        // 反转指定区域right-left
        for (int i = 0; i < right - left; i++) {
            ListNode next = cur.next;
            cur.next=next.next;
            next.next=pre.next;
            pre.next=next;
        }
        return dummyNode.next;

    }

    public static void main(String[] args) {
        ListNode head = ListNode.initNode(new int[]{1,2,3,4,5});
        int left = 2;
        int right = 4;
        ListNode reversedHead = reverseBetween(head, left, right);
        ListNode current = reversedHead;
        while (current != null) {
            System.out.print(current.val + " ");
            current = current.next;
        }
    }
}