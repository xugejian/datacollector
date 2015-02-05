/**
 * (c) 2014 StreamSets, Inc. All rights reserved. May not
 * be copied, modified, or distributed in whole or part without
 * written consent of StreamSets, Inc.
 */
package com.streamsets.pipeline.lib.kafka;

import com.streamsets.pipeline.lib.json.StreamingJsonParser;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;

import java.util.concurrent.CountDownLatch;

public class ProducerRunnable implements Runnable {

  private final String topic;
  private final Producer<String, String> producer;
  private final CountDownLatch startLatch;
  private final DataType dataType;
  private final int partitions;
  private int lastPartition;
  private static int counter = 0;
  private final StreamingJsonParser.Mode jsonMode;

  public ProducerRunnable(String topic, int partitions,
                          Producer<String, String> producer, CountDownLatch startLatch, DataType dataType,
                          StreamingJsonParser.Mode jsonMode) {
    this.topic = topic;
    this.partitions = partitions;
    this.producer = producer;
    this.startLatch = startLatch;
    this.dataType = dataType;
    this.lastPartition = 0;
    this.jsonMode = jsonMode;
  }

  @Override
  public void run() {
    try {
      startLatch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    int i = 0;
    while(true) {
      producer.send(new KeyedMessage<>(topic, getPartitionKey(), KafkaTestUtil.generateTestData(dataType, jsonMode)));
    }
  }

  private String getPartitionKey() {
    lastPartition = (lastPartition + 1) % partitions;
    return String.valueOf(lastPartition);
  }
}
