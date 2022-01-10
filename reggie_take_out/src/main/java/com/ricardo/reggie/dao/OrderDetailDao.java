package com.ricardo.reggie.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ricardo.reggie.domain.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDetailDao extends BaseMapper<OrderDetail> {
}
