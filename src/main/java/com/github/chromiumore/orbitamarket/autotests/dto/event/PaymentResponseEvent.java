package com.github.chromiumore.orbitamarket.autotests.dto.event;

import java.util.UUID;

public record PaymentResponseEvent(
        UUID eventId,
        Long orderId,
        UUID userId,
        Double amount,
        Double newBalance,
        String reason,
        String eventType
) {
}
