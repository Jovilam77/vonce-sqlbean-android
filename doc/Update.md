## Update
##### Update对象的方法请看代码中的文档描述
#### 方法和示例
###### 1：根据id条件更新
```java
long updateById(T bean, Object id, boolean updateNotNull);
```
```java
public RS update() {
	long i = essayService.updateById(1);
	if(i > 0){
		//更新成功
	}
}
```
###### 2：根据实体类id条件更新
```java
long updateByBeanId(T bean, boolean updateNotNull);
```
```java
public RS update() {
	Essay essay = new Essay();
	essay.setId(1);
	essay.setTitle("测试");
	essay.setContent("测试123");
	//true仅更新不为null的字段
	long i = essayService.updateByBeanId(essay, ture);
	if(i > 0){
		//更新成功
	}
}
```
###### 3：根据实体类id条件更新（可过滤某些字段）
```java
long updateByBeanId(T bean, boolean updateNotNull, String[] filterFields);
```
```java
public RS update() {
	Essay essay = new Essay();
	essay.setId(1);
	essay.setTitle("测试");
	essay.setContent("测试123");
	//true仅更新不为null的字段，filterFields过滤某些字段
	long i = essayService.updateByBeanId(essay, ture, new String[]{"title"});
	if(i > 0){
		//更新成功
	}
}
```
###### 4：根据外部id条件更新（可过滤某些字段）
```java
long updateById(T bean, Object id, boolean updateNotNull, String[] filterFields);
```
```java
public RS update() {
	Essay essay = new Essay();
	essay.setTitle("测试");
	essay.setContent("测试123");
	//true仅更新不为null的字段，filterFields过滤某些字段
	long i = essayService.updateById(essay, 20, ture, new String[]{"title"});
	if(i > 0){
		//更新成功
	}
}
```
###### 5：根据条件更新
```java
long updateByCondition(T bean, boolean updateNotNull, String where, Object... args);
```
```java
public RS update() {
	Essay essay = new Essay();
	essay.setTitle("测试");
	essay.setContent("测试123");
	//true仅更新不为null的字段
	long i = essayService.updateByCondition(essay, ture, "id > ? and id < ?", 1 ,10);
	if(i > 0){
		//更新成功
	}
}
```
###### 6：根据条件更新（可过滤某些字段）
```java
long updateByCondition(T bean, boolean updateNotNull, String[] filterFields, String where, Object... args);
```
```java
public RS update() {
	Essay essay = new Essay();
	essay.setId(1);
	essay.setTitle("测试");
	essay.setContent("测试123");
	//true仅更新不为null的字段，filterFields过滤某些字段
	long i = essayService.updateByCondition(essay, ture, new String[]{"id"},
	"id > ? and id < ?", 1 ,10);
	if(i > 0){
		//更新成功
	}
}
```
###### 7：根据实体类字段条件更新
```java
long updateByBeanCondition(T bean, boolean updateNotNull, String where);
```
```java
public RS update() {
	Essay essay = new Essay();
	essay.setId(1);
	essay.setTitle("测试");
	essay.setContent("测试123");
	//true仅更新不为null的字段，${为你实体类中的字段名}
	long i = essayService.updateByBeanCondition(essay, ture, "id = ${id}");
	if(i > 0){
		//更新成功
	}
}
```
###### 8：根据实体类字段条件更新（可过滤某些字段）
```java
long updateByBeanCondition(T bean, boolean updateNotNull, String[] filterFields, String where);
```
```java
public RS update() {
	Essay essay = new Essay();
	essay.setId(1);
	essay.setTitle("测试");
	essay.setContent("测试123");
	//true仅更新不为null的字段，filterFields过滤某些字段，${为你实体类中的字段名}
	long i = essayService.updateByBeanCondition(essay, ture, new String[]{"title"},
	"id = ${id}");
	if(i > 0){
		//更新成功
	}
}
```
###### 9：更新（where条件为空会抛异常，因为更新全部非常危险）
```java
long update(Update update);
```
```java
public RS update() {
	Essay essay = new Essay();
	essay.setTitle("测试");
	essay.setContent("测试123");
	Update update = new Update();
	update.setUpdateBean(essay);
	update.where("id", 1);//必须有where条件
	essayService.update(update);
	if(i > 0){
		//更新成功
	}
}
```
###### 10：更新（如果要更新全部可以用这个）
```java
long update(Update update, boolean ignore);
```
```java
public RS update() {
	Essay essay = new Essay();
	essay.setTitle("测试");
	essay.setContent("测试123");
	Update update = new Update();
	update.setUpdateBean(essay);
	//如果这里为false并且没有where条件则抛异常
	essayService.update(update, true);
	if(i > 0){
		//更新成功
	}
}
```