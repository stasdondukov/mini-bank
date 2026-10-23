package bank;

import java.util.ArrayDeque;
import java.util.Deque;

public class TransferQueue {
    private final Deque<TransferRequest> queue = new ArrayDeque<>();

    public void add(TransferRequest request) {
        queue.offer(request);
    }

    public TransferRequest next() {
        return queue.poll();
    }

    public TransferRequest peek() {
        return queue.peek();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
        return queue.size();
    }
}
