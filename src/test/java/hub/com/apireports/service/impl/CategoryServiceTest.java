package hub.com.apireports.service.impl;


import hub.com.apireports.dto.category.CategoryDTORequest;
import hub.com.apireports.dto.category.CategoryDTOResponse;
import hub.com.apireports.entity.Category;
import hub.com.apireports.mapper.CategoryMapper;
import hub.com.apireports.repo.CategoryRepo;
import hub.com.apireports.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepo categoryRepo;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    CategoryDTORequest categoryDTORequest;
    Category category;
    CategoryDTOResponse categoryDTOResponse;


    @BeforeEach
    public void setUp() {
        categoryDTORequest = new CategoryDTORequest("Vandalismo", "Propiedad privada");
        category = new Category(1L, "Vandalismo", "Propiedad privada",true);
        categoryDTOResponse = new CategoryDTOResponse(1L, "Vandalismo", "Propiedad privada",true);
    }

    @Test
    public void createCategory() {
        // Arrange
        when(categoryMapper.toCategory(categoryDTORequest)).thenReturn(category);
        when(categoryRepo.save(category)).thenReturn(category);
        when(categoryMapper.toCategoryDTOResponse(category)).thenReturn(categoryDTOResponse);
        // Act
        CategoryDTOResponse result = categoryService.createCategory(categoryDTORequest);
        // Assert
        assertAll(
                () -> assertEquals(categoryDTOResponse.id(), result.id()),
                () -> assertEquals(categoryDTOResponse.name(), result.name()),
                () -> assertEquals(categoryDTOResponse.description(), result.description()),
                () -> assertEquals(categoryDTOResponse.active(), result.active())
        );
        // InOrder & Verify
        InOrder inOrder = Mockito.inOrder(categoryRepo, categoryMapper);
        inOrder.verify(categoryMapper).toCategory(categoryDTORequest);
        inOrder.verify(categoryRepo).save(category);
        inOrder.verify(categoryMapper).toCategoryDTOResponse(category);
        inOrder.verifyNoMoreInteractions();
    }
}
