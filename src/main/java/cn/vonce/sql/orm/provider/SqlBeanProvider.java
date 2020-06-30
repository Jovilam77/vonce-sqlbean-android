package cn.vonce.sql.orm.provider;

import cn.vonce.sql.bean.*;
import cn.vonce.sql.config.SqlBeanConfig;
import cn.vonce.sql.constant.SqlHelperCons;
import cn.vonce.sql.enumerate.DbType;
import cn.vonce.sql.enumerate.SqlOperator;
import cn.vonce.sql.exception.SqlBeanException;
import cn.vonce.sql.helper.SqlHelper;
import cn.vonce.sql.uitls.ReflectUtil;
import cn.vonce.sql.uitls.SqlBeanUtil;
import cn.vonce.sql.uitls.StringUtil;

import java.lang.reflect.Field;

/**
 * 通用的数据库操作sql语句生成
 *
 * @author Jovi
 * @version 1.0
 * @email 766255988@qq.com
 * @date 2018年5月15日下午2:23:47
 */
public class SqlBeanProvider {

    /**
     * 根据id条件查询
     *
     * @param clazz
     * @param id
     * @return
     */
    public String selectByIdSql(SqlBeanConfig sqlBeanConfig, Class<?> clazz, Object id) {
        return selectByIdsSql(sqlBeanConfig, clazz, new Object[]{id});
    }

    /**
     * 根据ids条件查询
     *
     * @param clazz
     * @param ids
     * @return
     */
    public String selectByIdsSql(SqlBeanConfig sqlBeanConfig, Class<?> clazz, Object... ids) {
        Select select;
        Field idField;
        try {
            select = newSelect(sqlBeanConfig, clazz, false);
            idField = SqlBeanUtil.getIdField(clazz);
        } catch (SqlBeanException e) {
            e.printStackTrace();
            return null;
        }
        if (ids.length > 1) {
            select.where(SqlBeanUtil.getTable(clazz).getAlias(), SqlBeanUtil.getTableFieldName(idField), ids, SqlOperator.IN);
        } else {
            select.where(SqlBeanUtil.getTable(clazz).getAlias(), SqlBeanUtil.getTableFieldName(idField), ids[0], SqlOperator.EQUAL_TO);
        }
        return SqlHelper.buildSelectSql(select);
    }

    /**
     * 根据条件查询
     *
     * @param clazz
     * @param paging
     * @param where
     * @param args
     * @return
     */
    public String selectByConditionSql(SqlBeanConfig sqlBeanConfig, Class<?> clazz, Paging paging, String where, Object... args) {
        Select select = newSelect(sqlBeanConfig, clazz, false);
        select.setWhere(where, args);
        setPaging(select, paging, clazz);
        return SqlHelper.buildSelectSql(select);
    }

    /**
     * 根据条件查询统计
     *
     * @param clazz
     * @param where
     * @param args
     * @return
     */
    public String selectCountByConditionSql(SqlBeanConfig sqlBeanConfig, Class<?> clazz, String where, Object[] args) {
        Select select = newSelect(sqlBeanConfig, clazz, true);
        select.setWhere(where, args);
        return SqlHelper.buildSelectSql(select);
    }

    /**
     * 查询全部
     *
     * @param clazz
     * @return
     */
    public String selectAllSql(SqlBeanConfig sqlBeanConfig, Class<?> clazz, Paging paging) {
        Select select = newSelect(sqlBeanConfig, clazz, false);
        setPaging(select, paging, clazz);
        return SqlHelper.buildSelectSql(select);
    }

    /**
     * 根据自定义条件查询（可自动分页）
     *
     * @param sqlBeanConfig
     * @param clazz
     * @param select
     * @return
     */
    public String selectSql(SqlBeanConfig sqlBeanConfig, Class<?> clazz, Select select) {
        if (select.getSqlBeanConfig() == null) {
            select.setSqlBeanConfig(sqlBeanConfig);
        }
        if (select.getColumnList().isEmpty()) {
            try {
                select.setColumnList(SqlBeanUtil.getSelectColumns(clazz, select.getFilterFields()));
                if (select.getPage() != null && select.getSqlBeanConfig().getDbType() == DbType.SQLServer2008) {
                    select.getPage().setIdName(SqlBeanUtil.getTableFieldName(SqlBeanUtil.getIdField(clazz)));
                }
            } catch (SqlBeanException e) {
                e.printStackTrace();
                return null;
            }
        }
        return setSelectAndBuild(clazz, select);
    }

