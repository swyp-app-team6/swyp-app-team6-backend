package org.swyp.com.backend.interest.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyp.com.backend.interest.dto.InterestResponse;
import org.swyp.com.backend.profile.domain.Interest;
import org.swyp.com.backend.profile.domain.repository.InterestRepository;
import org.swyp.com.backend.profile.dto.InterestTypeLabel;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterestService {
    private final InterestRepository interestRepository;

    public InterestResponse getInterestResponse() {
        List<Interest> interestList = interestRepository.findByDeletedFalse();
        return new InterestResponse(interestList.stream()
                .map(i -> new InterestTypeLabel(i.getType(), i.getType().getLabel())).toList());
    }
}
