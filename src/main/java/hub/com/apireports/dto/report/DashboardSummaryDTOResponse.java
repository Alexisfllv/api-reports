package hub.com.apireports.dto.report;

public record DashboardSummaryDTOResponse(

        // total de status
        Long totalReports,

        Long pending,
        Long inReview,
        Long resolved,
        Long rejected,
        Long closed,

        // priority
        Long totalCritical,
        Long totalHigh,
        Long totalMedium,
        Long totalLow,

        // hoy
        Long criticalToday,
        Long pendingToday,
        Long createdToday
) {}