    /**
     * 根据自定义条件统计
     *
     * @param sqlBeanConfig
     * @param clazz
     * @param select
     * @return
     */
    public String countSql(SqlBeanConfig sqlBeanConfig, Class<?> clazz, Select select) {
        if (select.getSqlBeanConfig() == null) {
            select.setSqlBeanConfig(sqlBeanConfig);
        }
        if (select.getColumnList() == null || select.getColumnList().isEmpty()) {
            select.column(SqlHelperCons.COUNT + SqlHelperCons.BEGIN_BRACKET + SqlHelperCons.ALL + SqlHelperCons.END_BRACKET);
        }
        return setSelectAndBuild(clazz, select);
    }

    /**
     * 根据id条件删除
     *
     * @param clazz
     * @param id
     * @return
     */
    public String deleteByIdSql(SqlBeanConfig sqlBeanConfig, Class<?> clazz, Object id) {
        if (StringUtil.isEmpty(id)) {
            try {
                throw new SqlBeanException("deleteByIdSql id不能为空");
            } catch (SqlBeanException e) {
                e.printStackTrace();
                return null;
            }
        }
        Delete delete = new Delete();
        delete.setSqlBeanConfig(sqlBeanConfig);
        delete.setTable(clazz);
        Field idField;
        try {
            idField = SqlBeanUtil.getIdField(clazz);
        } catch (SqlBeanException e) {
            e.printStackTrace();
            return null;
        }
        delete.where("", SqlBeanUtil.getTableFieldName(idField), id, SqlOperator.IN);
        return SqlHelper.buildDeleteSql(delete);
    }

    /**
     * 根据条件删除
     *
     * @param clazz
     * @param where
     * @param args
     * @return
     */
    public String deleteByConditionSql(SqlBeanConfig sqlBeanConfig, Class<?> clazz, String where, Object[] args) {
        Delete delete = new Delete();
        delete.setSqlBeanConfig(sqlBeanConfig);
        delete.setTable(clazz);
        delete.setWhere(where, args);
        return SqlHelper.buildDeleteSql(delete);
    }

