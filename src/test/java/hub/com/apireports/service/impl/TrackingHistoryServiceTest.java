package hub.com.apireports.service.impl;

import hub.com.apireports.dto.tracking.TrackingHistoryDTOResponse;
import hub.com.apireports.dto.tracking.TrackingHistoryGroupedDTOResponse;
import hub.com.apireports.entity.Report;
import hub.com.apireports.entity.TrackingHistory;
import hub.com.apireports.entity.enums.TrackingAction;
import hub.com.apireports.entity.security.Member;
import hub.com.apireports.mapper.TrackingHistoryMapper;
import hub.com.apireports.repo.TrackingHistoryRepo;
import hub.com.apireports.service.TrackingHistoryService;
import hub.com.apireports.service.domain.ReportServiceDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrackingHistoryServiceTest {

    @Mock
    private TrackingHistoryRepo trackingHistoryRepo;

    @Mock
    private ReportServiceDomain  reportServiceDomain;

    @Mock
    private TrackingHistoryMapper  trackingHistoryMapper;

    @InjectMocks
    private TrackingHistoryServiceImpl trackingHistoryService;

    TrackingHistory trackingHistory;
    TrackingHistoryDTOResponse  trackingHistoryDTOResponse;

    Member member;
    Report report;

    @BeforeEach
    void setUp(){
        member = new Member();
        member.setId(2L);
        member.setName("alexis");

        report = new Report();
        report.setId(1L);


        trackingHistory = new TrackingHistory(
                1L,
                TrackingAction.REPORT_CREATED,
                "Initial report",
                report,
                member
        );

        trackingHistoryDTOResponse = new TrackingHistoryDTOResponse(
                1L,
                TrackingAction.REPORT_CREATED,
                "Initial report",
                report.getId(),
                member.getId(),
                member.getName()
        );
    }

    @Test
    void getTrackingHistoryByReportId(){
        // Arrange
        Long reportId = 1L;
        when(reportServiceDomain.findById(reportId)).thenReturn(report);
        when(trackingHistoryRepo.findByReportId(reportId)).thenReturn(List.of(trackingHistory));
        when(trackingHistoryMapper.toTrackingDTOResponse(trackingHistory, report)).thenReturn(trackingHistoryDTOResponse);

        // Act
        List<TrackingHistoryDTOResponse> Listresponse = trackingHistoryService.getTrackingHistoryByReportId(reportId);

        // Assert
        assertAll(
                () -> assertTrue(Listresponse.size() == 1),
                () -> assertTrue(Listresponse.get(0).id().equals(trackingHistoryDTOResponse.id())),
                () -> assertTrue(Listresponse.get(0).action().equals(trackingHistoryDTOResponse.action())),
                () -> assertTrue(Listresponse.get(0).comment().equals(trackingHistoryDTOResponse.comment())),
                () -> assertTrue(Listresponse.get(0).reportId().equals(trackingHistoryDTOResponse.reportId())),
                () -> assertTrue(Listresponse.get(0).memberId().equals(trackingHistoryDTOResponse.memberId())),
                () -> assertTrue(Listresponse.get(0).memberName().equals(trackingHistoryDTOResponse.memberName()))
        );

        //
        InOrder inOrder = Mockito.inOrder(reportServiceDomain,trackingHistoryRepo, trackingHistoryMapper);
        inOrder.verify(reportServiceDomain).findById(reportId);
        inOrder.verify(trackingHistoryRepo).findByReportId(reportId);
        inOrder.verify(trackingHistoryMapper).toTrackingDTOResponse(trackingHistory, report);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void getAllTrackingHistoryGroupedByReport(){
        // Arrange
        Member member2 = new Member();
        member2.setId(3L);
        member2.setName("carlos");

        Report report2 = new Report();
        report2.setId(2L);

        TrackingHistory trackingHistory2 = new TrackingHistory(
                2L,
                TrackingAction.REPORT_CREATED,
                "Second report",
                report2,
                member2
        );

        TrackingHistoryDTOResponse trackingHistoryDTOResponse2 = new TrackingHistoryDTOResponse(
                2L,
                TrackingAction.REPORT_CREATED,
                "Second report",
                report2.getId(),
                member2.getId(),
                member2.getName()
        );

        // Mock findAll devolviendo múltiples tracking histories
        when(trackingHistoryRepo.findAll()).thenReturn(List.of(trackingHistory, trackingHistory2));

        // Mock mapper para ambos tracking histories
        when(trackingHistoryMapper.toTrackingDTOResponse(trackingHistory, report)).thenReturn(trackingHistoryDTOResponse);
        when(trackingHistoryMapper.toTrackingDTOResponse(trackingHistory2, report2)).thenReturn(trackingHistoryDTOResponse2);

        // Act
        TrackingHistoryGroupedDTOResponse result = trackingHistoryService.getAllTrackingHistoryGroupedByReport();

        // Assert
        assertAll(
                () -> assertTrue(result.groupedByReport().size() == 2),
                () -> assertTrue(result.groupedByReport().containsKey(1L)),
                () -> assertTrue(result.groupedByReport().containsKey(2L)),
                () -> assertTrue(result.groupedByReport().get(1L).size() == 1),
                () -> assertTrue(result.groupedByReport().get(2L).size() == 1),
                () -> assertTrue(result.groupedByReport().get(1L).get(0).id().equals(1L)),
                () -> assertTrue(result.groupedByReport().get(2L).get(0).id().equals(2L))
        );

        // Verify mocks were called
        InOrder inOrder = Mockito.inOrder(trackingHistoryRepo, trackingHistoryMapper);
        inOrder.verify(trackingHistoryRepo).findAll();
        inOrder.verify(trackingHistoryMapper).toTrackingDTOResponse(trackingHistory, report);
        inOrder.verify(trackingHistoryMapper).toTrackingDTOResponse(trackingHistory2, report2);
        inOrder.verifyNoMoreInteractions();
    }
}
