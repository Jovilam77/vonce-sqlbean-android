package cn.vonce.sql.android.helper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import cn.vonce.sql.android.mapper.RowMapper;
import static android.os.Build.VERSION.SDK_INT;

/**
 * SQLite 执行sql模板
 * @author Jovi
 */
public class SQLiteTemplate {

    private SQLiteDatabase db;

    static {
        //解决安卓9及以上无法进行反射调用非官网api的问题
        if (SDK_INT >= 28) {
            try {
                Method forName = Class.class.getDeclaredMethod("forName", String.class);
                Method getDeclaredMethod = Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class);
                Class<?> vmRuntimeClass = (Class<?>) forName.invoke(null, "dalvik.system.VMRuntime");
                Method getRuntime = (Method) getDeclaredMethod.invoke(vmRuntimeClass, "getRuntime", null);
                Method setHiddenApiExemptions = (Method) getDeclaredMethod.invoke(vmRuntimeClass, "setHiddenApiExemptions", new Class[]{String[].class});
                Object sVmRuntime = getRuntime.invoke(null);
                setHiddenApiExemptions.invoke(sVmRuntime, new Object[]{new String[]{"L"}});
            } catch (Throwable e) {
                Log.e("sqlbean", "reflect bootstrap failed:", e);
            }
        }
    }

    public SQLiteTemplate(SQLiteDatabase db) {
        this.db = db;
    }

    /**
     * 查询某个对象列表
     *
     * @param sql
     * @param rowMapper
     * @param <T>
     * @return
     */
    public <T> List<T> query(String sql, RowMapper<T> rowMapper) {
        List<T> list = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        for (int i = 0; i < cursor.getCount(); i++) {
            list.add(rowMapper.mapRow(cursor, i));
        }
        cursor.close();
        return list;
    }

    /**
     * 查询返回某个对象类型
     *
     * @param sql
     * @param rowMapper
     * @param <T>
     * @return
     */
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper) {
        Cursor cursor = db.rawQuery(sql, null);
        T t = rowMapper.mapRow(cursor, 0);
        cursor.close();
        return t;
    }

    /**
     * 执行sql(主要用于insert,update,delete)
     *
     * @param sql
     * @return
     */
    public int update(final String sql) {
        int result = 0;
        try {
            Method method = db.getClass().getDeclaredMethod("executeSql", String.class, Object[].class);
            method.setAccessible(true);
            result = (int) method.invoke(db, sql, null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 执行sql 无返回
     *
     * @param sql
     */
    public void execSQL(final String sql) {
        db.execSQL(sql);
    }

}
