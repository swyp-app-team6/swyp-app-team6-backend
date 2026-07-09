package org.swyp.com.backend.terms.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.swyp.com.backend.terms.domain.TermsAgreement;
import org.swyp.com.backend.user.domain.User;

public interface TermsAgreementRepository extends JpaRepository<TermsAgreement, Long> {

    List<TermsAgreement> findByUser(User user);

    void deleteAllByUser(User user);
}
