package com.github.chromiumore.orbitamarket.autotests.dto;

import java.util.UUID;

public record AccountDto(Long id, UUID userId, Double balance) {
}
