package hub.com.apireports.dto.file;

import hub.com.apireports.entity.enums.FileType;

import java.time.LocalDateTime;

public record ReportFileDTOResponse(
        Long id,
        String fileName,
        String url,
        FileType type,
        Long sizeBytes,
        Long reportId
) {}