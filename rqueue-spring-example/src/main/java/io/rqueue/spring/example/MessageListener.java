package io.rqueue.spring.example;

import io.rqueue.annotation.RqueueListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageListener {
  @RqueueListener(value = "test")
  public void onMessage(String message) {
    log.info("test: {}", message);
  }

  @RqueueListener(value = "dtest", delayedQueue = "true")
  public void onMessage(Object message) {
    log.info("dtest: {}", message);
  }

  @RqueueListener(value = "job-queue", delayedQueue = "true")
  public void onMessage(Job job) {
    log.info("job-queue: {}", job);
  }
}
