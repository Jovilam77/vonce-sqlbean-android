package cn.vonce.sql.android.service;

import android.util.Log;
import cn.vonce.sql.android.helper.DatabaseHelper;
import cn.vonce.sql.android.helper.SQLiteTemplate;
import cn.vonce.sql.android.mapper.SqlBeanMapper;
import cn.vonce.sql.bean.*;
import cn.vonce.sql.config.SqlBeanConfig;
import cn.vonce.sql.config.SqlBeanDB;
import cn.vonce.sql.enumerate.DbType;
import cn.vonce.sql.exception.SqlBeanException;
import cn.vonce.sql.helper.Wrapper;
import cn.vonce.sql.page.PageHelper;
import cn.vonce.sql.page.ResultData;
import cn.vonce.sql.provider.SqlBeanProvider;
import cn.vonce.sql.service.SqlBeanService;
import cn.vonce.sql.service.TableService;
import cn.vonce.sql.uitls.DateUtil;
import cn.vonce.sql.uitls.SqlBeanUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 通用的业务实现
 *
 * @param <T>
 * @author Jovi
 * @version 1.0
 * @email imjovi@qq.com
 * @date 2019年5月22日下午16:20:12
 */
public class SqlBeanServiceImpl<T, ID> implements SqlBeanService<T, ID>, TableService {


    private SQLiteTemplate sqliteTemplate;

    private SqlBeanDB sqlBeanDB;

    @Override
    public SqlBeanDB getSqlBeanDB() {
        if (sqlBeanDB == null) {
            sqlBeanDB = new SqlBeanDB();
            sqlBeanDB.setDbType(DbType.SQLite);
            sqlBeanDB.setSqlBeanConfig(new SqlBeanConfig());
        }
        return sqlBeanDB;
    }

    public Class<?> clazz;

    public SqlBeanServiceImpl() {
    }

