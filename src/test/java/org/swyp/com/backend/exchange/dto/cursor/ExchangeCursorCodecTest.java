package org.swyp.com.backend.exchange.dto.cursor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.swyp.com.backend.global.exception.BusinessException;

class ExchangeCursorCodecTest {

    @Test
    void encode_decode_roundtrip_성공() {
        // given
        ExchangeCursor cursor = new ExchangeCursor(LocalDateTime.of(2026, 7, 2, 18, 30, 26, 371178000), 42L);

        // when
        String encoded = ExchangeCursorCodec.encode(cursor);
        ExchangeCursor decoded = ExchangeCursorCodec.decode(encoded);

        // then
        assertThat(decoded).isEqualTo(cursor);
    }

    @Test
    void decode_잘못된Base64_BusinessException() {
        assertThrows(BusinessException.class, () -> ExchangeCursorCodec.decode("not-valid-base64!!!"));
    }

    @Test
    void decode_형식오류_언더스코어없음_BusinessException() {
        // given
        String encoded = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("noDelimiterHere".getBytes(StandardCharsets.UTF_8));

        // when & then
        assertThrows(BusinessException.class, () -> ExchangeCursorCodec.decode(encoded));
    }

    @Test
    void decode_숫자아닌id_BusinessException() {
        // given
        String encoded = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("2026-07-02T18:30:26_notANumber".getBytes(StandardCharsets.UTF_8));

        // when & then
        assertThrows(BusinessException.class, () -> ExchangeCursorCodec.decode(encoded));
    }

    @Test
    void decode_날짜파싱실패_BusinessException() {
        // given
        String encoded = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("not-a-date_123".getBytes(StandardCharsets.UTF_8));

        // when & then
        assertThrows(BusinessException.class, () -> ExchangeCursorCodec.decode(encoded));
    }
}
