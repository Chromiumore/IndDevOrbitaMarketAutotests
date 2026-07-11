package com.github.chromiumore.orbitamarket.autotests.client.kafka;

import com.github.chromiumore.orbitamarket.autotests.dto.event.PaymentRequestedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentEventProducer {

    public final static String ORDER_EVENTS_TOPIC = "orders-payment-requests";
    private final KafkaTemplate<String, PaymentRequestedEvent> kafkaTemplate;

    public void sendToKafka(PaymentRequestedEvent event) {
        kafkaTemplate.send(ORDER_EVENTS_TOPIC, event);
    }
}
