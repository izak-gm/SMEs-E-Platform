package com.izak.notification_service.configurations.kafka;

import com.izak.notification_service.kafka.kafka_events.Payment;
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
  public ConsumerFactory<String, Payment> consumerFactory() {
    JsonDeserializer<Payment> deserializer = new JsonDeserializer<>(Payment.class);
    deserializer.addTrustedPackages("*");
    deserializer.setRemoveTypeHeaders(false);
    deserializer.setUseTypeMapperForKey(true);

    return new DefaultKafkaConsumerFactory<>(
          Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_IP4_ADDRESS,
                ConsumerConfig.GROUP_ID_CONFIG, "notification-service-group",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer
          ),
          new StringDeserializer(),
          deserializer
    );
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, Payment> paymentEventKafkaFactory(
        ConsumerFactory<String, Payment> consumerFactory) {
    ConcurrentKafkaListenerContainerFactory<String, Payment> factory =
          new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory);
    return factory;
  }
}
