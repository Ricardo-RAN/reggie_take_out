package com.ricardo.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ricardo.reggie.common.R;
import com.ricardo.reggie.domain.Category;
import com.ricardo.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Category category){
        log.info("新增菜品为：{}",category);

//        //设置生成时间和修改时间
//        category.setCreateTime(LocalDateTime.now());
//        category.setUpdateTime(LocalDateTime.now());
//
//        //设置创建者和修改者
//        Long employeeId = (Long) request.getSession().getAttribute("employee");
//        category.setCreateUser(employeeId);
//        category.setUpdateUser(employeeId);

        categoryService.save(category);
        return R.success("新增菜品成功");
    }

    @GetMapping("/page")
    public R<Page> paging(int page,int pageSize){
        Page pageInfo = new Page(page,pageSize);
        //创建条件构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        lambdaQueryWrapper.orderByDesc(Category::getSort);

        categoryService.page(pageInfo,lambdaQueryWrapper);

        return R.success(pageInfo);
    }

    @DeleteMapping
    public R<String> delete(Long id){
        log.info("根据id删除分类，id为：{}",id);
        categoryService.remove(id);
        return R.success("删除成功");
    }

    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("按id修改信息");
        categoryService.updateById(category);
        return R.success("修改信息成功");
    }

    @GetMapping("/list")
   public R<List<Category>>  list(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper();

        lambdaQueryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //添加排序条件
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(lambdaQueryWrapper);

        return R.success(list);
   }
}
