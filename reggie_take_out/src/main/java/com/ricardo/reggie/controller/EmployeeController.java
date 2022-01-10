package com.ricardo.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ricardo.reggie.common.R;
import com.ricardo.reggie.domain.Employee;
import com.ricardo.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> employeeLogin(HttpServletRequest request, @RequestBody Employee employee){
        String password = employee.getPassword();
        password=DigestUtils.md5DigestAsHex(password.getBytes());

        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Employee::getUsername,employee.getUsername());

        //比对用户是否存在
        Employee emp = employeeService.getOne(lambdaQueryWrapper);
        if (emp==null){
            return R.error("登录失败");
        }
        //比对密码是否一致
        if (!emp.getPassword().equals(password)){
            return R.error("登录失败");
        }
        //比对账号状态
        if (emp.getStatus()==0){
            return R.error("账号已禁用");
        }

        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }


    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String>  logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Employee employee){
        //将密码用md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //设置生成时间和修改时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

        //设置创建者和修改者
//        Long employeeId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(employeeId);
//        employee.setUpdateUser(employeeId);

        //保存员工信息
        employeeService.save(employee);

        return R.success("新增员工成功");
    }

    /**
     * 员工分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> paging(int page,int pageSize,String name){
        //写日志
        log.info("page = {},pageSize = {},name = {}",page,pageSize,name);
        //创建分页构造器
        Page pageInfo = new Page(page,pageSize);
        //创建条件构造器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);
        //查询分页数据
        employeeService.page(pageInfo,lambdaQueryWrapper);
        //返回信息
        return R.success(pageInfo);
    }

    /**
     * 启用 禁用
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Employee employee){
        log.info(employee.toString());
//        Long empId = (Long) request.getSession().getAttribute("employee");
//
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);

        employeeService.updateById(employee);

        return R.success("修改员工信息成功");
    }

    /**
     * 按Id查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("按Id查询用户信息...");
        Employee employee = employeeService.getById(id);
        //判断得到的信息是否为空
        if (employee != null){
            return R.success(employee);
        }
        return R.error("没有查询到对应的用户信息");
    }
}
