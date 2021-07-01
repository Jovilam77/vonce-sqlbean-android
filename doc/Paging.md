##分页查询
###### 分页属性
```java
//当前页
private Integer pagenum;
//每页数量
private Integer pagesize;
//排序字段
private String sortdatafield;
//排序方式
private String sortorder;
//时间戳
private String timestamp;
```
#### 方法一
##### 返回分页数据包含页数总数信息
```java
public ResultData<List<Essay>> getList(int pagenum,int pagesize) {
	Select select = new Select();// 查询对象（具体用法去看Select）
	select.where("id", 1, SqlOperator.GREATER_THAN);//条件
	select.wAND("id", 10, SqlOperator.LESS_THAN);//条件
	select.wORBracket("type", "军事");//条件
	PageHelper<Essay> pageHelper = new PageHelper<>(pagenum, pagesize, null);//分页助手，如果你要联表查询请将泛型对象改为你的包装对象（具体请看联表查询注解文档）
	pageHelper.paging(select, sqlBeanService);//分页查询
	//如果你要联表查询请使用下面这个（具体请看联表查询注解那里）
	//pageHelper.paging(EssayUnion.class, select, sqlBeanService);
	return pageHelper.getResultData();//返回结果
}
```
#### 方法二
##### 返回分页数据包含页数总数信息
```java
public ResultData<List<Essay>> getList(int pagenum,int pagesize) {
	PageHelper<Essay> pageHelper = new PageHelper<>(pagenum, pagesize, null);
	pageHelper.dispose(sqlBeanService.countAll());
	pageHelper.setDataList(sqlBeanService.selectAll(pageHelper.getPaging()));
	//如果你有条件则用这个
	//String sql = "(id > ? and id < ?) or type = ?";
	//Object[] args = new Object[]{1, 10, "军事"};
	//pageHelper.dispose(sqlBeanService.selectCountByCondition(sql, args));
	//pageHelper.setDataList(sqlBeanService.selectByCondition(pageHelper.getPaging(), sql, args));
	return pageHelper.getResultData("获取文章列表成功");
}
```
#### 方法三
##### 仅返回分页数据
```java
public List<Essay> getList(int pagenum,int pagesize) {
	PageHelper<Essay> pageHelper = new PageHelper<>(pagenum, pagesize, null);
	return sqlBeanService.selectAll(pageHelper.getPaging());
	//如果你有条件则用这个
	//String sql = "(id > ? and id < ?) or type = ?";
	//Object[] args = new Object[]{1, 10, "军事"};
	//return sqlBeanService.selectByCondition(pageHelper.getPaging(), sql, args);
}
```