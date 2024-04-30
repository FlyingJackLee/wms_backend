package com.lizumin.wms.service;

import com.lizumin.wms.dao.MerchandiseMapper;
import com.lizumin.wms.entity.Merchandise;
import com.lizumin.wms.tool.Verify;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 *  Merchandise Service -- 需要owner id
 *
 * @author Zumin Li
 * @date 2024/2/14 2:08
 */
@Service
public class MerchandiseService extends AbstractAuthenticationService{
    public static final int DEFAULT_PAGE_OFFSET = 0; // 查询起点

    public static final int DEFAULT_PAGE_LIMIT = 50; // 每次查询数量

    private final MerchandiseMapper merchandiseMapper;

    public MerchandiseService(MerchandiseMapper merchandiseMapper) {
        this.merchandiseMapper = merchandiseMapper;
    }

    /**
     * 获取所有商品
     *
     * @param limit
     * @param offset
     * @return
     */
    @PreAuthorize("hasRole('STAFF')")
    public List<Merchandise> getMerchandiseByPage( boolean sold, int limit, int offset) {
        Assert.isTrue( limit >= 0 && offset >= 0, "invalid id or page options");
        return merchandiseMapper.getAllMerchandise(getUserId(), sold, limit, offset);
    }

    public List<Merchandise> getMerchandiseByPage(int limit, int offset) {
        return this.getMerchandiseByPage(false, limit, offset);
    }

    /**
     * 批量插入商品
     *
     * @param cateId
     * @param cost
     * @param price
     * @param imeiCollection
     * @param createTime
     */
    @PreAuthorize("hasRole('STAFF')")
    @Transactional
    public void insertMerchandise(int cateId, double cost, double price, Collection<String> imeiCollection, Date createTime) {
        for (String imei: imeiCollection) {
            this.insertMerchandise(cateId, cost, price, imei, createTime);
        }
    }

    /**
     * 插入单个商品
     *
     * @param cateId
     * @param cost
     * @param price
     * @param imei
     * @param createTime
     */
    @PreAuthorize("hasRole('STAFF')")
    public void insertMerchandise(int cateId, double cost, double price, String imei, Date createTime) {
        Assert.isTrue( cateId > 0 && cost >= 0 && price >= 0 && createTime != null && Verify.isNotBlank(imei), "invalid parameter");
        merchandiseMapper.insertMerchandise(cateId, cost, price, imei, createTime, getUserId(), getGroupId());
    }

    /**
     * 获取分类下所有商品
     *
     * @param cateId
     * @param sold
     * @return
     */
    @PreAuthorize("hasRole('STAFF')")
    public List<Merchandise> getMerchandiseByCateId(int cateId, boolean sold) {
        Assert.isTrue( cateId > 0, "invalid cate id");
        return merchandiseMapper.getMerchandiseByCateID(cateId, sold, getGroupId());
    }

    public List<Merchandise> getMerchandiseByCateId(int cateId) {
        return getMerchandiseByCateId(cateId, false);
    }

    /**
     * 获取所有未售出商品总数
     *
     */
    @PreAuthorize("hasRole('STAFF')")
    public int getMerchandiseCount() {
        return this.merchandiseMapper.getMerchandiseCount(getGroupId(), false);
    }

    /**
     * 获取所有未售出或售出商品总数
     *
     * @param sold 是否售出
     * @return
     */
    @PreAuthorize("hasRole('STAFF')")
    public int getMerchandiseCount(boolean sold) {
        return this.merchandiseMapper.getMerchandiseCount(getGroupId(), sold);
    }

    /**
     * 通过id查询Merchandise
     *
     * @param id
     * @return
     */
    @PreAuthorize("hasRole('STAFF')")
    public Merchandise getMerchandiseById(int id) {
        Assert.isTrue(id > 0, "illegal id");
        return this.merchandiseMapper.getMerchandiseById(id, getGroupId());
    }

    /**
     * 更新商品信息
     *
     * @param meId
     * @param cost
     * @param price
     * @param imei
     */
    @PreAuthorize("hasRole('STAFF')")
    public void updateMerchandise(int meId, double cost, double price, String imei) {
        Assert.isTrue( meId > 0 && cost >= 0 && price >= 0 && Verify.isNotBlank(imei), "invalid parameter");
        this.merchandiseMapper.updateMerchandise(meId, cost, price, imei, getGroupId());
    }

    /**
     * 删除商品以及其相关记录
     *
     * @param id
     */
    @PreAuthorize("hasRole('STAFF')")
    public void deleteMerchandise(int id){
        Assert.isTrue( id > 0, "invalid cate id");
        this.merchandiseMapper.deleteMerchandise(id, getGroupId());
    }

    /**
     * 按照型号或者串号搜索未售出
     *
     * @param text
     * @return
     */
    @PreAuthorize("hasRole('STAFF')")
    public List<Merchandise> searchMerchandise(String text, boolean sold){
        Assert.isTrue(Verify.isNotBlank(text), "search text cannot be empty");
        return this.merchandiseMapper.searchMerchandise(text, sold, getGroupId());
    }

    public List<Merchandise> searchMerchandise(String text){
        return this.searchMerchandise(text, false);
    }

    /**
     * 设置商品销售情况
     *
     * @param meId
     * @param sold
     */
    public void updateSold(int meId, boolean sold) {
        Assert.isTrue( meId > 0, "invalid cate id");
        this.merchandiseMapper.updateSold(meId, sold, getGroupId());
    }
}
