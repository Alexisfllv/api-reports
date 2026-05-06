package hub.com.apireports.service.domain;

import hub.com.apireports.entity.Report;
import hub.com.apireports.entity.ReportFile;
import hub.com.apireports.entity.enums.FileType;
import hub.com.apireports.entity.enums.ReportStatus;
import hub.com.apireports.entity.enums.TrackingAction;
import hub.com.apireports.repo.ReportRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportServiceDomainTest {

    @Mock
    private ReportRepo reportRepo;

    @InjectMocks
    private ReportServiceDomain reportServiceDomain;

    ReportFile reportFile;
    Report report;

    @BeforeEach
    void setUp() {
        report = new Report();
        report.setId(1L);

        reportFile = new ReportFile(
                1L,
                "prueba.png",
                "/uploads/reports/1/aksjd8923emksdmasd.jpg",
                FileType.IMAGE,
                404019L,
                report
        );
    }

    @Nested
    class FindByIdTeST{

        @Test
        void findById() {
            // Arrange
            when(reportRepo.findById(1L)).thenReturn(Optional.of(report));

            // Act
            Report result = reportServiceDomain.findById(1L);

            // Assert
            assertEquals(1L, result.getId());

            // InOrder & Verify
            InOrder inOrder = Mockito.inOrder(reportRepo);
            inOrder.verify(reportRepo).findById(1L);
            inOrder.verifyNoMoreInteractions();
        }

        @Test
        void findByIdNotFound() {
            // Arrange
            Long idNotExist = 99L;
            when(reportRepo.findById(idNotExist)).thenReturn(Optional.empty());

            // Act
            RuntimeException ex = assertThrows(RuntimeException.class, () -> reportServiceDomain.findById(idNotExist));

            // Assert
            assertTrue(ex.getMessage().contains("Report not found :"));

            // InOrder & Verify
            InOrder inOrder = Mockito.inOrder(reportRepo);
            inOrder.verify(reportRepo).findById(idNotExist);
            inOrder.verifyNoMoreInteractions();
        }
    }

    @Nested
    @DisplayName("validateStatus")
    class ValidateStatus {

        @Nested
        @DisplayName("valid transitions")
        class ValidTransitions {

            @Test
            @DisplayName("PENDING → IN_REVIEW is valid")
            void validateStatus_pendingToInReview_noException() {
                assertDoesNotThrow(() ->
                        reportServiceDomain.validateStatus(ReportStatus.PENDING, ReportStatus.IN_REVIEW));
            }

            @Test
            @DisplayName("PENDING → REJECTED is valid")
            void validateStatus_pendingToRejected_noException() {
                assertDoesNotThrow(() ->
                        reportServiceDomain.validateStatus(ReportStatus.PENDING, ReportStatus.REJECTED));
            }

            @Test
            @DisplayName("IN_REVIEW → RESOLVED is valid")
            void validateStatus_inReviewToResolved_noException() {
                assertDoesNotThrow(() ->
                        reportServiceDomain.validateStatus(ReportStatus.IN_REVIEW, ReportStatus.RESOLVED));
            }

            @Test
            @DisplayName("IN_REVIEW → REJECTED is valid")
            void validateStatus_inReviewToRejected_noException() {
                assertDoesNotThrow(() ->
                        reportServiceDomain.validateStatus(ReportStatus.IN_REVIEW, ReportStatus.REJECTED));
            }

            @Test
            @DisplayName("RESOLVED → CLOSED is valid")
            void validateStatus_resolvedToClosed_noException() {
                assertDoesNotThrow(() ->
                        reportServiceDomain.validateStatus(ReportStatus.RESOLVED, ReportStatus.CLOSED));
            }
        }

        @Nested
        @DisplayName("invalid transitions")
        class InvalidTransitions {

            @Test
            @DisplayName("PENDING → CLOSED is invalid")
            void validateStatus_pendingToClosed_throwsException() {
                RuntimeException ex = assertThrows(RuntimeException.class, () ->
                        reportServiceDomain.validateStatus(ReportStatus.PENDING, ReportStatus.CLOSED));
                assertTrue(ex.getMessage().contains("Cannot transition from PENDING to CLOSED"));
            }

            @Test
            @DisplayName("REJECTED → any is invalid")
            void validateStatus_fromRejected_throwsException() {
                assertThrows(RuntimeException.class, () ->
                        reportServiceDomain.validateStatus(ReportStatus.REJECTED, ReportStatus.IN_REVIEW));
            }

            @Test
            @DisplayName("CLOSED → any is invalid")
            void validateStatus_fromClosed_throwsException() {
                assertThrows(RuntimeException.class, () ->
                        reportServiceDomain.validateStatus(ReportStatus.CLOSED, ReportStatus.RESOLVED));
            }

            @Test
            @DisplayName("IN_REVIEW → PENDING is invalid")
            void validateStatus_inReviewToPending_throwsException() {
                assertThrows(RuntimeException.class, () ->
                        reportServiceDomain.validateStatus(ReportStatus.IN_REVIEW, ReportStatus.PENDING));
            }
        }
    }


    @Nested
    @DisplayName("getTrackingAction")
    class GetTrackingAction {

        @Test
        @DisplayName("IN_REVIEW → STATUS_CHANGED_TO_IN_REVIEW")
        void getTrackingAction_inReview_returnsCorrectAction() {
            assertEquals(TrackingAction.STATUS_CHANGED_TO_IN_REVIEW,
                    reportServiceDomain.getTrackingAction(ReportStatus.IN_REVIEW));
        }

        @Test
        @DisplayName("RESOLVED → STATUS_CHANGED_TO_RESOLVED")
        void getTrackingAction_resolved_returnsCorrectAction() {
            assertEquals(TrackingAction.STATUS_CHANGED_TO_RESOLVED,
                    reportServiceDomain.getTrackingAction(ReportStatus.RESOLVED));
        }

        @Test
        @DisplayName("REJECTED → STATUS_CHANGED_TO_REJECTED")
        void getTrackingAction_rejected_returnsCorrectAction() {
            assertEquals(TrackingAction.STATUS_CHANGED_TO_REJECTED,
                    reportServiceDomain.getTrackingAction(ReportStatus.REJECTED));
        }

        @Test
        @DisplayName("CLOSED → STATUS_CHANGED_TO_CLOSED")
        void getTrackingAction_closed_returnsCorrectAction() {
            assertEquals(TrackingAction.STATUS_CHANGED_TO_CLOSED,
                    reportServiceDomain.getTrackingAction(ReportStatus.CLOSED));
        }

        @Test
        @DisplayName("PENDING → throws exception")
        void getTrackingAction_pending_throwsException() {
            RuntimeException ex = assertThrows(RuntimeException.class, () ->
                    reportServiceDomain.getTrackingAction(ReportStatus.PENDING));
            assertTrue(ex.getMessage().contains("Report status not found"));
        }
    }



}
