package hub.com.apireports.service.impl;

import hub.com.apireports.dto.report.ReportDTORequest;
import hub.com.apireports.dto.report.ReportDTOResponse;
import hub.com.apireports.entity.Category;
import hub.com.apireports.entity.Report;
import hub.com.apireports.entity.TrackingHistory;
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
import org.junit.jupiter.api.BeforeEach;
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

    @InjectMocks
    private ReportServiceImpl reportService;



    private LocalDateTime fixedNow;
    private Member member;
    private Category category;
    private ReportDTORequest request;
    private ReportDTOResponse response;
    private Report report;

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
    void getAllReports_(){

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
                () -> assertEquals(response,result.get(0))
        );

        // InOrder & Verify
        InOrder inOrder = inOrder(reportRepo, reportMapper);
        inOrder.verify(reportRepo, times(1)).findAll();
        inOrder.verify(reportMapper, times(1)).toReportDTOResponse(report);
        inOrder.verifyNoMoreInteractions();
    }
}
