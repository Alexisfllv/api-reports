package hub.com.apireports.service.impl;

import hub.com.apireports.dto.report.ReportDTORequest;
import hub.com.apireports.dto.report.ReportDTOResponse;
import hub.com.apireports.entity.Category;
import hub.com.apireports.entity.Report;
import hub.com.apireports.entity.TrackingHistory;
import hub.com.apireports.entity.enums.TrackingAction;
import hub.com.apireports.entity.security.Member;
import hub.com.apireports.mapper.ReportMapper;
import hub.com.apireports.repo.CategoryRepo;
import hub.com.apireports.repo.MemberRepo;
import hub.com.apireports.repo.ReportRepo;
import hub.com.apireports.repo.TrackingHistoryRepo;
import hub.com.apireports.service.ReportService;
import hub.com.apireports.service.domain.CategoryServiceDomain;
import hub.com.apireports.service.domain.MemberServiceDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final ReportMapper reportMapper;
    private final ReportRepo reportRepo;
    private final TrackingHistoryRepo  trackingHistoryRepo;
    // cat
    private final CategoryServiceDomain categoryServiceDomain;
    // member
    private final MemberServiceDomain memberServiceDomain;
    // clock
    private final Clock clock;

    @Override
    @Transactional
    public ReportDTOResponse createReport(ReportDTORequest reportDTORequest, Long memberId) {
        // member
        Member member = memberServiceDomain.findById(memberId);
        // category
        Category category = categoryServiceDomain.findById(reportDTORequest.categoryId());

        // report
        Report report = reportMapper.toReport(reportDTORequest, category);
        report.setMember(member);
        report.setReportDate(LocalDateTime.now(clock));
        report.setCountry("PERU");

        Report savedReport = reportRepo.save(report);

        // trackingHistory
        TrackingHistory tracking = new TrackingHistory();
        tracking.setAction(TrackingAction.REPORT_CREATED);
        tracking.setCoomment("Initial report");
        tracking.setReport(savedReport);
        tracking.setMember(member);
        trackingHistoryRepo.save(tracking);

        return reportMapper.toReportDTOResponse(savedReport);

    }

    @Override
    public List<ReportDTOResponse> getAllReports() {
        List<Report> reports = reportRepo.findAll();
        return reports.stream()
                .map(reportMapper::toReportDTOResponse)
                .toList();
    }
}
