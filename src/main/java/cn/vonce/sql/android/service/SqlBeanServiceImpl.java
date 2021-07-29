package cn.vonce.sql.android.service;

import android.util.Log;
import cn.vonce.sql.android.helper.DatabaseHelper;
import cn.vonce.sql.android.helper.SQLiteTemplate;
import cn.vonce.sql.android.mapper.SqlBeanMapper;
import cn.vonce.sql.bean.*;
import cn.vonce.sql.config.SqlBeanConfig;
import cn.vonce.sql.config.SqlBeanDB;
import cn.vonce.sql.enumerate.DbType;
import cn.vonce.sql.helper.Wrapper;
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
public class SqlBeanServiceImpl<T, ID> implements SqlBeanService<T, ID>, TableService {


    private SQLiteTemplate sqliteTemplate;

    private TableService tableService;

    private SqlBeanDB sqlBeanDB;

    @Override
    public SqlBeanDB getSqlBeanDB() {
        if (sqlBeanDB == null) {
            SqlBeanDB sqlBeanDB = new SqlBeanDB();
            sqlBeanDB.setDbType(DbType.SQLite);
            sqlBeanDB.setSqlBeanConfig(new SqlBeanConfig());
        }
        return sqlBeanDB;
    }

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
    public void dropTable() {
        SqlBeanDB sqlBeanDB = getSqlBeanDB();
        if (sqlBeanDB.getDbType() != DbType.MySQL && sqlBeanDB.getDbType() != DbType.MariaDB && sqlBeanDB.getDbType() != DbType.PostgreSQL && sqlBeanDB.getDbType() != DbType.SQLServer) {
            List<String> nameList =  sqliteTemplate.query(SqlBeanProvider.selectTableListSql(getSqlBeanDB(), SqlBeanUtil.getTable(clazz).getName()), new SqlBeanMapper<String>(clazz, String.class));
            if (nameList == null || nameList.isEmpty()) {
                return;
            }
        }
        sqliteTemplate.execSQL(SqlBeanProvider.dropTableSql(getSqlBeanDB(), clazz));
    }

    @Override
    public void createTable() {
        sqliteTemplate.execSQL(SqlBeanProvider.createTableSql(getSqlBeanDB(), clazz));
    }

    @Override
    public void dropAndCreateTable() {
        dropTable();
        createTable();
    }

    @Override
    public List<String> getTableList() {
        return sqliteTemplate.query(SqlBeanProvider.selectTableListSql(getSqlBeanDB(), null), new SqlBeanMapper<String>(clazz, String.class));
    }

    @Override
    public String backup() {
        return null;
    }

    @Override
    public void backup(String targetTableName) {

    }

    @Override
    public void backup(String targetSchema, String targetTableName) {

    }

    @Override
    public void backup(String targetTableName, Column[] columns, Wrapper wrapper) {

    }

    @Override
    public void backup(String targetSchema, String targetTableName, Column[] columns, Wrapper wrapper) {

    }

    @Override
    public int copy(String targetTableName, Wrapper wrapper) {
        return 0;
    }

    @Override
    public int copy(String targetSchema, String targetTableName, Wrapper wrapper) {
        return 0;
    }

    @Override
    public int copy(String targetTableName, Column[] columns, Wrapper wrapper) {
        return 0;
    }

    @Override
    public int copy(String targetSchema, String targetTableName, Column[] columns, Wrapper wrapper) {
        return 0;
    }

