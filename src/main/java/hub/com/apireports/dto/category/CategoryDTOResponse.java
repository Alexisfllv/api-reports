package hub.com.apireports.dto.category;

public record CategoryDTOResponse(
        Long id,
        String name,
        String description,
        Boolean active
) {}
