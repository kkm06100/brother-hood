package brother.hood.sharedlibrary.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class KafkaEvent {

    public KafkaEvent() {
    }

    private String topic;

    private Class<?> eventClass;

    private String payload;

    private int retryCount;

    private String errorMessage;

    public void increaseRetryCount() {
        ++ this.retryCount;
    }
}
