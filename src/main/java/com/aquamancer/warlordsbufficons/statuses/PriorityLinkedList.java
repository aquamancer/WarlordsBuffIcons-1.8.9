package com.aquamancer.warlordsbufficons.statuses;


import java.util.*;

public class PriorityLinkedList implements Iterable<Status> {
    private static class Node {
        Status status;
        int priority;
        Node prev;
        Node next;
        public Node(Status status, int priority) {
            this.status = status;
            this.priority = priority;
        }
    }

    /**
     * Sorts List of Integers using insertion sort.
     * @param list
     */
    private static void sort(List<Integer> list) {
        for (int i = 1; i < list.size(); i++) {
            Integer current = list.get(i);
            int j = i - 1;
            while (j >= 0 && current < list.get(j)) {
                j--;
            }
            if (j != i - 1) {
                list.remove(i);
                list.add(j + 1, current);
            }
        }
    }
    public static int insertIntoSortedListNoDupe(List<Integer> list, int num) {
        int left = 0, right = list.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (list.get(mid) == num) {
                return mid;
            } else if (list.get(mid) < num) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        list.add(left, num);
        return left;
    }

    private Node preHead;
    private Node postTail;
    private Map<Status, Node> nodeMap;
    private Map<Integer, Node> priorityTailMap;
    private List<Integer> priorities;
    public PriorityLinkedList() {
        this.nodeMap = new HashMap<>();
        this.priorityTailMap = new HashMap<>();
        this.priorities = new ArrayList<>();

        this.preHead = new Node(null, Integer.MIN_VALUE);
        this.postTail = new Node(null, Integer.MAX_VALUE);
        this.priorityTailMap.put(Integer.MIN_VALUE, this.preHead);
        this.priorityTailMap.put(Integer.MAX_VALUE, this.postTail);
        this.priorities.add(Integer.MIN_VALUE);
        this.priorities.add(Integer.MAX_VALUE);
    }
    public void insert(Status status) {
        int priority = status.getPriority();
        Node newNode = new Node(status, priority);
        Node priorityTail = priorityTailMap.get(priority);
        if (priorityTail == null) {
            // new priority level
            int relativePriority = insertIntoSortedListNoDupe(this.priorities, priority);
            Node tailOfLowerPriority = this.priorityTailMap.get(this.priorities.get(relativePriority - 1));
            newNode.prev = tailOfLowerPriority;
            newNode.next = tailOfLowerPriority.next;
            tailOfLowerPriority.next.prev = newNode;
            tailOfLowerPriority.next = newNode;
            this.priorityTailMap.put(priority, newNode);
        } else {
            if (priority == this.postTail.priority) {
                newNode.prev = this.postTail.prev;
                newNode.next = this.postTail;
            } else {
                newNode.prev = priorityTail;
                newNode.next = priorityTail.next;
                priorityTail.next.prev = newNode;
                priorityTail.next = newNode;
            }
        }
        this.nodeMap.put(status, newNode);
    }
    public void remove(Status status) {
        Node toRemove = this.nodeMap.remove(status);
        if (toRemove == null) return;
        if (this.priorityTailMap.get(toRemove.priority) == toRemove) {
            if (toRemove.prev.priority == toRemove.priority) {
                this.priorityTailMap.put(toRemove.priority, toRemove.prev);
            } else {
                this.priorityTailMap.remove(toRemove.priority);
                this.priorities.remove(toRemove.priority);
            }
        }
        toRemove.prev.next = toRemove.next;
        toRemove.next.prev = toRemove.prev;
    }

    @Override
    public Iterator<Status> iterator() {
        return new PriorityLinkedListIterator();
    }

    private class PriorityLinkedListIterator implements Iterator<Status> {
        private Node currentNode = preHead.next;
        @Override
        public boolean hasNext() {
            return this.currentNode != postTail;
        }

        @Override
        public Status next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            Status data = this.currentNode.status;
            currentNode = currentNode.next;
            return data;
        }
    }
}
