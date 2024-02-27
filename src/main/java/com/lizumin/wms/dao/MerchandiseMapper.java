package com.lizumin.wms.dao;

import com.lizumin.wms.entity.Merchandise;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Merchandise Mapper
 * 为保证安全性，此处请求均需要携带owner id
 *
 * @author Zumin Li
 * @date 2024/2/14 0:07
 */
@Mapper
public interface MerchandiseMapper {
    /**
     * 查询拥有的总量，便于分页查询
     *
     * @param ownerId 拥有id
     * @param sold 查询的销售状态
     * @return
     */
    int getMerchandiseCount(@Param("owner_id") int ownerId, @Param("sold") boolean sold);

    /**
     * 根据商品id和用户id寻找商品
     *
     * @param id
     * @param ownerId
     * @return
     */
    Merchandise getMerchandiseById(@Param("me_id") int id, @Param("owner_id") int ownerId);

    /**
     * 根据用户id和售出情况, 分页寻找所有商品
     *
     * @param ownerId 用户id，一般由jwt决定
     * @param sold 是否售出
     * @param offset 查询起点，e.g. 5意味着第6行开始
     * @param limit  查询长度, e.g. 10 意味着 6,7 ... 15
     * @return
     */
    List<Merchandise> getAllMerchandise(@Param("owner_id") int ownerId,
                                        @Param("sold") boolean sold,
                                        @Param("limit") int limit,
                                        @Param("offset") int offset);

    /**
     * 根据imei查询商品
     *
     * @param imei
     * @param ownerId
     * @return
     */
    Merchandise getMerchandiseByImei(@Param("imei") String imei, @Param("owner_id") int ownerId);

    /**
     * 根据分类id查询商品
     *
     * @param cate_id
     * @param ownerId
     * @return
     */
    List<Merchandise> getMerchandiseByCateID(@Param("cate_id") int cate_id,
                                             @Param("sold") boolean sold,
                                             @Param("owner_id") int ownerId);

    /**
     * 插入商品
     *
     * @param cateId 商品分类的id
     * @param cost 商品进价
     * @param imei 商品imei
     * @param createDate 商品创建日期
     * @param ownerId 商品对象
     * @return
     */
    int insertMerchandise(@Param("cate_id") int cateId, @Param("cost") double cost, @Param("price") double price,
                          @Param("imei") String imei, @Param("create_time") Date createDate, @Param("owner_id") int ownerId);

    /**
     * 更新销售状况
     *
     * @param meId
     * @param sold
     * @param ownerId
     */
    void updateSold(@Param("me_id") int meId, @Param("sold") boolean sold,  @Param("owner_id") int ownerId);
}
