package hub.com.apireports.dto.report;

import hub.com.apireports.entity.enums.ReportStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReportDTORequestToggleStatus(

        @NotNull(message = "{field.required}")
        ReportStatus newStatus,

        @Size(max = 500)
        String comment
) {}
