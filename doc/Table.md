## 表结构操作
#### 方法和示例
###### 1：删除表结构
```java
long dropTable();
```
```java
public RS dropTable() {
	long i = sqlBeanService.getTableService().dropTable();
	if(i > 0){
		//删除成功
	}
}
```
###### 2：创建表结构
```java
long createTable();
```
```java
public RS createTable() {
	long i = sqlBeanService.getTableService().createTable();
	if(i > 0){
		//创建成功
	}
}
```
