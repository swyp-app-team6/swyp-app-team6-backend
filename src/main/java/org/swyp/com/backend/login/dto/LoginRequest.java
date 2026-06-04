package org.swyp.com.backend.login.dto;

import software.amazon.awssdk.annotations.NotNull;

public record LoginRequest(@NotNull String id) {

}
