package com.ssafy.test1.api;

import com.ssafy.test1.dao.response.TestResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test1")
    TestResponse test(){
        return new TestResponse("Hello World");
    }
}
