## Sqlbean For Android
#### 介绍
###### 这是一款使用Java面向对象思想来编写并生成Sql语句的工具，并在此基础上对Android实现了轻量化的SQLite插件支持。插件中内置大量常用Sql执行的方法，目的是为了提高开发效率，减少大量的Sql语句编写，让开发者更专注于业务代码的编写。

###### 特点：零配置，自动建表，连表查询，乐观锁，分页
###### 环境：Android 4.0+

###### Sqlbean-Core与Java-Spring版请移步这里👉 [gitee](https://gitee.com/iJovi/vonce-sqlbean "vonce-sqlbean")， [github](https://github.com/Jovilam77/vonce-sqlbean "vonce-sqlbean")

#### 简单上手


###### 1：引入Gradle依赖
	implementation 'cn.vonce:vonce-sqlbean-android:1.0.1'
	annotationProcessor 'cn.vonce:vonce-sqlbean-android:1.0.1'
###### 2：标注实体类，实体类与表字段映射

```java
@SqlTable("d_essay")
public class Essay {

	@SqlId(generateType = GenerateType.UUID)
	@SqlColumn("id")
	private String id;

	@SqlColumn("userId" )
	private String userId;

	@SqlColumn("content" )
	private String content;

	@SqlColumn("creationTime" )
	private Date creationTime;
	
	/**省略get set方法*/
	
}
```
###### 3：获取连接（建议在上一步把所有表字段关系建立好，第一次获取连接时会自动创建表结构）
```java
    private SqlBeanService<Essay, String> essayService;
	//private SqlBeanService<User, String> userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //方式一，单库模式
        SQLiteHelper.init(this, "testdb", 1);//建议放在MainActivity或继承的Application
        essayService = SQLiteHelper.db().get(Essay.class);

        //方式二，多库模式
//        essayService = SQLiteHelper.db(this, "testdb1", 1).get(Essay.class);
//        userService = SQLiteHelper.db(this, "testdb2", 1).get(User.class);

    }
}
```
###### 4：CRUD操作
```java
//查询
public void select(){
	Essay essay = essayService.selectById("20");
	List<Essay> essayList = essayService.selectByCondition("& > ?",SqlEssay.id, 10);//SqlEssay.id常量是自动生成
	Essay essay1 = essayService.selectOneByCondition("id = ?", 10);//也可以直接写sql字段
	List<Essay> essayList1 = essayService.selectAll(new Paging(0, 10));
	//多达24个查询方法，具体请查看文档
}

//删除
public void delete(){
	essayService.deleteById("3", "4");
	essayService.deleteByCondition("& > ?",SqlEssay.id , 10);
	//更多请查看文档
}

//插入
public void insert(){
	Date date = new Date();
	Essay essay = new Essay();
	essay.setId("id" + i);
	essay.setContent("content" + i);
	essay.setUserId("userId" + i);
	essay.setCreationTime(date);
	essayService.insert(essay);
	
	Essay essay1;
    List<Essay> essayList = new ArrayList<>();
    for (int i = 0; i < 50; i++) {
        essay1 = new Essay();
        essay1.setId("id" + i);
        essay1.setContent("content" + i);
        essay1.setUserId("userId" + i);
        essay1.setCreationTime(date);
        essayList.add(essay1);
    }
    essayService.insert(essayList);
}

//更新
public void update(){
    Essay essay = new Essay();
    essay.setId("2");
    essay.setContent("测试 update");
    essayService.updateByBeanId(updateEssay, true);
	
	Essay essay1 = new Essay();
	essay1.setContent("测试 update");
	essayService.updateById(updateEssay, "10", true);
	//更多请查看文档
}
```
[========]

##### ↓更多用法请查看下方文档↓

#### [注解与用法（含ID生成、乐观锁、逻辑删除、连表查询）](https://github.com/Jovilam77/vonce-sqlbean-android/blob/develop/doc/Annotation.md "注解与用法（含ID生成、乐观锁、逻辑删除、连表查询）")
#### [Select操作相关方法](https://github.com/Jovilam77/vonce-sqlbean-android/blob/develop/doc/Select.md "Select操作相关方法")
#### [Insert操作相关方法](https://github.com/Jovilam77/vonce-sqlbean-android/blob/develop/doc/Insert.md "Insert操作相关方法")
#### [Delete操作相关方法](https://github.com/Jovilam77/vonce-sqlbean-android/blob/develop/doc/Delete.md "Delete操作相关方法")
#### [Update操作相关方法](https://github.com/Jovilam77/vonce-sqlbean-android/blob/develop/doc/Update.md "Update操作相关方法")
#### [表结构操作相关方法](https://github.com/Jovilam77/vonce-sqlbean-android/blob/develop/doc/Table.md "表结构操作相关方法")
#### [Service接口和实现类](https://github.com/Jovilam77/vonce-sqlbean-android/blob/develop/doc/Interface.md "Service接口和实现类")
#### [SqlBean和SqlHelper](https://github.com/Jovilam77/vonce-sqlbean-android/blob/develop/doc/SqlHelper.md "SqlBean和SqlHelper")
#### [Where条件和占位符](https://github.com/Jovilam77/vonce-sqlbean-android/blob/develop/doc/Where.md "Where条件和占位符")
#### [分页查询](https://github.com/Jovilam77/vonce-sqlbean-android/blob/develop/doc/Paging.md "分页查询")