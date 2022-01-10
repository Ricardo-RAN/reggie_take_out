package com.ricardo.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ricardo.reggie.common.BaseContext;
import com.ricardo.reggie.common.R;
import com.ricardo.reggie.domain.OrderDetail;
import com.ricardo.reggie.domain.Orders;
import com.ricardo.reggie.domain.ShoppingCart;
import com.ricardo.reggie.dto.OrdersDto;
import com.ricardo.reggie.service.OrderDetailService;
import com.ricardo.reggie.service.OrdersService;
import com.ricardo.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 提交订单
     *
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        log.info(orders.toString());
        ordersService.submit(orders);
        return R.success("订单支付成功");
    }

    @GetMapping("/list")
    public R<List<Orders>> list() {
        Long userId = BaseContext.getId();

        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Orders::getUserId, userId);
        List<Orders> ordersList = ordersService.list(lambdaQueryWrapper);

        return R.success(ordersList);
    }

    /**
     * 订单分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> paging(int page, int pageSize) {
        //分页构造器
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        Page<OrdersDto> detailPage = new Page<>();
        //获取用户id
        Long userId = BaseContext.getId();
        //条件构造器
        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Orders::getUserId, userId);
        lambdaQueryWrapper.orderByDesc(Orders::getOrderTime);
        //将ordersPage的属性拷贝到detailPage中
        BeanUtils.copyProperties(ordersPage, detailPage);

         //获取分页的所有数据
        ordersService.page(ordersPage, lambdaQueryWrapper);
        //获取分页记录集合
        List<Orders> records = ordersPage.getRecords();


        List<OrdersDto> dtoList = records.stream().map(item -> {
            //创建dto对象
            OrdersDto ordersDto = new OrdersDto();
            //将分页记录拷贝到dto中
            BeanUtils.copyProperties(item, ordersDto);
            //获取订单id
            Long ordersId = ordersDto.getId();
            //根据订单id查询订单明细
            LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(OrderDetail::getOrderId, ordersId);
            //获取订单明细集合
            List<OrderDetail> orderDetails = orderDetailService.list(queryWrapper);
            //将集合设置到dto中
            ordersDto.setOrderDetails(orderDetails);
            return ordersDto;
        }).collect(Collectors.toList());
        //将分页记录设置到Page中
        detailPage.setRecords(dtoList);

        return R.success(detailPage);
    }

    @PostMapping("/again")
    public R<List<ShoppingCart>>  again(@RequestBody Orders orders){
        LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OrderDetail::getOrderId,orders.getId());


        List<OrderDetail> orderDetails = orderDetailService.list(lambdaQueryWrapper);

        List<ShoppingCart> shoppingCarts = orderDetails.stream().map((item) -> {
            ShoppingCart shoppingCart = new ShoppingCart();
//            shoppingCart.setId(8L);
            shoppingCart.setName(item.getName());
            shoppingCart.setImage(item.getImage());
            shoppingCart.setUserId(BaseContext.getId());
            shoppingCart.setDishId(item.getDishId());
            shoppingCart.setSetmealId(item.getSetmealId());
            shoppingCart.setDishFlavor(item.getDishFlavor());
            shoppingCart.setNumber(item.getNumber());
            shoppingCart.setAmount(item.getAmount());
            return shoppingCart;
        }).collect(Collectors.toList());

        shoppingCartService.saveBatch(shoppingCarts);

        return R.success(shoppingCarts);
    }


    //===================================================================
    //后台
    @GetMapping("/orderDetail/{id}")
    public R<List<OrderDetail>> queryOrders(@PathVariable Long id) {
        LambdaQueryWrapper<OrderDetail>  lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OrderDetail::getOrderId,id);

        List<OrderDetail> orderDetails = orderDetailService.list(lambdaQueryWrapper);
        return R.success(orderDetails);
    }


    /**
     * 订单分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number, String beginTime, String endTime) {

        Page<Orders> ordersPage = new Page<>(page, pageSize);

        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(number!=null,Orders::getId,number);
        lambdaQueryWrapper.between(beginTime!=null,Orders::getOrderTime,beginTime,endTime);
        lambdaQueryWrapper.orderByDesc(Orders::getOrderTime);

        ordersService.page(ordersPage,lambdaQueryWrapper);

        return R.success(ordersPage);

    }


    @PutMapping
    public R<String> update(@RequestBody Orders orders){
//       LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//       lambdaQueryWrapper.eq(Orders::getId,orders.getId());

        Orders order = ordersService.getById(orders.getId());


        if (orders.getStatus()==3) {
            order.setStatus(3);
        }

        if (orders.getStatus() == 4){
            order.setStatus(4);
        }
        ordersService.updateById(order);
        return R.success("状态修改成功");
    }
}
