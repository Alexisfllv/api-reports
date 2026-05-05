package hub.com.apireports.service;

import hub.com.apireports.dto.report.ReportDTORequest;
import hub.com.apireports.dto.report.ReportDTORequestToggleStatus;
import hub.com.apireports.dto.report.ReportDTOResponse;
import hub.com.apireports.dto.report.ReportSummaryDTOResponse;

import java.util.List;

public interface ReportService {

    // POST
    ReportDTOResponse createReport(ReportDTORequest reportDTORequest,Long memberId);


    // GET
    List<ReportDTOResponse> getAllReports();

    // Summary
    List<ReportSummaryDTOResponse> getAllReportSummaries();


    // PATCH
    ReportSummaryDTOResponse toggleReportStatus(Long idReport,Long idMember,ReportDTORequestToggleStatus  toggleStatus);
}
