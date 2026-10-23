package bank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TransferQueueTest {
    private final AccountNumber from = new AccountNumber("0000000001");
    private final AccountNumber to = new AccountNumber("0000000002");
    private final TransferRequest requestA = new TransferRequest(from, to, 100.0);
    private final TransferRequest requestB = new TransferRequest(from, to, 200.0);
    private final TransferRequest requestC = new TransferRequest(from, to, 300.0);

    @Test
    void requestsAreReturnedInInsertionOrder() {
        TransferQueue queue = new TransferQueue();
        queue.add(requestA);
        queue.add(requestB);
        queue.add(requestC);

        assertFalse(queue.isEmpty());
        assertSame(requestA, queue.next());
        assertSame(requestB, queue.next());
        assertSame(requestC, queue.next());
        assertTrue(queue.isEmpty());
    }

    @Test
    void peekDoesNotRemoveFirstRequest() {
        TransferQueue queue = new TransferQueue();
        queue.add(requestA);
        queue.add(requestB);

        assertSame(requestA, queue.peek());
        assertSame(requestA, queue.peek());
        assertEquals(2, queue.size());
        assertSame(requestA, queue.next());
        assertSame(requestB, queue.peek());
    }

    @Test
    void emptyQueueReturnsNull() {
        TransferQueue queue = new TransferQueue();

        assertNull(queue.next());
        assertNull(queue.peek());
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
    }

    @Test
    void sizeDecreasesAfterRemovingRequest() {
        TransferQueue queue = new TransferQueue();
        queue.add(requestA);
        queue.add(requestB);
        queue.add(requestC);
        assertEquals(3, queue.size());

        queue.next();
        assertEquals(2, queue.size());

        queue.next();
        assertEquals(1, queue.size());

        queue.next();
        assertEquals(0, queue.size());
        assertNull(queue.next());
    }
}
