package cn.vonce.sql.android.helper;

import android.content.Context;
import cn.vonce.sql.uitls.SqlBeanUtil;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * 数据库连接助手
 *
 * @author Jovi
 */
public class SQLiteHelper {

    private volatile static SQLiteHelper defaultSqLiteHelper;
    private final static Map<String, SQLiteHelper> sqLiteHelperMap = new WeakHashMap<>();
    private final Map<Class<?>, SqlBeanHelper> sqlBeanHelperMap = new WeakHashMap<>();

    private Context context;
    private String name;
    private int version;
    private DatabaseHelper databaseHelper;

    private SQLiteHelper(Context context, String name, int version) {
        this.context = context;
        this.name = name;
        this.version = version;
    }

    /**
     * 初始化默认数据库参数
     *
     * @param context 上下文
     * @param name    数据库名称
     * @param version 数据库版本
     */
    public static void init(Context context, String name, int version) {
        if (defaultSqLiteHelper == null) {
            synchronized (SQLiteHelper.class) {
                if (defaultSqLiteHelper == null) {
                    defaultSqLiteHelper = new SQLiteHelper(context, name, version);
                }
            }
        }
    }

    /**
     * 动态设置数据库参数
     *
     * @param context 上下文
     * @param name    数据库名称
     * @param version 数据库版本
     * @return
     */
    public static SQLiteHelper db(Context context, String name, int version) {
        SQLiteHelper sqLiteHelper = sqLiteHelperMap.get(name);
        if (sqLiteHelper == null) {
            sqLiteHelper = new SQLiteHelper(context, name, version);
        } else {
            if (sqLiteHelper.version != version) {
                sqLiteHelper.version = version;
                sqLiteHelper.sqlBeanHelperMap.clear();
                sqLiteHelperMap.put(name, sqLiteHelper);
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
        SqlBeanUtil.isNull(defaultSqLiteHelper, "请初始化默认数据库");
        return defaultSqLiteHelper;
    }

    /**
     * 获得数据库连接
     *
     * @param clazz
     * @return
     */
    public <T, ID> SqlBeanHelper<T, ID> get(Class<T> clazz) {
        SqlBeanHelper<T, ID> sqlBeanHelper = sqlBeanHelperMap.get(clazz);
        if (sqlBeanHelper == null) {
            if (databaseHelper == null) {
                databaseHelper = new DatabaseHelper(clazz, context, name, null, version);
            }
            sqlBeanHelper = new SqlBeanHelper<>(clazz, context, databaseHelper);
            sqlBeanHelperMap.put(clazz, sqlBeanHelper);
        }
        return sqlBeanHelper;
    }

}