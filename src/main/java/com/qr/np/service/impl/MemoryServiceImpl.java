package com.qr.np.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qr.np.mapper.MemoryMapper;
import com.qr.np.model.Memory;
import com.qr.np.service.IMemoryService;
import org.springframework.stereotype.Service;

@Service
public class MemoryServiceImpl extends ServiceImpl<MemoryMapper, Memory> implements IMemoryService {
}
