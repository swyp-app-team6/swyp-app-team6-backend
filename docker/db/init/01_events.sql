-- jwt.refresh-exp = 1209600000ms (14 days)
-- expired refresh tokens are purged every hour
USE swyp;

CREATE EVENT IF NOT EXISTS evt_delete_expired_refresh_tokens
    ON SCHEDULE EVERY 1 HOUR
        STARTS NOW()
    ON COMPLETION PRESERVE
    ENABLE
    DO
    DELETE
    FROM refresh_token
    WHERE expires_at < NOW();
