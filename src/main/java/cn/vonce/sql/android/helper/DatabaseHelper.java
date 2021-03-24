package cn.vonce.sql.android.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

import android.util.Log;
import cn.vonce.sql.android.util.PackageUtil;
import cn.vonce.sql.annotation.SqlTable;
import cn.vonce.sql.bean.Create;
import cn.vonce.sql.bean.Table;
import cn.vonce.sql.config.SqlBeanConfig;
import cn.vonce.sql.enumerate.DbType;
import cn.vonce.sql.helper.SqlHelper;
import cn.vonce.sql.uitls.SqlBeanUtil;
import cn.vonce.sql.uitls.StringUtil;

/**
 * 初始化数据库
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private String dbName;
    private Class<?> clazz;
    private Context context;

    public DatabaseHelper(Class<?> clazz, Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.dbName = name;
        this.clazz = clazz;
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        List<String> classNames = PackageUtil.getClasses(context, clazz.getPackage().getName());
        try {
            Create create;
            SqlBeanConfig sqlBeanConfig = new SqlBeanConfig(DbType.SQLite);
            for (String className : classNames) {
                Table table = SqlBeanUtil.getTable(clazz);
                if (StringUtil.isEmpty(table.getSchema()) || table.getSchema().equals(dbName)) {
                    Class<?> clazz = Class.forName(className);
                    SqlTable sqlTable = clazz.getAnnotation(SqlTable.class);
                    if (sqlTable == null || sqlTable.autoCreate()) {
                        create = new Create();
                        create.setSqlBeanConfig(sqlBeanConfig);
                        create.setBeanClass(clazz);
                        String sql = SqlHelper.buildCreateSql(create);
                        db.execSQL(sql);
                        Log.i("sqlbean", sql);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.i("sqlbean", e.getMessage(), e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
