package com.ricardo.reggie.common;

public class BaseContext extends ThreadLocal {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    /**
     * 设置值
     * @param id
     */
    public static void setId(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取值
     * @return
     */
    public static Long getId(){
       return threadLocal.get();
    }
}
