package hub.com.apireports.service.domain;


import hub.com.apireports.entity.Category;
import hub.com.apireports.repo.CategoryRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceDomainTest {

    @Mock
    private CategoryRepo categoryRepo;

    @InjectMocks
    private CategoryServiceDomain categoryService;

    Category category;

    @BeforeEach
    public void setUp(){
        category = new Category(1L,"Vandalismo","Disturbios",true);
    }

    @Nested
    class FindById{
        @Test
        public void findByIdSuccess(){
            // Arrange
            Long idExist = 1L;
            when(categoryRepo.findById(idExist)).thenReturn(Optional.of(category));

            // Act
            Category result = categoryService.findById(idExist);
            // Assert
            assertAll(
                () -> assertEquals(category.getId(), result.getId()),
                () -> assertEquals(category.getName(), result.getName()),
                () -> assertEquals(category.getDescription(), result.getDescription())
            );

            // InOrder & Verify
            InOrder inOrder = Mockito.inOrder(categoryRepo);
            inOrder.verify(categoryRepo).findById(idExist);
            inOrder.verifyNoMoreInteractions();
        }

        @Test
        public void findByIdFail(){
            // Arrange
            Long idNotExist = 99L;
            when(categoryRepo.findById(idNotExist)).thenReturn(Optional.empty());

            // Act
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> categoryService.findById(idNotExist));

            // Assert
            assertAll(
                    () -> assertNotNull(ex.getMessage()),
                    () -> assertEquals("Category not found :"+idNotExist, ex.getMessage())
            );
            InOrder inOrder = Mockito.inOrder(categoryRepo);
            inOrder.verify(categoryRepo).findById(idNotExist);
            inOrder.verifyNoMoreInteractions();
        }
    }
}
