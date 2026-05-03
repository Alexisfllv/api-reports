package hub.com.apireports.controller;

import hub.com.apireports.dto.file.ReportFileDTOResponse;
import hub.com.apireports.entity.security.Member;
import hub.com.apireports.service.ReportFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/report-file")
public class ReportFileController {

    private final ReportFileService reportFileService;

    @PostMapping(value = "/{reportId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<ReportFileDTOResponse>> uploadFiles(
            @PathVariable Long reportId,
            @RequestParam("files")List<MultipartFile> files,
            @AuthenticationPrincipal Member member){
        List<ReportFileDTOResponse> responses = reportFileService.uploadFiles(reportId, member.getId(), files);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }
}
