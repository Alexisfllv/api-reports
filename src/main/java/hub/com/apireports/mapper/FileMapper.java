package hub.com.apireports.mapper;

import hub.com.apireports.dto.file.ReportFileDTOResponse;
import hub.com.apireports.entity.Report;
import hub.com.apireports.entity.ReportFile;
import org.springframework.stereotype.Component;

@Component
public class FileMapper {


    public ReportFileDTOResponse toFileDTOResponse(ReportFile file){
        return new ReportFileDTOResponse(
                file.getId(),
                file.getFileName(),
                file.getUrl(),
                file.getFileType(),
                file.getSizeBytes(),
                file.getReport().getId()
        );
    }
}
