package hub.com.apireports.mapper;


import hub.com.apireports.dto.tracking.TrackingHistoryDTOResponse;
import hub.com.apireports.entity.Report;
import hub.com.apireports.entity.TrackingHistory;
import hub.com.apireports.entity.security.Member;
import org.springframework.stereotype.Component;

@Component
public class TrackingHistoryMapper {

    public TrackingHistoryDTOResponse toTrackingDTOResponse(TrackingHistory trackingHistory, Report report ){
        return new  TrackingHistoryDTOResponse(
                trackingHistory.getId(),
                trackingHistory.getAction(),
                trackingHistory.getCoomment(),
                report.getId(),
                trackingHistory.getMember().getId(),
                trackingHistory.getMember().getName()
        );
    }
}
