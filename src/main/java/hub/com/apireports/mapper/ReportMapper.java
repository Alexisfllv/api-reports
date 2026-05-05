package hub.com.apireports.mapper;

import hub.com.apireports.dto.file.ReportFileSummaryDTOResponse;
import hub.com.apireports.dto.report.ReportDTORequest;
import hub.com.apireports.dto.report.ReportDTOResponse;
import hub.com.apireports.dto.report.ReportSummaryDTOResponse;
import hub.com.apireports.entity.Category;
import hub.com.apireports.entity.Report;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReportMapper {

    public Report toReport(ReportDTORequest reportDTORequest, Category category){
        return new Report(
                null,
                reportDTORequest.title(),
                reportDTORequest.description(),
                reportDTORequest.incidentDate(),
                null,
                null,
                reportDTORequest.region(),
                reportDTORequest.province(),
                reportDTORequest.district(),
                reportDTORequest.address(),
                reportDTORequest.reference(),
                reportDTORequest.latitude(),
                reportDTORequest.longitude(),
                reportDTORequest.priorityLevel(),
                reportDTORequest.status(),
                category,
                null,
                null,
                null
        );
    }

    public ReportDTOResponse toReportDTOResponse(Report report){
        return new ReportDTOResponse(
                report.getId(),
                report.getTitle(),
                report.getDescription(),
                report.getIncidentDate(),
                report.getReportDate(),
                report.getCountry(),
                report.getRegion(),
                report.getProvince(),
                ReportMapperDomain.toLabel(report.getProvince()),
                report.getDistrict(),
                ReportMapperDomain.toLabel(report.getDistrict()),
                report.getAddress(),
                report.getReference(),
                report.getLatitude(),
                report.getLongitude(),
                report.getPriorityLevel(),
                report.getStatus(),
                // Category
                report.getCategory().getId(),
                report.getCategory().getName(),
                // Member
                report.getMember().getId(),
                report.getMember().getName()
        );
    }


    // Summary
    public ReportSummaryDTOResponse toReportSummaryDTOResponse(Report report){

        List<ReportFileSummaryDTOResponse> files = report.getFiles().stream()
                .map(f -> new ReportFileSummaryDTOResponse(
                        f.getId(),
                        f.getFileName(),
                        f.getUrl(),
                        f.getFileType(),
                        f.getSizeBytes()
                ))
                .toList();

        return new ReportSummaryDTOResponse(
                report.getId(),
                report.getTitle(),
                report.getDescription(),
                report.getIncidentDate(),
                report.getReportDate(),
                report.getCountry(),
                report.getRegion(),
                report.getProvince(),
                ReportMapperDomain.toLabel(report.getProvince()),
                report.getDistrict(),
                ReportMapperDomain.toLabel(report.getDistrict()),
                report.getAddress(),
                report.getReference(),
                report.getLatitude(),
                report.getLongitude(),
                report.getPriorityLevel(),
                report.getStatus(),
                // Category
                report.getCategory().getId(),
                report.getCategory().getName(),
                // Member
                report.getMember().getId(),
                report.getMember().getName(),
                // files
                files.size(),
                files
        );

    }

}
