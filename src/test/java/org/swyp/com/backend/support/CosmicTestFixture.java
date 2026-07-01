package org.swyp.com.backend.support;

import org.springframework.test.util.ReflectionTestUtils;
import org.swyp.com.backend.cosmic.domain.Cosmic;
import org.swyp.com.backend.global.enumeration.CosmicDatingType;

public class CosmicTestFixture {
    public static final Long TEST_COSMIC_ID = 1L;
    public static final String TEST_COSMIC_DETAIL = "test_cosmic_detail";
    public static final String TEST_COSMIC_IMAGE_KEY = "test/image-key";
    public static final Boolean TEST_COSMIC_DELETED = false;

    public static Cosmic createCosmic(Long id, CosmicDatingType type, String detail, String imageKey, Boolean deleted) {
        Cosmic cosmic = new Cosmic();
        ReflectionTestUtils.setField(cosmic, "id", id);
        ReflectionTestUtils.setField(cosmic, "type", type);
        ReflectionTestUtils.setField(cosmic, "detail", detail);
        ReflectionTestUtils.setField(cosmic, "imageKey", imageKey);
        ReflectionTestUtils.setField(cosmic, "deleted", deleted);
        return cosmic;
    }
}
