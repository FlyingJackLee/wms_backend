package com.lizumin.wms.dao;

import com.lizumin.wms.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import java.util.List;

/**
 * 型号分类操作mapper
 *
 * @author Zumin Li
 * @date 2024/2/10 21:37
 */
@Mapper
public interface CategoryMapper {
    /**
     * 根据id获取Category
     *
     * @param cateId
     * @return
     */
    Category getCategoryById(@Param("cate_id") int cateId, @Param("group_id") int groupId);

    /**
     * 查询大分类下的所有分类
     *
     * @param parentId
     * @return categories
     */
    List<Category> getCategoriesByParentId(@Param("parent_id") int parentId, @Param("group_id") int groupId);

    /**
     * 根据name模糊查询category
     *
     * @param name
     * @return
     */
    List<Category> getCategoryByName(@Param("name") String name, @Param("group_id") int groupId);

    /**
     * 添加新的category
     *
     * @param parentID 父cate id，根为0
     * @param name 分类名称
     * @param ownId  操作人id
     * @param groupId 所在分组id
     * @return int 插入对象的id
     * @throws DuplicateKeyException 同一个parent id下不能有同名name
     *         DataIntegrityViolationException parent id必须有效（大于0）
     */
    int insertCategory(@Param("parent_id") int parentID, @Param("name") String name, @Param("own_id") int ownId, @Param("group_id") int groupId) throws DuplicateKeyException, DataIntegrityViolationException;

    /**
     * 修改父分类
     *
     * @param cateId
     * @param parentID
     * @throws DuplicateKeyException 同一个parent id下不能有同名name
     *         DataIntegrityViolationException parent id必须有效（大于0）
     */
    void updateParentId(@Param("cate_id") int cateId, @Param("parent_id") int parentID, @Param("group_id") int groupId) throws DuplicateKeyException, DataIntegrityViolationException;

    /**
     * 修改分类名
     *
     * @param cateId
     * @param name
     * @throws DuplicateKeyException 同一个parent id下不能有同名name
     */
    void updateName(@Param("cate_id") int cateId, @Param("name") String name, @Param("group_id") int groupId) throws DuplicateKeyException;

    /**
     * 删除category
     *
     * @param cateId
     */
    void deleteCategory(@Param("cate_id") int cateId, @Param("group_id") int groupId);

    /**
     * 通过parent id删除category
     *
     * @param parentId
     * @param groupId
     */
    void deleteCategoryByParentId(@Param("parent_id") int parentId, @Param("group_id") int groupId);
}