    /**
     * 删除
     *
     * @param clazz
     * @param delete
     * @param ignore
     * @return
     */
    public String deleteSql(SqlBeanConfig sqlBeanConfig, Class<?> clazz, Delete delete, boolean ignore) {
        if (delete.getSqlBeanConfig() == null) {
            delete.setSqlBeanConfig(sqlBeanConfig);
        }
        if (delete.getTable() == null || StringUtil.isEmpty(delete.getTable().getName())) {
            delete.setTable(clazz);
        }
        if (ignore || !delete.getWhereMap().isEmpty()) {
            return SqlHelper.buildDeleteSql(delete);
        } else {
            try {
                throw new SqlBeanException("该delete sql未设置where条件，如果确实不需要where条件，请使用delete(Select select, boolean ignore)");
            } catch (SqlBeanException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * 逻辑删除
     *
     * @param clazz
     * @param id
     * @return
     */
    public String logicallyDeleteByIdSql(SqlBeanConfig sqlBeanConfig, Class<?> clazz, Object id) {
        Update update = new Update();
        update.setSqlBeanConfig(sqlBeanConfig);
        Object bean;
        try {
            bean = newLogicallyDeleteBean(clazz);
            update.setUpdateBean(bean);
            Field idField = SqlBeanUtil.getIdField(bean.getClass());
            update.where(SqlBeanUtil.getTableFieldName(idField), id);
        } catch (SqlBeanException e) {
            e.printStackTrace();
            return null;
        }
        return SqlHelper.buildUpdateSql(update);
    }

    /**
     * 逻辑删除
     *
     * @param clazz
     * @param where
     * @param args
     * @return
     */
    public String logicallyDeleteByConditionSql(SqlBeanConfig sqlBeanConfig, Class<?> clazz, String where, Object[] args) {
        Update update = new Update();
        update.setSqlBeanConfig(sqlBeanConfig);
        try {
            update.setUpdateBean(newLogicallyDeleteBean(clazz));
            update.setWhere(where, args);
        } catch (SqlBeanException e) {
            e.printStackTrace();
            return null;
        }
        return SqlHelper.buildUpdateSql(update);
    }

    /**
     * 更新
     *
     * @param sqlBeanConfig
     * @param update
     * @param ignore
     * @return
     */
    public String updateSql(SqlBeanConfig sqlBeanConfig, Update update, boolean ignore) {
        if (update.getSqlBeanConfig() == null) {
            update.setSqlBeanConfig(sqlBeanConfig);
        }
        if (ignore || (!update.getWhereMap().isEmpty() || StringUtil.isNotEmpty(update.getWhere()))) {
            return SqlHelper.buildUpdateSql(update);
        } else {
            try {
                throw new SqlBeanException("该update sql未设置where条件，如果确实不需要where条件，请使用update(Select select, boolean ignore)");
            } catch (SqlBeanException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * 根据实体类id条件更新
     *
     * @param bean
     * @param updateNotNull
     * @param filterFields
     * @return
     */
    public String updateByIdSql(SqlBeanConfig sqlBeanConfig, Object bean, Object id, boolean updateNotNull, String[] filterFields) {
        if (StringUtil.isEmpty(id)) {
            try {
                throw new SqlBeanException("updateByIdSql id不能为空");
            } catch (SqlBeanException e) {
                e.printStackTrace();
                return null;
            }
        }
        Update update = newUpdate(sqlBeanConfig, bean, updateNotNull);
        update.setFilterFields(filterFields);
        Field idField;
        try {
            idField = SqlBeanUtil.getIdField(bean.getClass());
        } catch (SqlBeanException e) {
            e.printStackTrace();
            return null;
        }
        update.where(SqlBeanUtil.getTableFieldName(idField), id);
        return SqlHelper.buildUpdateSql(update);
    }

    /**
     * 根据实体类id条件更新
     *
     * @param bean
     * @param updateNotNull
     * @param filterFields
     * @return
     */
    public String updateByBeanIdSql(SqlBeanConfig sqlBeanConfig, Object bean, boolean updateNotNull, String[] filterFields) {
        Update update = newUpdate(sqlBeanConfig, bean, updateNotNull);
        update.setFilterFields(filterFields);
        Field idField;
        try {
            idField = SqlBeanUtil.getIdField(bean.getClass());
            Object id = ReflectUtil.getFieldValue(bean, idField.getName());
            if (StringUtil.isEmpty(id)) {
                try {
                    throw new SqlBeanException("updateByBeanIdSql id不能为空");
                } catch (SqlBeanException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            update.where(SqlBeanUtil.getTableFieldName(idField), id);
        } catch (SqlBeanException e) {
            e.printStackTrace();
            return null;
        }
        return SqlHelper.buildUpdateSql(update);
    }

    /**
     * 根据条件更新
     *
     * @param bean
     * @param updateNotNull
     * @param filterFields
     * @param where
     * @param args
     * @return
     */
    public String updateByConditionSql(SqlBeanConfig sqlBeanConfig, Object bean, boolean updateNotNull, String[] filterFields, String where, Object[] args) {
        Update update = newUpdate(sqlBeanConfig, bean, updateNotNull);
        update.setFilterFields(filterFields);
        update.setWhere(where, args);
        return SqlHelper.buildUpdateSql(update);
    }

    /**
     * 根据实体类字段条件更新
     *
     * @param bean
     * @param updateNotNull
     * @param filterFields
     * @param where
     * @return
     */
    public String updateByBeanConditionSql(SqlBeanConfig sqlBeanConfig, Object bean, boolean updateNotNull, String[] filterFields, String where) {
        Update update = newUpdate(sqlBeanConfig, bean, updateNotNull);
        update.setFilterFields(filterFields);
        update.setWhere(where, null);
        return SqlHelper.buildUpdateSql(update);
    }

    /**
     * 插入数据
     *
     * @param bean
     * @return
     */
    public String insertBeanSql(SqlBeanConfig sqlBeanConfig, Object bean) {
        Insert insert = new Insert();
        insert.setSqlBeanConfig(sqlBeanConfig);
        insert.setInsertBean(bean);
        return SqlHelper.buildInsertSql(insert);
    }

    /**
     * 插入数据
     *
     * @param sqlBeanConfig
     * @param insert
     * @return
     */
    public String insertSql(SqlBeanConfig sqlBeanConfig, Insert insert) {
        if (insert.getSqlBeanConfig() == null) {
            insert.setSqlBeanConfig(sqlBeanConfig);
        }
        return SqlHelper.buildInsertSql(insert);
    }

    /**
     * 删除表
     *
     * @param sqlBeanConfig
     * @param clazz
     * @return
     */
    public String dropTableSql(SqlBeanConfig sqlBeanConfig, Class<?> clazz) {
        return "DROP TABLE IF EXISTS " + SqlBeanUtil.getTable(clazz).getName();
    }

    /**
     * 创建表
     *
     * @param sqlBeanConfig
     * @param clazz
     * @return
     */
    public String createTableSql(SqlBeanConfig sqlBeanConfig, Class<?> clazz) {
        Create create = new Create();
        create.setSqlBeanConfig(sqlBeanConfig);
        create.setBeanClass(clazz);
        return SqlHelper.buildCreateSql(create);
    }

    /**
     * 实例化Select
     *
     * @param clazz
     * @param isCount
     * @return
     * @throws SqlBeanException
     */
    private Select newSelect(SqlBeanConfig sqlBeanConfig, Class<?> clazz, boolean isCount) {
        Select select = new Select();
        select.setSqlBeanConfig(sqlBeanConfig);
        select.setTable(clazz);
        try {
            if (isCount) {
                select.column(SqlHelperCons.COUNT + SqlHelperCons.BEGIN_BRACKET + SqlHelperCons.ALL + SqlHelperCons.END_BRACKET);
            } else {
                select.setColumnList(SqlBeanUtil.getSelectColumns(clazz, select.getFilterFields()));
            }
            SqlBeanUtil.setJoin(select, clazz);
        } catch (SqlBeanException e) {
            e.printStackTrace();
            return null;
        }
        return select;
    }

    /**
     * 设置SqlBean中的Select 并生成select sql
     *
     * @param clazz
     * @param select
     * @return
     * @throws SqlBeanException
     */
    private String setSelectAndBuild(Class<?> clazz, Select select) {
        if (StringUtil.isEmpty(select.getTable().getName())) {
            Table table = SqlBeanUtil.getTable(clazz);
            select.getTable().setName(table.getName());
            if (StringUtil.isEmpty(select.getTable().getAlias())) {
                select.getTable().setAlias(table.getAlias());
            }
        }
        try {
            SqlBeanUtil.setJoin(select, clazz);
        } catch (SqlBeanException e) {
            e.printStackTrace();
            return null;
        }
        return SqlHelper.buildSelectSql(select);
    }

    /**
     * 逻辑删除需要的对象
     *
     * @param clazz
     * @return
     * @throws IllegalAccessException
     */
    private Object newLogicallyDeleteBean(Class<?> clazz) throws SqlBeanException {
        Object bean = null;
        try {
            bean = clazz.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        Field field = SqlBeanUtil.getLogicallyField(clazz);
        ReflectUtil.setFieldValue(bean, field.getName(), true);
        return bean;
    }

    /**
     * 实例化Select
     *
     * @param bean
     * @param updateNotNull
     * @return
     * @throws SqlBeanException
     */
    private Update newUpdate(SqlBeanConfig sqlBeanConfig, Object bean, boolean updateNotNull) {
        Update update = new Update();
        update.setSqlBeanConfig(sqlBeanConfig);
        update.setUpdateBean(bean);
        update.setUpdateNotNull(updateNotNull);
        return update;
    }

    /**
     * 设置分页参数
     *
     * @param select
     * @param paging
     * @param clazz
     */
    private void setPaging(Select select, Paging paging, Class<?> clazz) {
        if (paging != null) {
            if (select.getSqlBeanConfig().getDbType() == DbType.SQLServer2008) {
                try {
                    select.setPage(SqlBeanUtil.getTableFieldName(SqlBeanUtil.getIdField(clazz)), paging.getPagenum(), paging.getPagesize());
                } catch (SqlBeanException e) {
                    e.printStackTrace();
                }
            } else {
                select.setPage(null, paging.getPagenum(), paging.getPagesize());
            }
            select.orderBy(paging.getOrders());
        }
    }

}
