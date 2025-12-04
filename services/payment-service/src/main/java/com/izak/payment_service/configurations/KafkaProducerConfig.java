package com.izak.payment_service.configurations;

import com.izak.payment_service.kafka.events.PaymentEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@Configuration
public class KafkaProducerConfig {
  @Bean
  public ProducerFactory<String, PaymentEvent> producerFactory(){
    return new DefaultKafkaProducerFactory<>(Map.of(
          ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092",
          ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
          ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
    ));
  }

  @Bean
  public KafkaTemplate<String, PaymentEvent> kafkaTemplate(){
    return new KafkaTemplate<>(producerFactory());
  }
}