    public SqlBeanServiceImpl(Class<?> clazz, DatabaseHelper databaseHelper) {
        this.clazz = clazz;
        sqliteTemplate = new SQLiteTemplate(databaseHelper.getWritableDatabase());
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
            List<String> nameList = sqliteTemplate.query(SqlBeanProvider.selectTableListSql(getSqlBeanDB(), SqlBeanUtil.getTable(clazz).getName()), new SqlBeanMapper<String>(clazz, String.class));
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
    public List<TableInfo> getTableList(String tableName) {
        return sqliteTemplate.query(SqlBeanProvider.selectTableListSql(getSqlBeanDB(), null), new SqlBeanMapper<TableInfo>(clazz, TableInfo.class));
    }

    @Override
    public List<ColumnInfo> getColumnInfoList(String tableName) {
        return sqliteTemplate.query(SqlBeanProvider.selectTableListSql(getSqlBeanDB(), null), new SqlBeanMapper<ColumnInfo>(clazz, ColumnInfo.class));
    }

    @Override
    public String backup() {
        String targetTableName = SqlBeanUtil.getTable(clazz).getName() + "_" + DateUtil.dateToString(new Date(), "yyyyMMddHHmmssSSS");
        sqliteTemplate.update(SqlBeanProvider.backupSql(getSqlBeanDB(), clazz, null, targetTableName, null, null));
        return targetTableName;
    }

    @Override
    public void backup(String targetTableName) {
        sqliteTemplate.update(SqlBeanProvider.backupSql(getSqlBeanDB(), clazz, null, targetTableName, null, null));
    }

    @Override
    public void backup(String targetSchema, String targetTableName) {
        sqliteTemplate.update(SqlBeanProvider.backupSql(getSqlBeanDB(), clazz, targetSchema, targetTableName, null, null));
    }

    @Override
    public void backup(String targetTableName, Column[] columns, Wrapper wrapper) {
        sqliteTemplate.update(SqlBeanProvider.backupSql(getSqlBeanDB(), clazz, null, targetTableName, columns, null));
    }

    @Override
    public void backup(String targetSchema, String targetTableName, Column[] columns, Wrapper wrapper) {
        sqliteTemplate.update(SqlBeanProvider.backupSql(getSqlBeanDB(), clazz, targetSchema, targetTableName, columns, null));
    }

    @Override
    public int copy(String targetTableName, Wrapper wrapper) {
        return sqliteTemplate.update(SqlBeanProvider.copySql(getSqlBeanDB(), clazz, null, targetTableName, null, wrapper));
    }

    @Override
    public int copy(String targetSchema, String targetTableName, Wrapper wrapper) {
        return sqliteTemplate.update(SqlBeanProvider.copySql(getSqlBeanDB(), clazz, targetSchema, targetTableName, null, wrapper));
    }

    @Override
    public int copy(String targetTableName, Column[] columns, Wrapper wrapper) {
        return sqliteTemplate.update(SqlBeanProvider.copySql(getSqlBeanDB(), clazz, null, targetTableName, columns, wrapper));
    }

    @Override
    public int copy(String targetSchema, String targetTableName, Column[] columns, Wrapper wrapper) {
        return sqliteTemplate.update(SqlBeanProvider.copySql(getSqlBeanDB(), clazz, targetSchema, targetTableName, columns, wrapper));
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
    public <R> R selectById(Class<R> returnType, ID id) {
        if (id == null) {
            return null;
        }
        try {
            if (!SqlBeanUtil.isBaseType(returnType.getName()) && !SqlBeanUtil.isMap(returnType.getName())) {
                clazz = returnType;
            }
            return sqliteTemplate.queryForObject(SqlBeanProvider.selectByIdSql(getSqlBeanDB(), clazz, id),
                    new SqlBeanMapper<R>(clazz, returnType));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<T> selectByIds(ID... ids) {
        if (ids == null || ids.length == 0) {
            throw new SqlBeanException("selectByIds方法ids参数至少拥有一个值");
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
    public <R> List<R> selectByIds(Class<R> returnType, ID... ids) {
        if (ids == null || ids.length == 0) {
            throw new SqlBeanException("selectByIds方法ids参数至少拥有一个值");
        }
        try {
            if (!SqlBeanUtil.isBaseType(returnType.getName()) && !SqlBeanUtil.isMap(returnType.getName())) {
                clazz = returnType;
            }
            return sqliteTemplate.query(SqlBeanProvider.selectByIdsSql(getSqlBeanDB(), clazz, ids),
                    new SqlBeanMapper<R>(clazz, returnType));
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
    public <R> R selectOne(Class<R> returnType, Select select) {
        try {
            if (!SqlBeanUtil.isBaseType(returnType.getName()) && !SqlBeanUtil.isMap(returnType.getName())) {
                clazz = returnType;
            }
            return sqliteTemplate.queryForObject(SqlBeanProvider.selectSql(getSqlBeanDB(), clazz, select),
                    new SqlBeanMapper<R>(clazz, returnType));
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

    @Deprecated
    @Override
    public T selectOneByCondition(String where, Object... args) {
        return selectOneBy(where, args);
    }

    @Override
    public T selectOneBy(String where, Object... args) {
        try {
            return sqliteTemplate.queryForObject(SqlBeanProvider.selectByConditionSql(getSqlBeanDB(), clazz, null, where, args),
                    new SqlBeanMapper<T>(clazz, clazz));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Deprecated
    @Override
    public <R> R selectOneByCondition(Class<R> returnType, String where, Object... args) {
        return selectOneBy(returnType, where, args);
    }

    @Override
    public <R> R selectOneBy(Class<R> returnType, String where, Object... args) {
        try {
            if (!SqlBeanUtil.isBaseType(returnType.getName()) && !SqlBeanUtil.isMap(returnType.getName())) {
                clazz = returnType;
            }
            return sqliteTemplate.queryForObject(SqlBeanProvider.selectByConditionSql(getSqlBeanDB(), clazz, null, where, args),
                    new SqlBeanMapper<R>(clazz, returnType));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Deprecated
    @Override
    public T selectOneByCondition(Wrapper where) {
        return selectOneBy(where);
    }

    @Override
    public T selectOneBy(Wrapper where) {
        Select select = new Select();
        select.setWhere(where);
        return sqliteTemplate.queryForObject(SqlBeanProvider.selectSql(getSqlBeanDB(), clazz, select), new SqlBeanMapper<T>(clazz, clazz));
    }

    @Deprecated
    @Override
    public <R> R selectOneByCondition(Class<R> returnType, Wrapper where) {
        return selectOneBy(returnType, where);
    }

    @Override
    public <R> R selectOneBy(Class<R> returnType, Wrapper where) {
        Select select = new Select();
        select.setWhere(where);
        try {
            if (!SqlBeanUtil.isBaseType(returnType.getName()) && !SqlBeanUtil.isMap(returnType.getName())) {
                clazz = returnType;
            }
            return sqliteTemplate.queryForObject(SqlBeanProvider.selectSql(getSqlBeanDB(), clazz, select), new SqlBeanMapper<R>(clazz, clazz));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Deprecated
    @Override
    public <R> List<R> selectByCondition(Class<R> returnType, String where, Object... args) {
        return selectBy(returnType, where, args);
    }

    @Override
    public <R> List<R> selectBy(Class<R> returnType, String where, Object... args) {
        try {
            if (!SqlBeanUtil.isBaseType(returnType.getName()) && !SqlBeanUtil.isMap(returnType.getName())) {
                clazz = returnType;
            }
            return sqliteTemplate.query(SqlBeanProvider.selectByConditionSql(getSqlBeanDB(), clazz, null, where, args),
                    new SqlBeanMapper<R>(clazz, returnType));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Deprecated
    @Override
    public <R> List<R> selectByCondition(Class<R> returnType, Wrapper where) {
        return selectBy(returnType, where);
    }

    @Override
    public <R> List<R> selectBy(Class<R> returnType, Wrapper where) {
        Select select = new Select();
        select.setWhere(where);
        try {
            if (!SqlBeanUtil.isBaseType(returnType.getName()) && !SqlBeanUtil.isMap(returnType.getName())) {
                clazz = returnType;
            }
            return sqliteTemplate.query(SqlBeanProvider.selectSql(getSqlBeanDB(), clazz, select), new SqlBeanMapper<R>(clazz, clazz));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Deprecated
    @Override
    public <R> List<R> selectByCondition(Class<R> returnType, Paging paging, String where, Object... args) {
        return selectBy(returnType, paging, where, args);
    }

    @Override
    public <R> List<R> selectBy(Class<R> returnType, Paging paging, String where, Object... args) {
        try {
            if (!SqlBeanUtil.isBaseType(returnType.getName()) && !SqlBeanUtil.isMap(returnType.getName())) {
                clazz = returnType;
            }
            return sqliteTemplate.query(SqlBeanProvider.selectByConditionSql(getSqlBeanDB(), clazz, paging, where, args),
                    new SqlBeanMapper<R>(clazz, returnType));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Deprecated
    @Override
    public <R> List<R> selectByCondition(Class<R> returnType, Paging paging, Wrapper where) {
        return selectBy(returnType, paging, where);
    }

    @Override
    public <R> List<R> selectBy(Class<R> returnType, Paging paging, Wrapper where) {
        Select select = new Select();
        select.setWhere(where);
        select.setPage(paging.getPagenum(), paging.getPagesize(), paging.getStartByZero());
        select.orderBy(paging.getOrders());
        try {
            if (!SqlBeanUtil.isBaseType(returnType.getName()) && !SqlBeanUtil.isMap(returnType.getName())) {
                clazz = returnType;
            }
            return sqliteTemplate.query(SqlBeanProvider.selectSql(getSqlBeanDB(), clazz, select), new SqlBeanMapper<R>(clazz, clazz));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Deprecated
    @Override
    public List<T> selectByCondition(String where, Object... args) {
        return selectBy(where, args);
    }

    @Override
    public List<T> selectBy(String where, Object... args) {
        try {
            return sqliteTemplate.query(SqlBeanProvider.selectByConditionSql(getSqlBeanDB(), clazz, null, where, args),
                    new SqlBeanMapper<T>(clazz, clazz));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Deprecated
    @Override
    public List<T> selectByCondition(Wrapper where) {
        return selectBy(where);
    }

    @Override
    public List<T> selectBy(Wrapper where) {
        Select select = new Select();
        select.setWhere(where);
        return sqliteTemplate.query(SqlBeanProvider.selectSql(getSqlBeanDB(), clazz, select), new SqlBeanMapper<T>(clazz, clazz));
    }

    @Deprecated
    @Override
    public List<T> selectByCondition(Paging paging, String where, Object... args) {
        return selectBy(paging, where, args);
    }

    @Override
    public List<T> selectBy(Paging paging, String where, Object... args) {
        try {
            return sqliteTemplate.query(SqlBeanProvider.selectByConditionSql(getSqlBeanDB(), clazz, paging, where, args),
                    new SqlBeanMapper<T>(clazz, clazz));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Deprecated
    @Override
    public List<T> selectByCondition(Paging paging, Wrapper where) {
        return selectBy(paging, where);
    }

    @Override
    public List<T> selectBy(Paging paging, Wrapper where) {
        Select select = new Select();
        select.setWhere(where);
        select.setPage(paging.getPagenum(), paging.getPagesize(), paging.getStartByZero());
        select.orderBy(paging.getOrders());
        return sqliteTemplate.query(SqlBeanProvider.selectSql(getSqlBeanDB(), clazz, select), new SqlBeanMapper<T>(clazz, clazz));
    }

    @Deprecated
    @Override
    public int selectCountByCondition(String where, Object... args) {
        return countBy(where, args);
    }

    @Override
    public int countBy(String where, Object... args) {
        return sqliteTemplate.queryForObject(SqlBeanProvider.selectCountByConditionSql(getSqlBeanDB(), clazz, where, args), new SqlBeanMapper<Integer>(clazz, Integer.class));
    }

    @Deprecated
    @Override
    public int selectCountByCondition(Wrapper where) {
        return countBy(where);
    }

    @Override
    public int countBy(Wrapper where) {
        Select select = new Select();
        select.setWhere(where);
        return sqliteTemplate.queryForObject(SqlBeanProvider.countSql(getSqlBeanDB(), clazz, select), new SqlBeanMapper<>(clazz, Integer.class));
    }

    @Deprecated
    @Override
    public int countAll() {
        return count();
    }

    @Override
    public int count() {
        return sqliteTemplate.queryForObject(SqlBeanProvider.selectCountByConditionSql(getSqlBeanDB(), clazz, null, null), new SqlBeanMapper<Integer>(clazz, Integer.class));
    }

    @Deprecated
    @Override
    public List<T> selectAll() {
        return select();
    }

    @Override
    public List<T> select() {
        try {
            return sqliteTemplate.query(SqlBeanProvider.selectAllSql(getSqlBeanDB(), clazz, null),
                    new SqlBeanMapper<T>(clazz, clazz));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Deprecated
    @Override
    public List<T> selectAll(Paging paging) {
        return select(paging);
    }

    @Override
    public List<T> select(Paging paging) {
        try {
            return sqliteTemplate.query(SqlBeanProvider.selectAllSql(getSqlBeanDB(), clazz, paging),
                    new SqlBeanMapper<T>(clazz, clazz));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Deprecated
    @Override
    public <R> List<R> selectAll(Class<R> returnType) {
        return select(returnType);
    }

    @Override
    public <R> List<R> select(Class<R> returnType) {
        try {
            if (!SqlBeanUtil.isBaseType(returnType.getName()) && !SqlBeanUtil.isMap(returnType.getName())) {
                clazz = returnType;
            }
            return sqliteTemplate.query(SqlBeanProvider.selectAllSql(getSqlBeanDB(), clazz, null),
                    new SqlBeanMapper<R>(clazz, returnType));
        } catch (Exception e) {
            Log.e("sqlbean", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <R> List<R> selectAll(Class<R> returnType, Paging paging) {
        return select(returnType, paging);
    }

    @Override
    public <R> List<R> select(Class<R> returnType, Paging paging) {
        try {
            if (!SqlBeanUtil.isBaseType(returnType.getName()) && !SqlBeanUtil.isMap(returnType.getName())) {
                clazz = returnType;
            }
            return sqliteTemplate.query(SqlBeanProvider.selectAllSql(getSqlBeanDB(), clazz, paging),
                    new SqlBeanMapper<R>(clazz, returnType));
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
    public <R> List<R> select(Class<R> returnType, Select select) {
        try {
            if (!SqlBeanUtil.isBaseType(returnType.getName()) && !SqlBeanUtil.isMap(returnType.getName())) {
                clazz = returnType;
            }
            return sqliteTemplate.query(SqlBeanProvider.selectSql(getSqlBeanDB(), clazz, select),
                    new SqlBeanMapper<R>(clazz, returnType));
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
        return sqliteTemplate.queryForObject(SqlBeanProvider.countSql(getSqlBeanDB(), clazz, select), new SqlBeanMapper<Integer>(clazz, Integer.class));
    }

    @Override
    public int count(Class<?> clazz, Select select) {
        return sqliteTemplate.queryForObject(SqlBeanProvider.countSql(getSqlBeanDB(), clazz, select), new SqlBeanMapper<Integer>(clazz, Integer.class));
    }

    @Override
    public ResultData<T> paging(Select select, PageHelper<T> pageHelper) {
        pageHelper.paging(select, this);
        return pageHelper.getResultData();
    }

    @Override
    public ResultData<T> paging(Select select, int pagenum, int pagesize) {
        PageHelper<T> pageHelper = new PageHelper<>(pagenum, pagesize);
        pageHelper.paging(select, this);
        return pageHelper.getResultData();
    }

    @Override
    public <R> ResultData<R> paging(Class<R> tClazz, Select select, PageHelper<R> pageHelper) {
        pageHelper.paging(tClazz, select, this);
        return pageHelper.getResultData();
    }

    @Override
    public <R> ResultData<R> paging(Class<R> tClazz, Select select, int pagenum, int pagesize) {
        PageHelper<R> pageHelper = new PageHelper<>(pagenum, pagesize);
        pageHelper.paging(tClazz, select, this);
        return pageHelper.getResultData();
    }

    @Override
    public int deleteById(ID... id) {
        if (id == null || id.length == 0) {
            throw new SqlBeanException("deleteById方法id参数至少拥有一个值");
        }
        return sqliteTemplate.update(SqlBeanProvider.deleteByIdSql(getSqlBeanDB(), clazz, id));
    }

    @Deprecated
    @Override
    public int deleteByCondition(String where, Object... args) {
        return deleteBy(where, args);
    }

    @Override
    public int deleteBy(String where, Object... args) {
        return sqliteTemplate.update(SqlBeanProvider.deleteByConditionSql(getSqlBeanDB(), clazz, where, args));
    }

    @Deprecated
    @Override
    public int deleteByCondition(Wrapper where) {
        return deleteBy(where);
    }

    @Override
    public int deleteBy(Wrapper where) {
        Delete delete = new Delete();
        delete.setWhere(where);
        return sqliteTemplate.update(SqlBeanProvider.deleteSql(getSqlBeanDB(), clazz, delete, false));
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
        if (id == null || id.length == 0) {
            throw new SqlBeanException("logicallyDeleteById方法id参数至少拥有一个值");
        }
        return sqliteTemplate.update(SqlBeanProvider.logicallyDeleteByIdSql(getSqlBeanDB(), clazz, id));
    }

    @Deprecated
    @Override
    public int logicallyDeleteByCondition(String where, Object... args) {
        return logicallyDeleteBy(where, args);
    }

    @Override
    public int logicallyDeleteBy(String where, Object... args) {
        return sqliteTemplate.update(SqlBeanProvider.logicallyDeleteByConditionSql(getSqlBeanDB(), clazz, where, args));
    }

    @Deprecated
    @Override
    public int logicallyDeleteByCondition(Wrapper where) {
        return logicallyDeleteBy(where);
    }

    @Override
    public int logicallyDeleteBy(Wrapper where) {
        return sqliteTemplate.update(SqlBeanProvider.logicallyDeleteByConditionSql(getSqlBeanDB(), clazz, where));
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
    public int updateById(T bean, ID id) {
        return sqliteTemplate.update(SqlBeanProvider.updateByIdSql(getSqlBeanDB(), clazz, bean, id, true, false, null));
    }

    @Override
    public int updateById(T bean, ID id, boolean updateNotNull, boolean optimisticLock) {
        return sqliteTemplate.update(SqlBeanProvider.updateByIdSql(getSqlBeanDB(), clazz, bean, id, updateNotNull, optimisticLock, null));
    }

    @Override
    public int updateByBeanId(T bean) {
        return sqliteTemplate.update(SqlBeanProvider.updateByBeanIdSql(getSqlBeanDB(), clazz, bean, true, false, null));
    }

    @Override
    public int updateById(T bean, ID id, boolean updateNotNull, boolean optimisticLock, String[] filterFields) {
        return sqliteTemplate.update(SqlBeanProvider.updateByIdSql(getSqlBeanDB(), clazz, bean, id, updateNotNull, optimisticLock, filterFields));
    }

    @Deprecated
    @Override
    public int updateByCondition(T bean, String where, Object... args) {
        return updateBy(bean, where, args);
    }

    @Override
    public int updateBy(T bean, String where, Object... args) {
        return sqliteTemplate.update(SqlBeanProvider.updateByConditionSql(getSqlBeanDB(), clazz, bean, true, false, null, where, args));
    }

    @Override
    public int updateByBeanId(T bean, boolean updateNotNull, boolean optimisticLock) {
        return sqliteTemplate.update(SqlBeanProvider.updateByBeanIdSql(getSqlBeanDB(), clazz, bean, updateNotNull, optimisticLock, null));
    }

    @Override
    public int updateByBeanId(T bean, boolean updateNotNull, boolean optimisticLock, String[] filterFields) {
        return sqliteTemplate.update(SqlBeanProvider.updateByBeanIdSql(getSqlBeanDB(), clazz, bean, updateNotNull, optimisticLock, filterFields));
    }

    @Deprecated
    @Override
    public int updateByCondition(T bean, boolean updateNotNull, boolean optimisticLock, String where, Object... args) {
        return updateBy(bean, updateNotNull, optimisticLock, where, args);
    }

    @Override
    public int updateBy(T bean, boolean updateNotNull, boolean optimisticLock, String where, Object... args) {
        return sqliteTemplate.update(SqlBeanProvider.updateByConditionSql(getSqlBeanDB(), clazz, bean, updateNotNull, optimisticLock, null, where, args));
    }

    @Deprecated
    @Override
    public int updateByCondition(T bean, Wrapper where) {
        return updateBy(bean, where);
    }

    @Override
    public int updateBy(T bean, Wrapper where) {
        Update update = new Update();
        update.setUpdateBean(bean);
        update.setUpdateNotNull(true);
        update.setOptimisticLock(false);
        update.setWhere(where);
        return sqliteTemplate.update(SqlBeanProvider.updateSql(getSqlBeanDB(), clazz, update, false));
    }

    @Deprecated
    @Override
    public int updateByCondition(T bean, boolean updateNotNull, boolean optimisticLock, Wrapper where) {
        return updateBy(bean, updateNotNull, optimisticLock, where);
    }

    @Override
    public int updateBy(T bean, boolean updateNotNull, boolean optimisticLock, Wrapper where) {
        Update update = new Update();
        update.setUpdateBean(bean);
        update.setUpdateNotNull(updateNotNull);
        update.setOptimisticLock(optimisticLock);
        update.setWhere(where);
        return sqliteTemplate.update(SqlBeanProvider.updateSql(getSqlBeanDB(), clazz, update, false));
    }

    @Deprecated
    @Override
    public int updateByCondition(T bean, boolean updateNotNull, boolean optimisticLock, String[] filterFields, String where, Object... args) {
        return updateBy(bean, updateNotNull, optimisticLock, filterFields, where, args);
    }

    @Override
    public int updateBy(T bean, boolean updateNotNull, boolean optimisticLock, String[] filterFields, String where, Object... args) {
        return sqliteTemplate.update(SqlBeanProvider.updateByConditionSql(getSqlBeanDB(), clazz, bean, updateNotNull, optimisticLock, filterFields, where, args));
    }

    @Deprecated
    @Override
    public int updateByCondition(T bean, boolean updateNotNull, boolean optimisticLock, String[] filterFields, Wrapper where) {
        return updateBy(bean, updateNotNull, optimisticLock, filterFields, where);
    }

    @Override
    public int updateBy(T bean, boolean updateNotNull, boolean optimisticLock, String[] filterFields, Wrapper where) {
        Update update = new Update();
        update.setUpdateBean(bean);
        update.setUpdateNotNull(updateNotNull);
        update.setOptimisticLock(optimisticLock);
        update.setFilterFields(filterFields);
        update.setWhere(where);
        return sqliteTemplate.update(SqlBeanProvider.updateSql(getSqlBeanDB(), clazz, update, false));
    }

    @Deprecated
    @Override
    public int updateByBeanCondition(T bean, String where) {
        return updateByBean(bean, where);
    }

    @Override
    public int updateByBean(T bean, String where) {
        return sqliteTemplate.update(SqlBeanProvider.updateByBeanConditionSql(getSqlBeanDB(), clazz, bean, true, false, null, where));
    }

    @Deprecated
    @Override
    public int updateByBeanCondition(T bean, boolean updateNotNull, boolean optimisticLock, String where) {
        return updateByBean(bean, updateNotNull, optimisticLock, where);
    }

    @Override
    public int updateByBean(T bean, boolean updateNotNull, boolean optimisticLock, String where) {
        return sqliteTemplate.update(SqlBeanProvider.updateByBeanConditionSql(getSqlBeanDB(), clazz, bean, updateNotNull, optimisticLock, null, where));
    }

    @Deprecated
    @Override
    public int updateByBeanCondition(T bean, boolean updateNotNull, boolean optimisticLock, String[] filterFields, String where) {
        return updateByBean(bean, updateNotNull, optimisticLock, filterFields, where);
    }

    @Override
    public int updateByBean(T bean, boolean updateNotNull, boolean optimisticLock, String[] filterFields, String where) {
        return sqliteTemplate.update(SqlBeanProvider.updateByBeanConditionSql(getSqlBeanDB(), clazz, bean, updateNotNull, optimisticLock, filterFields, where));
    }

    @Override
    public int insert(T... bean) {
        if (bean == null || bean.length == 0) {
            throw new SqlBeanException("insert方法bean参数至少拥有一个值");
        }
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
