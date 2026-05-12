package hub.com.apireports.service.impl;

import hub.com.apireports.dto.file.ReportFileDTOResponse;
import hub.com.apireports.entity.Report;
import hub.com.apireports.entity.ReportFile;
import hub.com.apireports.entity.TrackingHistory;
import hub.com.apireports.entity.enums.FileType;
import hub.com.apireports.entity.enums.PriorityLevel;
import hub.com.apireports.entity.enums.RegionType;
import hub.com.apireports.entity.enums.ReportStatus;
import hub.com.apireports.entity.security.Member;
import hub.com.apireports.entity.security.RoleType;
import hub.com.apireports.mapper.FileMapper;
import hub.com.apireports.repo.FileRepo;
import hub.com.apireports.repo.TrackingHistoryRepo;
import hub.com.apireports.service.domain.MemberServiceDomain;
import hub.com.apireports.service.domain.ReportServiceDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportFileServiceTest {

    @Mock
    private FileRepo fileRepo;

    @Mock
    private FileMapper fileMapper;

    @Mock
    private TrackingHistoryRepo trackingHistoryRepo;

    @Mock
    private MemberServiceDomain memberServiceDomain;

    @Mock
    private ReportServiceDomain reportServiceDomain;

    @InjectMocks
    private ReportFileServiceImpl reportFileService;

    @TempDir
    Path tempDir;

    private Member member;
    private Report report;

    @BeforeEach
    void setUp() throws Exception {
        // uploadDir temp
        Field uploadDirField = ReportFileServiceImpl.class.getDeclaredField("uploadDir");
        uploadDirField.setAccessible(true);
        uploadDirField.set(reportFileService, tempDir.toString());

        // maxsizeBytes
        Field maxSizeField = ReportFileServiceImpl.class.getDeclaredField("maxSizeBytes");
        maxSizeField.setAccessible(true);
        maxSizeField.set(reportFileService, 209715200L); // 200MB

        member = new Member();
        member.setId(1L);
        member.setName("Alexis");

        report = new Report();
        report.setId(1L);
        report.setTitle("Poste caído");

    }

    @Nested
    @DisplayName("uploadFiles happy path")
    class uploadFilesSuccess {

        @Test
        @DisplayName("should upload multiple valid files and return responses")
        void shouldUploadMultipleValidFilesAndReturnResponses() {
            // Arrange
            MockMultipartFile foto1 = new MockMultipartFile(
                    "files", "foto1.jpg", "image/jpeg", "foto1content".getBytes()
            );
            MockMultipartFile foto2 = new MockMultipartFile(
                    "files", "foto2.jpg", "image/jpeg", "foto2content".getBytes()
            );

            List<MultipartFile> files = List.of(foto1, foto2);

            ReportFile savedFile1 = new ReportFile();
            savedFile1.setId(null);
            savedFile1.setFileName("foto1.jpg");
            savedFile1.setUrl("uploads/reports/1/uuid1.jpge");
            savedFile1.setFileType(FileType.IMAGE);
            savedFile1.setReport(report);

            ReportFile savedFile2 = new ReportFile();
            savedFile2.setId(null);
            savedFile2.setFileName("foto2.jpg");
            savedFile2.setUrl("uploads/reports/2/uuid2.jpge");
            savedFile2.setFileType(FileType.IMAGE);
            savedFile2.setReport(report);

            ReportFileDTOResponse response1 = new ReportFileDTOResponse(
                    1L, "foto1.jpg", "uploads/reports/1/uuid1.jpge",
                    FileType.IMAGE, 10L, report.getId());

            ReportFileDTOResponse response2 = new ReportFileDTOResponse(
                    2L, "foto2.jpg", "uploads/reports/1/uuid2.jpge",
                    FileType.IMAGE, 10L, report.getId());

            when(reportServiceDomain.findById(1L)).thenReturn(report);
            when(memberServiceDomain.findById(1L)).thenReturn(member);
            when(fileRepo.save(any(ReportFile.class)))
                    .thenReturn(savedFile1)
                    .thenReturn(savedFile2);
            when(fileMapper.toFileDTOResponse(savedFile1)).thenReturn(response1);
            when(fileMapper.toFileDTOResponse(savedFile2)).thenReturn(response2);
            // Act
            List<ReportFileDTOResponse> result = reportFileService.uploadFiles(1L, 1L, files);

            // Assert
            assertAll(
                    () -> assertNotNull(result),
                    () -> assertEquals(2, result.size()),
                    () -> assertEquals(response1, result.get(0)),
                    () -> assertEquals(response2, result.get(1)),
                    () -> assertEquals("foto1.jpg", result.get(0).fileName()),
                    () -> assertEquals("foto2.jpg", result.get(1).fileName())
            );

            // InOrder & Verify
            verify(reportServiceDomain, times(1)).findById(1L);
            verify(memberServiceDomain, times(1)).findById(1L);
            verify(fileRepo, times(2)).save(any(ReportFile.class));
            verify(trackingHistoryRepo, times(2)).save(any(TrackingHistory.class));
            verify(fileMapper, times(2)).toFileDTOResponse(any(ReportFile.class));

        }
    }


    // validation
    @Nested
    @DisplayName("uploadFiles - validate list")
    class UploadFilesListValidation {

        @Test
        @DisplayName("should throw when list is empty")
        void uploadFiles_withEmptyList_throwsBusinessRuleException() {
            // Arrange
            when(reportServiceDomain.findById(1L)).thenReturn(report);
            when(memberServiceDomain.findById(1L)).thenReturn(member);


            // Act Assert
            assertThrows(RuntimeException.class,
                    () -> reportFileService.uploadFiles(1L, 1L, List.of()));

            // Verify
            verify(fileRepo, never()).save(any());
            verify(trackingHistoryRepo, never()).save(any());
        }

        @Test
        @DisplayName("should throw when files exceed max limit")
        void uploadFiles_withNullList_throwBusinessRuleException() {

            // Arrange
            when(reportServiceDomain.findById(1L)).thenReturn(report);
            when(memberServiceDomain.findById(1L)).thenReturn(member);
            // Act
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> reportFileService.uploadFiles(1L, 1L, null));
            // Assert
            assertEquals("At least one file is required", exception.getMessage());

            // Verify
            verify(fileRepo, never()).save(any());
            verify(trackingHistoryRepo, never()).save(any());

        }

        @Test
        @DisplayName("should throw when files exceed max limit")
        void uploadFiles_exceedingMaxFiles_throwsBusinessRuleException() {
            // Arrange

            // MAX = 10
            List<MultipartFile> files = Collections.nCopies(11,
                    new MockMultipartFile("files", "foto.jpg", "image/jpeg", "content".getBytes()));
            when(reportServiceDomain.findById(1L)).thenReturn(report);
            when(memberServiceDomain.findById(1L)).thenReturn(member);
            // Act
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> reportFileService.uploadFiles(1L, 1L, files));

            // Assert
            assertTrue(exception.getMessage().contains("Cannot upload more than "));
            // Verify
            verify(fileRepo, never()).save(any());
            verify(trackingHistoryRepo, never()).save(any());
        }
    }

    // validate files
    @Nested
    @DisplayName("uploadFiles - validate files individual")
    class UploadFilesSingleFileValidation {

        @Test
        @DisplayName("should throw when file is empty")
        void uploadFiles_withEmptyList_throwsBusinessRuleException() {
            // Arrange
            MockMultipartFile emptyFile = new MockMultipartFile(
                    "files", "foto.jpg", "image/jpeg", new byte[0]  // contenido vacío
            );

            when(reportServiceDomain.findById(1L)).thenReturn(report);
            when(memberServiceDomain.findById(1L)).thenReturn(member);

            // Act
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> reportFileService.uploadFiles(1L, 1L, List.of(emptyFile)));

            // Assert
            assertTrue(exception.getMessage().contains("File cannot be empty"));

            // Verify
            verify(fileRepo, never()).save(any());
            verify(trackingHistoryRepo, never()).save(any());
        }


        @Test
        @DisplayName("should throw when file type is not allowed")
        void uploadFiles_withInvalidFileType_throwBusinessRuleException() {
            // Arrange

            MockMultipartFile invalidFile = new MockMultipartFile(
                    "files", "documento.exe", "application/exe", "content".getBytes()
            );

            when(reportServiceDomain.findById(1L)).thenReturn(report);
            when(memberServiceDomain.findById(1L)).thenReturn(member);
            // Act
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> reportFileService.uploadFiles(1L, 1L, List.of(invalidFile)));
            // Assert
            assertTrue(exception.getMessage().contains("File type not allowed: "));

            // Verify
            verify(fileRepo, never()).save(any());
            verify(trackingHistoryRepo, never()).save(any());
        }

        @Test
        @DisplayName("should throw when file size exceeds limit")
        void uploadFiles_withFileSizeExceeded_throwsBusinessRuleException() {
            // Arrange
            // MAX  = 5MB
            byte[] bigContent = new byte[201 * 1024 * 1024];
            MockMultipartFile bigFile = new MockMultipartFile(
                    "files", "grande.jpge", "image/jpeg", bigContent
            );

            when(reportServiceDomain.findById(1L)).thenReturn(report);
            when(memberServiceDomain.findById(1L)).thenReturn(member);

            // Act
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> reportFileService.uploadFiles(1L, 1L, List.of(bigFile)));

            // Assert
            assertTrue(exception.getMessage().contains("exceeds maximum size of "));

            // Verify
            verify(fileRepo, never()).save(any());
            verify(trackingHistoryRepo, never()).save(any());
        }

        @Test
        @DisplayName("should throw when any file in list is invalid — none should be saved")
        void uploadFiles_withOneInvalidInList_noFileSaved() {
            // Arrange
            MockMultipartFile validFile = new MockMultipartFile(
                    "files", "foto.jpg", "image/jpeg", "content".getBytes()
            );

            MockMultipartFile invalidFile = new MockMultipartFile(
                    "files", "doc.exe", "application/exe", "content".getBytes()
            );

            when(reportServiceDomain.findById(1L)).thenReturn(report);
            when(memberServiceDomain.findById(1L)).thenReturn(member);
            // Act
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> reportFileService.uploadFiles(1L, 1L, List.of(validFile, invalidFile)));

            // Assert
            assertTrue(exception.getMessage().contains("File type not allowed"));

            // Verify
            verify(fileRepo, never()).save(any());
            verify(trackingHistoryRepo, never()).save(any());
        }
    }

}
