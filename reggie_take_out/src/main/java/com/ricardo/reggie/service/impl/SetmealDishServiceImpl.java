package com.ricardo.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ricardo.reggie.dao.SetmealDishDao;
import com.ricardo.reggie.domain.SetmealDish;
import com.ricardo.reggie.service.SetmealDishService;
import org.springframework.stereotype.Service;

@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishDao, SetmealDish> implements SetmealDishService {
}
