package com.gao.reggie.contoller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gao.reggie.common.R;
import com.gao.reggie.entity.Employee;
import com.gao.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")

public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登陆（）
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {

        //密码通过md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //根据页面提交的用户username查数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getName, employee.getName());
        Employee emp = employeeService.getOne(queryWrapper);

        //如果没有查询到则返回登陆失败的结果
        if (emp == null) {
            return R.error("登陆失败");
        }

        //密码比对，如果不一致返回登陆失败的结果
        if (!emp.getPassword().equals(password)) {
            return R.error("登陆失败");
        }

        //查看员工状态，如果为禁用状态，则返回员工已禁用的结果
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }

        //登陆成功，将员工id存入Session并且返回登陆成功的结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工，员工信息为：{}", employee.toString());

        //设置初始密码，但是需要md5加密处理
//        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//
//        //设置创建时间以及更新时间（这俩个简单）
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
//        //来获得当前登陆用户的id（request.getSession().getAttribute("employee")这是获取id的方法
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /**
     * 员工信息分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

        //构造分页构造器（page pageSize）
        Page pageinfo = new Page(page, pageSize);

        //构造条件构造器（name）
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();

        //添加构造条件,这里的like里面应该是：StringUtils.isNotEmpty(name),这里的意思是当这个地方不为空的情况才会添加
        queryWrapper.like(StringUtils.hasText(name), Employee::getName, name);

        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageinfo, queryWrapper);

        return R.success(pageinfo);
    }

    /**
     * 根据id修改员工信息
     *
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> updata(HttpServletRequest request, @RequestBody Employee employee) {



        Long emId = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateUser(emId);
        employeeService.updateById(employee);
        employee.setUpdateTime(LocalDateTime.now());

        return R.success("员工信息修改成功");
    }

    /**
     * 根据员工id查找信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        log.info("根据员工id查找员工信息");
        Employee employee = employeeService.getById(id);

        return R.success(employee);
    }

}