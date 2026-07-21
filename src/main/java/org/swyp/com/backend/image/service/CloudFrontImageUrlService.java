package org.swyp.com.backend.image.service;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${aws.s3.bucket.package.main}")
    private String mainPackagePrefix;
    @Value("${aws.s3.bucket.package.thumbnail}")
    private String thumbnailPackagePrefix;

    @Override
    public String toSignedUrl(String key) {
        if (key == null) {
            return null;
        }

        return sign(key);
    }

    @Override
    public String toSignedMainUrl(String imageKey) {
        if (imageKey == null) {
            return null;
        }

        return sign(mainPackagePrefix + imageKey + ".webp");
    }

    @Override
    public String toSignedThumbnailUrl(String imageKey) {
        if (imageKey == null) {
            return null;
        }

        return sign(thumbnailPackagePrefix + imageKey + ".webp");
    }

    private String sign(String key) {
        String resourceUrl = "https://" + cloudFrontProperties.getDomain() + "/" + key;

        CannedSignerRequest request = CannedSignerRequest.builder()
                .resourceUrl(resourceUrl)
                .privateKey(cloudFrontKeyProvider.getPrivateKey())
                .keyPairId(cloudFrontProperties.getKeyPairId())
                .expirationDate(Instant.now().plusSeconds(cloudFrontProperties.getTtlSeconds()))
                .build();

        return cloudFrontUtilities.getSignedUrlWithCannedPolicy(request).url();
    }
}
