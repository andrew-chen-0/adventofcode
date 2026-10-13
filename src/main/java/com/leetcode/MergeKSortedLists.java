package com.leetcode;

import java.util.Comparator;
import java.util.PriorityQueue;

public class MergeKSortedLists {

    public static class ListNode {
        int val;
        ListNode next;

        ListNode() {
        }

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }

    private record SizedList(ListNode head, int length) {
    }

    // The conventional solution keeps each list's current head in a min-heap, removes the
    // globally smallest node, and inserts that node's successor. Unlike this implementation,
    // which first measures every list and repeatedly merges the two shortest whole lists, it
    // needs no length prepass or SizedList bookkeeping and also works when lengths are unknown.
    // Both take O(N log k) time and O(k) auxiliary space, but the conventional approach is
    // generally preferred for its simpler invariant; this approach minimizes the total work
    // performed when the operation is specifically expressed as repeated pairwise list merges.
    public ListNode mergeKLists(ListNode[] lists) {
        if (lists == null || lists.length == 0) {
            return null;
        }

        var shortestLists = new PriorityQueue<SizedList>(Comparator.comparingInt(SizedList::length));
        for (ListNode list : lists) {
            int length = lengthOf(list);
            if (length > 0) {
                shortestLists.add(new SizedList(list, length));
            }
        }

        while (shortestLists.size() > 1) {
            SizedList first = shortestLists.remove();
            SizedList second = shortestLists.remove();
            ListNode merged = merge(first.head(), second.head());
            shortestLists.add(new SizedList(merged, first.length() + second.length()));
        }

        return shortestLists.isEmpty() ? null : shortestLists.remove().head();
    }

    private int lengthOf(ListNode head) {
        int length = 0;
        for (ListNode current = head; current != null; current = current.next) {
            length++;
        }
        return length;
    }

    private ListNode merge(ListNode first, ListNode second) {
        ListNode dummy = new ListNode();
        ListNode tail = dummy;

        while (first != null && second != null) {
            if (first.val <= second.val) {
                tail.next = first;
                first = first.next;
            } else {
                tail.next = second;
                second = second.next;
            }
            tail = tail.next;
        }

        tail.next = first != null ? first : second;
        return dummy.next;
    }
}