package hub.com.apireports.dto.tracking;

import java.util.List;
import java.util.Map;

public record TrackingHistoryGroupedDTOResponse(
        Map<Long, List<TrackingHistoryDTOResponse>> groupedByReport
) {}
