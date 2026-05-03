package hub.com.apireports.service;

import hub.com.apireports.dto.file.ReportFileDTOResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReportFileService {
    List<ReportFileDTOResponse> uploadFiles(Long reportId,Long memberId, List<MultipartFile> files);
}
