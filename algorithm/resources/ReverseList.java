package resources;

import java.util.Stack;

public class ReverseList {

    public ListNode reverseList(ListNode head) {
        ListNode prev = null;
        ListNode curr = head;
        while (curr != null) {
            ListNode next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }
        return prev;
    }

    //递归实现
    public ListNode recursionReverseList(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        ListNode newHead = reverseList(head.next);
        head.next.next = head;
        head.next = null;
        return newHead;
    }

    //栈实现
    public ListNode stackReverseList(ListNode head) {
        if(head==null){
            return null;
        }
        Stack<ListNode> stack= new Stack<>();
        while (head!=null){
            stack.push(head);
            head=head.next;
        }
        ListNode listNode=stack.pop();
        ListNode p=listNode;
        listNode.next=null;
        while (!stack.isEmpty()){
            listNode.next=stack.pop();
            listNode=listNode.next;
            listNode.next=null;
        }
        return p;




    }

}
