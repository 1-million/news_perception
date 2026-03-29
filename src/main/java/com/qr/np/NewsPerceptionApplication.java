package com.qr.np;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NewsPerceptionApplication {

    public static void main(String[] args) {
        // 指定使用 JDK 自带的客户端（兼容性好）
        System.setProperty("langchain4j.http.clientBuilderFactory", "dev.langchain4j.http.client.jdk.JdkHttpClientBuilderFactory");
        SpringApplication.run(NewsPerceptionApplication.class, args);
    }

}
