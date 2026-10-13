package com.leetcode;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MergeKSortedListsTest {

    private final MergeKSortedLists solution = new MergeKSortedLists();

    @Test
    void mergesListsByLengthIntoSortedOrder() {
        var lists = new MergeKSortedLists.ListNode[]{
                listOf(1, 4, 5),
                listOf(1, 3, 4),
                listOf(2, 6)
        };

        assertEquals(List.of(1, 1, 2, 3, 4, 4, 5, 6), valuesOf(solution.mergeKLists(lists)));
    }

    @Test
    void ignoresEmptyLists() {
        var lists = new MergeKSortedLists.ListNode[]{null, listOf(0), null};

        assertEquals(List.of(0), valuesOf(solution.mergeKLists(lists)));
    }

    @Test
    void returnsNullForNoLists() {
        assertNull(solution.mergeKLists(new MergeKSortedLists.ListNode[0]));
        assertNull(solution.mergeKLists(null));
    }

    private MergeKSortedLists.ListNode listOf(int... values) {
        MergeKSortedLists.ListNode dummy = new MergeKSortedLists.ListNode();
        MergeKSortedLists.ListNode tail = dummy;
        for (int value : values) {
            tail.next = new MergeKSortedLists.ListNode(value);
            tail = tail.next;
        }
        return dummy.next;
    }

    private List<Integer> valuesOf(MergeKSortedLists.ListNode head) {
        var values = new ArrayList<Integer>();
        for (MergeKSortedLists.ListNode current = head; current != null; current = current.next) {
            values.add(current.val);
        }
        return values;
    }
}