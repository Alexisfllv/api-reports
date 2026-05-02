package hub.com.apireports.controller;


import hub.com.apireports.dto.report.ReportDTORequest;
import hub.com.apireports.dto.report.ReportDTOResponse;
import hub.com.apireports.entity.security.Member;
import hub.com.apireports.service.ReportService;
import hub.com.apireports.service.security.MemberDetailServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/reports")
public class ReportController {
    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ReportDTOResponse> createReport(
            @Valid @RequestBody ReportDTORequest reportDTORequest,
            @AuthenticationPrincipal Member member){
        ReportDTOResponse reportDTOResponse = reportService.createReport(reportDTORequest, member.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(reportDTOResponse);
    }

    @GetMapping
    public ResponseEntity<List<ReportDTOResponse>> getAllReports(){
        List<ReportDTOResponse> reports = reportService.getAllReports();
        return ResponseEntity.status(HttpStatus.OK).body(reports);
    }
}
