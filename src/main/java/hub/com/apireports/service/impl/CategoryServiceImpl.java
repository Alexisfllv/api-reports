package hub.com.apireports.service.impl;


import hub.com.apireports.dto.category.CategoryDTORequest;
import hub.com.apireports.dto.category.CategoryDTOResponse;
import hub.com.apireports.entity.Category;
import hub.com.apireports.mapper.CategoryMapper;
import hub.com.apireports.repo.CategoryRepo;
import hub.com.apireports.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final CategoryRepo categoryRepo;

    @Override
    public CategoryDTOResponse createCategory(CategoryDTORequest categoryDTORequest) {
        Category category = categoryMapper.toCategory(categoryDTORequest);
        category.setActive(true);
        Category categorySaved = categoryRepo.save(category);
        return categoryMapper.toCategoryDTOResponse(categorySaved);
    }
}
