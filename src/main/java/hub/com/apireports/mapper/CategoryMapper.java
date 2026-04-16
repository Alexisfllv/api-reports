package hub.com.apireports.mapper;

import hub.com.apireports.dto.category.CategoryDTORequest;
import hub.com.apireports.dto.category.CategoryDTOResponse;
import hub.com.apireports.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toCategory(CategoryDTORequest categoryDTORequest){
        return new Category(
                null,
                categoryDTORequest.name(),
                categoryDTORequest.description(),
                null
        );
    }

    public CategoryDTOResponse  toCategoryDTOResponse(Category category){
        return new CategoryDTOResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getActive()
        );
    }
}
