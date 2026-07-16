package org.swyp.com.backend.image.service;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.swyp.com.backend.image.config.CloudFrontKeyProvider;
import org.swyp.com.backend.image.config.CloudFrontProperties;
import software.amazon.awssdk.services.cloudfront.CloudFrontUtilities;
import software.amazon.awssdk.services.cloudfront.model.CannedSignerRequest;

@Service
@RequiredArgsConstructor
public class CloudFrontImageUrlService implements ImageUrlService {

    private final CloudFrontUtilities cloudFrontUtilities;
    private final CloudFrontProperties cloudFrontProperties;
    private final CloudFrontKeyProvider cloudFrontKeyProvider;

    @Override
    public String toSignedUrl(String imageKey) {
        if (imageKey == null) {
            return null;
        }

        String resourceUrl = "https://" + cloudFrontProperties.getDomain() + "/" + imageKey;

        CannedSignerRequest request = CannedSignerRequest.builder()
                .resourceUrl(resourceUrl)
                .privateKey(cloudFrontKeyProvider.getPrivateKey())
                .keyPairId(cloudFrontProperties.getKeyPairId())
                .expirationDate(Instant.now().plusSeconds(cloudFrontProperties.getTtlSeconds()))
                .build();

        return cloudFrontUtilities.getSignedUrlWithCannedPolicy(request).url();
    }
}
