# 修改员工信息功能

## 逻辑分析

1. 点击修改按钮，发送AJAX信息，请求当前修改员工的数据库信息
2. 弹出模态框，回显员工信息，姓名不允许修改
3. 校验邮箱

**注意**
由于修改和删除按钮都是页面加载完成后，由AJAX发送请求，再添加到页面的，所有click事件不生效，所以要使用dom对象操作
![](http://ww1.sinaimg.cn/large/006pqaYvgy1g2v0hyhl8xj30dw060758.jpg)

TOMCAT直接使用PUT请求的时候不会封装请求体数据，要使用SpringMVC的过滤器完成PUT
![](http://ww1.sinaimg.cn/large/006pqaYvgy1g2v0iau7wdj30p805rmx6.jpg)
## 业务实现
1. 获取部门信息增加到下拉框
2. 获取员工信息添加到模态框
3. 点击保存按钮更新员工信息到数据库



