package com.ricardo.reggie.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ricardo.reggie.domain.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShoppingCartDao extends BaseMapper<ShoppingCart> {
}
