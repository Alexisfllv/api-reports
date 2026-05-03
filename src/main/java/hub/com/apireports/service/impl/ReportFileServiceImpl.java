package hub.com.apireports.service.impl;

import hub.com.apireports.dto.file.ReportFileDTOResponse;
import hub.com.apireports.entity.Report;
import hub.com.apireports.entity.ReportFile;
import hub.com.apireports.entity.TrackingHistory;
import hub.com.apireports.entity.enums.FileType;
import hub.com.apireports.entity.enums.TrackingAction;
import hub.com.apireports.entity.security.Member;
import hub.com.apireports.mapper.FileMapper;
import hub.com.apireports.repo.FileRepo;
import hub.com.apireports.repo.ReportRepo;
import hub.com.apireports.repo.TrackingHistoryRepo;
import hub.com.apireports.service.ReportFileService;
import hub.com.apireports.service.domain.MemberServiceDomain;
import hub.com.apireports.service.domain.ReportServiceDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportFileServiceImpl implements ReportFileService {


    private final ReportRepo reportRepo;
    private final FileRepo fileRepo;
    private final ReportServiceDomain reportServiceDomain;
    private final FileMapper fileMapper;
    private final MemberServiceDomain memberServiceDomain;
    private final TrackingHistoryRepo trackingHistoryRepo;

    // dir
    @Value("${file.upload.dir}")
    private String uploadDir;

    @Value("${file.upload.max-size-bytes}")
    private Long maxSizeBytes;


    private static final Map<String, FileType> ALLOWED_TYPES = Map.of(
            "image/jpeg",      FileType.IMAGE,
            "image/png",       FileType.IMAGE,
            "video/mp4",       FileType.VIDEO,
            "application/pdf", FileType.PDF
    );
    private static final int MAX_FILES = 10;

    @Override
    @Transactional
    public List<ReportFileDTOResponse> uploadFiles(Long reportId,Long memberId, List<MultipartFile> files) {
        // report
        Report reportExist = reportServiceDomain.findById(reportId);
        Member memberExist = memberServiceDomain.findById(memberId);

        // validate list empty
        if (files == null || files.isEmpty()) {
            throw new RuntimeException("At least one file is required");
        }

        // validate size max
        if (files.size() > MAX_FILES) {
            throw new RuntimeException(
                    "Cannot upload more than " + MAX_FILES + " files at once"
            );
        }

        // validate files
        for (MultipartFile file : files) {
            validateFile(file);
        }

        // create dir file if not exist
        Path reportFolder = Paths.get(uploadDir,String.valueOf(reportId));
        try {
            Files.createDirectories(reportFolder);
        } catch (IOException E){
            throw new RuntimeException("Could not create upload directory");
        }

        // save file
        List<ReportFileDTOResponse> responses = new ArrayList<>();

        for (MultipartFile file : files) {
            ReportFileDTOResponse response = processSingleFile(
                    file,reportExist,memberExist,reportFolder
            );
            responses.add(response);
        }
        return responses;
    }

    // Methods private
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()){
            throw new RuntimeException("File cannot be empty");
        }

        if (file.getSize() > maxSizeBytes){
            throw new RuntimeException(
                    "File " + file.getOriginalFilename() +
                            " exceeds maximum size of " + (maxSizeBytes / 1024 / 1024) + "MB"
            );
        }

        String contentType = file.getContentType();

        if (contentType == null || !ALLOWED_TYPES.containsKey(contentType)){
            throw new RuntimeException(
                    "File type not allowed: " + contentType +
                            ". Allowed: image/jpeg, image/png, video/mp4, application/pdf"
            );
        }
    }

    // create ReportFile and TrackingHistory
    private ReportFileDTOResponse processSingleFile(MultipartFile file,Report report,Member member,Path reportFolder) {

        // generate name unique
        String extesion = getExtension(file.getOriginalFilename());
        String uniqueFileName = UUID.randomUUID() + "." + extesion;
        Path filePath = reportFolder.resolve(uniqueFileName);

        // save to disk
        try{
            Files.copy(file.getInputStream(),filePath,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException E){
            throw new RuntimeException(
                    "Could not save file: " + file.getOriginalFilename()
            );
        }

        // Build URL
        String url = "/uploads/reports/" + report.getId() + "/" + uniqueFileName;

        // save to bd
        ReportFile reportFile = new ReportFile(
                null,
                file.getOriginalFilename(),
                url,
                ALLOWED_TYPES.get(file.getContentType()),
                file.getSize(),
                report
        );

        ReportFile saved = fileRepo.save(reportFile);

        // tracking save
        TrackingHistory tracking = new TrackingHistory(
                null,
                TrackingAction.FILE_UPLOADED,
                "File uploaded : "+ file.getOriginalFilename(),
                report,
                member
        );
        trackingHistoryRepo.save(tracking);

        return fileMapper.toFileDTOResponse(saved);

    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "bin";
        return filename.substring(filename.lastIndexOf(".") + 1);
    }


}
