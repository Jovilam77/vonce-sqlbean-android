package cn.vonce.sql.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

import cn.vonce.sql.bean.Create;
import cn.vonce.sql.config.SqlBeanConfig;
import cn.vonce.sql.enumerate.DbType;
import cn.vonce.sql.uitls.PackageUtil;

/**
 * 初始化数据库
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private Class<?> clazz;
    private Context context;

    public DatabaseHelper(Class<?> clazz, Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.clazz = clazz;
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        List<String> classNames = PackageUtil.getClasses(context, clazz.getPackage().getName());
        try {
            SqlBeanConfig sqlBeanConfig = new SqlBeanConfig(DbType.SQLite);
            Create create;
            for (String name : classNames) {
                Class<?> clazz = Class.forName(name);
                create = new Create();
                create.setSqlBeanConfig(sqlBeanConfig);
                create.setBeanClass(clazz);
                String sql = SqlHelper.buildCreateSql(create);
                System.out.println(sql);
                db.execSQL(sql);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
