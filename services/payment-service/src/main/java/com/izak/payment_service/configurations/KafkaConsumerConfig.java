package com.izak.payment_service.configurations;

import com.izak.payment_service.events.OrderEvent;
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
  public ConsumerFactory<String, OrderEvent> consumerFactory() {
    JsonDeserializer<OrderEvent> deserializer = new JsonDeserializer<>(OrderEvent.class);
    deserializer.addTrustedPackages("*");
    deserializer.setRemoveTypeHeaders(false);
    deserializer.setUseTypeMapperForKey(true);

    return new DefaultKafkaConsumerFactory<>(
          Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.25.0.3:9092",
                ConsumerConfig.GROUP_ID_CONFIG, "payment-service-group",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class
          ),
          new StringDeserializer(),
          deserializer
    );
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, OrderEvent> orderEventKafkaFactory(
        ConsumerFactory<String, OrderEvent> consumerFactory) {
    ConcurrentKafkaListenerContainerFactory<String, OrderEvent> factory =
          new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory);
    return factory;
  }
}
