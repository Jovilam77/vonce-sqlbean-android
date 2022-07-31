## Sqlbean For Android

#### 介绍

###### Sqlbean是一款使用Java面向对象思想来编写并生成Sql语句的工具，在此基础上对Android SQLite实现轻量级插件支持。其中内置大量常用SQL执行的方法，可以非常方便的达到你想要的目的，相对复杂的SQL语句也得以支持，在常规的项目开发几乎做到不写SQL，可以有效的提高项目开发的效率，让开发者更专注于业务代码的编写。

###### 🚀特点: 零入侵, 自动建表, 连表查询, 乐观锁，分页

###### 💻环境: Android 4.0+

###### Sqlbean-Core与Java-Spring版请移步这里👉 [gitee](https://gitee.com/iJovi/vonce-sqlbean "vonce-sqlbean"), [github](https://github.com/Jovilam77/vonce-sqlbean "vonce-sqlbean")

#### 简单上手

###### 1.引入Gradle依赖

	implementation 'cn.vonce:vonce-sqlbean-android:1.1.8'
	annotationProcessor 'cn.vonce:vonce-sqlbean-android:1.1.8'

###### 2.标注实体类，实体类与表字段映射

```java

@SqlTable("d_user")
public class User {
    @SqlId(type = IdType.SNOWFLAKE_ID_16)
    private Long id;
    private String name;
    private Integer age;
    private Integer stature;
    private Integer gender;
    private String phone;
    private Date createTime;
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
        List<User> list = userService.select();
        list = sqlBeanHelper.selectBy(Wrapper.where(gt(User$.id, 10)).and(lt(User$.id, 20)));
        //指定查询
        list = sqlBeanHelper.select(new Select().column(User$.id$, User$.name$, User$.phone).where().eq());

        //查询一条
        User user = userService.selectById(1);
        user = sqlBeanHelper.selectOneBy(Wrapper.where(eq(User$.id, 1001)));

        //sql语义化查询《20岁且是女性的用户根据创建时间倒序，获取前10条》
        list = sqlBeanHelper.select(new Select().column(User$.id$, User$.name$, User$.phone$).where().eq(User$.age, 22).and().eq(User$.gender, 0).back().orderByDesc(User$.createTime).page(0, 10));

        //联表查询《20岁且是女性的用户根据创建时间倒序，查询前10条用户的信息和地址》
        Select select = new Select();
        select.column(User$.id$, User$.name$, User$.phone$, UserAddress$.province$, UserAddress$.city$, UserAddress$.area$, UserAddress$.details$);
        select.join(JoinType.INNER_JOIN, UserAddress$._tableName, UserAddress$.user_id, User$.id);
        select.where().gt(User$.age$, 22).and().eq(User$.gender$, 0);
        select.orderByDesc(User$.createTime$);
        select.page(0, 10);

        //查询Map
        Map<String, Object> map = sqlBeanHelper.selectMap(select);
        List<Map<String, Object>> mapList = sqlBeanHelper.selectMapList(select);
    }

    //分页
    public void getPageList() {
        // 查询对象
        Select select = new Select();
        PageHelper<User> pageHelper = new PageHelper<>(0, 10);
        pageHelper.paging(select, sqlBeanHelper);
        ResultData<List<Essay>> data = pageHelper.getResultData();
    }

    //更新
    public void update(Essay essay) {
        //根据bean内部id更新
        long i = sqlBeanHelper.updateByBeanId(essay);
        //根据外部id更新
        //i = sqlBeanHelper.updateById(essay, 20);
        //根据条件更新
        //i = sqlBeanHelper.updateBy(Wrapper.where(gt(User$.age, 22)).and(eq(User$.gender, 1)));
    }

    //删除
    public void deleteById(String[] id) {
        //根据id删除
        long i = sqlBeanHelper.deleteById(id);
        //根据条件删除
        //i = sqlBeanHelper.deleteBy(Wrapper.where(gt(User$.age, 22)).and(eq(User$.gender, 1)));
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
