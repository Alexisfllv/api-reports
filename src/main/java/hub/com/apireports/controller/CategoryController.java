package hub.com.apireports.controller;

import hub.com.apireports.dto.category.CategoryDTORequest;
import hub.com.apireports.dto.category.CategoryDTOResponse;
import hub.com.apireports.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDTOResponse> createCategory(
            @Valid @RequestBody CategoryDTORequest categoryDTORequest){
        CategoryDTOResponse res = categoryService.createCategory(categoryDTORequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTOResponse>> getAllCategories(){
        List<CategoryDTOResponse> res = categoryService.getAllCategories();
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}