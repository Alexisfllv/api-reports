package hub.com.apireports.service.domain;


import hub.com.apireports.entity.Category;
import hub.com.apireports.repo.CategoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryServiceDomain {

    private final CategoryRepo  categoryRepo;

    public Category findById(Long id){
        return categoryRepo.findById(id)
                .orElseThrow(()-> new RuntimeException("Category not found :"+id));
    }
}
