package com.qr.np.controller;

import com.qr.np.model.RequestMsg;
import com.qr.np.model.ResultResponse;
import com.qr.np.service.IAssistant;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class AdvChatController {

    @Resource
    private IAssistant assistant;

    @GetMapping("/adv")
    public ModelAndView index() {
        return new ModelAndView("chat_v2");
    }

    @PostMapping("/api/adv/chat")
    public ResultResponse chat(@RequestBody RequestMsg request) {
        String response = assistant.chat(request.getMessage());
        return new ResultResponse(response, true);
    }
}
