package org.example.post.infrastructure.mq.kafka.application.post.delete;

import brother.hood.sharedlibrary.kafka.KafkaEvent;
import lombok.RequiredArgsConstructor;
import org.example.post.application.event.DeletePostEvent;
import org.example.post.infrastructure.mq.kafka.util.JsonSerializer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static org.example.post.infrastructure.mq.kafka.properties.KafkaTopicProperties.DELETE_TOPIC;

@RequiredArgsConstructor
@Component
public class DeletePostProducer {

    private final KafkaTemplate<String, KafkaEvent> kafkaTemplate;

    private final JsonSerializer jsonSerializer;

    public void publish(DeletePostEvent event) {
        KafkaEvent kafkaEvent = KafkaEvent.builder()
            .topic(DELETE_TOPIC)
            .eventClass(DeletePostEvent.class)
            .payload(jsonSerializer.toJson(event))
            .retryCount(0)
            .build();

        kafkaTemplate.send(DELETE_TOPIC, kafkaEvent);
    }
}
