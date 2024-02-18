package com.lizumin.wms.service;

import com.lizumin.wms.dao.MerchandiseMapper;
import com.lizumin.wms.entity.Merchandise;
import com.lizumin.wms.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

/**
 *  Merchandise Service -- 需要owner id
 *
 * @author Zumin Li
 * @date 2024/2/14 2:08
 */
@Service
public class MerchandiseService {
    public static final int DEFAULT_PAGE_OFFSET = 0; // 查询起点

    public static final int DEFAULT_PAGE_LIMIT = 50; // 每次查询数量

    private final MerchandiseMapper merchandiseMapper;

    public MerchandiseService(MerchandiseMapper merchandiseMapper) {
        this.merchandiseMapper = merchandiseMapper;
    }

    /**
     * 获取所有未售出商品
     *
     * @param ownerId
     * @param limit
     * @param offset
     * @return
     */
    public List<Merchandise> getNonSoldMerchandise(int ownerId, int limit, int offset) {
        Assert.isTrue( ownerId > 0 && limit > 0 && offset >= 0, "invalid id or page options");
        return merchandiseMapper.getAllMerchandiseBySold(ownerId, false, limit, offset);
    }

    /**
     * 获取所有未售出商品总数
     *
     * @param authentication
     * @return
     */
    public int getMerchandiseCount(Authentication authentication) {
        return this.merchandiseMapper.getMerchandiseCount(getIdFromAuthentication(authentication), false);
    }

    /**
     * 获取所有未售出或售出商品总数
     *
     * @param authentication
     * @param sold 是否售出
     * @return
     */
    public int getMerchandiseCount(Authentication authentication, boolean sold) {
        return this.merchandiseMapper.getMerchandiseCount(getIdFromAuthentication(authentication), sold);
    }

    private int getIdFromAuthentication(Authentication authentication) {
        Assert.notNull(authentication, "authentication cannot be null");
        Object user = authentication.getPrincipal();
        Assert.isInstanceOf(User.class, user, "Principal must be a user");

        return ((User) user).getId();
    }
}
