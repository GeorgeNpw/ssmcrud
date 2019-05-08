package com.npw.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.npw.bean.Employee;
import com.npw.bean.Msg;
import com.npw.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author npw
 * @date 2019/4/26
 * 处理员工CRUD请求
 */
@Controller
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;

    /**
     * 导入jackson包 把对象转换成JSON字符串 第二稿 支持移动设备
     *
     * @param pn
     * @return
     */
    @RequestMapping("/emps")
    @ResponseBody
    public Msg getEmpsWithJson(@RequestParam(value = "pn", defaultValue = "1") Integer pn) {
        // 引入PageHelper分页插件
        // 查询前调用，传入页码和记录数
        PageHelper.startPage(pn, 5);
        // startPage紧跟着的这个查询就是一个分页查询
        List<Employee> emps = employeeService.getAll();
        // PageInfo包装查询结果，封装了详细的分页信息和详细数据
        // 连续显示5页
        PageInfo pageInfo = new PageInfo(emps, 5);

        return Msg.success().add("pageInfo", pageInfo);
    }

//    /**
//     * 展示list.jsp页面 查询员工数据（分页查询）
//     *
//     * @return
//     */
//    @RequestMapping("/emps")
//    public String getEmps(@RequestParam(value = "pn", defaultValue = "1") Integer pn, Model model) {
//        // 引入PageHelper分页插件
//        // 查询前调用，传入页码和记录数
//        PageHelper.startPage(pn, 5);
//        // startPage紧跟着的这个查询就是一个分页查询
//        List<Employee> emps = employeeService.getAll();
//        // PageInfo包装查询结果，封装了详细的分页信息和详细数据
//        // 连续显示5页
//        PageInfo pageInfo = new PageInfo(emps, 5);
//        // 把PageInfo交给页面即可
//        model.addAttribute("pageInfo", pageInfo);
//
//        return "list";
//    }

    /**
     * 校验用户名是否被占用
     *
     * @param empName
     * @return
     */
    @RequestMapping(value = "/checkuser", method = RequestMethod.POST)
    @ResponseBody
    public Msg checkuser(@RequestParam("empName") String empName) {
        // 判断用户名是否符合正则表达式

        String regex = "(^[A-Za-z0-9]{6,16}$)|(^[\\u2E80-\\u9FFF]{2,5}$)";
        if (!empName.matches(regex)) {
            // System.out.println(empName.matches(regex));
            return Msg.fail().add("va_msg", "名字必须是2-5个中文或者6-16位英文数字组合");
        }

        if (employeeService.checkuser(empName)) {
            return Msg.success();
        } else {
            return Msg.fail().add("va_msg", "用户名不可用");
        }
    }

    /**
     * 保存员工信息
     * 加入JSR303校验 @Valid  BindingResult result
     */
    @RequestMapping(value = "/emp", method = RequestMethod.POST)
    @ResponseBody
    public Msg saveEmp(@Valid Employee employee, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, Object> map = new HashMap<String, Object>();
            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                map.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
            return Msg.fail().add("errorFields", map);
        } else {
            employeeService.saveEmp(employee);
            return Msg.success();
        }
    }

    /**
     * 查询员工信息
     *
     */
    @RequestMapping(value = "/emp/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Msg getEmp(@PathVariable("id") Integer id) {
        Employee emp = employeeService.getEmp(id);
        return Msg.success().add("emp", emp);
    }

    /**
     * 如果直接发送Ajax=PUT形式的请求，封装的数据为
     * Employee[empId=1001,empName=null, gender=null, email=null, dID=null]
     *
     * 问题：请求体中有数据，employee对象封装不上
     * 原因：tomcat：
     *          1.将请求体中的数据封装为一个map
     *          2.request.getParameter("empName")就会从这个map中取值
     *
     * ajax发送PUT请求出现问题：
     *      PUT请求，请求体中的数据request.getParameter("empName")拿不到
     *      tomcat一看时PUT请求，就不会封装请求体中的数据为map，只有POST请求，才会封装请求体为map
     *
     * 解决方法：
     *      在web.xml文件中配置HttpPutFormContentFilter
     *      作用：将请求体中的数据解析包装成一个map
     *          request被重新包装，request.getParameter()被重写，会从自己封装的map中读取数据
     *
     * 修改员工信息
     * @param employee
     */
    @RequestMapping(value = "/emp/{empId}", method = RequestMethod.PUT)
    @ResponseBody
    public Msg updateEmp(Employee employee) {
        employeeService.updateEmp(employee);
        return Msg.success();
    }


//    /**
//     * 单个删除员工信息
//     * @param id
//     * @return
//     */
//    @RequestMapping(value="/emp/{id}",method=RequestMethod.DELETE)
//    @ResponseBody
//    public Msg deleteEmpById(@PathVariable("id")Integer id) {
//        employeeService.deleteEmp(id);
//        return Msg.success();
//    }


    /**
     * 批量删除员工信息:1-2-3 单个：1
     * @param ids
     * @return
     */
    @RequestMapping(value = "/emp/{ids}", method = RequestMethod.DELETE)
    @ResponseBody
    public Msg deleteEmpById(@PathVariable("ids") String ids) {
        if (ids.contains("-")) {
            String[] strIds = ids.split("-");
            /*
             * 一种实现 for (String str : strIds) {
             * employeeService.deleteEmp(Integer.parseInt(str)); }
             */
            // 另一种实现
            List<Integer> del_ids = new ArrayList<Integer>();
            for (String str : strIds) {
                del_ids.add(Integer.parseInt(str));
            }
            employeeService.deleteBatchEmp(del_ids);
        } else {
            employeeService.deleteEmp(Integer.parseInt(ids));
        }
        return Msg.success();
    }

}
