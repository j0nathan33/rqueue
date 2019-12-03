/*
 * Copyright (c) 2019-2019, Sonu Kumar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.sonus21.rqueue.config;

import com.github.sonus21.rqueue.core.RqueueMessageTemplate;
import com.github.sonus21.rqueue.listener.RqueueMessageHandler;
import com.github.sonus21.rqueue.listener.RqueueMessageListenerContainer;
import java.util.List;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.util.Assert;

/**
 * This is a bare minimal factory class, that can be used to create {@link
 * RqueueMessageListenerContainer} object. Factory has multiple methods to support different types
 * of requirements.
 */
public class SimpleRqueueListenerContainerFactory {
  private AsyncTaskExecutor taskExecutor;
  private boolean autoStartup = true;
  private RedisConnectionFactory redisConnectionFactory;
  private RqueueMessageHandler rqueueMessageHandler;
  private List<MessageConverter> messageConverters;
  private Long backOffTime;
  private Integer maxNumWorkers;
  private RqueueMessageTemplate rqueueMessageTemplate;

  /**
   * Get configured task executor
   *
   * @return async task executor
   */
  public AsyncTaskExecutor getTaskExecutor() {
    return taskExecutor;
  }

  /**
   * Configures the {@link TaskExecutor} which is used to poll messages and execute them by calling
   * the handler methods. If no {@link TaskExecutor} is set, a default one is created.
   *
   * @param taskExecutor The {@link TaskExecutor} used by the container
   * @see RqueueMessageListenerContainer#createDefaultTaskExecutor()
   */
  public void setTaskExecutor(AsyncTaskExecutor taskExecutor) {
    Assert.notNull(taskExecutor, "taskExecutor can not be null");
    this.taskExecutor = taskExecutor;
  }

  public Boolean getAutoStartup() {
    return autoStartup;
  }

  /**
   * Configures if this container should be automatically started. The default value is true.
   *
   * @param autoStartup - false if the container will be manually started
   */
  public void setAutoStartup(boolean autoStartup) {
    this.autoStartup = autoStartup;
  }

  /**
   * Return configured message handler
   *
   * @return RqueueMessageHandler object
   */
  public RqueueMessageHandler getRqueueMessageHandler() {
    return rqueueMessageHandler;
  }

  /**
   * Set message handler, this can be used to set custom message handlers that could have some
   * special features apart of the default one
   *
   * @param rqueueMessageHandler {@link RqueueMessageHandler} object
   */
  public void setRqueueMessageHandler(RqueueMessageHandler rqueueMessageHandler) {
    Assert.notNull(rqueueMessageHandler, "rqueueMessageHandler must not be null");
    this.rqueueMessageHandler = rqueueMessageHandler;
  }

  /**
   * @return The number of milliseconds the polling thread must wait before trying to recover when
   *     an error occurs (e.g. connection timeout)
   */
  public Long getBackOffTime() {
    return backOffTime;
  }

  /**
   * The number of milliseconds the polling thread must wait before trying to recover when an error
   * occurs (e.g. connection timeout). Default value is 10000 milliseconds.
   *
   * @param backOffTime in milliseconds
   */
  public void setBackOffTime(long backOffTime) {
    this.backOffTime = backOffTime;
  }

  public Integer getMaxNumWorkers() {
    return maxNumWorkers;
  }

  /**
   * Maximum number of workers, that would be used to run tasks.
   *
   * @param maxNumWorkers Maximum number of workers
   */
  public void setMaxNumWorkers(int maxNumWorkers) {
    this.maxNumWorkers = maxNumWorkers;
  }

  /** @return list of configured message converters */
  public List<MessageConverter> getMessageConverters() {
    return messageConverters;
  }

  /**
   * For message (de)serialization we might need one or more message converters, configure those
   * message converters
   *
   * @param messageConverters list of message converters
   */
  public void setMessageConverters(List<MessageConverter> messageConverters) {
    Assert.notEmpty(messageConverters, "messageConverters must not be empty");
    this.messageConverters = messageConverters;
  }

  /** @return get Redis connection factor */
  public RedisConnectionFactory getRedisConnectionFactory() {
    return redisConnectionFactory;
  }

  /**
   * Set redis connection factory, that would be used to configured message template and other
   * components
   *
   * @param redisConnectionFactory redis connection factory object
   */
  public void setRedisConnectionFactory(RedisConnectionFactory redisConnectionFactory) {
    Assert.notNull(redisConnectionFactory, "redisConnectionFactory must not be null");
    this.redisConnectionFactory = redisConnectionFactory;
  }

  /** @return message template */
  public RqueueMessageTemplate getRqueueMessageTemplate() {
    return rqueueMessageTemplate;
  }

  /**
   * Set RqueueMessageTemplate that's used to pull and push messages from/to Redis.
   *
   * @param messageTemplate a message template object
   */
  public void setRqueueMessageTemplate(RqueueMessageTemplate messageTemplate) {
    Assert.notNull(messageTemplate, "messageTemplate must not be null");
    rqueueMessageTemplate = messageTemplate;
  }

  /**
   * Creates a {@link RqueueMessageListenerContainer} container. To create this container we would
   * need redis connection factory {@link RedisConnectionFactory }as well as message handler {@link
   * RqueueMessageHandler}.
   *
   * @return an object of {@link RqueueMessageListenerContainer} object
   */
  public RqueueMessageListenerContainer createMessageListenerContainer() {
    Assert.notNull(rqueueMessageHandler, "rqueueMessageHandler must not be null");
    Assert.notNull(redisConnectionFactory, "redisConnectionFactory must not be null");
    if (rqueueMessageTemplate == null) {
      rqueueMessageTemplate = new RqueueMessageTemplate(redisConnectionFactory);
    }
    RqueueMessageListenerContainer messageListenerContainer =
        new RqueueMessageListenerContainer(rqueueMessageHandler, rqueueMessageTemplate);
    messageListenerContainer.setAutoStartup(autoStartup);
    if (taskExecutor != null) {
      messageListenerContainer.setTaskExecutor(taskExecutor);
    }
    if (maxNumWorkers != null) {
      messageListenerContainer.setMaxNumWorkers(maxNumWorkers);
    }
    if (backOffTime != null) {
      messageListenerContainer.setBackOffTime(backOffTime);
    }
    return messageListenerContainer;
  }
}
