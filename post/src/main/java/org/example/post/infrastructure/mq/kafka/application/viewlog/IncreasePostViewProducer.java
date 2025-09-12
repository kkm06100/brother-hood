package org.example.post.infrastructure.mq.kafka.application.viewlog;

import brother.hood.sharedlibrary.kafka.KafkaEvent;
import lombok.RequiredArgsConstructor;
import org.example.post.application.event.IncreasePostViewEvent;
import org.example.post.infrastructure.mq.kafka.util.JsonSerializer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static org.example.post.infrastructure.mq.kafka.properties.KafkaTopicProperties.INCREASE_VIEW_TOPIC;

@RequiredArgsConstructor
@Component
public class IncreasePostViewProducer {

    private final KafkaTemplate<String, KafkaEvent> kafkaTemplate;

    private final JsonSerializer jsonSerializer;

    public void publish(IncreasePostViewEvent event) {
        KafkaEvent kafkaEvent = KafkaEvent.builder()
            .topic(INCREASE_VIEW_TOPIC)
            .eventClass(IncreasePostViewEvent.class)
            .payload(jsonSerializer.toJson(event))
            .retryCount(0)
            .build();

        kafkaTemplate.send(INCREASE_VIEW_TOPIC, kafkaEvent);
    }
}
