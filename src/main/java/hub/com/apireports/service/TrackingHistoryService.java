package hub.com.apireports.service;

import hub.com.apireports.dto.tracking.TrackingHistoryDTOResponse;

import java.util.List;

public interface TrackingHistoryService {

    // GET ADMIN
    List<TrackingHistoryDTOResponse> getTrackingHistoryByReportId(Long reportId);
}
