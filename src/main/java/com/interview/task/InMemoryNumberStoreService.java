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
    @Value("${in.memory.store.capacity:100}")
    private int capacity;

    public synchronized boolean offerNumber(Integer number) {
        if (cache.containsKey(number)) {
            return false;
        }

        queue.addFirst(number);
        cache.put(number, true);
        if (queue.size() > capacity) {
            Integer removed = queue.removeLast();
            cache.put(removed, false);
        }

        return true;
    }
}