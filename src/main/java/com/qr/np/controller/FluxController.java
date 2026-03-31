package com.qr.np.controller;

import com.qr.np.model.ResultResponse;
import com.qr.np.service.IAssistant;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@RestController
public class FluxController {

    @Resource
    private IAssistant assistant;

    @Resource
    private StreamingChatModel streamingChatModel;

    @GetMapping("/flux/chat")
    public Mono<ResultResponse> chat(@RequestParam String prompt) {
        return Mono.fromCallable(() -> {
            String response = assistant.chat(prompt);
            return new ResultResponse(response, true);
        }).onErrorResume(e -> {
            return Mono.just(new ResultResponse("处理出错: " + e.getMessage(), false));
        });
    }

    @GetMapping("/flux/chat/async")
    public Mono<String> chatAsync(@RequestParam String prompt) {
        return Mono.fromCallable(() -> assistant.chat(prompt))
                .map(response -> "AI回复: " + response)
                .onErrorReturn("请求处理失败");
    }

    @GetMapping("/flux/delay")
    public Mono<String> delayedResponse(@RequestParam String prompt) {
        return Mono.fromCallable(() -> assistant.chat(prompt))
                .map(response -> "延迟回复: " + response)
                .delayElement(java.time.Duration.ofSeconds(1));
    }

    @GetMapping("/flux/concurrent")
    public Flux<ResultResponse> concurrentChat(@RequestParam String prompt) {
        List<String> questions = List.of(
            prompt,
            prompt + "，用英语回答",
            prompt + "，简短回答",
            prompt + "，详细回答"
        );

        return Flux.fromIterable(questions)
                .flatMap(question ->
                    Mono.fromCallable(() -> assistant.chat(question))
                        .subscribeOn(Schedulers.boundedElastic())
                        .map(response -> new ResultResponse("问题: " + question + "\n回复: " + response, true))
                )
                .onErrorResume(e -> Flux.just(new ResultResponse("并发处理出错: " + e.getMessage(), false)));
    }

    @GetMapping("/flux/stream")
    public Flux<String> streamChat(@RequestParam String prompt) {
        return Flux.fromIterable(generateResponses(prompt))
                .delayElements(java.time.Duration.ofMillis(500))
                .map(response -> "流式输出: " + response)
                .onErrorReturn("流式传输失败");
    }

    @GetMapping("/flux/parallel")
    public Mono<String> parallelProcessing(@RequestParam String prompt) {
        long startTime = System.currentTimeMillis();

        return Flux.just("分析问题", "生成回复", "优化内容", "格式化输出")
                .flatMap(task ->
                    Mono.fromCallable(() -> processTask(task, prompt))
                        .subscribeOn(Schedulers.parallel())
                )
                .collectList()
                .map(results -> {
                    long endTime = System.currentTimeMillis();
                    return String.format("并行处理完成 (耗时: %dms):\n%s",
                            endTime - startTime, String.join("\n", results));
                });
    }

    @GetMapping(value = "/flux/stream/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChatWithModel(@RequestParam String prompt) {
        Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();
        AtomicBoolean completed = new AtomicBoolean(false);

        Mono.fromRunnable(() -> {
            try {
                streamingChatModel.chat(prompt, new StreamingChatResponseHandler() {
                    @Override
                    public void onPartialResponse(String partialResponse) {
                        if (!completed.get()) {
                            sink.tryEmitNext(partialResponse);
                        }
                    }

                    @Override
                    public void onCompleteResponse(ChatResponse completeResponse) {
                        if (!completed.getAndSet(true)) {
                            sink.tryEmitComplete();
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        if (!completed.getAndSet(true)) {
                            sink.tryEmitError(error);
                        }
                    }
                });
            } catch (Exception e) {
                if (!completed.getAndSet(true)) {
                    sink.tryEmitError(e);
                }
            }
        }).subscribeOn(Schedulers.boundedElastic()).subscribe();

        return sink.asFlux()
                .doOnCancel(() -> completed.set(true))
                .doOnError(e -> completed.set(true))
                .doOnComplete(() -> completed.set(true));
    }

    @GetMapping(value = "/flux/stream/chat/v2", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChatWithModelV2(@RequestParam String prompt) {
        Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();

        streamingChatModel.chat(prompt, new dev.langchain4j.model.chat.response.StreamingChatResponseHandler() {
            @Override
            public void onPartialResponse(String partialResponse) {
                sink.tryEmitNext(partialResponse);
            }

            @Override
            public void onCompleteResponse(ChatResponse completeResponse) {
                sink.tryEmitComplete();
            }

            @Override
            public void onError(Throwable error) {
                sink.tryEmitError(error);
            }
        });

        return sink.asFlux();
    }

    @GetMapping(value = "/flux/stream/multi", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamMultipleChats(@RequestParam String prompt) {
        List<String> questions = List.of(prompt, prompt + " 用中文", prompt + " 用英文");

        return Flux.fromIterable(questions)
                .flatMap(question -> {
                    Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();

                    streamingChatModel.chat(question, new dev.langchain4j.model.chat.response.StreamingChatResponseHandler() {
                        @Override
                        public void onPartialResponse(String partialResponse) {
                            sink.tryEmitNext("[" + question.substring(0, Math.min(10, question.length())) + "] " + partialResponse);
                        }

                        @Override
                        public void onCompleteResponse(ChatResponse completeResponse) {
                            sink.tryEmitComplete();
                        }

                        @Override
                        public void onError(Throwable error) {
                            sink.tryEmitError(error);
                        }
                    });

                    return sink.asFlux().subscribeOn(Schedulers.boundedElastic());
                });
    }

    @GetMapping(value = "/flux/stream/async", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamAsyncChat(@RequestParam String prompt) {
        return Mono.fromCallable(() -> {
            AtomicReference<StringBuilder> fullResponse = new AtomicReference<>(new StringBuilder());
            streamingChatModel.chat(prompt, new dev.langchain4j.model.chat.response.StreamingChatResponseHandler() {
                @Override
                public void onPartialResponse(String partialResponse) {
                    fullResponse.get().append(partialResponse);
                }

                @Override
                public void onCompleteResponse(ChatResponse completeResponse) {
                }

                @Override
                public void onError(Throwable error) {
                }
            });
            return fullResponse.get().toString();
        })
        .flatMapMany(response -> Flux.fromArray(response.split("(?<=.)")))
        .delayElements(java.time.Duration.ofMillis(50))
        .onErrorResume(e -> Flux.just("流式传输失败: " + e.getMessage()));
    }

    private String processTask(String task, String prompt) {
        try {
            Thread.sleep(500);
            return task + " - 完成";
        } catch (InterruptedException e) {
            return task + " - 失败";
        }
    }

    private List<String> generateResponses(String prompt) {
        return List.of(
            "开始分析: " + prompt,
            "理解用户意图...",
            "检索相关知识...",
            "生成初步回复...",
            "优化回复内容...",
            "完成最终输出"
        );
    }
}
