package com.ricardo.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ricardo.reggie.domain.Category;

public interface CategoryService extends IService<Category> {
    void remove(Long id);
}
