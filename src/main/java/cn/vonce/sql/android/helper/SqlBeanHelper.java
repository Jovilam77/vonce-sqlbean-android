package cn.vonce.sql.android.helper;


import cn.vonce.sql.android.service.SqlBeanServiceImpl;

/**
 * 生成表字段常量处理器
 *
 * @author Jovi
 * @version 1.0
 * @email imjovi@qq.com
 * @date 2022/4/16 0:35
 */
public class SqlBeanHelper<T, ID> extends SqlBeanServiceImpl<T, ID> {


    public SqlBeanHelper(Class<?> clazz, DatabaseHelper databaseHelper) {
        super(clazz, databaseHelper);
    }

    public SqlBeanHelper(DatabaseHelper databaseHelper) {
        super(databaseHelper);
    }
}
