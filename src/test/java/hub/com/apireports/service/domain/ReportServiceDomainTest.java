package hub.com.apireports.service.domain;

import hub.com.apireports.entity.Report;
import hub.com.apireports.entity.ReportFile;
import hub.com.apireports.entity.enums.FileType;
import hub.com.apireports.repo.ReportRepo;
import org.junit.jupiter.api.BeforeEach;
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

}
