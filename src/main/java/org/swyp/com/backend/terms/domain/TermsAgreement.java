package org.swyp.com.backend.terms.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.swyp.com.backend.global.enumeration.TermsType;
import org.swyp.com.backend.user.domain.User;

@Entity
@Table(name = "terms_agreement")
@Getter
@EntityListeners(AuditingEntityListener.class)
public class TermsAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TermsType termsType;
    @Column(nullable = false)
    private Integer version;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime agreedAt;

    public static TermsAgreement create(User user, TermsType termsType, int version) {
        TermsAgreement termsAgreement = new TermsAgreement();
        termsAgreement.user = user;
        termsAgreement.termsType = termsType;
        termsAgreement.version = version;
        return termsAgreement;
    }
}
