package hub.com.apireports.service.impl;

import hub.com.apireports.dto.tracking.TrackingHistoryDTOResponse;
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


}
