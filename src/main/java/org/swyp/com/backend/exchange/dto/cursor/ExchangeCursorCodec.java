package org.swyp.com.backend.exchange.dto.cursor;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import org.springframework.http.HttpStatus;
import org.swyp.com.backend.global.exception.BusinessException;

public class ExchangeCursorCodec {

    private static final String DELIMITER = "_";

    private ExchangeCursorCodec() {
    }

    public static String encode(ExchangeCursor cursor) {
        String raw = cursor.createdAt() + DELIMITER + cursor.profileExchangeId();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    public static ExchangeCursor decode(String encoded) {
        try {
            String raw = new String(Base64.getUrlDecoder().decode(encoded), StandardCharsets.UTF_8);
            int delimiterIndex = raw.lastIndexOf(DELIMITER);
            if (delimiterIndex < 0) {
                throw new IllegalArgumentException("잘못된 커서 형식입니다.");
            }

            LocalDateTime createdAt = LocalDateTime.parse(raw.substring(0, delimiterIndex));
            Long profileExchangeId = Long.parseLong(raw.substring(delimiterIndex + DELIMITER.length()));

            return new ExchangeCursor(createdAt, profileExchangeId);
        } catch (Exception e) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "잘못된 커서 값입니다.");
        }
    }
}
