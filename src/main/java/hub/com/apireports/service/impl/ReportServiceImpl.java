package hub.com.apireports.service.impl;

import hub.com.apireports.dto.report.*;
import hub.com.apireports.entity.Category;
import hub.com.apireports.entity.Report;
import hub.com.apireports.entity.TrackingHistory;
import hub.com.apireports.entity.enums.PriorityLevel;
import hub.com.apireports.entity.enums.ReportStatus;
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
import hub.com.apireports.service.domain.ReportServiceDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
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
    private final ReportServiceDomain reportServiceDomain;

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
        report.setStatus(ReportStatus.PENDING);
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

    @Override
    public DashboardSummaryDTOResponse getDashboardByReport() {
        LocalDateTime startOfDay = LocalDate.now(clock).atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
        // total por status
        Long pending = reportRepo.countByStatus(ReportStatus.PENDING);
        Long inReview = reportRepo.countByStatus(ReportStatus.IN_REVIEW);
        Long resolved = reportRepo.countByStatus(ReportStatus.RESOLVED);
        Long rejected = reportRepo.countByStatus(ReportStatus.REJECTED);
        Long closed = reportRepo.countByStatus(ReportStatus.CLOSED);
        Long total = pending + inReview + resolved + rejected + closed;

        // total priority
        Long critical = reportRepo.countByPriorityLevel(PriorityLevel.CRITICAL);
        Long High = reportRepo.countByPriorityLevel(PriorityLevel.HIGH);
        Long medium = reportRepo.countByPriorityLevel(PriorityLevel.MEDIUM);
        Long Low = reportRepo.countByPriorityLevel(PriorityLevel.LOW);

        // today
        Long criticalToday = reportRepo.countByPriorityLevelAndReportDateBetween(
                PriorityLevel.CRITICAL, startOfDay, endOfDay
        );
        Long pendingToday = reportRepo.countByStatusAndReportDateBetween(
                ReportStatus.PENDING, startOfDay, endOfDay
        );
        Long createdToday = reportRepo.countByReportDateBetween(
                startOfDay, endOfDay
        );
        return new DashboardSummaryDTOResponse(
                total, pending, inReview, resolved, rejected, closed,
                critical, High, medium, Low,
                criticalToday, pendingToday, createdToday
        );
    }

    @Override
    public List<ReportSummaryDTOResponse> getAllReportSummaries() {
        return reportRepo.findallWithFiles()
                .stream()
                .map(reportMapper::toReportSummaryDTOResponse)
                .toList();
    }

    @Override
    public List<ReportSummaryDTOResponse> getReportSummariesByMember(Member member) {

        List<Report> reports;

        reports = reportServiceDomain.validateMember(member);

        return reports.stream()
                .map(reportMapper::toReportSummaryDTOResponse)
                .toList();
    }

    @Override
    @Transactional
    public ReportSummaryDTOResponse toggleReportStatus(Long idReport,Long idMember, ReportDTORequestToggleStatus toggleStatus) {
        // report id
        Report report = reportServiceDomain.findById(idReport);

        // member id
        Member member = memberServiceDomain.findById(idMember);

        reportServiceDomain.validateStatus(report.getStatus(), toggleStatus.newStatus());

        report.setStatus(toggleStatus.newStatus());
        Report updatedReport = reportRepo.save(report);

        // create TrackingHistory
        TrackingHistory tracking = new TrackingHistory();
        tracking.setId(null);
        tracking.setCoomment(toggleStatus.comment());
        tracking.setAction(reportServiceDomain.getTrackingAction(toggleStatus.newStatus()));
        tracking.setReport(updatedReport);
        tracking.setMember(member);
        trackingHistoryRepo.save(tracking);

        return reportMapper.toReportSummaryDTOResponse(updatedReport);
    }
}
