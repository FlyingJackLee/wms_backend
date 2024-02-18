package com.lizumin.wms.service;

import com.lizumin.wms.dao.CategoryMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @InjectMocks
    private CategoryService categoryService;

    /**
     * 根分类获取测试
     */
    @Test
    public void should_use_zero_when_find_root_categories() {
        categoryService.getRootCategories();
        verify(categoryMapper, times(1)).getCategoriesByParentId(0);
    }

    /**
     * getCategoriesByParentId异常测试
     */
    @Test
    public void should_throw_runtime_exception_when_parent_id_illegal() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->{
            categoryService.getCategoriesByParentId(0);
        });
    }

    /**
     * getCategoriesByParentId正常测试
     */
    @Test
    public void should_call_mapper_when_get_categories() {
        categoryService.getCategoriesByParentId(5);
        verify(categoryMapper, times(1)).getCategoriesByParentId(5);
    }

    /**
     * 参数错误insertCategory测试
     */
    @Test
    public void should_throw_exception_when_parent_or_name_illegal_when_insert() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->{
            categoryService.insertCategory(-0, "test");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () ->{
            categoryService.insertCategory(2, "");
        });
    }

    /**
     * insertCategory正常测试
     */
    @Test
    public void should_call_mapper_when_insert() {
        categoryService.insertCategory(5, "test");
        verify(categoryMapper, times(1)).insertCategory(5, "test");
    }
}
