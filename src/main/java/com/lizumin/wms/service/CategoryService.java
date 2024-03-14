package com.lizumin.wms.service;

import com.lizumin.wms.dao.CategoryMapper;
import com.lizumin.wms.entity.Category;
import com.lizumin.wms.tool.Verify;
import org.springframework.security.core.Authentication;
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
     * 获取所有根分类
     *
     * @return
     */
    public List<Category> getRootCategories(Authentication authentication) {
        return this.categoryMapper.getCategoriesByParentId(0, getUserId(authentication));
    }

    /**
     * 通过id获取cate
     *
     * @param authentication
     * @param id
     * @return
     */
    public Category getCategory(Authentication authentication, int id){
        Assert.isTrue(id > 0, "id should be larger than 0");
        return this.categoryMapper.getCategoryById(id, getUserId(authentication));
    }

    /**
     * 获取分类下所有分类
     *
     * @param parentId
     * @return
     */
    public List<Category> getCategoriesByParentId(Authentication authentication, int parentId) {
        Assert.isTrue(parentId >= 0, "parent_id should be larger than or equal 0");
        return this.categoryMapper.getCategoriesByParentId(parentId, getUserId(authentication));
    }

    /**
     * 插入新category
     *
     * @param parentID
     * @param name
     * @return
     */
    public int insertCategory(Authentication authentication, int parentID, String name) {
        Assert.isTrue(parentID >= 0, "parent_id should be larger than 0");
        Assert.isTrue(Verify.isNotBlank(name), "category must have a name");
        return this.categoryMapper.insertCategory(parentID, name, getUserId(authentication));
    }

    /**
     * 删除cate
     *
     * @param authentication
     * @param id
     */
    @Transactional
    public void deleteCategory(Authentication authentication, int id){
        Assert.isTrue(id >= 1, "id should be larger than 0");
        // 先删除子类 在删除父类
        this.categoryMapper.deleteCategoryByParentId(id, getUserId(authentication));
        this.categoryMapper.deleteCategory(id, getUserId(authentication));
    }
}
