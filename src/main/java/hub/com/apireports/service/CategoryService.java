package hub.com.apireports.service;

import hub.com.apireports.dto.category.CategoryDTORequest;
import hub.com.apireports.dto.category.CategoryDTOResponse;

public interface CategoryService {

    // POST
    CategoryDTOResponse createCategory(CategoryDTORequest categoryDTORequest);
}
