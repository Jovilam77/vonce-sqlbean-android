## Select
##### Select对象的方法请看代码中的文档描述
#### 方法和示例
###### 1：根据id条件查询
```java
T selectById(Object id);
```
```java
public Essay getEssay() {
	Essay essay = essayService.selectById(1);
	return essay;
}
```
###### 2：根据id条件查询（指定返回类型）
```java
<O> O selectById(Class<O> returnType, Object id);
```
```java
public EssayUnion getEssay() {
	EssayUnion essayUnion = essayService.selectById(EssayUnion.class,1);
	//也可以这样使用
	//返回第一个字段内容，可以用你想要的类型接收
	//String id = essayService.selectById(String.class,1);
	//返回Map
	//Map<String,Object> map = essayService.selectById(Map.class,1);
	return essayUnion;
}
```
###### 3：根据ids条件查询
```java
List<T> selectByIds(Object... ids);
```
```java
public List<Essay> getEssay() {
	List<Essay> essayList = essayService.selectByIds(1,2,3);
	return essayList;
}
```
###### 4：根据ids条件查询（指定返回类型）
```java
<O> List<O> selectByIds(Class<O> returnType, Object... ids);
```
```java
public List<EssayUnion> getEssay() {
	List<EssayUnion> essayUnionList = essayService.selectByIds(EssayUnion.class, 1,2,3);
	//也可以这样使用
	//返回第一个字段内容，可以用你想要的类型接收
	//String id = essayService.selectByIds(String.class, 1,2,3);
	//返回Map
	//Map<String,Object> map = essayService.selectByIds(Map.class, 1,2,3);
	return essayUnionList;
}
```
###### 5：根据自定义条件查询 只返回一条记录
```java
T selectOne(Select select);
```
```java
public Essay getEssay() {
	Select select = new Select();
	select.column("id");
	select.column("title");
	select.where("id",1);
	Essay essay = essayService.selectOne(select);
	return essay;
}
```
###### 6：根据自定义条件查询 只返回一条记录（指定返回类型）
```java
<O> O selectOne(Class<O> returnType, Select select);
```
```java
public EssayUnion getEssay() {
	Select select = new Select();
	select.column("id");
	select.column("title");
	select.where("id",1);
	EssayUnion essayUnion = essayService.selectOne(EssayUnion.class, select);	//也可以这样使用
	//返回第一个字段内容,column("你想要的字段")，可以用你想要的类型接收
	//String id = essayService.selectOne(String.class, select);
	//返回Map
	//Map<String,Object> map = essayService.selectOne(Map.class, select);
	return essayUnion;
}
```
###### 7：根据自定义条件查询 返回Map
```java
Map<String, Object> selectMap(Select select);
```
```java
public Map<String,Object> getEssay() {
	Select select = new Select();
	select.column("id");
	select.column("title");
	select.where("id",1);
	Map<String,Object> map = essayService.selectMap(select);
	return map;
}
```
###### 8：根据条件查询
```java
T selectOneBy(String where, Object... args);
```
```java
public Essay getEssay() {
	Essay essay = essayService.selectOneBy("id = ?",1);
	return essay;
}
```
###### 9：根据条件查询（指定返回类型）
```java
<O> O selectOneBy(Class<O> returnType, String where, Object... args);
```
```java
public EssayUnion getEssay() {
	EssayUnion essayUnion = essayService.selectOneBy(EssayUnion.class, "id = ?",1);
	//也可以这样使用
	//返回第一个字段内容，可以用你想要的类型接收
	//String id = essayService.selectOneBy(String.class, "id = ?",1);
	//返回Map
	//Map<String,Object> map = essayService.selectOneBy(Map.class, "id = ?",1);
	return essayUnion;
}
```
###### 10：根据条件查询
```java
List<T> selectBy(String where, Object... args);
```
```java
public List<Essay> getEssay() {
	List<Essay> essayList = essayService.selectBy("userId = ?",888);
	return essayList;
}
```
###### 11：根据条件查询 分页
```java
List<T> selectBy(Paging paging, String where, Object... args);
```
```java
public List<Essay> getEssay() {
	//查询第1页，一页显示10条，根据创建时间降序
	Paging paging = new Paging(0,10,"creationTime","desc");
	//PageHelper<Essay> pageHelper = new PageHelper<>(0,10,null);
	//Paging paging = pageHelper.getPaging()
	List<Essay> essayList = essayService.selectBy(paging,"userId = ?",888);
	return essayList;
}
```
###### 12：根据条件查询（指定返回类型）
```java
<O> List<O> selectBy(Class<O> returnType, String where, Object... args);
```
```java
public List<EssayUnion> getEssay() {
	List<EssayUnion> essayUnionList = essayService.selectBy(EssayUnion.class,
	"userId = ?",888);
	//也可以这样使用
	//返回第一个字段内容，可以用你想要的类型接收
	//List<String> idList = essayService.selectBy(String.class,
	"userId = ?",888);
	//返回Map
	//List<Map<String,Object>> mapList = essayService.selectBy(Map.class,
	"userId = ?",888);
	return essayUnionList;
}
```
###### 13：根据条件查询 分页（指定返回类型）
```java
<O> List<O> selectBy(Class<O> returnType, Paging paging, String where, Object... args);
```
```java
public List<EssayUnion> getEssay() {
	//查询第1页，一页显示10条，根据创建时间降序
	Paging paging = new Paging(0,10,"creationTime","desc");
	//PageHelper<Essay> pageHelper = new PageHelper<>(request);
	//Paging paging = pageHelper.getPaging()
	List<EssayUnion> essayUnionList = essayService.selectBy(EssayUnion.class, paging, 
	"userId = ?",888);
	//也可以这样使用
	//返回第一个字段内容，可以用你想要的类型接收
	//List<String> idList = essayService.selectBy(String.class, paging, 
	"userId = ?",888);
	//返回Map
	//List<Map<String,Object>> mapList = essayService.selectBy(Map.class, paging, 
	"userId = ?",888);
	return essayUnionList;
}
```
###### 14：根据条件查询统计
```java
long selectCountBy(String where, Object... args);
```
```java
public long getEssay() {
	long count = essayService.selectCountBy("userId = ?",888);
	return count;
}
```
###### 15：统计全部
```java
long countAll();
```
```java
public long getEssay() {
	long count = essayService.countAll();
	return count;
}
```
###### 16：查询全部
```java
List<T> selectAll();
```
```java
public List<Essay> getEssay() {
	List<Essay> essayList = essayService.selectAll();
	return essayList;
}
```
###### 17：查询全部（指定返回类型）
```java
<O> List<O> selectAll(Class<O> returnType);
```
```java
public List<EssayUnion> getEssay() {
	List<EssayUnion> essayUnionList = essayService.selectAll(EssayUnion.class);
	//也可以这样使用
	//返回第一个字段内容，可以用你想要的类型接收
	//List<String> idList = essayService.selectAll(String.class);
	//返回Map
	//List<Map<String,Object>> mapList = essayService.selectAll(Map.class);
	return essayUnionList;
}
```
###### 18：查询全部 分页
```java
List<T> selectAll(Paging paging);
```
```java
public List<Essay> getEssay() {
	//查询第1页，一页显示10条，根据创建时间降序
	Paging paging = new Paging(0,10,"creationTime","desc");
	//PageHelper<Essay> pageHelper = new PageHelper<>(request);
	//Paging paging = pageHelper.getPaging()
	List<Essay> essayList = essayService.selectAll(paging);
	return essayList;
}
```
###### 19：查询全部 分页（指定返回类型）
```java
<O> List<O> selectAll(Class<O> returnType, Paging paging);
```
```java
public List<EssayUnion> getEssay() {
	//查询第1页，一页显示10条，根据创建时间降序
	Paging paging = new Paging(0,10,"creationTime","desc");
	//PageHelper<Essay> pageHelper = new PageHelper<>(request);
	//Paging paging = pageHelper.getPaging()
	List<EssayUnion> essayUnionList = essayService.selectAll(EssayUnion.class,paging);
	//也可以这样使用
	//返回第一个字段内容，可以用你想要的类型接收
	//List<String> idList = essayService.selectAll(String.class,paging);
	//返回Map
	//List<Map<String,Object>> mapList = essayService.selectAll(Map.class,paging);
	return essayUnionList;
}
```
###### 20：根据自定义条件查询 返回Map
```java
List<Map<String, Object>> selectMapList(Select select);
```
```java
public List<Map<String, Object>> getEssay() {
	Select select = new Select();
	select.column("id");
	select.column("title");
	List<Map<String, Object>> mapList = essayService.selectMapList(select);
	return mapList;
}
```
###### 21：根据自定义条件查询
```java
List<T> select(Select select);
```
```java
public List<Essay> getEssay() {
	Select select = new Select();
	select.column("id");
	select.column("title");
	List<Essay> essayList = essayService.select(select);
	return essayList;
}
```
###### 22：根据自定义条件查询
```java
<O> List<O> select(Class<O> returnType, Select select);
```
```java
public List<EssayUnion> getEssay() {
	Select select = new Select();
	select.column("id");
	select.column("title");
	List<EssayUnion> essayUnionList = essayService.select(EssayUnion.class, select);
	//也可以这样使用
	//返回第一个字段内容,column("你想要的字段")，可以用你想要的类型接收
	//List<String> idList = essayService.select(String.class,select);
	//返回Map
	//List<Map<String,Object>> mapList = essayService.select(Map.class,select);
	return essayUnionList;
}
```
###### 23：根据自定义条件查询
```java
long count(Select select);
```
```java
public long getEssay() {
	Select select = new Select();
	select.column("id");
	select.column("title");
	long count = essayService.select(select);
	return count;
}
```
###### 24：根据自定义条件查询（插件内部使用）
```java
long count(Class<?> clazz, Select select);
```
```java
public long getEssay() {
	Select select = new Select();
	select.column("id");
	select.column("title");
	long count = essayService.select(EssayUnion.class, select);
	return long;
}
```