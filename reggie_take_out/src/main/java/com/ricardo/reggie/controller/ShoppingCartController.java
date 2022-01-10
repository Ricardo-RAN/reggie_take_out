package com.ricardo.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ricardo.reggie.common.BaseContext;
import com.ricardo.reggie.common.R;
import com.ricardo.reggie.domain.ShoppingCart;
import com.ricardo.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;
//    @Autowired
//    private DishService dishService;
//    @Autowired
//    private SetmealService setmealService;
    /**
     * 添加菜品或套餐到购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        //获取当前用户id
        Long userId = BaseContext.getId();
        shoppingCart.setUserId(userId);

        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,userId);

        Long dishId = shoppingCart.getDishId();
        //查询添加到购物车的是菜品还是套餐
        if (dishId!=null){
            lambdaQueryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else{
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart shoppingCartOne = shoppingCartService.getOne(lambdaQueryWrapper);
        //查询购物车里是否有此菜品或套餐
        if (shoppingCartOne!=null){
            Integer number = shoppingCartOne.getNumber();
            shoppingCartOne.setNumber(number + 1);
            shoppingCartService.updateById(shoppingCartOne);
        }else {
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartOne = shoppingCart;
        }
        return R.success(shoppingCartOne);
    }

    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getId());
        lambdaQueryWrapper.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(lambdaQueryWrapper);
        return R.success(list);
    }

    /**
     * 减少购物车中的商品数量
     * @param
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        //按用户id查询出购物车信息
       LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
       lambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getId());
       //判断减少的是菜品数量还是套餐数量
        if (shoppingCart.getDishId()!=null){
            //如果是菜品，则按菜品查询
            lambdaQueryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }else {
            //否则按套餐查询
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        //获取查询到的购物车信息
        ShoppingCart shoppingCartOne = shoppingCartService.getOne(lambdaQueryWrapper);

        //判断购物车是否为空，如果不为空，则可以减少
        if (shoppingCartOne!=null){
            //获取商品数量
            Integer number = shoppingCartOne.getNumber();
            //如果商品数量大于1
            if (number > 1) {
                //数量减一
                shoppingCartOne.setNumber(number - 1);
                shoppingCartService.updateById(shoppingCartOne);
            }else {
                //当商品为一时，商品减一
                shoppingCartOne.setNumber(number - 1);
                //清除该商品
                shoppingCartService.remove(lambdaQueryWrapper);
            }
        }
        return R.success(shoppingCartOne);
    }

    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getId());
        shoppingCartService.remove(lambdaQueryWrapper);
        return R.success("购物车清空成功");
    }
}
