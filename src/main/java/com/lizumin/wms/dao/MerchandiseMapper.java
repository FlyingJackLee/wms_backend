package com.lizumin.wms.dao;

import com.lizumin.wms.entity.MeCount;
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
     * @param groupId 拥有组id
     * @param sold 查询的销售状态
     * @return
     */
    int getMerchandiseCount(@Param("group_id") int groupId, @Param("sold") boolean sold);

    /**
     * 根据商品id和用户id寻找商品
     *
     * @param id
     * @param groupId 拥有组id
     * @return
     */
    Merchandise getMerchandiseById(@Param("me_id") int id, @Param("group_id") int groupId);

    /**
     * 根据用户id和售出情况, 分页寻找所有商品
     *
     * @param groupId 拥有组id
     * @param sold 是否售出
     * @param offset 查询起点，e.g. 5意味着第6行开始
     * @param limit  查询长度, e.g. 10 意味着 6,7 ... 15
     * @return
     */
    List<Merchandise> getAllMerchandise(@Param("group_id") int groupId,
                                        @Param("sold") boolean sold,
                                        @Param("limit") int limit,
                                        @Param("offset") int offset);

    /**
     * 根据imei查询商品
     *
     * @param imei
     * @param groupId 拥有组id
     * @return
     */
    Merchandise getMerchandiseByImei(@Param("imei") String imei, @Param("sold") boolean sold, @Param("group_id") int groupId);

    /**
     * 根据分类id查询商品
     *
     * @param cate_id
     * @param groupId 拥有组id
     * @return
     */
    List<Merchandise> getMerchandiseByCateID(@Param("cate_id") int cate_id,
                                             @Param("sold") boolean sold,
                                             @Param("group_id") int groupId);

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
                          @Param("imei") String imei, @Param("create_time") Date createDate, @Param("owner_id") int ownerId, @Param("group_id") int groupId);

    /**
     * 更新销售状况
     *
     * @param meId
     * @param sold
     * @param groupId
     */
    void updateSold(@Param("me_id") int meId, @Param("sold") boolean sold,  @Param("group_id") int groupId);

    /**
     * 修改商品信息
     *
     * @param meId
     * @param cost
     * @param price
     * @param imei
     * @param groupId
     */
    void updateMerchandise(@Param("me_id") int meId, @Param("cost") double cost, @Param("price") double price,
                           @Param("imei") String imei, @Param("group_id") int groupId);

    /**
     * 搜索商品
     *
     * @param text
     * @return
     */
    List<Merchandise> searchMerchandise(@Param("text") String text, @Param("sold") boolean sold, @Param("group_id") int groupId);

    /**
     * 强影响操作：
     *  删除商品,以及相关order
     *
     * @param meId
     * @param groupId
     */
    void deleteMerchandise(@Param("me_id") int meId, @Param("group_id") int groupId);

    /**
     * 查询某个型号下统计数据
     *
     * @param cate_id
     * @param groupId
     * @return MeCount： 包含数量，总计花费，总计售价的结构体
     */
    MeCount countMerchandiseByCateId(@Param("cate_id") int cate_id, @Param("sold") boolean sold, @Param("group_id") int groupId);
}
