package com.lizumin.wms.dao;

import com.lizumin.wms.entity.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Category Mapper测试
 *
 * @author Zumin Li
 * @date 2024/2/10 21:47
 */
@SpringBootTest
@ActiveProfiles(value = "test")
public class CategoryMapperTest {
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * getCategoryById测试
     */
    @Test
    public void should_get_relevant_category(){
        Category category = categoryMapper.getCategoryById(1, 1);
        assertThat(category.getParentId(), is(0));
        assertThat(category.getName(), equalTo("华为/HUAWEI"));

        category = categoryMapper.getCategoryById(11, 1);
        assertThat(category.getParentId(), is(2));
        assertThat(category.getName(), equalTo("A97"));

        category = categoryMapper.getCategoryById(-1, 1);
        assertThat(category, nullValue());

        category = categoryMapper.getCategoryById(-1, 99);
        assertThat(category, nullValue());
    }

    /**
     * 根分类获取测试getCategoriesByParentId测试
     */
    @Test
    public void should_get_all_root_categories_when_parent_id_is_zero() {
        List<Category> categories = categoryMapper.getCategoriesByParentId(0, 1);
        assertThat(categories.size(), is(10));
    }

    /**
     *  无效parent id下getCategoriesByParentId测试
     */
    @Test
    public void should_get_empty_list_when_parent_id_is_not_exist() {
        List<Category> categories = categoryMapper.getCategoriesByParentId(99, 1);
        assertThat(categories.size(), is(0));
    }

    /**
     * 根据name模糊查询category测试
     */
    @Test
    public void should_get_relevant_categories_when_input_a_valid_category() {
        List<Category> categories = categoryMapper.getCategoryByName("10", 1);
        assertThat(categories.size(), is(2));

        Category category = categoryMapper.getCategoryByName("RENO 11", 1).get(0);
        assertThat(category.getName(), equalTo("RENO 11"));

        List<Category> empty = categoryMapper.getCategoryByName("notexist", 1);
        assertThat(empty,empty());
    }

    /**
     * 重复category和非法parent id 插入时insertCategory测试
     */
    @Test
    public void should_throw_error_when_input_same_category() {
        Assertions.assertThrows(DuplicateKeyException.class,() -> {
             categoryMapper.insertCategory(0, "VIVO", 1, 1);
        });

        Assertions.assertThrows(DuplicateKeyException.class,() -> {
            categoryMapper.insertCategory(3, "X100", 2, 1);
        });

        Assertions.assertThrows(DataIntegrityViolationException.class,() -> {
            categoryMapper.insertCategory(-1, "X100", 1, 1);
        });
    }

    /**
     * 正常插入category测试
     */
    @Test
    public void should_insert_category_when_give_a_valid_category() {
        int i = categoryMapper.insertCategory(0, "test", 1, 1);
        assertThat(i, greaterThan(1));
    }

    /**
     * 更新违反约束测试
     */
    @Test
    public void should_throw_error_when_update_into_same_category() {
        Assertions.assertThrows(DuplicateKeyException.class,() -> {
            categoryMapper.updateName(3, "OPPO", 1);
        });

        categoryMapper.insertCategory(2, "dup_test", 1, 1);
        int id = categoryMapper.insertCategory(3, "dup_test", 1, 1);

        Assertions.assertThrows(DuplicateKeyException.class,() -> {
            categoryMapper.updateParentId(id, 2, 1);
        });
    }

    /**
     * updateParentId正常测试
     */
    @Test
    public void should_update_category_when_category_is_valid() {
        int id = categoryMapper.insertCategory(1, "valid_update_test", 1, 1);

        categoryMapper.updateParentId(id, 2, 1);
        assertThat(categoryMapper.getCategoryById(id, 1).getParentId(), is(2));

        categoryMapper.updateName(id, "valid_update_test2",1);
        assertThat(categoryMapper.getCategoryById(id, 1).getName(), equalToIgnoringCase("valid_update_test2"));
    }

    /**
     * 删除测试
     */
    @Test
    public void should_delete_category() {
        int id = categoryMapper.insertCategory(1, "delete_test", 1, 1);
        categoryMapper.deleteCategory(id, 1);
        assertThat(categoryMapper.getCategoryById(id, 1), nullValue());
    }

    /**
     * 删除大分类测试
     */
    @Test
    public void should_delete_children_category() {
        int id = categoryMapper.insertCategory(1, "children_delete_test", 1, 1);
        categoryMapper.insertCategory(id, "M1", 1, 1);
        categoryMapper.insertCategory(id, "M2", 1, 1);
        categoryMapper.insertCategory(id, "M3", 1, 1);

        categoryMapper.deleteCategoryByParentId(id, 1);
        assertThat(categoryMapper.getCategoriesByParentId(id, 1), empty());
    }
}
