package com.qr.np.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RAGController {

    @GetMapping("/rag/save")
    public String saveRag(String sId,String prompt) {
        return "ok";
    }
}
