package com.gao.reggie.contoller;

import com.gao.reggie.common.R;
import com.gao.reggie.entity.Category;
import com.gao.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 分类管理
 */
@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryContoller {


    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     *
     * @param category
     * @return
     */
    @PutMapping
    public R<String> save(@RequestBody Category category) {

        log.info("category: {} ", category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }
}
