package com.nasko.marauder;

import java.util.LinkedList;
import java.util.Queue;

public class BlockingQueue<T> {

    private static final int DEFAULT_CAPACITY = 16;

    private Queue<T> queue;
    private int capacity;
    private Object lock = new Object();

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
        this.queue = new LinkedList<>();
    }

    public BlockingQueue() {
        this(DEFAULT_CAPACITY);
    }

    public void add(T item) {
        synchronized (lock) {
            while (this.queue.size() == this.capacity) {
                try {
                    lock.wait();
                } catch (InterruptedException e) { }
            }

            this.queue.add(item);
            lock.notifyAll();
        }
    }

    public T remove() {
        synchronized (this.lock) {
            while (this.queue.size() == 0) {
                try {
                    lock.wait();
                } catch (InterruptedException e) { }
            }

            T result = this.queue.remove();
            this.lock.notifyAll();
            return result;
        }
    }

    public T peek() {
        synchronized (this.lock) {
            if (this.queue.size() == 0) {
                throw new UnsupportedOperationException("Queue is empty");
            }

            return this.queue.peek();
        }
    }
}
