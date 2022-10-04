package com.interview.task;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class NumberController {

    private final InMemoryNumberStoreService service;

    @GetMapping(path = "/offerNumber")
    public ResponseEntity<String> offerNumber(@NotNull @RequestParam("number") Integer number) {
        try {
            boolean result = service.offerNumber(number);
            return result ? ResponseEntity.ok().build() : ResponseEntity.badRequest().body("offered number not unique");
        } catch (StoreIsFullException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
