package com.ricardo.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ricardo.reggie.domain.Orders;

public interface OrdersService extends IService<Orders> {
    /**
     * 下单
     * @param orders
     */
    void submit(Orders orders);
}
