package com.interview.task;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Service
@Getter
public class InMemoryNumberStoreService {
    private final Map<Integer, Boolean> cache = new HashMap<>();
    private final Deque<Integer> queue = new LinkedList<>();
    @Value("${in.memory.queue.capacity:100}")
    private int queueCapacity;
    @Value("${in.memory.store.capacity:500}")
    private int storeCapacity;

    public synchronized boolean offerNumber(Integer number) throws StoreIsFullException {
        if (cache.containsKey(number)) {
            return false;
        }

        if (cache.size() >= storeCapacity) {
            throw new StoreIsFullException(String.format("storage reached its limit size '%s'", storeCapacity));
        }

        queue.addFirst(number);
        cache.put(number, true);
        if (queue.size() > queueCapacity) {
            Integer removed = queue.removeLast();
            cache.put(removed, false);
        }

        return true;
    }
}