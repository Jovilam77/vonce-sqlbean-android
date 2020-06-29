package cn.vonce.sql.helper;

import android.content.Context;

import java.util.Map;
import java.util.WeakHashMap;

import cn.vonce.sql.orm.service.SqlBeanService;
import cn.vonce.sql.orm.service.impl.SqlBeanServiceImpl;

/**
 * 数据库连接助手
 */
public class SQLiteHelper {

    private static SQLiteHelper defaultSqLiteHelper;
    private final static Map<String, SQLiteHelper> sqLiteHelperMap = new WeakHashMap<>();
    private final Map<Class<?>, SqlBeanService> sqlBeanServiceImplMap = new WeakHashMap<>();

    private Context context;
    private String name;
    private int version;

    private SQLiteHelper(Context context, String name, int version) {
        this.context = context;
        this.name = name;
        this.version = version;
    }

    /**
     * 初始化默认数据库参数
     *
     * @param context
     * @param name
     * @param version
     */
    public static void init(Context context, String name, int version) {
        if (defaultSqLiteHelper == null) {
            defaultSqLiteHelper = new SQLiteHelper(context, name, version);
        }
    }

    /**
     * 动态设置数据库参数
     *
     * @param context
     * @param name
     * @param version
     * @return
     */
    public static SQLiteHelper db(Context context, String name, int version) {
        SQLiteHelper sqLiteHelper = sqLiteHelperMap.get(name);
        if (sqLiteHelper == null) {
            sqLiteHelper = new SQLiteHelper(context, name, version);
        } else {
            if (sqLiteHelper.version != version) {
                sqLiteHelper.version = version;
                sqLiteHelperMap.put(name, sqLiteHelper);
                sqLiteHelper.sqlBeanServiceImplMap.clear();
            }
        }
        return sqLiteHelper;
    }

    /**
     * 获取默认数据库参数
     *
     * @return
     */
    public static SQLiteHelper db() {
        SqlHelper.isNull(defaultSqLiteHelper, "请初始化默认数据库");
        return defaultSqLiteHelper;
    }

    /**
     * 获得数据库连接
     *
     * @param clazz
     * @return
     */
    public SqlBeanService get(Class<?> clazz) {
        SqlBeanService sqlBeanService = sqlBeanServiceImplMap.get(clazz);
        if (sqlBeanService == null) {
            sqlBeanService = new SqlBeanServiceImpl(clazz, new DatabaseHelper(clazz, context, name, null, version));
            sqlBeanServiceImplMap.put(clazz, sqlBeanService);
        }
        return sqlBeanService;
    }

}
