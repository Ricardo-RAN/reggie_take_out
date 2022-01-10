package com.ricardo.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ricardo.reggie.domain.Dish;
import com.ricardo.reggie.dto.DishDto;

import java.util.List;

public interface DishService extends IService<Dish> {
   // 新增菜品同时保存口味
    void saveWithFlavor(DishDto dishDto);

    //查询菜品和对应的口味
    DishDto getByIdWithFlavor(Long id);

    //修改菜品
    void updateWithFlavor(DishDto dishDto);

    //删除菜品并删除相关口味
    void removeWithFlavor(List<Long> ids);
}
