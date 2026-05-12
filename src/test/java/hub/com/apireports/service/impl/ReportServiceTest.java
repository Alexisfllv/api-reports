package hub.com.apireports.service.impl;

import hub.com.apireports.dto.file.ReportFileSummaryDTOResponse;
import hub.com.apireports.dto.report.*;
import hub.com.apireports.entity.Category;
import hub.com.apireports.entity.Report;
import hub.com.apireports.entity.ReportFile;
import hub.com.apireports.entity.TrackingHistory;
import hub.com.apireports.entity.enums.FileType;
import hub.com.apireports.entity.enums.PriorityLevel;
import hub.com.apireports.entity.enums.RegionType;
import hub.com.apireports.entity.enums.ReportStatus;
import hub.com.apireports.entity.security.Member;
import hub.com.apireports.mapper.ReportMapper;
import hub.com.apireports.repo.ReportRepo;
import hub.com.apireports.repo.TrackingHistoryRepo;
import hub.com.apireports.service.ReportService;
import hub.com.apireports.service.domain.CategoryServiceDomain;
import hub.com.apireports.service.domain.MemberServiceDomain;
import hub.com.apireports.service.domain.ReportServiceDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @Mock
    private ReportRepo reportRepo;

    @Mock
    private CategoryServiceDomain categoryServiceDomain;

    @Mock
    private TrackingHistoryRepo trackingHistoryRepo;

    @Mock
    private MemberServiceDomain memberServiceDomain;

    @Mock
    private ReportMapper reportMapper;

    @Mock
    private Clock clock;

    @Mock
    private ReportServiceDomain reportServiceDomain;

    @InjectMocks
    private ReportServiceImpl reportService;


    private LocalDateTime fixedNow;
    private Member member;
    private Category category;
    private ReportDTORequest request;
    private ReportDTOResponse response;
    private Report report;

    private LocalDateTime startOfDay;
    private LocalDateTime endOfDay;

    @BeforeEach
    void setUp() {
        fixedNow = LocalDateTime.of(2026, Month.MARCH, 24, 0, 0);
        lenient().when(clock.instant())
                .thenReturn(fixedNow.atZone(ZoneId.systemDefault()).toInstant());
        lenient().when(clock.getZone())
                .thenReturn(ZoneId.systemDefault());

        Member member = new Member();
        member.setId(2L);
        member.setName("Ferr");

        category = new Category();
        category.setId(2L);
        category.setName("Seguridad");

        report = new Report();
        report.setId(2L);
        report.setTitle("Robo en almacén");
        report.setDescription("Acceso no autorizado detectado");
        report.setIncidentDate(LocalDateTime.of(2026, Month.MARCH, 20, 10, 0));
        report.setReportDate(LocalDateTime.now());
        report.setCountry("PERU");
        report.setRegion(RegionType.LIMA);
        report.setProvince("LIMA");
        report.setDistrict("MIRAFLORES");
        report.setAddress("Av. Principal 123");
        report.setReference("Cerca de la puerta trasera");
        report.setLatitude(new BigDecimal("-12.0464"));
        report.setLongitude(new BigDecimal("-77.0428"));
        report.setPriorityLevel(PriorityLevel.HIGH);
        report.setStatus(ReportStatus.PENDING);
        report.setCategory(category);
        report.setMember(member);

        request = new ReportDTORequest(
                "Robo en almacén",
                "Acceso no autorizado detectado",
                LocalDateTime.of(2026, Month.MARCH, 20, 10, 0),
                RegionType.LIMA,
                "Lima",
                "Miraflores",
                "Av. Principal 123",
                "Cerca de la puerta trasera",
                new BigDecimal("-12.0464"),
                new BigDecimal("-77.0428"),
                PriorityLevel.HIGH,
                ReportStatus.PENDING,
                2L
        );

        response = new ReportDTOResponse(
                2L, // ID del reporte generado
                "Robo en almacén",
                "Acceso no autorizado detectado",
                LocalDateTime.of(2026, Month.MARCH, 20, 10, 0),
                LocalDateTime.now(), // O LocalDateTime.parse("2026-03-20T10:05:00")
                "PERU",
                RegionType.LIMA,
                "LIMA",
                "Lima",
                "MIRAFLORES",
                "Miraflores",
                "Av. Principal 123",
                "Cerca de la puerta trasera",
                new BigDecimal("-12.0464"),
                new BigDecimal("-77.0428"),
                PriorityLevel.HIGH,
                ReportStatus.PENDING,
                2L,
                "Seguridad", // Nombre de categoría asumido para ID 2L
                2L,
                "Ferr"
        );
    }


    @Test
    void createReport_withValidData_returnsReportDTOResponse() {

        // Arrange
        Report mappedReport = new Report();
        mappedReport.setId(10L);
        mappedReport.setTitle("Robo en almacén");
        mappedReport.setStatus(ReportStatus.PENDING);
        mappedReport.setMember(member);
        mappedReport.setCategory(category);

        ReportDTOResponse expectedResponse = new ReportDTOResponse(
                10L,
                "Robo en almacén",
                "Acceso no autorizado detectado",
                LocalDateTime.of(2026, Month.MARCH, 20, 10, 0),
                fixedNow,
                "PERU",
                RegionType.LIMA,
                "Lima",
                "Lima",
                "Miraflores",
                "Miraflores",
                "Av. Principal 123",
                "Cerca de la puerta trasera",
                new BigDecimal("-12.0464"),
                new BigDecimal("-77.0428"),
                PriorityLevel.HIGH,
                ReportStatus.PENDING,
                2L,
                "Seguridad",
                1L,
                "Alexis"
        );

        when(memberServiceDomain.findById(1L)).thenReturn(member);
        when(categoryServiceDomain.findById(2L)).thenReturn(category);
        when(reportMapper.toReport(request, category)).thenReturn(mappedReport);
        when(reportRepo.save(mappedReport)).thenReturn(mappedReport);
        when(reportMapper.toReportDTOResponse(mappedReport)).thenReturn(expectedResponse);

        // Act
        ReportDTOResponse result = reportService.createReport(request, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.id());
        assertEquals("Robo en almacén", result.title());
        assertEquals(ReportStatus.PENDING, result.status());
        assertEquals(fixedNow, result.reportDate());
        assertEquals("Seguridad", result.categoryName());
        assertEquals("Alexis", result.memberName());

        verify(memberServiceDomain, times(1)).findById(1L);
        verify(categoryServiceDomain, times(1)).findById(2L);
        verify(reportRepo, times(1)).save(mappedReport);
        verify(trackingHistoryRepo, times(1)).save(any(TrackingHistory.class));
    }

    @Test
    void getAllReports_() {

        // Arrange
        List<ReportDTOResponse> expectedResponse = List.of(response);
        List<Report> listReport = List.of(report);
        when(reportRepo.findAll()).thenReturn(listReport);
        when(reportMapper.toReportDTOResponse(report)).thenReturn(response);
        // Act
        List<ReportDTOResponse> result = reportService.getAllReports();
        // Assert
        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(response.id(), result.get(0).id()),
                () -> assertEquals(response.title(), result.get(0).title()),
                () -> assertEquals(response.status(), result.get(0).status()),
                () -> assertEquals(response.reportDate(), result.get(0).reportDate()),
                () -> assertEquals(response.categoryName(), result.get(0).categoryName()),
                () -> assertEquals(response.memberName(), result.get(0).memberName()),
                () -> assertEquals(response, result.get(0))
        );

        // InOrder & Verify
        InOrder inOrder = inOrder(reportRepo, reportMapper);
        inOrder.verify(reportRepo, times(1)).findAll();
        inOrder.verify(reportMapper, times(1)).toReportDTOResponse(report);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void getAllReportSummaries() {
        // Arrange

        ReportFileSummaryDTOResponse file1 = new ReportFileSummaryDTOResponse(
                1L,
                "546f1a771884f29105307598a5cee0c8.jpg",
                "/uploads/reports/1/50a702ff-7086-4ca1-8765-e27d77155974.jpg",
                FileType.IMAGE,
                40419L
        );

        ReportFileSummaryDTOResponse file2 = new ReportFileSummaryDTOResponse(
                2L,
                "sam.jpg",
                "/uploads/reports/1/73a5d774-ce05-4dd3-bec3-ff25e382397a.jpg",
                FileType.IMAGE,
                65345L
        );

        List<ReportFileSummaryDTOResponse> files = List.of(file1, file2);

        ReportSummaryDTOResponse expectedResponse = new ReportSummaryDTOResponse(
                1L,
                "Poste de luz caído",
                "Se reporta un poste de luz caído en medio de la vía pública generando peligro para peatones y vehículos.",
                LocalDateTime.of(2026, Month.MARCH, 20, 9, 0),
                LocalDateTime.of(2026, Month.MARCH, 20, 10, 0),
                "PERU",
                RegionType.LIMA,
                "LIMA",
                "Lima",
                "SAN_JUAN_DE_LURIGANCHO",
                "San Juan de Lurigancho",
                "Av. Próceres de la Independencia 1234",
                "Frente al mercado principal",
                new BigDecimal("-12.0464"),
                new BigDecimal("-77.0428"),
                PriorityLevel.CRITICAL,
                ReportStatus.PENDING,
                1L,
                "Vandalismo",
                1l,
                "Ferr",
                2,
                files
        );

        Report report = new Report();
        report.setId(1L);

        when(reportRepo.findallWithFiles()).thenReturn(List.of(report));
        when(reportMapper.toReportSummaryDTOResponse(report)).thenReturn(expectedResponse);

        // Act
        List<ReportSummaryDTOResponse> result = reportService.getAllReportSummaries();

        // Assert

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1, result.size()),
                () -> assertEquals(1L, result.get(0).id()),
                () -> assertEquals("Poste de luz caído", result.get(0).title()),
                () -> assertEquals(ReportStatus.PENDING, result.get(0).status()),
                () -> assertEquals(2, result.get(0).totalFiles()),
                () -> assertEquals(expectedResponse, result.get(0))
        );


        //  InOrder & Verify
        InOrder inOrder = inOrder(reportRepo, reportMapper);
        inOrder.verify(reportRepo, times(1)).findallWithFiles();
        inOrder.verify(reportMapper, times(1)).toReportSummaryDTOResponse(report);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void toggleReportStatus() {
        // Arrange
        Long reportId = 2L;
        Long memberId = 2L;

        Report reportUpdate = report;
        reportUpdate.setStatus(ReportStatus.IN_REVIEW);

        when(reportServiceDomain.findById(reportId)).thenReturn(report);
        when(memberServiceDomain.findById(memberId)).thenReturn(member);

        when(reportRepo.save(report)).thenReturn(reportUpdate);
        when(trackingHistoryRepo.save(any(TrackingHistory.class))).thenReturn(new TrackingHistory());

        ReportSummaryDTOResponse expectedResponse = new ReportSummaryDTOResponse(
                2L,
                "Robo en almacén",
                "Acceso no autorizado detectado",
                LocalDateTime.of(2026, Month.MARCH, 20, 10, 0),
                fixedNow,
                "PERU",
                RegionType.LIMA,
                "LIMA",
                "Lima",
                "MIRAFLORES",
                "Miraflores",
                "Av. Principal 123",
                "Cerca de la puerta trasera",
                new BigDecimal("-12.0464"),
                new BigDecimal("-77.0428"),
                PriorityLevel.HIGH,
                ReportStatus.IN_REVIEW,
                2L,
                "Seguridad",
                2L,
                "Ferr",
                0,
                List.of()
        );

        when(reportMapper.toReportSummaryDTOResponse(reportUpdate)).thenReturn(expectedResponse);

        ReportDTORequestToggleStatus toggleStatus = new ReportDTORequestToggleStatus(
                ReportStatus.IN_REVIEW,
                "Revisión inicial"
        );
        // Act
        ReportSummaryDTOResponse res = reportService.toggleReportStatus(reportId, memberId, toggleStatus);

        // Assert
        assertAll(
                () -> assertEquals(ReportStatus.IN_REVIEW, report.getStatus()),
                () -> assertEquals(expectedResponse, res)
        );

        // InOrder & Verify
    }

    @Test
    void getAllReportSummariesByMember() {
        // Arrange
        Member member = new Member();
        member.setId(1L);

        when(reportServiceDomain.validateMember(member)).thenReturn(List.of(report));

        ReportSummaryDTOResponse expectedResponse = new ReportSummaryDTOResponse(
                2L,
                "Robo en almacén",
                "Acceso no autorizado detectado",
                LocalDateTime.of(2026, Month.MARCH, 20, 10, 0),
                fixedNow,
                "PERU",
                RegionType.LIMA,
                "LIMA",
                "Lima",
                "MIRAFLORES",
                "Miraflores",
                "Av. Principal 123",
                "Cerca de la puerta trasera",
                new BigDecimal("-12.0464"),
                new BigDecimal("-77.0428"),
                PriorityLevel.HIGH,
                ReportStatus.PENDING,
                2L,
                "Seguridad",
                2L,
                "Ferr",
                0,
                List.of()
        );

        when(reportMapper.toReportSummaryDTOResponse(report)).thenReturn(expectedResponse);
        // Act
        List<ReportSummaryDTOResponse> responses = reportService.getReportSummariesByMember(member);

        // Assert
        assertAll(
                () -> assertEquals(1, responses.size()),
                () -> assertEquals(expectedResponse, responses.get(0))
        );

        // InOrder & Verify
        InOrder inOrder = inOrder(reportServiceDomain, reportMapper);
        inOrder.verify(reportServiceDomain, times(1)).validateMember(member);
        inOrder.verify(reportMapper, times(1)).toReportSummaryDTOResponse(report);
        inOrder.verifyNoMoreInteractions();

    }

    @Test
    void dashboardSummaryDTOResponse() {
        // Arrange
        when(reportRepo.countByStatus(ReportStatus.PENDING)).thenReturn(10L);
        when(reportRepo.countByStatus(ReportStatus.IN_REVIEW)).thenReturn(5L);
        when(reportRepo.countByStatus(ReportStatus.RESOLVED)).thenReturn(6L);
        when(reportRepo.countByStatus(ReportStatus.REJECTED)).thenReturn(2L);
        when(reportRepo.countByStatus(ReportStatus.CLOSED)).thenReturn(2L);

        when(reportRepo.countByPriorityLevel(PriorityLevel.CRITICAL)).thenReturn(4L);
        when(reportRepo.countByPriorityLevel(PriorityLevel.HIGH)).thenReturn(8L);
        when(reportRepo.countByPriorityLevel(PriorityLevel.MEDIUM)).thenReturn(9L);
        when(reportRepo.countByPriorityLevel(PriorityLevel.LOW)).thenReturn(4L);

        startOfDay = LocalDateTime.of(2026, Month.MARCH, 24, 0, 0);
        endOfDay = startOfDay.plusDays(1).minusNanos(1);

        when(reportRepo.countByPriorityLevelAndReportDateBetween(
                PriorityLevel.CRITICAL, startOfDay, endOfDay)).thenReturn(2L);

        when(reportRepo.countByStatusAndReportDateBetween(
                ReportStatus.PENDING, startOfDay, endOfDay)).thenReturn(3L);

        when(reportRepo.countByReportDateBetween(
                startOfDay, endOfDay)).thenReturn(5L);
        // Act
        DashboardSummaryDTOResponse result = reportService.getDashboardByReport();

        // Assert
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(25L, result.totalReports()),
                () -> assertEquals(10L, result.pending()),
                () -> assertEquals(5L, result.inReview()),
                () -> assertEquals(6L, result.resolved()),
                () -> assertEquals(2L, result.rejected()),
                () -> assertEquals(2L, result.closed()),
                () -> assertEquals(4L, result.totalCritical()),
                () -> assertEquals(8L, result.totalHigh()),
                () -> assertEquals(9L, result.totalMedium()),
                () -> assertEquals(4L, result.totalLow()),
                () -> assertEquals(2L, result.criticalToday()),
                () -> assertEquals(3L, result.pendingToday()),
                () -> assertEquals(5L, result.createdToday())
        );

        // Verify
        verify(reportRepo, times(1)).countByStatus(ReportStatus.PENDING);
        verify(reportRepo, times(1)).countByStatus(ReportStatus.IN_REVIEW);
        verify(reportRepo, times(1)).countByStatus(ReportStatus.RESOLVED);
        verify(reportRepo, times(1)).countByStatus(ReportStatus.REJECTED);
        verify(reportRepo, times(1)).countByStatus(ReportStatus.CLOSED);
        verify(reportRepo, times(1)).countByPriorityLevel(PriorityLevel.CRITICAL);
        verify(reportRepo, times(1)).countByPriorityLevel(PriorityLevel.HIGH);
        verify(reportRepo, times(1)).countByPriorityLevel(PriorityLevel.MEDIUM);
        verify(reportRepo, times(1)).countByPriorityLevel(PriorityLevel.LOW);
        verify(reportRepo, times(1)).countByPriorityLevelAndReportDateBetween(
                PriorityLevel.CRITICAL, startOfDay, endOfDay);
        verify(reportRepo, times(1)).countByStatusAndReportDateBetween(
                ReportStatus.PENDING, startOfDay, endOfDay);
        verify(reportRepo, times(1)).countByReportDateBetween(startOfDay, endOfDay);
        verifyNoMoreInteractions(reportRepo);

    }

}
