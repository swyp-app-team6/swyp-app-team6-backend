package org.swyp.com.backend.region.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyp.com.backend.region.domain.repository.RegionGroupEntityRepository;
import org.swyp.com.backend.region.dto.Region;
import org.swyp.com.backend.region.dto.RegionLabel;
import org.swyp.com.backend.region.dto.RegionResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegionService {
    private final RegionGroupEntityRepository regionGroupEntityRepository;

    public RegionResponse getRegionResponse() {
        List<Region> regionList = regionGroupEntityRepository.findAll().stream().map(rg -> {
            List<RegionLabel> labels = rg.getRegionDetailList().stream()
                    .map(rd -> new RegionLabel(rd.getRegionDetail(), rd.getRegionDetail()
                            .getLabel())).toList();

            return new Region(rg.getRegionGroup(), rg.getRegionGroup().getLabel(), labels);
        }).toList();

        return new RegionResponse(regionList);
    }
}
