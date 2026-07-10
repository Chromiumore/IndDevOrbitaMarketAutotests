package com.github.chromiumore.orbitamarket.autotests.dto.event;

import java.time.Instant;
import java.util.UUID;

public record PaymentRequestedEvent(
        UUID eventId,
        Long orderId,
        UUID userId,
        Double amount,
        Instant occurredAt
) {
}
