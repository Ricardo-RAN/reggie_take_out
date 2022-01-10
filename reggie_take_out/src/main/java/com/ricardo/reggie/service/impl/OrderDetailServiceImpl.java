package com.ricardo.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ricardo.reggie.dao.OrderDetailDao;
import com.ricardo.reggie.domain.OrderDetail;
import com.ricardo.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailDao, OrderDetail> implements OrderDetailService {
}
