package com.uwasa.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
public class SseController {

    public static final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping("/sse/subscribe")
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));
        return emitter;
    }

    public static void sendConsumed(Long postId) {
        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();
        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("consumed")
                        .data(postId));
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        });
        emitters.removeAll(deadEmitters);
    }
    
    public static void sendCreated(Long postId, String title, String writerNickname, String createdAt) {
        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();
        String data = postId + "|" + title + "|" + writerNickname + "|" + createdAt;
        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("created")
                        .data(data));
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        });
        emitters.removeAll(deadEmitters);
    }
    
    public static void sendStats(Long consumedToday, Long alive, Long totalConsumed) {
        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();
        String data = consumedToday + "|" + alive + "|" + totalConsumed;
        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("stats")
                        .data(data));
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        });
        emitters.removeAll(deadEmitters);
    }
}