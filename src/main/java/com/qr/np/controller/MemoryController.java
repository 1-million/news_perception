package com.qr.np.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qr.np.mapper.MemoryMapper;
import com.qr.np.model.Memory;
import com.qr.np.service.IMemoryService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class MemoryController {

    @Resource
    private IMemoryService memoryService;

    @GetMapping("/memory/save")
    public String saveMemory(String sId,String prompt) {
        memoryService.save(new Memory(null,sId,prompt,new Date()));
        return "memory";
    }
    @GetMapping("/memory/delete")
    public String deleteMemory(String sId) {
        memoryService.remove(new QueryWrapper<Memory>().eq("session_id",Integer.valueOf(sId)));
        return "ok";
    }

}
