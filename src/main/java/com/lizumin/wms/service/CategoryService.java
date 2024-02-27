package com.lizumin.wms.service;

import com.lizumin.wms.dao.CategoryMapper;
import com.lizumin.wms.entity.Category;
import com.lizumin.wms.tool.Verify;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Category Service
 *
 * @author Zumin Li
 * @date 2024/2/13 15:07
 */
@Service
public class CategoryService {
    private CategoryMapper categoryMapper;

    public CategoryService(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    /**
     * 获取所有根分类
     *
     * @return
     */
    public List<Category> getRootCategories() {
        return this.categoryMapper.getCategoriesByParentId(0);
    }

    public Category getCategory(int id){
        Assert.isTrue(id > 0, "id should be larger than 0");
        return this.categoryMapper.getCategoryById(id);
    }

    /**
     * 获取分类下所有分类
     *
     * @param parentId
     * @return
     */
    public List<Category> getCategoriesByParentId(int parentId) {
        Assert.isTrue(parentId >= 0, "parent_id should be larger than or equal 0");
        return this.categoryMapper.getCategoriesByParentId(parentId);
    }

    /**
     * 插入新category
     *
     * @param parentID
     * @param name
     * @return
     */
    public int insertCategory(int parentID, String name) {
        Assert.isTrue(parentID > 0, "parent_id should be larger than 0");
        Assert.isTrue(Verify.isNotBlank(name), "category must have a name");
        return this.categoryMapper.insertCategory(parentID, name);
    }
}
