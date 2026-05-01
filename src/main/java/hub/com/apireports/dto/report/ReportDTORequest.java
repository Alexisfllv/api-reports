package hub.com.apireports.dto.report;

import hub.com.apireports.entity.enums.PriorityLevel;
import hub.com.apireports.entity.enums.RegionType;
import hub.com.apireports.entity.enums.ReportStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReportDTORequest(

        @NotBlank(message = "{field.required}")
        @Size(min = 2, max = 150, message = "{field.size.range}")
        String title,

        @NotBlank(message = "{field.required}")
        @Size(min = 2, max = 1000, message = "{field.size.range}")
        String description,

        @NotNull(message = "{field.required}")
        @PastOrPresent(message = "{field.date.past.or.present}")
        LocalDateTime incidentDate,

        @NotNull(message = "{field.required}")
        RegionType region,

        @NotBlank(message = "{field.required}")
        @Size(min = 2, max = 30, message = "{field.size.range}")
        String province,

        @NotBlank(message = "{field.required}")
        @Size(min = 2, max = 30, message = "{field.size.range}")
        String district,

        @NotBlank(message = "{field.required}")
        @Size(min = 2, max = 300, message = "{field.size.range}")
        String address,

        @NotBlank(message = "{field.required}")
        @Size(min = 2, max = 300, message = "{field.size.range}")
        String reference,

        @NotNull(message = "{field.required}")
        BigDecimal latitude,

        @NotNull(message = "{field.required}")
        BigDecimal longitude,

        @NotNull(message = "{field.required}")
        PriorityLevel priorityLevel,

        @NotNull(message = "{field.required}")
        ReportStatus status,

        @NotNull(message = "{field.required}")
        @Positive(message = "{field.must.be.positive}")
        Long categoryId

) {}
