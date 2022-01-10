package com.ricardo.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ricardo.reggie.common.R;
import com.ricardo.reggie.domain.User;
import com.ricardo.reggie.service.UserService;
import com.ricardo.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String>  sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();
        //判断手机号是否为空
        if (StringUtils.isNotEmpty(phone)){
            //随机生成6位验证码
            String code = ValidateCodeUtils.generateValidateCode(6).toString();

            log.info("code={}",code);
            //发送验证码到手机
//            SMSUtils.sendMessage("瑞吉外卖","",phone,code);

            //将验证码保存到session中
            session.setAttribute(phone,code);

            return R.success("手机短信发送成功,验证码为："+code);
        }
        return R.error("短信发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());
        String phone = map.get("phone").toString();

        String code = map.get("code").toString();

        Object codeInSession = session.getAttribute(phone);

        if (codeInSession != null && codeInSession.equals(code)){
            //查询是否有此用户
            LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(User::getPhone,phone);

            User user = userService.getOne(lambdaQueryWrapper);
            //如果没有此用户则注册
            if (user == null){
                user = new User();
                user.setPhone(phone);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("登录失败");
    }

    @PostMapping("/loginout")
    public R<String>  logout(HttpSession session){
        session.removeAttribute("user");
        return R.success("退出成功");
    }
}
