package resources;

import java.util.Stack;

public class ReversePrint {
    public static int[] reversePrint(ListNode head) {
        Stack<ListNode> stack = new Stack<>();
        while (head != null){
            stack.push(head);
            head = head.next;
        }
        int size = stack.size();
        int[] result = new int[size];
        for (int i = 0; i < size; i++) {
            result[i] = stack.pop().val;
        }
        return result;
    }

    public static void main(String[] args) {
        ListNode listNode = new ListNode(3);
        ListNode next = new ListNode(1);
        listNode.next = next;
        ListNode next2 = new ListNode(2);
        next.next = next2;


        int[] result;
        result = reversePrint(listNode);
        for (int a:result){
            System.out.println(a);
        }
        //test
        //test1

    }
}
