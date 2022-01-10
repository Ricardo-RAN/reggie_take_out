package com.ricardo.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ricardo.reggie.common.CustomException;
import com.ricardo.reggie.dao.SetmealDao;
import com.ricardo.reggie.domain.Setmeal;
import com.ricardo.reggie.domain.SetmealDish;
import com.ricardo.reggie.dto.SetmealDto;
import com.ricardo.reggie.service.SetmealDishService;
import com.ricardo.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealDao, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 新增套餐并保存到对应分类中
     * @param setmealDto
     */
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //新增套餐
        this.save(setmealDto);

        Long setmealId = setmealDto.getId();
        //保存到对应分类
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        setmealDishes=setmealDishes.stream().map((item)->{
              item.setSetmealId(setmealId);
              return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }


    /**
     * 删除套餐并删除相关表中的信息
     * @param ids
     */

    public void removeWithDish(List<Long> ids) {
        //查询套餐是否正在售卖中
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Setmeal::getId,ids);
        lambdaQueryWrapper.eq(Setmeal::getStatus,1);

        int count = this.count(lambdaQueryWrapper);
        //判断是否存在正在售卖的套餐，如果有就抛出异常
        if (count > 0){
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        //如果套餐停售，则可以删除，先删除setmeal中的数据
        this.removeByIds(ids);
        //在删除setmeal_dish中的数据，先将要删除的信息查询出来
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getSetmealId,ids);

        setmealDishService.remove(queryWrapper);
    }
}
