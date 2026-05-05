package hub.com.apireports.service.domain;

import hub.com.apireports.entity.Report;
import hub.com.apireports.entity.enums.ReportStatus;
import hub.com.apireports.entity.enums.TrackingAction;
import hub.com.apireports.repo.ReportRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ReportServiceDomain {

    private final ReportRepo reportRepo;

    public Report findById(Long id){
            return  reportRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Report not found :"+id));
    }


    // status exist
    public void validateStatus(ReportStatus current, ReportStatus next) {

        Map<ReportStatus, List<ReportStatus>> validTransitions = Map.of(
                ReportStatus.PENDING,   List.of(ReportStatus.IN_REVIEW, ReportStatus.REJECTED),
                ReportStatus.IN_REVIEW, List.of(ReportStatus.RESOLVED, ReportStatus.REJECTED),
                ReportStatus.RESOLVED,  List.of(ReportStatus.CLOSED),
                ReportStatus.REJECTED,  List.of(),
                ReportStatus.CLOSED,    List.of()
        );

        List<ReportStatus> allowed = validTransitions.getOrDefault(current, List.of());

        if (!allowed.contains(next)) {
            throw new RuntimeException(
                    "Cannot transition from " + current + " to " + next +
                            ". Allowed: " + allowed
            );
        }
    }

    // trackingAction
    public TrackingAction getTrackingAction(ReportStatus status){
        return switch (status) {
            case IN_REVIEW -> TrackingAction.STATUS_CHANGED_TO_IN_REVIEW;
            case ASSIGNED -> TrackingAction.STATUS_CHANGED_TO_ASSIGNED;
            case RESOLVED -> TrackingAction.STATUS_CHANGED_TO_RESOLVED;
            case REJECTED -> TrackingAction.STATUS_CHANGED_TO_REJECTED;
            case CLOSED -> TrackingAction.STATUS_CHANGED_TO_CLOSED;

            default ->  throw new RuntimeException("Report status not found :"+status);
        };
    }
}
