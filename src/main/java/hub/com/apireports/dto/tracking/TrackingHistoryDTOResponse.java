package hub.com.apireports.dto.tracking;

import hub.com.apireports.entity.enums.TrackingAction;

public record TrackingHistoryDTOResponse(
        Long id,
        TrackingAction action,
        String comment,
        Long reportId,
        Long memberId,
        String memberName
) {}
