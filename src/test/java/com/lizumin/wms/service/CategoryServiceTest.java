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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.mockito.Mockito.*;

/**
 * CategoryService Test
 * @author Zumin Li
 * @date 2024/2/13 23:42
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles(value = "test")
public class CategoryServiceTest {
    @MockBean
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryService categoryService;

    /**
     * 根分类获取getRootCategories角色测试 - DEFAULT测试
     */
    @Test
    @WithMockUserPrincipal
    public void should_access_denied_when_get_root_with_bad_user_role() {
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            categoryService.getRootCategories();
        });
    }

    /**
     * 根分类获取getRootCategories测试 - STAFF角色测试
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_STAFF", groupId = 1)
    public void should_use_zero_when_find_root_categories() {
        categoryService.getRootCategories();
        verify(categoryMapper, times(1)).getCategoriesByParentId(0, 1);
    }

    /**
     * 根分类获取getRootCategories测试 - OWNER测试
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_OWNER", groupId = 1)
    public void should_use_zero_when_find_root_categories_with_owner() {
        categoryService.getRootCategories();
        verify(categoryMapper, times(1)).getCategoriesByParentId(0, 1);
    }

    /**
     * getCategory测试 - DEFAULT测试
     */
    @Test
    @WithMockUserPrincipal
    public void should_access_denied_when_get_category_with_bad_user_role() {
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            categoryService.getCategory(2);
        });
    }

    /**
     * getCategory测试 STAFF角色测试
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_STAFF", groupId = 2)
    public void should_relevant_result_when_get_category() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->{
            categoryService.getCategory(-1);
        });

        categoryService.getCategory( 3);
        verify(categoryMapper, times(1)).getCategoryById(3, 2);
    }

    /**
     * getCategory测试 OWNER测试
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_STAFF", groupId = 2)
    public void should_relevant_result_when_get_category_with_owner() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->{
            categoryService.getCategory(-1);
        });

        categoryService.getCategory( 3);
        verify(categoryMapper, times(1)).getCategoryById(3, 2);
    }

    /**
     * getCategoriesByParentId测试  - DEFAULT测试
     */
    @Test
    @WithMockUserPrincipal
    public void should_access_denied_when_get_category_by_parent_with_bad_user_role() {
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            categoryService.getCategoriesByParentId(2);
        });
    }

    /**
     * getCategoriesByParentId测试 -  STAFF角色测试
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_STAFF", groupId = 2)
    public void should_throw_runtime_exception_when_get_category_by_parent_with_staff() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->{
            categoryService.getCategoriesByParentId(-1);
        });

        categoryService.getCategoriesByParentId(5);
        verify(categoryMapper, times(1)).getCategoriesByParentId(5, 2);
    }

    /**
     * getCategoriesByParentId测试 -  OWNER测试
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_OWNER", groupId = 2)
    public void should_throw_runtime_exception_when_get_category_by_parent_with_owner() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->{
            categoryService.getCategoriesByParentId(-1);
        });

        categoryService.getCategoriesByParentId(5);
        verify(categoryMapper, times(1)).getCategoriesByParentId(5, 2);
    }

    /**
     * insertCategory测试  - DEFAULT测试
     */
    @Test
    @WithMockUserPrincipal
    public void should_access_denied_when_insert_category_by_parent_with_bad_user_role() {
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            categoryService.insertCategory(-1, "test");
        });
    }

    /**
     * insertCategory测试  - STAFF
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_STAFF", groupId = 2)
    public void should_throw_exception_or_insert_when_insert_with_staff() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->{
            categoryService.insertCategory(-1, "test");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () ->{
            categoryService.insertCategory(2, "");
        });

        categoryService.insertCategory(5, "test");
        verify(categoryMapper, times(1)).insertCategory(5, "test", 1, 2);
    }

    /**
     * insertCategory测试  - STAFF
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_STAFF", groupId = 2)
    public void should_throw_exception_or_insert_when_insert_with_owner() {
        categoryService.insertCategory(5, "test");
        verify(categoryMapper, times(1)).insertCategory(5, "test", 1, 2);
    }

    /**
     * deleteCategory测试  - DEFAULT测试
     */
    @Test
    @WithMockUserPrincipal
    public void should_access_denied_when_delete_category_by_parent_with_bad_user_role() {
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            categoryService.deleteCategory(2);
        });
    }

    /**
     * deleteCategory测试  - STAFF
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_STAFF", groupId = 2)
    public void should_access_denied_when_delete_category_by_parent_with_staff() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->{
            categoryService.deleteCategory(0);
        });

        categoryService.deleteCategory(5);
        verify(categoryMapper, times(1)).deleteCategoryByParentId(5, 2);
        verify(categoryMapper, times(1)).deleteCategory(5, 2);
    }

    /**
     * deleteCategory测试  - STAFF
     */
    @Test
    @WithMockUserPrincipal(role = "ROLE_OWNER", groupId = 2)
    public void should_access_denied_when_delete_category_by_parent_with_owner() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->{
            categoryService.deleteCategory(0);
        });

        categoryService.deleteCategory(5);
        verify(categoryMapper, times(1)).deleteCategoryByParentId(5, 2);
        verify(categoryMapper, times(1)).deleteCategory(5, 2);
    }
}
