package com.izak.payment_service.configurations;

import com.izak.payment_service.kafka.dto.OrderEventMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
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
  @Value("${KAFKA_IP4_ADDRESS}")
  private String KAFKA_IP4_ADDRESS;

  @Bean
  public ConsumerFactory<String, OrderEventMessage> consumerFactory() {
    JsonDeserializer<OrderEventMessage> deserializer = new JsonDeserializer<>(OrderEventMessage.class);
    deserializer.addTrustedPackages("*");
    deserializer.setRemoveTypeHeaders(false);
    deserializer.setUseTypeMapperForKey(true);

    return new DefaultKafkaConsumerFactory<>(
          Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_IP4_ADDRESS,
                ConsumerConfig.GROUP_ID_CONFIG, "payment-service-group",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class
          ),
          new StringDeserializer(),
          deserializer
    );
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, OrderEventMessage> orderEventKafkaFactory(
        ConsumerFactory<String, OrderEventMessage> consumerFactory) {
    ConcurrentKafkaListenerContainerFactory<String, OrderEventMessage> factory =
          new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory);
    return factory;
  }
}
