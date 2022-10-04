package com.interview.task;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "in.memory.store.capacity=500",
        "in.memory.queue.capacity=100"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class InMemoryStoreServiceTests {

    @Autowired
    InMemoryNumberStoreService inMemoryNumberStoreService;

    @Test
    @SneakyThrows
    void allUniqueReturnTrue() {
        boolean result = true;
        for (int i = 0; i < 100; i++) {
            result = result && inMemoryNumberStoreService.offerNumber(i);
        }
        assertTrue(result);
    }

    @Test
    @SneakyThrows
    void allNotUniqueReturnFalse() {
        for (int i = 0; i < 100; i++) {
           inMemoryNumberStoreService.offerNumber(i);
        }

        boolean result = false;
        for (int i = 0; i < 100; i++) {
            result = result || inMemoryNumberStoreService.offerNumber(i);
        }

        assertFalse(result);
    }

    @Test
    @SneakyThrows
    void checkIfNumberProperlyEvicted() {
        //firstBatch candidate to be evicted
        for (int i = 1; i <= 50; i++) {
            inMemoryNumberStoreService.offerNumber(i);
        }

        boolean actual = true;
        for (int i = 51; i <= 150; i++) {
            actual = actual && inMemoryNumberStoreService.offerNumber(i);
        }
        //must be true because all unique
        assertTrue(actual);

        Set<Integer> firstBatchSet = IntStream.rangeClosed(1, 50).boxed().collect(Collectors.toSet());
        // expected that all firstBatch evicted
        boolean anyFirstBatchNumberInQueue = inMemoryNumberStoreService.getQueue().stream().anyMatch(firstBatchSet::contains);
        assertFalse(anyFirstBatchNumberInQueue);

        // expected that cache contains all evicted
        boolean allEvictedInCache = IntStream.rangeClosed(1, 50).boxed().allMatch(n -> inMemoryNumberStoreService.getCache().containsKey(n));
        assertTrue(allEvictedInCache);

        // expected all evicted has false value
        boolean anyEvictedValue = IntStream.rangeClosed(1, 50).boxed().anyMatch(n -> inMemoryNumberStoreService.getCache().get(n));
        assertFalse(anyEvictedValue);
    }

    @Test
    void checkForProperlyExceptionWhenStoreIsFull() {
        try {
            for (int i = 0; i < 501; i++) {
                inMemoryNumberStoreService.offerNumber(i);
            }
            fail("StoreIsFullException expected");
        } catch (StoreIsFullException e) {
        }
    }
}