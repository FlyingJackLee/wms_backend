package com.lizumin.wms.service;

import com.lizumin.wms.dao.CategoryMapper;
import com.lizumin.wms.entity.Category;
import com.lizumin.wms.tool.Verify;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Category Service
 *
 * @author Zumin Li
 * @date 2024/2/13 15:07
 */
@Service
public class CategoryService extends AbstractAuthenticationService {
    private CategoryMapper categoryMapper;

    public CategoryService(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    /**
     * 获取所有根分类 -- 至少是员工才能获取
     *
     */
    @PreAuthorize("hasRole('STAFF')")
    public List<Category> getRootCategories() {
        return this.categoryMapper.getCategoriesByParentId(0, getGroupId());
    }

    /**
     * 通过id获取cate
     *
     * @param id
     * @return
     */
    @PreAuthorize("hasRole('STAFF')")
    public Category getCategory(int id){
        Assert.isTrue(id > 0, "id should be larger than 0");
        return this.categoryMapper.getCategoryById(id, getGroupId());
    }

    /**
     * 获取分类下所有分类
     *
     * @param parentId
     * @return
     */
    @PreAuthorize("hasRole('STAFF')")
    public List<Category> getCategoriesByParentId(int parentId) {
        Assert.isTrue(parentId >= 0, "parent_id should be larger than or equal 0");
        return this.categoryMapper.getCategoriesByParentId(parentId, getGroupId());
    }

    /**
     * 插入新category
     *
     * @param parentID
     * @param name
     * @return
     */
    @PreAuthorize("hasRole('STAFF')")
    public int insertCategory(int parentID, String name) {
        Assert.isTrue(parentID >= 0, "parent_id should be larger than 0");
        Assert.isTrue(Verify.isNotBlank(name), "category must have a name");
        return this.categoryMapper.insertCategory(parentID, name, getUserId(), getGroupId());
    }

    /**
     * 删除cate
     *
     * @param id
     */
    @Transactional
    public void deleteCategory(int id){
        Assert.isTrue(id >= 1, "id should be larger than 0");
        // 先删除子类 在删除父类
        this.categoryMapper.deleteCategoryByParentId(id, getGroupId());
        this.categoryMapper.deleteCategory(id, getGroupId());
    }
}
