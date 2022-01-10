package com.ricardo.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ricardo.reggie.dao.DishFlavorDao;
import com.ricardo.reggie.domain.DishFlavor;
import com.ricardo.reggie.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorDao, DishFlavor> implements DishFlavorService {
}
