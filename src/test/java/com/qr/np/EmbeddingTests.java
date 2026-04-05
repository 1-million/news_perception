package com.qr.np;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentByCharacterSplitter;
import dev.langchain4j.data.document.splitter.DocumentByLineSplitter;
import dev.langchain4j.data.document.splitter.DocumentByWordSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.junit.Test;

import java.util.List;

public class EmbeddingTests {

    @Test
    public void testEmbedding() {
        System.setProperty("langchain4j.http.clientBuilderFactory", "dev.langchain4j.http.client.spring.restclient.SpringRestClientBuilderFactory");
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        EmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
                .baseUrl("http://127.0.0.1:1234/v1")
                .apiKey("sk-lm-4XHQmrlU:HtoVACg1lkRjZI4DPNcf")
                .modelName("text-embedding-paraphrase-multilingual-minilm-l12-v2.gguf:2")
                //.logRequests(true)
                //.logResponses(true)
                .build();
        // 读取文档转换为文本片段。
        Document document = FileSystemDocumentLoader.loadDocument("E:\\data\\test.txt");
        DocumentByWordSplitter splitter2 = new DocumentByWordSplitter(8,5);
        List<TextSegment> textSegments2 = splitter2.split(document);
        for(TextSegment textSegment : textSegments2){
            Embedding embedding = embeddingModel.embed(textSegment).content();
            embeddingStore.add(embedding, textSegment);
        }
        // 创建向量搜索请求。
        Embedding embedding99 = embeddingModel.embed("萧炎").content();
        EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(embedding99)
                .maxResults(100)
                .build();
        // 通过向量存储库，执行向量搜索。
        List<EmbeddingMatch<TextSegment>> matches = embeddingStore.search(searchRequest).matches();
        for (EmbeddingMatch<TextSegment> match : matches) {
            System.out.println("matches:" + match.score()+"text:"+match.embedded());
        }
    }
}
