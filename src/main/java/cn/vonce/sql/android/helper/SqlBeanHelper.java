package cn.vonce.sql.android.helper;


import android.content.Context;
import android.util.Log;
import cn.vonce.sql.android.service.SqlBeanServiceImpl;
import cn.vonce.sql.android.util.PackageUtil;
import cn.vonce.sql.annotation.SqlTable;
import cn.vonce.sql.bean.Table;
import cn.vonce.sql.uitls.SqlBeanUtil;

import java.util.List;

/**
 * SqlBean数据库助手
 *
 * @author Jovi
 * @version 1.0
 * @email imjovi@qq.com
 * @date 2022/4/16 0:35
 */
public class SqlBeanHelper<T, ID> extends SqlBeanServiceImpl<T, ID> {

    private Context context;

    public SqlBeanHelper(Class<?> clazz, Context context, DatabaseHelper databaseHelper) {
        super(clazz, databaseHelper);
        this.context = context;
    }

    /**
     * 维护表结构
     */
    public void autoAlterTable() {
        List<String> classNames = PackageUtil.getClasses(context, clazz.getPackage().getName());
        try {
            for (String className : classNames) {
                Class<?> clazz = Class.forName(className);
                SqlTable sqlTable = SqlBeanUtil.getSqlTable(clazz);
                Table table = SqlBeanUtil.getTable(clazz);
                //更新表结构
                if (sqlTable.autoAlter()) {
                    super.alter(table, super.getColumnInfoList(table.getName()));
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.e("sqlbean", e.getMessage(), e);
        }
    }

}
