package com.ricardo.reggie.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ricardo.reggie.domain.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryDao extends BaseMapper<Category> {
}
