package com.ricardo.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ricardo.reggie.common.R;
import com.ricardo.reggie.domain.Category;
import com.ricardo.reggie.domain.Dish;
import com.ricardo.reggie.domain.DishFlavor;
import com.ricardo.reggie.dto.DishDto;
import com.ricardo.reggie.service.CategoryService;
import com.ricardo.reggie.service.DishFlavorService;
import com.ricardo.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<DishDto> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        dishService.saveWithFlavor(dishDto);

        return R.success(dishDto);
    }

    /**
     * 菜品分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page>  paging(int page, int pageSize, String name){
        //分页构造器
        Page<Dish> pageInfo = new Page(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);
        //添加排序条件
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        dishService.page(pageInfo,lambdaQueryWrapper);

        //将pageInfo中的属性拷贝到dishDtoPage中，并排除records
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        //获取分页记录
        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            //拷贝基本字段
            BeanUtils.copyProperties(item, dishDto);
            //得到categoryId
            Long categoryId = dishDto.getCategoryId();
            //获取Category对象
            Category category = categoryService.getById(categoryId);
            if (category!=null) {
                String categoryName = category.getName();
                //设置categoryName
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
         //将records设置进去
        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id){
        log.info("id为：{}",id);
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品信息
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        return R.success("菜品信息修改成功");
    }

//    /**
//     * 查询
//     * @param dish
//     * @return
//     */
//    @GetMapping("/list")
//    public R<List<Dish>>  list(Dish dish){
//       //条件构造器
//        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
//       //查询在售菜品
//        lambdaQueryWrapper.eq(Dish::getStatus,1);
//        //添加排序条件
//        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> list = dishService.list(lambdaQueryWrapper);
//
//        return R.success(list);
//    }
    /**
     * 查询
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>>  list(Dish dish){
        //条件构造器
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //查询在售菜品
        lambdaQueryWrapper.eq(Dish::getStatus,1);
        //添加排序条件
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(lambdaQueryWrapper);

        List<DishDto> dishDtoList = list.stream().map(item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long dishId = dishDto.getId();

            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DishFlavor::getDishId, dishId);

            List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapper);

            dishDto.setFlavors(dishFlavorList);

            return dishDto;
        }).collect(Collectors.toList());
        return R.success(dishDtoList);
    }

    /**
     * 删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("{}",ids);
        dishService.removeWithFlavor(ids);
        return R.success("菜品删除成功");
    }


    /**
     * 改变售卖状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status, @RequestParam List<Long> ids){
        log.info("{}",status);
//        List<Long> list = ids.stream().map(item -> {
//            Dish dish = dishService.getById(item);
//            dish.setStatus(status);
//            return item;
//        }).collect(Collectors.toList());

        for (Long id : ids) {
            Dish dish = dishService.getById(id);
            dish.setStatus(status);
            dishService.updateById(dish);
        }
        return R.success("状态修改成功");
    }
}
