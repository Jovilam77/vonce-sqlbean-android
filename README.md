## Sqlbean For Android
#### 介绍
###### Sqlbean是一款使用Java面向对象思想来编写并生成Sql语句的工具，在此基础上对Mybatis和Spring Jdbc实现了类似于JPA的轻量级插件支持。其中内置大量常用SQL执行的方法，可以非常方便的达到你想要的目的，相对复杂的SQL语句也得以支持，在常规的项目开发几乎做到不写SQL，可以有效的提高项目开发的效率，让开发者更专注于业务代码的编写。

###### 🚀特点: 零入侵, 自动建表, 连表查询, 乐观锁，分页
###### 💻环境: Android 4.0+

###### Sqlbean-Core与Java-Spring版请移步这里👉 [gitee](https://gitee.com/iJovi/vonce-sqlbean "vonce-sqlbean"), [github](https://github.com/Jovilam77/vonce-sqlbean "vonce-sqlbean")

#### 简单上手


###### 1.引入Gradle依赖
	implementation 'cn.vonce:vonce-sqlbean-android:1.0.5-beta9'
	annotationProcessor 'cn.vonce:vonce-sqlbean-android:1.0.5-beta9'
###### 2.标注实体类，实体类与表字段映射

```java
@SqlTable("d_essay")
public class Essay {

    //标识id字段
    @SqlId(type = IdType.SNOWFLAKE_ID_16)
    //@SqlColumn("id")
    private Long id;

    //@SqlColumn("user_id")
    private String userId;

    //@SqlColumn("content")
    private String content;

    //@SqlColumn("creation_time")
    private Date creationTime;

    //标识乐观锁字段
    @SqlVersion
    //@SqlColumn("update_time")
    private Date updateTime;
	
	/**省略get set方法*/
	
}
```
###### 3.获取连接（建议在上一步把所有表字段关系建立好，第一次获取连接时会自动创建表结构）
```java
public class MainActivity extends AppCompatActivity {

    private SqlBeanHelper<Essay, String> essaySqlBeanHelper;
	//private SqlBeanHelper<User, String> userSqlBeanHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //方式一，单库模式
        SQLiteHelper.init(this, "testdb", 1);//建议放在MainActivity或继承的Application
        essaySqlBeanHelper = SQLiteHelper.db().get(Essay.class);

        //方式二，多库模式
        //essaySqlBeanHelper = SQLiteHelper.db(this, "testdb1", 1).get(Essay.class);
        //userSqlBeanHelper = SQLiteHelper.db(this, "testdb2", 1).get(User.class);

    }
}
```
###### 4.CRUD操作
```java

public class MainActivity extends AppCompatActivity {
	
    private SqlBeanHelper<Essay, String> sqlBeanHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SQLiteHelper.init(this, "testdb", 1);
        sqlBeanHelper = SQLiteHelper.db().get(Essay.class);

    }

	//查询
	public void select() {
        
        //查询列表
        List<Essay> list = essayService.selectAll();
        //list = sqlBeanHelper.selectByCondition("& > ?", $Essay.id, 20);
        list = sqlBeanHelper.selectByCondition(Wrapper.where(gt($Essay.id, 10)).and(lt($Essay.id, 20)));

        //查询一条
        Essay essay = sqlBeanHelper.selectById(1L);
        //essay = sqlBeanHelper.selectOneByCondition("& = ?", $Essay.id, 1);
        essay = sqlBeanHelper.selectOneByCondition(Wrapper.where(eq($Essay.id, 333)));

        //复杂查询
        Select select = new Select();
        select.column(SqlEssay.id).column($Essay.content);
        //指定查询的表 可不写
        //select.setTable(Essay.class);
        //看需求指定连表 这里不演示
        //select.join("","");
        select.where().gt("id", 1).and().eq("content", "222");
        //复杂条件推荐使用
        //select.setWhere(Wrapper.where(gt($Essay.id, 1)).and(eq($Essay.content, "222")));
        //也可使用表达式 如果这三种条件同时出现 那么此方式优先级最高 上面包装器次之
        //select.setWhere("& = ? AND & = ?", $Essay.id, 1, $Essay.content, "222");
        select.orderBy("id", SqlSort.DESC);
        list = sqlBeanHelper.select(select);

        //用于查询Map
        Map<String, Object> map = sqlBeanHelper.selectMap(select);

        //用于查询Map列表
        List<Map<String, Object>> mapList = sqlBeanHelper.selectMapList(select);
        
	}

	//分页
	public void getList(HttpServletRequest request) {
        
		// 查询对象
        Select select = new Select();
        PageHelper<Essay> pageHelper = new PageHelper<>(0,1);
        pageHelper.paging(select, sqlBeanHelper);
        ResultData<List<Essay>> data = pageHelper.getResultData();
        
        // 或者这样
        // data = new PageHelper<Essay>(0,1).paging(new Select(),sqlBeanHelper).toResult.getResultData();
        
        //又或者 更简便的用法（不带统计和页数信息）
        //List<Essay> list = sqlBeanHelper.selectByCondition(new Paging(0,10), Wrapper.where(Cond.gt(SqlEssay.id, 10)).and(Cond.lt(SqlEssay.id, 20)));
	}

	//更新
	public void update(Essay essay) {
        
        //根据bean内部id更新
        long i = sqlBeanHelper.updateByBeanId(essay);
        //根据外部id更新
        //i = sqlBeanHelper.updateById(essay, 20);
        //根据条件更新
        //i = sqlBeanHelper.updateByCondition(essay, Wrapper.where(gt($Essay.id, 1)).and(eq($Essay.content, "222")));
        
	}

	//删除
	public void deleteById(Integer[] id) {
        
        //根据id删除
        long i = sqlBeanHelper.deleteById(id);
        //根据条件删除
        //i = sqlBeanHelper.deleteByCondition(Wrapper.where(gt($Essay.id, 1)).and(eq($Essay.content, "222")));

    }

	//插入
	public void add() {
        
		List<Essay> essayList = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			Essay essay = new Essay(i, "name" + i);
			essayList.add(essay);
		}
        sqlBeanHelper.insert(essayList);
        
	}

}
```
##### 👇👇👇更多用法请查看下方文档👇👇👇

###### [0️⃣. 注解详情与使用](doc/Annotation.md "注解详情与使用")
###### [1️⃣. Select](doc/Select.md "Select")
###### [2️⃣. Insert](doc/Insert.md "Insert")
###### [3️⃣. Delete](doc/Delete.md "Delete")
###### [4️⃣. Update](doc/Update.md "Update")
###### [5️⃣. 表操作相关](doc/Table.md "表操作相关")
###### [6️⃣. 分页查询](doc/Paging.md "分页查询")
###### [7️⃣. Service接口和实现类](doc/Interface.md "Service接口和实现类")
###### [8️⃣. SqlBean和SqlHelper](doc/SqlHelper.md "SqlBean和SqlHelper")
###### [9️⃣. Where条件和包装器](doc/Where.md "Where条件和包装器")
