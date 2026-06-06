package org.swyp.com.backend.global.auth;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Date;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {
    @Id
    @Setter(AccessLevel.NONE)
    private String accountId;
    private String token;
    private Date expiresAt;
}
