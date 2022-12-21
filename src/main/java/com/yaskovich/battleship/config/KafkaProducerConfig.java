package com.yaskovich.battleship.config;

import com.yaskovich.battleship.entity.kafka.SavingGame;
import com.yaskovich.battleship.models.GameModelUI;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public ProducerFactory<String, SavingGame> producerFactoryForGames() {
        return new DefaultKafkaProducerFactory<>(getProps());
    }

    @Bean
    public ProducerFactory<String, GameModelUI> producerFactoryForGameModels() {
        return new DefaultKafkaProducerFactory<>(getProps());
    }

    @Bean
    public KafkaTemplate<String, SavingGame> kafkaTemplateForGames() {
        return new KafkaTemplate<>(producerFactoryForGames());
    }

    @Bean
    public KafkaTemplate<String, GameModelUI> kafkaTemplateForGameModels() {
        return new KafkaTemplate<>(producerFactoryForGameModels());
    }

    private Map<String, Object> getProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return props;
    }
}
