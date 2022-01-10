package com.ricardo.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ricardo.reggie.common.CustomException;
import com.ricardo.reggie.dao.CategoryDao;
import com.ricardo.reggie.domain.Category;
import com.ricardo.reggie.domain.Dish;
import com.ricardo.reggie.domain.Setmeal;
import com.ricardo.reggie.service.CategoryService;
import com.ricardo.reggie.service.DishService;
import com.ricardo.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, Category> implements CategoryService {
   @Autowired
    private DishService dishService;
   
   @Autowired
    private SetmealService setmealService;

   public void remove(Long id){
       LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
       dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
       int count1 = dishService.count(dishLambdaQueryWrapper);

       if (count1 > 0){
           throw new CustomException("当前分类下关联了菜品，不能删除");
       }

       LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
       setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
       int count2 = setmealService.count(setmealLambdaQueryWrapper);

       if (count2 > 0){
           throw new CustomException("当前分类下关联了套餐，不能删除");
       }

       super.removeById(id);
   }
}
