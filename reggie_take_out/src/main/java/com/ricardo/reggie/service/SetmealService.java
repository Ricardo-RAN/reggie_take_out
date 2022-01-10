package com.ricardo.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ricardo.reggie.domain.Setmeal;
import com.ricardo.reggie.dto.SetmealDto;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    //新增套餐并保存到对应分类中
    void saveWithDish(SetmealDto setmealDto);

    void removeWithDish(List<Long> ids);
}
