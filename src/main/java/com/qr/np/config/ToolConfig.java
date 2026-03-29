package com.qr.np.config;

import com.qr.np.tools.WebSearchTools;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolConfig {

    @Bean
    public WebSearchTools getWebSearchTools() {
        return new WebSearchTools();
    }
}
