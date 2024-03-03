package com.lizumin.wms.service;

import com.lizumin.wms.dao.MerchandiseMapper;
import com.lizumin.wms.entity.Merchandise;
import com.lizumin.wms.tool.Verify;
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
     * @param authentication
     * @param limit
     * @param offset
     * @return
     */
    public List<Merchandise> getMerchandiseByPage(Authentication authentication, boolean sold, int limit, int offset) {
        Assert.isTrue( limit >= 0 && offset >= 0, "invalid id or page options");
        return merchandiseMapper.getAllMerchandise(getOwnerId(authentication), sold, limit, offset);
    }

    public List<Merchandise> getMerchandiseByPage(Authentication authentication, int limit, int offset) {
        return this.getMerchandiseByPage(authentication, false, limit, offset);
    }

    /**
     * 批量插入商品
     *
     * @param authentication
     * @param cateId
     * @param cost
     * @param price
     * @param imeiCollection
     * @param createTime
     */
    @Transactional
    public void insertMerchandise(Authentication authentication, int cateId, double cost, double price, Collection<String> imeiCollection, Date createTime) {
        for (String imei: imeiCollection) {
            this.insertMerchandise(authentication, cateId, cost, price, imei, createTime);
        }
    }

    /**
     * 插入单个商品
     *
     * @param authentication
     * @param cateId
     * @param cost
     * @param price
     * @param imei
     * @param createTime
     */
    public void insertMerchandise(Authentication authentication, int cateId, double cost, double price, String imei, Date createTime) {
        Assert.isTrue( cateId > 0 && cost >= 0 && price >= 0 && createTime != null && Verify.isNotBlank(imei), "invalid parameter");
        merchandiseMapper.insertMerchandise(cateId, cost, price, imei, createTime, getOwnerId(authentication));
    }

    /**
     * 获取分类下所有商品
     *
     * @param authentication
     * @param cateId
     * @param sold
     * @return
     */
    public List<Merchandise> getMerchandiseByCateId(Authentication authentication, int cateId, boolean sold) {
        Assert.isTrue( cateId > 0, "invalid cate id");
        return merchandiseMapper.getMerchandiseByCateID(cateId, sold, getOwnerId(authentication));
    }

    public List<Merchandise> getMerchandiseByCateId(Authentication authentication, int cateId) {
        return getMerchandiseByCateId(authentication, cateId, false);
    }

    /**
     * 获取所有未售出商品总数
     *
     * @param authentication
     * @return
     */
    public int getMerchandiseCount(Authentication authentication) {
        return this.merchandiseMapper.getMerchandiseCount(getOwnerId(authentication), false);
    }

    /**
     * 获取所有未售出或售出商品总数
     *
     * @param authentication
     * @param sold 是否售出
     * @return
     */
    public int getMerchandiseCount(Authentication authentication, boolean sold) {
        return this.merchandiseMapper.getMerchandiseCount(getOwnerId(authentication), sold);
    }

    /**
     * 通过id查询Merchandise
     *
     * @param authentication
     * @param id
     * @return
     */
    public Merchandise getMerchandiseById(Authentication authentication, int id) {
        Assert.isTrue(id > 0, "illegal id");
        return this.merchandiseMapper.getMerchandiseById(id, getOwnerId(authentication));
    }

    /**
     * 更新商品信息
     *
     * @param authentication
     * @param meId
     * @param cost
     * @param price
     * @param imei
     */
    public void updateMerchandise(Authentication authentication, int meId, double cost, double price, String imei) {
        Assert.isTrue( meId > 0 && cost >= 0 && price >= 0 && Verify.isNotBlank(imei), "invalid parameter");
        this.merchandiseMapper.updateMerchandise(meId, cost, price, imei, getOwnerId(authentication));
    }

    /**
     * 删除商品以及其相关记录
     *
     * @param authentication
     * @param id
     */
    public void deleteMerchandise(Authentication authentication, int id){
        Assert.isTrue( id > 0, "invalid cate id");
        this.merchandiseMapper.deleteMerchandise(id, getOwnerId(authentication));
    }

    /**
     * 按照型号或者串号搜索未售出
     *
     * @param authentication
     * @param text
     * @return
     */
    public List<Merchandise> searchMerchandise(Authentication authentication, String text, boolean sold){
        Assert.isTrue(Verify.isNotBlank(text), "search text cannot be empty");
        return this.merchandiseMapper.searchMerchandise(text, sold , getOwnerId(authentication));
    }

    public List<Merchandise> searchMerchandise(Authentication authentication, String text){
        return this.searchMerchandise(authentication, text, false);
    }

    /**
     * 设置商品销售情况
     *
     * @param authentication
     * @param meId
     * @param sold
     */
    public void updateSold(Authentication authentication, int meId, boolean sold) {
        Assert.isTrue( meId > 0, "invalid cate id");
        this.merchandiseMapper.updateSold(meId, sold, getOwnerId(authentication));
    }
}
