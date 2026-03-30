package com.qr.np.config;

import dev.langchain4j.community.web.search.searxng.SearXNGWebSearchEngine;
import dev.langchain4j.web.search.searchapi.SearchApiWebSearchEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SearchConfig {

    private static final String SEARCH_API_API_KEY = "czAY983m14cXARMVBd9cCBBZ";

    @Bean
    public SearchApiWebSearchEngine getSearchEngine() {
        return SearchApiWebSearchEngine.builder()
                .apiKey(SEARCH_API_API_KEY)
                .engine("google")
                .build();
    }

    @Bean("searcxng")
    public SearXNGWebSearchEngine getSearxngWebSearchEngine() {
        return SearXNGWebSearchEngine.builder()
                .baseUrl("http://111.228.34.65:8888")
                .logRequests(true)
                .logResponses(true)
                .build();
    }

}
