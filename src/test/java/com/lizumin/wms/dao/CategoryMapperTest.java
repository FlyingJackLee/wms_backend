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
        Category category = categoryMapper.getCategoryById(1);
        assertThat(category.getParentId(), is(0));
        assertThat(category.getName(), equalTo("华为/HUAWEI"));

        category = categoryMapper.getCategoryById(11);
        assertThat(category.getParentId(), is(2));
        assertThat(category.getName(), equalTo("A97"));

        category = categoryMapper.getCategoryById(-1);
        assertThat(category, nullValue());
    }

    /**
     * 根分类获取测试getCategoriesByParentId测试
     */
    @Test
    public void should_get_all_root_categories_when_parent_id_is_zero() {
        List<Category> categories = categoryMapper.getCategoriesByParentId(0);
        assertThat(categories.size(), is(10));
        assertThat(categories.get(0).getName(), equalTo("华为/HUAWEI"));
    }

    /**
     *  无效parent id下getCategoriesByParentId测试
     */
    @Test
    public void should_get_empty_list_when_parent_id_is_not_exist() {
        List<Category> categories = categoryMapper.getCategoriesByParentId(99);
        assertThat(categories.size(), is(0));
    }

    /**
     * 根据name模糊查询category测试
     */
    @Test
    public void should_get_relevant_categories_when_input_a_valid_category() {
        List<Category> categories = categoryMapper.getCategoryByName("10");
        assertThat(categories.size(), is(2));

        Category category = categoryMapper.getCategoryByName("RENO 11").get(0);
        assertThat(category.getName(), equalTo("RENO 11"));

        List<Category> empty = categoryMapper.getCategoryByName("notexist");
        assertThat(empty,empty());
    }

    /**
     * 重复category和非法parent id 插入时insertCategory测试
     */
    @Test
    public void should_throw_error_when_input_same_category() {
        Assertions.assertThrows(DuplicateKeyException.class,() -> {
             categoryMapper.insertCategory(0, "VIVO");
        });

        Assertions.assertThrows(DuplicateKeyException.class,() -> {
            categoryMapper.insertCategory(3, "X100");
        });

        Assertions.assertThrows(DataIntegrityViolationException.class,() -> {
            categoryMapper.insertCategory(-1, "X100");
        });
    }

    /**
     * 正常插入category测试
     */
    @Test
    public void should_insert_category_when_give_a_valid_category() {
        int i = categoryMapper.insertCategory(0, "test");
        assertThat(i, greaterThan(1));
    }

    /**
     * 更新违反约束测试
     */
    @Test
    public void should_throw_error_when_update_into_same_category() {
        Assertions.assertThrows(DuplicateKeyException.class,() -> {
            categoryMapper.updateName(3, "OPPO");
        });

        categoryMapper.insertCategory(2, "dup_test");
        int id = categoryMapper.insertCategory(3, "dup_test");

        Assertions.assertThrows(DuplicateKeyException.class,() -> {
            categoryMapper.updateParentId(id, 2);
        });
    }

    /**
     * updateParentId正常测试
     */
    @Test
    public void should_update_category_when_category_is_valid() {
        int id = categoryMapper.insertCategory(1, "valid_update_test");

        categoryMapper.updateParentId(id, 2);
        assertThat(categoryMapper.getCategoryById(id).getParentId(), is(2));

        categoryMapper.updateName(id, "valid_update_test2");
        assertThat(categoryMapper.getCategoryById(id).getName(), equalToIgnoringCase("valid_update_test2"));
    }

    /**
     * 删除测试
     */
    @Test
    public void should_delete_category() {
        int id = categoryMapper.insertCategory(1, "delete_test");
        categoryMapper.deleteCategory(id);
        assertThat(categoryMapper.getCategoryById(id), nullValue());
    }
}
