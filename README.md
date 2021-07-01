## Sqlbean For Android
#### 介绍
###### Sqlbean是一款使用Java面向对象思想来编写并生成Sql语句的工具，在此基础上对Mybatis和Spring Jdbc实现了类似于JPA的轻量级插件支持。其中内置大量常用SQL执行的方法，可以非常方便的达到你想要的目的，相对复杂的SQL语句也得以支持，在常规的项目开发几乎做到不写DAO层，可以有效的提高项目开发的效率，让开发者更专注于业务代码的编写。

###### 特点：零入侵，自动建表，连表查询，乐观锁，分页
###### 环境：Android 4.0+

###### Sqlbean-Core与Java-Spring版请移步这里👉 [gitee](https://gitee.com/iJovi/vonce-sqlbean "vonce-sqlbean")， [github](https://github.com/Jovilam77/vonce-sqlbean "vonce-sqlbean")

#### 简单上手


###### 1：引入Gradle依赖
	implementation 'cn.vonce:vonce-sqlbean-android:1.0.5-beta'
	annotationProcessor 'cn.vonce:vonce-sqlbean-android:1.0.5-beta'
###### 2：标注实体类，实体类与表字段映射

```java
@SqlTable("d_essay")
public class Essay {

	@SqlId(type = IdType.SNOWFLAKE_ID_16) //标识id字段
	//@SqlColumn("id") 常规情况下可不写
	private Long id;

	//@SqlColumn("user_id" ) 常规情况下可不写
	private String userId;

	//@SqlColumn("content" ) 常规情况下可不写
	private String content;

	//@SqlColumn("creation_time" ) 常规情况下可不写
	private Date creationTime;

    @SqlVersion //标识乐观锁字段
    //@SqlColumn("update_time" ) 常规情况下可不写
	private Date updateTime;
	
	/**省略get set方法*/
	
}
```
###### 3：获取连接（建议在上一步把所有表字段关系建立好，第一次获取连接时会自动创建表结构）
```java
public class MainActivity extends AppCompatActivity {

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

public class MainActivity extends AppCompatActivity {
	
    private SqlBeanService<Essay, String> essayService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SQLiteHelper.init(this, "testdb", 1);
        essayService = SQLiteHelper.db().get(Essay.class);

    }

	//查询
	public void select() {
		//查询列表  全部
        List<Essay> list = essayService.selectAll();
        //查询列表  根据条件查询 方式一
        list = essayService.selectByCondition("& > ?", SqlEssay.id, 20);
        //查询列表  根据条件查询 方式二 推荐
        list = essayService.selectByCondition(Wrapper.where(Cond.gt(SqlEssay.id, 10)).and(Cond.lt(SqlEssay.id, 20)));


        //查询单条  根据id
        Essay essay = essayService.selectById(1L);
        //查询单条  根据条件查询 方式一
        essay = essayService.selectOneByCondition("& = ?", SqlEssay.id, 1);
        //查询单条  根据条件查询 方式二 推荐
        essay = essayService.selectOneByCondition(Wrapper.where(Cond.eq(SqlEssay.id, 333)));

        //复杂查询
        Select select = new Select();
        //指定查询的字段
        select.column(SqlEssay.id).column(SqlEssay.content);
        //指定查询的表 可不写
        //select.setTable(Essay.class);
        //看需求指定连表 这里不演示
        //select.join("","");
        //id 大于 1  这里的id建议用SqlEssay.id 常量替代 这里演示多种写法特意不写
        select.where("id", 1, SqlOperator.GREATER_THAN);
        //并且 内容等于222 这里的content建议用SqlEssay.content 常量替代 这里演示多种写法特意不写
        select.wAND("content", "222");
        //条件也可用包装器 复杂条件推荐使用
        //select.setWhere(Wrapper.where(Cond.gt(SqlEssay.id, 1)).and(Cond.eq(SqlEssay.content, "222")));
        //也可使用表达式 如果这三种条件同时出现 那么此方式优先级最高 上面包装器次之
        //select.setWhere("& = ? AND & = ?", SqlEssay.id, 1, SqlEssay.content, "222");
        //根据id倒序
        select.orderBy("id", SqlSort.DESC);

        //用于查询Map 多条结果时会报错
        Map<String, Object> map = essayService.selectMap(select);
		//用于查询Map列表
        List<Map<String, Object>> mapList = essayService.selectMapList(select);

        //用于查询对象列表
        list = essayService.select(select);
		
		//更多用法请查看下方详细文档...
	}

	//分页
	public void getList(HttpServletRequest request) {
		// 查询对象
        Select select = new Select();
        // 分页助手ReqPageHelper
        PageHelper<Essay> pageHelper = new PageHelper<>(0,1);
        //分页查询
        pageHelper.paging(select, essayService);
        //返回结果
        ResultData<List<Essay>> data = pageHelper.getResultData();
        // 或者这样
        // data = new PageHelper<Essay>(0,1).paging(new Select(),essayService).toResult.getResultData();
        
        //又或者 更简便的用法（不带统计和页数信息）
        //List<Essay> list = essayService.selectByCondition(new Paging(0,10), Wrapper.where(Cond.gt(SqlEssay.id, 10)).and(Cond.lt(SqlEssay.id, 20)));
	}

	//更新
	public void update(Essay essay) {
	    //根据bean内部id更新
		long i = essayService.updateByBeanId(essay);
		//根据外部id更新 参数3的true代表仅更新不为null字段 参数4的true代表使用乐观锁
        //i = essayService.updateById(essay,20,true,true);
		//根据条件更新 参数2的true代表仅更新不为null字段 参数3的true代表使用乐观锁
        //i = essayService.updateByCondition(essay,true,true,Wrapper.where(Cond.gt(SqlEssay.id, 1)).and(Cond.eq(SqlEssay.content, "222")));
		//更多用法请查看下方详细文档...
	}

	//删除
	public void deleteById(Integer[] id) {
	    //根据id删除
		long i = essayService.deleteById(id);
		//根据条件删除
        //i = essayService.deleteByCondition(Wrapper.where(Cond.gt(SqlEssay.id, 1)).and(Cond.eq(SqlEssay.content, "222")));
        //更多用法请查看下方详细文档...
	}

	//插入
	public void add() {
		List<Essay> essayList = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			Essay essay = new Essay(i, "name" + i);
			essayList.add(essay);
		}
		essayService.insert(essayList);
	}

}

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