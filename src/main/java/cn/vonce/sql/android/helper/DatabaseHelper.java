package cn.vonce.sql.android.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

import android.util.Log;
import cn.vonce.sql.android.util.PackageUtil;
import cn.vonce.sql.bean.Create;
import cn.vonce.sql.bean.Table;
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
            for (String name : classNames) {
                Table table = SqlBeanUtil.getTable(clazz);
                if (StringUtil.isEmpty(table.getSchema()) || table.getSchema().equals(dbName)) {
                    Class<?> clazz = Class.forName(name);
                    create = new Create();
                    create.setBeanClass(clazz);
                    String sql = SqlHelper.buildCreateSql(create);
                    db.execSQL(sql);
                    Log.i("sqlbean", sql);
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
