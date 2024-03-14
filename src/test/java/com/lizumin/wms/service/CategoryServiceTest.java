package com.lizumin.wms.service;

import com.lizumin.wms.dao.CategoryMapper;
import com.lizumin.wms.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.mockito.Mockito.*;

/**
 * CategoryService Test
 * @author Zumin Li
 * @date 2024/2/13 23:42
 */
@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private CategoryMapper categoryMapper;

    private Authentication authentication;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    public void setUp() {
        User user = new User.Builder().username("test").password("123456").id(1).build();
        this.authentication = UsernamePasswordAuthenticationToken.authenticated(user, "test", List.of());
    }

    /**
     * 根分类获取测试
     */
    @Test
    public void should_use_zero_when_find_root_categories() {
        categoryService.getRootCategories(this.authentication);
        verify(categoryMapper, times(1)).getCategoriesByParentId(0, 1);
    }

    /**
     * getCategory测试
     */
    @Test
    public void should_relevant_result_when_get_category() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->{
            categoryService.getCategory(this.authentication, -1);
        });

        categoryService.getCategory(this.authentication, 3);
        verify(categoryMapper, times(1)).getCategoryById(3, 1);
    }

    /**
     * getCategoriesByParentId异常测试
     */
    @Test
    public void should_throw_runtime_exception_when_parent_id_illegal() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->{
            categoryService.getCategoriesByParentId(this.authentication, -1);
        });
    }

    /**
     * getCategoriesByParentId正常测试
     */
    @Test
    public void should_call_mapper_when_get_categories() {
        categoryService.getCategoriesByParentId(this.authentication, 5);
        verify(categoryMapper, times(1)).getCategoriesByParentId(5, 1);
    }

    /**
     * 参数错误insertCategory测试
     */
    @Test
    public void should_throw_exception_when_parent_or_name_illegal_when_insert() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->{
            categoryService.insertCategory(this.authentication, -1, "test");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () ->{
            categoryService.insertCategory(this.authentication, 2, "");
        });
    }

    /**
     * insertCategory正常测试
     */
    @Test
    public void should_call_mapper_when_insert() {
        categoryService.insertCategory(this.authentication,5, "test");
        verify(categoryMapper, times(1)).insertCategory(5, "test", 1);
    }

    /**
     * deleteCategory正常测试
     */
    @Test
    public void should_call_mapper_when_delete() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->{
            categoryService.deleteCategory(this.authentication, 0);
        });

        categoryService.deleteCategory(this.authentication,5);
        verify(categoryMapper, times(1)).deleteCategoryByParentId(5, 1);
        verify(categoryMapper, times(1)).deleteCategory(5, 1);
    }
}
