package hub.com.apireports.dto.report;

import hub.com.apireports.dto.file.ReportFileSummaryDTOResponse;
import hub.com.apireports.entity.enums.PriorityLevel;
import hub.com.apireports.entity.enums.RegionType;
import hub.com.apireports.entity.enums.ReportStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ReportSummaryDTOResponse(

        Long id,
        String title,
        String description,
        LocalDateTime incidentDate,
        LocalDateTime reportDate,
        String country,
        RegionType region,
        String province,
        String provinceLabel,
        String district,
        String districtLabel,
        String address,
        String reference,
        BigDecimal latitude,
        BigDecimal longitude,
        PriorityLevel priorityLevel,
        ReportStatus status,
        // Category
        Long categoryId,
        String categoryName,
        // Member
        Long memberId,
        String memberName,
        // TotalReportFiles
        Integer totalFiles,
        // List ReportFiles
        List<ReportFileSummaryDTOResponse> files

) {}
