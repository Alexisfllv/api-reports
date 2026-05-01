package hub.com.apireports.service;

import hub.com.apireports.dto.report.ReportDTORequest;
import hub.com.apireports.dto.report.ReportDTOResponse;

public interface ReportService {

    // POST
    ReportDTOResponse createReport(ReportDTORequest reportDTORequest,Long memberId);
}
