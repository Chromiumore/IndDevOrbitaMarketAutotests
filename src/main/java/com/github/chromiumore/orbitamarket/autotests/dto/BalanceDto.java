package com.github.chromiumore.orbitamarket.autotests.dto;

import java.util.UUID;

public record BalanceDto(UUID userId, Double balance, String currency) {
}
