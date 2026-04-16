package hub.com.apireports.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryDTORequest(
        @Schema(description = "name of accion", example = "Vandalismo", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{field.required}")
        @Size(min = 2, max = 50, message = "{field.size.range}")
        String name,

        @Schema(description = "description of the actions", example = "Actividad sospechosa", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{field.required}")
        @Size(min = 2, max = 500, message = "{field.size.range}")
        String description
) {}
