package hub.com.apireports.dto.file;

import hub.com.apireports.entity.enums.FileType;

public record ReportFileSummaryDTOResponse(
        Long id,
        String fileName,
        String url,
        FileType fileType,
        Long sizeBytes
) {}