    @Override
    public T selectById(ID id) {
        if (id == null) {
            return null;
        }
        try {
            return sqliteTemplate.queryForObject(SqlBeanProvider.selectByIdSql(getSqlBeanDB(), clazz, id),
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
            return sqliteTemplate.queryForObject(SqlBeanProvider.selectByIdSql(getSqlBeanDB(), clazz, id),
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
            return sqliteTemplate.query(SqlBeanProvider.selectByIdsSql(getSqlBeanDB(), clazz, ids),
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
            return sqliteTemplate.query(SqlBeanProvider.selectByIdsSql(getSqlBeanDB(), clazz, ids),
                    new SqlBeanMapper<O>(clazz, returnType));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }


    @Override
    public T selectOne(Select select) {
        try {
            return sqliteTemplate.queryForObject(SqlBeanProvider.selectSql(getSqlBeanDB(), clazz, select),
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
            return sqliteTemplate.queryForObject(SqlBeanProvider.selectSql(getSqlBeanDB(), clazz, select),
                    new SqlBeanMapper<O>(clazz, returnType));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> selectMap(Select select) {
        try {
            return sqliteTemplate.queryForObject(SqlBeanProvider.selectSql(getSqlBeanDB(), clazz, select),
                    new SqlBeanMapper<Map<String, Object>>(clazz, Map.class));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public T selectOneByCondition(String where, Object... args) {
        try {
            return sqliteTemplate.queryForObject(SqlBeanProvider.selectByConditionSql(getSqlBeanDB(), clazz, null, where, args),
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
            return sqliteTemplate.queryForObject(SqlBeanProvider.selectByConditionSql(getSqlBeanDB(), clazz, null, where, args),
                    new SqlBeanMapper<O>(clazz, returnType));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public T selectOneByCondition(Wrapper where) {
        return null;
    }

    @Override
    public <O> O selectOneByCondition(Class<O> returnType, Wrapper where) {
        return null;
    }

    @Override
    public <O> List<O> selectByCondition(Class<O> returnType, String where, Object... args) {
        try {
            if (!SqlBeanUtil.isBaseType(returnType.getName()) && !SqlBeanUtil.isMap(returnType.getName())) {
                clazz = returnType;
            }
            return sqliteTemplate.query(SqlBeanProvider.selectByConditionSql(getSqlBeanDB(), clazz, null, where, args),
                    new SqlBeanMapper<O>(clazz, returnType));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <O> List<O> selectByCondition(Class<O> returnType, Wrapper where) {
        return null;
    }

    @Override
    public <O> List<O> selectByCondition(Class<O> returnType, Paging paging, String where, Object... args) {
        try {
            if (!SqlBeanUtil.isBaseType(returnType.getName()) && !SqlBeanUtil.isMap(returnType.getName())) {
                clazz = returnType;
            }
            return sqliteTemplate.query(SqlBeanProvider.selectByConditionSql(getSqlBeanDB(), clazz, paging, where, args),
                    new SqlBeanMapper<O>(clazz, returnType));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <O> List<O> selectByCondition(Class<O> returnType, Paging paging, Wrapper where) {
        return null;
    }

    @Override
    public List<T> selectByCondition(String where, Object... args) {
        try {
            return sqliteTemplate.query(SqlBeanProvider.selectByConditionSql(getSqlBeanDB(), clazz, null, where, args),
                    new SqlBeanMapper<T>(clazz, clazz));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<T> selectByCondition(Wrapper where) {
        return null;
    }

    @Override
    public List<T> selectByCondition(Paging paging, String where, Object... args) {
        try {
            return sqliteTemplate.query(SqlBeanProvider.selectByConditionSql(getSqlBeanDB(), clazz, paging, where, args),
                    new SqlBeanMapper<T>(clazz, clazz));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<T> selectByCondition(Paging paging, Wrapper where) {
        return null;
    }

    public int selectCountByCondition(String where, Object... args) {
        return sqliteTemplate.queryForObject(SqlBeanProvider.selectCountByConditionSql(getSqlBeanDB(), clazz, where, args), new SqlBeanMapper<Integer>(clazz, Long.class));
    }

    @Override
    public int selectCountByCondition(Wrapper where) {
        return 0;
    }

    @Override
    public int countAll() {
        return sqliteTemplate.queryForObject(SqlBeanProvider.selectCountByConditionSql(getSqlBeanDB(), clazz, null, null), new SqlBeanMapper<Integer>(clazz, Long.class));
    }

    @Override
    public List<T> selectAll() {
        try {
            return sqliteTemplate.query(SqlBeanProvider.selectAllSql(getSqlBeanDB(), clazz, null),
                    new SqlBeanMapper<T>(clazz, clazz));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<T> selectAll(Paging paging) {
        try {
            return sqliteTemplate.query(SqlBeanProvider.selectAllSql(getSqlBeanDB(), clazz, paging),
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
            return sqliteTemplate.query(SqlBeanProvider.selectAllSql(getSqlBeanDB(), clazz, null),
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
            return sqliteTemplate.query(SqlBeanProvider.selectAllSql(getSqlBeanDB(), clazz, paging),
                    new SqlBeanMapper<O>(clazz, returnType));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> selectMapList(Select select) {
        try {
            return sqliteTemplate.query(SqlBeanProvider.selectSql(getSqlBeanDB(), clazz, select),
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
            return sqliteTemplate.query(SqlBeanProvider.selectSql(getSqlBeanDB(), clazz, select),
                    new SqlBeanMapper<O>(clazz, returnType));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<T> select(Select select) {
        try {
            return sqliteTemplate.query(SqlBeanProvider.selectSql(getSqlBeanDB(), clazz, select),
                    new SqlBeanMapper<T>(clazz, clazz));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public int count(Select select) {
        return sqliteTemplate.queryForObject(SqlBeanProvider.countSql(getSqlBeanDB(), clazz, select), new SqlBeanMapper<Integer>(clazz, Long.class));
    }

    @Override
    public int count(Class<?> clazz, Select select) {
        return sqliteTemplate.queryForObject(SqlBeanProvider.countSql(getSqlBeanDB(), clazz, select), new SqlBeanMapper<Integer>(clazz, Long.class));
    }

    @Override
    public int deleteById(ID... id) {
        return sqliteTemplate.update(SqlBeanProvider.deleteByIdSql(getSqlBeanDB(), clazz, id));
    }

    @Override
    public int deleteByCondition(String where, Object... args) {
        return sqliteTemplate.update(SqlBeanProvider.deleteByConditionSql(getSqlBeanDB(), clazz, where, args));
    }

    @Override
    public int deleteByCondition(Wrapper where) {
        return 0;
    }

    @Override
    public int delete(Delete delete) {
        return sqliteTemplate.update(SqlBeanProvider.deleteSql(getSqlBeanDB(), clazz, delete, false));
    }

    @Override
    public int delete(Delete delete, boolean ignore) {
        return sqliteTemplate.update(SqlBeanProvider.deleteSql(getSqlBeanDB(), clazz, delete, ignore));
    }

    @Override
    public int logicallyDeleteById(ID... id) {
        return sqliteTemplate.update(SqlBeanProvider.logicallyDeleteByIdSql(getSqlBeanDB(), clazz, id));
    }

    @Override
    public int logicallyDeleteByCondition(String where, Object... args) {
        return sqliteTemplate.update(SqlBeanProvider.logicallyDeleteByConditionSql(getSqlBeanDB(), clazz, where, args));
    }

    @Override
    public int logicallyDeleteByCondition(Wrapper where) {
        return 0;
    }

    @Override
    public int update(Update update) {
        return sqliteTemplate.update(SqlBeanProvider.updateSql(getSqlBeanDB(), clazz, update, false));
    }

    @Override
    public int update(Update update, boolean ignore) {
        return sqliteTemplate.update(SqlBeanProvider.updateSql(getSqlBeanDB(), clazz, update, ignore));
    }

    @Override
    public int updateById(T bean, ID id, boolean updateNotNull, boolean optimisticLock) {
        return sqliteTemplate.update(SqlBeanProvider.updateByIdSql(getSqlBeanDB(), clazz, bean, id, updateNotNull, optimisticLock, null));
    }

    @Override
    public int updateById(T bean, ID id, boolean updateNotNull, boolean optimisticLock, String[] filterFields) {
        return sqliteTemplate.update(SqlBeanProvider.updateByIdSql(getSqlBeanDB(), clazz, bean, id, updateNotNull, optimisticLock, filterFields));
    }

    @Override
    public int updateByBeanId(T bean, boolean updateNotNull, boolean optimisticLock) {
        return sqliteTemplate.update(SqlBeanProvider.updateByBeanIdSql(getSqlBeanDB(), clazz, bean, updateNotNull, optimisticLock, null));
    }

    @Override
    public int updateByBeanId(T bean, boolean updateNotNull, boolean optimisticLock, String[] filterFields) {
        return sqliteTemplate.update(SqlBeanProvider.updateByBeanIdSql(getSqlBeanDB(), clazz, bean, updateNotNull, optimisticLock, filterFields));
    }

    @Override
    public int updateByCondition(T bean, boolean updateNotNull, boolean optimisticLock, String where, Object... args) {
        return sqliteTemplate.update(SqlBeanProvider.updateByConditionSql(getSqlBeanDB(), clazz, bean, updateNotNull, optimisticLock, null, where, args));
    }

    @Override
    public int updateByCondition(T bean, boolean updateNotNull, boolean optimisticLock, Wrapper where) {
        Update update = new Update();
        update.setUpdateBean(bean);
        update.setUpdateNotNull(updateNotNull);
        update.setOptimisticLock(optimisticLock);
        update.setWhere(where);
        return sqliteTemplate.update(SqlBeanProvider.updateSql(getSqlBeanDB(), clazz, update, false));
    }

    @Override
    public int updateByCondition(T bean, boolean updateNotNull, boolean optimisticLock, String[] filterFields, String where, Object... args) {
        return sqliteTemplate.update(SqlBeanProvider.updateByConditionSql(getSqlBeanDB(), clazz, bean, updateNotNull, optimisticLock, filterFields, where, args));
    }

    @Override
    public int updateByCondition(T bean, boolean updateNotNull, boolean optimisticLock, String[] filterFields, Wrapper where) {
        Update update = new Update();
        update.setUpdateBean(bean);
        update.setUpdateNotNull(updateNotNull);
        update.setOptimisticLock(optimisticLock);
        update.setFilterFields(filterFields);
        update.setWhere(where);
        return sqliteTemplate.update(SqlBeanProvider.updateSql(getSqlBeanDB(), clazz, update, false));
    }

    @Override
    public int updateByBeanCondition(T bean, boolean updateNotNull, boolean optimisticLock, String where) {
        return sqliteTemplate.update(SqlBeanProvider.updateByBeanConditionSql(getSqlBeanDB(), clazz, bean, updateNotNull, optimisticLock, null, where));
    }

    @Override
    public int updateByBeanCondition(T bean, boolean updateNotNull, boolean optimisticLock, String[] filterFields, String where) {
        return sqliteTemplate.update(SqlBeanProvider.updateByBeanConditionSql(getSqlBeanDB(), clazz, bean, updateNotNull, optimisticLock, filterFields, where));
    }

    @Override
    public int insert(T... bean) {
        return sqliteTemplate.update(SqlBeanProvider.insertBeanSql(getSqlBeanDB(), clazz, bean));
    }

    @Override
    public int insert(List<T> beanList) {
        return sqliteTemplate.update(SqlBeanProvider.insertBeanSql(getSqlBeanDB(), clazz, beanList));
    }

    @Override
    public int insert(Insert insert) {
        return sqliteTemplate.update(SqlBeanProvider.insertBeanSql(getSqlBeanDB(), clazz, insert));
    }

}
