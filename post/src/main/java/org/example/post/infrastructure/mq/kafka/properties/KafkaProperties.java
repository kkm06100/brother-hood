package org.example.post.infrastructure.mq.kafka.properties;

public class KafkaProperties {

    public static final String GROUP_ID = "post-group";

    public static final String CONTAINER_FACTORY = "kafkaListenerContainerFactoryWithManualAck";

    public final static String INDEX_NAME = "storties_post_index";

    public KafkaProperties() {
    }
}
