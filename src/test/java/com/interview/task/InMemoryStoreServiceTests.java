package com.interview.task;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = {"in.memory.store.capacity=100"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class InMemoryStoreServiceTests {

    @Autowired
    InMemoryNumberStoreService inMemoryNumberStoreService;

    @Test
    void allUniqueReturnTrue() {
        boolean result = IntStream.rangeClosed(1, 100).boxed().allMatch(inMemoryNumberStoreService::offerNumber);
        assertTrue(result);
    }

    @Test
    void allNotUniqueReturnFalse() {
        IntStream.rangeClosed(1, 100).boxed().forEach(inMemoryNumberStoreService::offerNumber);
        boolean result = IntStream.rangeClosed(1, 100).boxed().anyMatch(inMemoryNumberStoreService::offerNumber);
        assertFalse(result);
    }

    @Test
    void checkIfNumberProperlyEvicted() {
        //firstBatch candidate to be evicted
        IntStream.rangeClosed(1, 50).boxed().forEach(inMemoryNumberStoreService::offerNumber);
        boolean actual = IntStream.rangeClosed(51, 150).boxed().allMatch(inMemoryNumberStoreService::offerNumber);
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
}