## Service接口和实现类
##### 增删查改接口
```java
public interface EssayService extends SqlBeanService<Essay, String> {

}
```
###### 在Service层除了可以实现SqlBeanService接口以外，如果你的业务只需要单独查询 或 插入 或 删除 或 更新，那么你还可以使用以下接口

##### 仅查询接口
```java
public interface EssayService extends SelectService<Essay, String> {

}
```
##### 仅插入接口
```java
public interface EssayService extends InsertService<Essay, String> {

}
```
##### 仅删除接口
```java
public interface EssayService extends DeleteService {

}
```
##### 仅更新接口
```java
public interface EssayService extends UpdateService<Essay, String> {

}
```

##### 实现类
```java
@Service
public class EssayServiceImpl extends SqlBeanServiceImpl<Essay, String> implements EssayService {

}
```
