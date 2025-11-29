package com.izak.notification_service.configurations;

import com.izak.notification_service.kafka_events.Notification;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {
  @Bean
  public ConsumerFactory<String, Notification> consumerFactory(){
    JsonDeserializer<Notification> deserializer =new JsonDeserializer<>(Notification.class);
    deserializer.addTrustedPackages("*");

    return new DefaultKafkaConsumerFactory<>(Map.of(
          ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092",
          // TODO :Notification comes from different servers please consume all in the group id
          ConsumerConfig.GROUP_ID_CONFIG,"order-service-group",
          ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
          ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,deserializer
    ),new StringDeserializer(), deserializer);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String,Notification> kafkaListenerContainerFactory(){
    ConcurrentKafkaListenerContainerFactory<String,Notification> factory=
          new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    return factory;
  }
}
