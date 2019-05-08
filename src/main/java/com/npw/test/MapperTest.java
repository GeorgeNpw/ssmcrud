package com.npw.test;

import com.npw.bean.Department;
import com.npw.bean.Employee;
import com.npw.dao.DepartmentMapper;
import com.npw.dao.EmployeeMapper;
import org.apache.ibatis.session.SqlSession;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import org.apache.ibatis.session.SqlSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import java.util.UUID;

/**
 * @author npw
 * @date 2019/4/25
 * 使用spring的测试模块
 *  1.导入springtest
 *  2.@contextconfiguration
 *  3.直接@autowire要使用的组件即可
 */
@RunWith(SpringJUnit4ClassRunner.class) //spring提供的单元测试模块
@ContextConfiguration(locations= {"classpath:applicationContext.xml"})
public class MapperTest {

    @Autowired
    DepartmentMapper departmentMapper;
    @Autowired
    EmployeeMapper employeeMapper;
    @Autowired
    SqlSession sqlSession;

    /**
     *测试DepartmentMapper
     */
    @Test
    public void testCRUD(){
        //1.创建IOC容器
//		ApplicationContext ioc = new ClassPathXmlApplicationContext("applicationContext.xml");
//		//2.从容器中获取mapper
//		DepartmentMapper bean = (DepartmentMapper) ioc.getBean(DepartmentMapper.class);
//
//		System.out.println(bean);
        System.out.println(departmentMapper);
        //1.插入几个部门
//		departmentMapper.insertSelective(new Department(null,"开发部"));
//		departmentMapper.insertSelective(new Department(null, "测试部"));
        //2.插入员工
        //employeeMapper.insertSelective(new Employee(null,"Jerry","M", "Jerry@163.com",1));

        //3.批量插入 使用可以执行批量执行的sqlsession
        EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
        for(int i=0;i<1000;i++) {
            String uid = UUID.randomUUID().toString().substring(0,5)+i;
            mapper.insertSelective(new Employee(null, uid, "M", uid+"@163.com", 1));

        }
        System.out.println("批量完成");
    }
}
