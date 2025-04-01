package com.example.musinsa.contorller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {
    /**
     * API 호출 테스트를 위한 프론트 페이지
     * @return
     */
    @RequestMapping("/")
    public String index() {
        return "index";
    }
}
