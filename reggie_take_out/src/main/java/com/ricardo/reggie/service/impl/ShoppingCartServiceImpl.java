package com.ricardo.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ricardo.reggie.dao.ShoppingCartDao;
import com.ricardo.reggie.domain.ShoppingCart;
import com.ricardo.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartDao, ShoppingCart> implements ShoppingCartService {
}
