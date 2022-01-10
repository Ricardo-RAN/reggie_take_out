package com.ricardo.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ricardo.reggie.common.R;
import com.ricardo.reggie.domain.Category;
import com.ricardo.reggie.domain.Setmeal;
import com.ricardo.reggie.domain.SetmealDish;
import com.ricardo.reggie.dto.SetmealDto;
import com.ricardo.reggie.service.CategoryService;
import com.ricardo.reggie.service.DishService;
import com.ricardo.reggie.service.SetmealDishService;
import com.ricardo.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private DishService dishService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info(setmealDto.toString());
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    @GetMapping("/page")
    public R<Page> paging(int page,int pageSize,String name){
        //分页构造器
        Page<Setmeal>  pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage = new Page<>();
        //条件构造器
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(name!=null, Setmeal::getName,name);
        lambdaQueryWrapper.orderByAsc(Setmeal::getUpdateTime);
        //获取分页信息
        setmealService.page(pageInfo,lambdaQueryWrapper);
        //复制属性
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        //获取分页数据
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((item) -> {
            //创建dto对象
            SetmealDto setmealDto = new SetmealDto();
            //获取分类id
            Long categoryId = item.getCategoryId();
           //将分页数据拷贝到dto中
            BeanUtils.copyProperties(item, setmealDto);
            //根据id获取分类对象
            Category category = categoryService.getById(categoryId);
            if (category!=null){
                //获取分类名称
                String categoryName = category.getName();
                //给dto设置分类名称
                setmealDto.setCategoryName(categoryName);
            }

            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);

        return R.success(dtoPage);
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info(ids.toString());

        setmealService.removeWithDish(ids);

        return R.success("删除成功");
    }

    /**
     * 修改套餐售卖状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status,@RequestParam List<Long> ids){
        for (Long id : ids) {
            Setmeal setmeal = setmealService.getById(id);
            setmeal.setStatus(status);
            setmealService.updateById(setmeal);
        }
        return R.success("状态修改成功");
    }

    @GetMapping("/list")
    public R<List<SetmealDto>> list(Setmeal setmeal){
        //查询套餐分类
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        lambdaQueryWrapper.eq(Setmeal::getStatus,1);
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(lambdaQueryWrapper);

        List<SetmealDto> dtoList = list.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();

            BeanUtils.copyProperties(item, setmealDto);

            Long categoryId = item.getCategoryId();

            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }

            Long setmealId = item.getId();

            LambdaQueryWrapper<SetmealDish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishLambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmealId);

            List<SetmealDish> dishList = setmealDishService.list(dishLambdaQueryWrapper);

            setmealDto.setSetmealDishes(dishList);

            return setmealDto;
        }).collect(Collectors.toList());
        return R.success(dtoList);
    }

    @GetMapping("/dish/{id}")
    public R<List<SetmealDish>> dish(@PathVariable Long id){
        log.info("id为：{}",id);
        //根据套餐id查询出菜品
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId,id);

        List<SetmealDish> setmealDishList = setmealDishService.list(lambdaQueryWrapper);

        return R.success(setmealDishList);
    }

}
