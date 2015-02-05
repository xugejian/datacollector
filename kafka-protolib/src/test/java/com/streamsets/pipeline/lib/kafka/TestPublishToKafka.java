/**
 * (c) 2014 StreamSets, Inc. All rights reserved. May not
 * be copied, modified, or distributed in whole or part without
 * written consent of StreamSets, Inc.
 */
package com.streamsets.pipeline.lib.kafka;

import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestPublishToKafka {

  private static final String TOPIC = "testHA4";
  private static final int MULTIPLE_PARTITIONS = 3;

  public static void main(String[] args) {

    Properties props = new Properties();
    props.put("metadata.broker.list", "localhost:9001,localhost:9002");
    props.put("serializer.class", "kafka.serializer.StringEncoder");
    props.put("request.required.acks", "1");

    ProducerConfig config = new ProducerConfig(props);
    Producer<String, String> producer = new Producer<>(config);

    CountDownLatch startProducing = new CountDownLatch(1);

    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(new ProducerRunnable(TOPIC, MULTIPLE_PARTITIONS, producer, startProducing, DataType.LOG,
      null));

    startProducing.countDown();

  }
}
