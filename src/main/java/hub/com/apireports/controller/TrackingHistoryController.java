package hub.com.apireports.controller;

import hub.com.apireports.dto.tracking.TrackingHistoryDTOResponse;
import hub.com.apireports.entity.security.Member;
import hub.com.apireports.service.TrackingHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tracking-history")
public class TrackingHistoryController {

    private final TrackingHistoryService  trackingHistoryService;

    @GetMapping("/admin/report/{reportId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<TrackingHistoryDTOResponse>> getTrackingHistoryByReportId(
            @PathVariable Long reportId,
            @AuthenticationPrincipal Member member
            ){
        List<TrackingHistoryDTOResponse> trackingHistoryDTOResponses = trackingHistoryService.getTrackingHistoryByReportId(reportId);
        return ResponseEntity.status(HttpStatus.OK).body(trackingHistoryDTOResponses);
    }
}
