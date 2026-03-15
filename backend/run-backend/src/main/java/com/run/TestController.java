package com.run;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// test endpoint to prove backend + database are connected
@RestController
public class TestController {

    private final TestRepository testRepository;

    public TestController(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    @GetMapping("/api/test")
    public String test() {
        testRepository.save(new TestEntity("Hello from Run"));
        List<TestEntity> all = testRepository.findAll();
        return "Rows in database: " + all.size();
    }
}