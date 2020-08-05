package cn.vonce.sql.android.service;

import android.util.Log;
import cn.vonce.sql.android.helper.DatabaseHelper;
import cn.vonce.sql.android.helper.SQLiteTemplate;
import cn.vonce.sql.android.mapper.SqlBeanMapper;
import cn.vonce.sql.bean.*;
import cn.vonce.sql.config.SqlBeanConfig;
import cn.vonce.sql.enumerate.DbType;
import cn.vonce.sql.provider.SqlBeanProvider;
import cn.vonce.sql.service.SqlBeanService;
import cn.vonce.sql.service.TableService;
import cn.vonce.sql.uitls.SqlBeanUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * 通用的业务实现
 *
 * @param <T>
 * @author Jovi
 * @version 1.0
 * @email 766255988@qq.com
 * @date 2019年5月22日下午16:20:12
 */
public class SqlBeanServiceImpl<T, ID> extends SqlBeanProvider implements SqlBeanService<T, ID> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private SQLiteTemplate sqliteTemplate;

    private TableService tableService;

    private final static SqlBeanConfig sqlBeanConfig = new SqlBeanConfig(DbType.SQLite);

    public Class<?> clazz;

    public SqlBeanServiceImpl(Class<?> clazz, DatabaseHelper databaseHelper) {
        this(databaseHelper);
        this.clazz = clazz;
    }

    public SqlBeanServiceImpl(DatabaseHelper databaseHelper) {
        sqliteTemplate = new SQLiteTemplate(databaseHelper.getWritableDatabase());
        if (this.clazz == null) {
            Type[] typeArray = new Type[]{getClass().getGenericSuperclass()};
            if (typeArray == null || typeArray.length == 0) {
                typeArray = getClass().getGenericInterfaces();
            }
            for (Type type : typeArray) {
                if (type instanceof ParameterizedType) {
                    Class<?> trueTypeClass = (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0];
                    try {
                        clazz = this.getClass().getClassLoader().loadClass(trueTypeClass.getName());
                        return;
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public SQLiteTemplate getSQLiteTemplate() {
        return sqliteTemplate;
    }

    @Override
    public Class<?> getBeanClass() {
        return clazz;
    }

    @Override
    public T selectById(ID id) {
        if (id == null) {
            return null;
        }
        try {
            return sqliteTemplate.queryForObject(super.selectByIdSql(sqlBeanConfig, clazz, id),
                    new SqlBeanMapper<T>(clazz, clazz));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <O> O selectById(Class<O> returnType, ID id) {
        if (id == null) {
            return null;
        }
        try {
            if (!SqlBeanUtil.isBaseType(returnType.getName()) && !SqlBeanUtil.isMap(returnType.getName())) {
                clazz = returnType;
            }
            return sqliteTemplate.queryForObject(super.selectByIdSql(sqlBeanConfig, clazz, id),
                    new SqlBeanMapper<O>(clazz, returnType));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<T> selectByIds(ID... ids) {
        if (ids == null || ids.length == 0) {
            return null;
        }
        try {
            return sqliteTemplate.query(super.selectByIdsSql(sqlBeanConfig, clazz, ids),
                    new SqlBeanMapper<T>(clazz, clazz));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <O> List<O> selectByIds(Class<O> returnType, ID... ids) {
        if (ids == null || ids.length == 0) {
            return null;
        }
        try {
            if (!SqlBeanUtil.isBaseType(returnType.getName()) && !SqlBeanUtil.isMap(returnType.getName())) {
                clazz = returnType;
            }
            return sqliteTemplate.query(super.selectByIdsSql(sqlBeanConfig, clazz, ids),
                    new SqlBeanMapper<O>(clazz, returnType));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }


    @Override
    public T selectOne(Select select) {
        try {
            return sqliteTemplate.queryForObject(super.selectSql(sqlBeanConfig, clazz, select),
                    new SqlBeanMapper<T>(clazz, clazz));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }


    @Override
    public <O> O selectOne(Class<O> returnType, Select select) {
        try {
            if (!SqlBeanUtil.isBaseType(returnType.getName()) && !SqlBeanUtil.isMap(returnType.getName())) {
                clazz = returnType;
            }
            return sqliteTemplate.queryForObject(super.selectSql(sqlBeanConfig, clazz, select),
                    new SqlBeanMapper<O>(clazz, returnType));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> selectMap(Select select) {
        try {
            return sqliteTemplate.queryForObject(super.selectSql(sqlBeanConfig, clazz, select),
                    new SqlBeanMapper<Map<String, Object>>(clazz, Map.class));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public T selectOneByCondition(String where, Object... args) {
        try {
            return sqliteTemplate.queryForObject(super.selectByConditionSql(sqlBeanConfig, clazz, null, where, args),
                    new SqlBeanMapper<T>(clazz, clazz));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <O> O selectOneByCondition(Class<O> returnType, String where, Object... args) {
        try {
            if (!SqlBeanUtil.isBaseType(returnType.getName()) && !SqlBeanUtil.isMap(returnType.getName())) {
                clazz = returnType;
            }
            return sqliteTemplate.queryForObject(super.selectByConditionSql(sqlBeanConfig, clazz, null, where, args),
                    new SqlBeanMapper<O>(clazz, returnType));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <O> List<O> selectByCondition(Class<O> returnType, String where, Object... args) {
        try {
            if (!SqlBeanUtil.isBaseType(returnType.getName()) && !SqlBeanUtil.isMap(returnType.getName())) {
                clazz = returnType;
            }
            return sqliteTemplate.query(super.selectByConditionSql(sqlBeanConfig, clazz, null, where, args),
                    new SqlBeanMapper<O>(clazz, returnType));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <O> List<O> selectByCondition(Class<O> returnType, Paging paging, String where, Object... args) {
        try {
            if (!SqlBeanUtil.isBaseType(returnType.getName()) && !SqlBeanUtil.isMap(returnType.getName())) {
                clazz = returnType;
            }
            return sqliteTemplate.query(super.selectByConditionSql(sqlBeanConfig, clazz, paging, where, args),
                    new SqlBeanMapper<O>(clazz, returnType));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<T> selectByCondition(String where, Object... args) {
        try {
            return sqliteTemplate.query(super.selectByConditionSql(sqlBeanConfig, clazz, null, where, args),
                    new SqlBeanMapper<T>(clazz, clazz));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<T> selectByCondition(Paging paging, String where, Object... args) {
        try {
            return sqliteTemplate.query(super.selectByConditionSql(sqlBeanConfig, clazz, paging, where, args),
                    new SqlBeanMapper<T>(clazz, clazz));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    public long selectCountByCondition(String where, Object... args) {
        return sqliteTemplate.queryForObject(super.selectCountByConditionSql(sqlBeanConfig, clazz, where, args), new SqlBeanMapper<Long>(clazz, Long.class));
    }

    @Override
    public long countAll() {
        return sqliteTemplate.queryForObject(super.selectCountByConditionSql(sqlBeanConfig, clazz, null, null), new SqlBeanMapper<Long>(clazz, Long.class));
    }

    @Override
    public List<T> selectAll() {
        try {
            return sqliteTemplate.query(super.selectAllSql(sqlBeanConfig, clazz, null),
                    new SqlBeanMapper<T>(clazz, clazz));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<T> selectAll(Paging paging) {
        try {
            return sqliteTemplate.query(super.selectAllSql(sqlBeanConfig, clazz, paging),
                    new SqlBeanMapper<T>(clazz, clazz));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <O> List<O> selectAll(Class<O> returnType) {
        try {
            if (!SqlBeanUtil.isBaseType(returnType.getName()) && !SqlBeanUtil.isMap(returnType.getName())) {
                clazz = returnType;
            }
            return sqliteTemplate.query(super.selectAllSql(sqlBeanConfig, clazz, null),
                    new SqlBeanMapper<O>(clazz, returnType));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <O> List<O> selectAll(Class<O> returnType, Paging paging) {
        try {
            if (!SqlBeanUtil.isBaseType(returnType.getName()) && !SqlBeanUtil.isMap(returnType.getName())) {
                clazz = returnType;
            }
            return sqliteTemplate.query(super.selectAllSql(sqlBeanConfig, clazz, paging),
                    new SqlBeanMapper<O>(clazz, returnType));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> selectMapList(Select select) {
        try {
            return sqliteTemplate.query(super.selectSql(sqlBeanConfig, clazz, select),
                    new SqlBeanMapper<Map<String, Object>>(clazz, Map.class));
        } catch (
                Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }

    }

    @Override
    public <O> List<O> select(Class<O> returnType, Select select) {
        try {
            if (!SqlBeanUtil.isBaseType(returnType.getName()) && !SqlBeanUtil.isMap(returnType.getName())) {
                clazz = returnType;
            }
            return sqliteTemplate.query(super.selectSql(sqlBeanConfig, clazz, select),
                    new SqlBeanMapper<O>(clazz, returnType));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<T> select(Select select) {
        try {
            return sqliteTemplate.query(super.selectSql(sqlBeanConfig, clazz, select),
                    new SqlBeanMapper<T>(clazz, clazz));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public long count(Select select) {
        return sqliteTemplate.queryForObject(super.countSql(sqlBeanConfig, clazz, select), new SqlBeanMapper<Long>(clazz, Long.class));
    }

    @Override
    public long count(Class<?> clazz, Select select) {
        return sqliteTemplate.queryForObject(super.countSql(sqlBeanConfig, clazz, select), new SqlBeanMapper<Long>(clazz, Long.class));
    }

    @Override
    public long deleteById(ID... id) {
        return sqliteTemplate.update(super.deleteByIdSql(sqlBeanConfig, clazz, id));
    }

    @Override
    public long deleteByCondition(String where, Object... args) {
        return sqliteTemplate.update(super.deleteByConditionSql(sqlBeanConfig, clazz, where, args));
    }

    @Override
    public long delete(Delete delete) {
        return sqliteTemplate.update(super.deleteSql(sqlBeanConfig, clazz, delete, false));
    }

    @Override
    public long delete(Delete delete, boolean ignore) {
        return sqliteTemplate.update(super.deleteSql(sqlBeanConfig, clazz, delete, ignore));
    }

    @Override
    public long logicallyDeleteById(ID id) {
        return sqliteTemplate.update(super.logicallyDeleteByIdSql(sqlBeanConfig, clazz, id));
    }

    @Override
    public long logicallyDeleteByCondition(String where, Object... args) {
        return sqliteTemplate.update(super.logicallyDeleteByConditionSql(sqlBeanConfig, clazz, where, args));
    }

    @Override
    public long update(Update update) {
        return sqliteTemplate.update(super.updateSql(sqlBeanConfig, update, false));
    }

    @Override
    public long update(Update update, boolean ignore) {
        return sqliteTemplate.update(super.updateSql(sqlBeanConfig, update, ignore));
    }

    @Override
    public long updateById(T bean, ID id, boolean updateNotNull) {
        return sqliteTemplate.update(super.updateByIdSql(sqlBeanConfig, bean, id, updateNotNull, null));
    }

    @Override
    public long updateById(T bean, ID id, boolean updateNotNull, String[] filterFields) {
        return sqliteTemplate.update(super.updateByIdSql(sqlBeanConfig, bean, id, updateNotNull, filterFields));
    }

    @Override
    public long updateByBeanId(T bean, boolean updateNotNull) {
        return sqliteTemplate.update(super.updateByBeanIdSql(sqlBeanConfig, bean, updateNotNull, null));
    }

    @Override
    public long updateByBeanId(T bean, boolean updateNotNull, String[] filterFields) {
        return sqliteTemplate.update(super.updateByBeanIdSql(sqlBeanConfig, bean, updateNotNull, filterFields));
    }

    @Override
    public long updateByCondition(T bean, boolean updateNotNull, String where, Object... args) {
        return sqliteTemplate.update(super.updateByConditionSql(sqlBeanConfig, bean, updateNotNull, null, where, args));
    }

    @Override
    public long updateByCondition(T bean, boolean updateNotNull, String[] filterFields, String where, Object... args) {
        return sqliteTemplate.update(super.updateByConditionSql(sqlBeanConfig, bean, updateNotNull, filterFields, where, args));
    }

    @Override
    public long updateByBeanCondition(T bean, boolean updateNotNull, String where) {
        return sqliteTemplate.update(super.updateByBeanConditionSql(sqlBeanConfig, bean, updateNotNull, null, where));
    }

    @Override
    public long updateByBeanCondition(T bean, boolean updateNotNull, String[] filterFields, String where) {
        return sqliteTemplate.update(super.updateByBeanConditionSql(sqlBeanConfig, bean, updateNotNull, filterFields, where));
    }

    @Override
    public long insert(T... bean) {
        return sqliteTemplate.update(super.insertBeanSql(sqlBeanConfig, bean));
    }

    @Override
    public long insert(List<T> beanList) {
        return sqliteTemplate.update(super.insertBeanSql(sqlBeanConfig, beanList));
    }

    @Override
    public long inset(Insert insert) {
        return sqliteTemplate.update(super.insertBeanSql(sqlBeanConfig, insert));
    }

    @Override
    public TableService getTableService() {
        if (tableService == null) {
            tableService = new TableService() {
                @Override
                public void dropTable() {
                    sqliteTemplate.execSQL(SqlBeanServiceImpl.this.dropTableSql(clazz));
                }

                @Override
                public void createTable() {
                    dropTable();
                    sqliteTemplate.execSQL(SqlBeanServiceImpl.this.createTableSql(sqlBeanConfig, clazz));
                }

                @Override
                public void dropAndCreateTable() {
                    dropTable();
                    createTable();
                }

                @Override
                public List<String> getTableList() {
                    return sqliteTemplate.query(SqlBeanServiceImpl.this.selectTableListSql(sqlBeanConfig), new SqlBeanMapper<String>(clazz, String.class));
                }
            };
        }
        return tableService;
    }

}
