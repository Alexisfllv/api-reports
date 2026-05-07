package hub.com.apireports.service.impl;


import hub.com.apireports.dto.tracking.TrackingHistoryDTOResponse;
import hub.com.apireports.entity.Report;
import hub.com.apireports.mapper.TrackingHistoryMapper;
import hub.com.apireports.repo.TrackingHistoryRepo;
import hub.com.apireports.service.TrackingHistoryService;
import hub.com.apireports.service.domain.ReportServiceDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrackingHistoryServiceImpl implements TrackingHistoryService {

    private final TrackingHistoryRepo trackingHistoryRepo;
    private final ReportServiceDomain reportServiceDomain;
    private final TrackingHistoryMapper trackingHistoryMapper;


    @Override
    public List<TrackingHistoryDTOResponse> getTrackingHistoryByReportId(Long reportId) {

        Report report = reportServiceDomain.findById(reportId);

        return trackingHistoryRepo.findByReportId(reportId)
                .stream()
                .map(trackingHistory -> trackingHistoryMapper.toTrackingDTOResponse(trackingHistory, report))
                .toList();
    }
}
