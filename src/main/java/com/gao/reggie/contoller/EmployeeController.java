package com.gao.reggie.contoller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gao.reggie.common.R;
import com.gao.reggie.entity.Employee;
import com.gao.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
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
     * 员工登陆
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
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
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }
    /**
     * 员工退出
     */
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }
}
