package com.stu.leetcode.listnode;

class RemoveNthFromEnd {


    public static ListNode removeNthFromEnd(ListNode head, int n) {
            ListNode pre = head;
            int len = 0;
            while(pre!=null){
                pre=pre.next;
                len++;
            }

            ListNode dummyNode = new ListNode(-1);
            dummyNode.next = head;
            if(len == n && n == 1){
                return null;
            }
            ListNode cur = dummyNode;
            for(int i = 0; i < len -n; i++){
                cur=cur.next;
            }

            cur.next = cur.next.next;
            return dummyNode.next;
    }

    public static void main(String[] args) {
        ListNode head = ListNode.initNode(new int[]{1,2,3,4,5});
        ListNode reversedHead = removeNthFromEnd(head,2);
        ListNode current = reversedHead;
        while (current != null) {
            System.out.print(current.val + " ");
            current = current.next;
        }
    }
}