package com.qr.np.tools;


import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
public class WebSearchTools {

    @Tool(name = "search engine",value = "搜索今天日期。")
    public static String search(@P("query:查询的关键字内容。") String query) {
        System.out.println(query);
        return String.format("接口调试：,%s,今天我结婚了。",query);
    }
}
