package hub.com.apireports.service.impl;


import hub.com.apireports.dto.tracking.TrackingHistoryDTOResponse;
import hub.com.apireports.dto.tracking.TrackingHistoryGroupedDTOResponse;
import hub.com.apireports.entity.Report;
import hub.com.apireports.mapper.TrackingHistoryMapper;
import hub.com.apireports.repo.TrackingHistoryRepo;
import hub.com.apireports.service.TrackingHistoryService;
import hub.com.apireports.service.domain.ReportServiceDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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


    @Override
    public TrackingHistoryGroupedDTOResponse getAllTrackingHistoryGroupedByReport() {
        Map<Long, List<TrackingHistoryDTOResponse>> grouped = trackingHistoryRepo.findAll()
                .stream()
                .collect(Collectors.groupingBy(th -> th.getReport().getId(),
                        Collectors.mapping(th -> trackingHistoryMapper.toTrackingDTOResponse(th, th.getReport()),
                                Collectors.toList())
                ));
        return new TrackingHistoryGroupedDTOResponse(grouped);
    }


}
