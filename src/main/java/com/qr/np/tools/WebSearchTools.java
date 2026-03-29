package com.qr.np.tools;


import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.community.web.search.searxng.SearXNGWebSearchEngine;
import dev.langchain4j.web.search.WebSearchResults;
import dev.langchain4j.web.search.searchapi.SearchApiWebSearchEngine;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class WebSearchTools {

    @Resource
    private SearXNGWebSearchEngine searXNGWebSearchEngine;
    @Resource
    private SearchApiWebSearchEngine searchApiWebSearchEngine;

    @Tool(name = "search engine",value = "适用于各种搜索类任务。")
    public String search(@P("query:查询的关键字内容。") String query) {
        System.out.println(query);
        WebSearchResults results = searchApiWebSearchEngine.search(query);
        return results.toString();
    }
}
