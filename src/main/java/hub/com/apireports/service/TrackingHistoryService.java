package hub.com.apireports.service;

import hub.com.apireports.dto.tracking.TrackingHistoryDTOResponse;
import hub.com.apireports.dto.tracking.TrackingHistoryGroupedDTOResponse;

import java.util.List;

public interface TrackingHistoryService {

    // GET ADMIN
    List<TrackingHistoryDTOResponse> getTrackingHistoryByReportId(Long reportId);


    // GET ALL grouped by report
    TrackingHistoryGroupedDTOResponse getAllTrackingHistoryGroupedByReport();
}
