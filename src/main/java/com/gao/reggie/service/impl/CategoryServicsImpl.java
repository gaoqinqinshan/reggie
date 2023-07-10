package com.gao.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gao.reggie.entity.Category;
import com.gao.reggie.mapper.CategoryMapper;
import com.gao.reggie.service.CategoryService;
import org.springframework.stereotype.Service;

@Service
public class CategoryServicsImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
}
