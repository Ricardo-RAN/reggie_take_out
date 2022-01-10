package com.ricardo.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ricardo.reggie.common.CustomException;
import com.ricardo.reggie.dao.DishDao;
import com.ricardo.reggie.domain.Dish;
import com.ricardo.reggie.domain.DishFlavor;
import com.ricardo.reggie.dto.DishDto;
import com.ricardo.reggie.service.DishFlavorService;
import com.ricardo.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishDao, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品同时保存口味
     * @param dishDto
     */
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //将对象添加到菜品当中
        this.save(dishDto);
        //把菜品标识获取出来
        Long dishId = dishDto.getId();
        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item)->{
            //给遍历出来的口味设置到对应的菜品中
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        //将口味集合保存到dishFlavor中
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //根据id获得菜品
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        //将菜品信息拷贝进来
        BeanUtils.copyProperties(dish,dishDto);
        //查询口味
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dish.getId());

        List<DishFlavor> flavors = dishFlavorService.list(lambdaQueryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    /**
     * 修改菜品
     * @param dishDto
     */
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表信息
         this.updateById(dishDto);
         //清除dish_flavor中的口味数据
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);
        //添加提交过来的口味信息
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors=flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }



    @Override
    public void removeWithFlavor(List<Long> ids) {
        //查询菜品售卖状态
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Dish::getId,ids);
        lambdaQueryWrapper.eq(Dish::getStatus,1);

        int count = this.count(lambdaQueryWrapper);
        //判断是否有菜品在售卖，如果有就抛出异常
        if (count > 0){
            throw new CustomException("菜品正在售卖，不能删除");
        }
        //如果菜品停售，则可以删除dish表中数据
        this.removeByIds(ids);

        //如果菜品停售，则可以删除，先查询出菜品对应的口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DishFlavor::getDishId,ids);

        dishFlavorService.remove(queryWrapper);

    }
}
