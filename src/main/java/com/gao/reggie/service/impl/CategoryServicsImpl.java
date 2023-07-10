package com.gao.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gao.reggie.common.CustomException;
import com.gao.reggie.entity.Category;
import com.gao.reggie.entity.Dish;
import com.gao.reggie.entity.Setmeal;
import com.gao.reggie.mapper.CategoryMapper;
import com.gao.reggie.service.CategoryService;
import com.gao.reggie.service.DishService;
import com.gao.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServicsImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类 在删除前需要判断
     *
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();

        //对id进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count1 = dishService.count(dishLambdaQueryWrapper);

        //查询当前分类是否关联了菜品，如果关联了抛出业务异常
        if (count1 > 0) {
            //已经关联了，抛出业务异常
            throw new CustomException("当前分类下已经关联了菜品，不能删除");
        }


        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();

        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count2 = setmealService.count();


        //查询当前分类是否关联了套餐，如果关联了抛出业务异常
        if (count2 > 0) {
            //已经关联了，抛出业务异常
            throw new CustomException("当前分类下已经关联了套餐，不能删除");
        }

        //可以正常删除分类
        super.removeById(id);
    }
}
