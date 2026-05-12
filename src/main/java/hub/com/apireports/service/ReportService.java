package hub.com.apireports.service;

import hub.com.apireports.dto.report.*;
import hub.com.apireports.entity.security.Member;

import java.util.List;

public interface ReportService {

    // POST
    ReportDTOResponse createReport(ReportDTORequest reportDTORequest,Long memberId);


    // GET
    List<ReportDTOResponse> getAllReports();

    // dashboard
    DashboardSummaryDTOResponse getDashboardByReport();

    // Summary
    List<ReportSummaryDTOResponse> getAllReportSummaries();

    // reports by rol "Member"
    List<ReportSummaryDTOResponse> getReportSummariesByMember(Member member);


    // PATCH
    ReportSummaryDTOResponse toggleReportStatus(Long idReport,Long idMember,ReportDTORequestToggleStatus  toggleStatus);
}
