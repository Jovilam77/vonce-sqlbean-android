package cn.vonce.sql.bean;

import cn.vonce.sql.constant.SqlHelperCons;
import cn.vonce.sql.enumerate.SqlLogic;
import cn.vonce.sql.enumerate.SqlOperator;
import cn.vonce.sql.helper.SqlHelper;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * 条件
 *
 * @author Jovi
 * @version 1.0
 * @email 766255988@qq.com
 * @date 2020年3月1日上午10:00:10
 */
public class Condition_copy extends Common {

    private String where = "";//条件
    private Object[] agrs = null;
    private ListMultimap<String, ConditionInfo> whereMap = LinkedListMultimap.create();//where条件包含的逻辑

    /**
     * 获取where sql 内容
     *
     * @return
     */
    public String getWhere() {
        return where;
    }

    /**
     * 设置where sql 内容
     *
     * @param where
     */
    public void setWhere(String where) {
        this.where = where;
    }

    /**
     * 设置where sql 内容
     *
     * @param where
     * @param args
     */
    public void setWhere(String where, Object... args) {
        this.where = where;
        this.agrs = args;
    }

    /**
     * 获取where参数
     *
     * @return
     */
    public Object[] getAgrs() {
        return agrs;
    }

    /**
     * 设置where参数
     *
     * @param agrs
     */
    public void setAgrs(Object[] agrs) {
        this.agrs = agrs;
    }

    /**
     * 获取where条件map
     *
     * @return
     */
    public ListMultimap<String, ConditionInfo> getWhereMap() {
        return whereMap;
    }

    /**
     * 添加where条件
     *
     * @param field 字段
     * @param value 字段值
     */
    public Condition_copy where(String field, Object value) {
        return where(field, value, SqlOperator.EQUAL_TO);
    }

    /**
     * 添加where条件
     *
     * @param column 字段信息
     * @param value  字段值
     * @author Jovi
     */
    public Condition_copy where(Column column, Object value) {
        return where(SqlLogic.AND, column.getSchema(), column.getTableAlias(), column.name(), value, SqlOperator.EQUAL_TO);
    }


    /**
     * 添加where条件
     *
     * @param field       字段
     * @param value       字段值
     * @param sqlOperator 操作符
     * @return
     */
    public Condition_copy where(String field, Object value, SqlOperator sqlOperator) {
        return where(SqlLogic.AND, "", "", field, value, sqlOperator);
    }

    /**
     * @param sqlLogic   该条件与下一条件之间的逻辑关系
     * @param tableAlias 表别名
     * @param field      字段
     * @param value      字段值
     * @return
     */
    public Condition_copy where(SqlLogic sqlLogic, String tableAlias, String field, Object value) {
        return where(sqlLogic, "", tableAlias, field, value, SqlOperator.EQUAL_TO);
    }

    /**
     * 添加where条件
     *
     * @param tableAlias  表别名
     * @param field       字段
     * @param value       字段值
     * @param sqlOperator 操作符
     * @return
     */
    public Condition_copy where(String tableAlias, String field, Object value, SqlOperator sqlOperator) {
        return where(SqlLogic.AND, "", tableAlias, field, value, sqlOperator);
    }

    /**
     * 添加where条件
     *
     * @param column      字段信息
     * @param value       字段值
     * @param sqlOperator 操作符
     * @return
     * @author Jovi
     */
    public Condition_copy where(Column column, Object value, SqlOperator sqlOperator) {
        return where(SqlLogic.AND, column.getSchema(), column.getTableAlias(), column.name(), value, sqlOperator);
    }

    /**
     * 添加where条件
     *
     * @param sqlLogic    该条件与下一条件之间的逻辑关系
     * @param column      字段信息
     * @param value       字段值
     * @param sqlOperator 操作符
     * @return
     * @author Jovi
     */
    public Condition_copy where(SqlLogic sqlLogic, Column column, Object value, SqlOperator sqlOperator) {
        return where(sqlLogic, column.getSchema(), column.getTableAlias(), column.name(), value, sqlOperator);
    }

    /**
     * 添加where条件
     *
     * @param sqlLogic    该条件与下一条件之间的逻辑关系
     * @param schema      schema
     * @param tableAlias  表别名
     * @param field       字段
     * @param value       字段值
     * @param sqlOperator 操作符
     * @return
     */
    public Condition_copy where(SqlLogic sqlLogic, String schema, String tableAlias, String field, Object value, SqlOperator sqlOperator) {
        if (value instanceof Select) {
            if (sqlOperator == SqlOperator.IN || sqlOperator == SqlOperator.NOT_IN) {
                value = new Original(SqlHelper.buildSelectSql((Select) value));
            } else {
                value = new Original(SqlHelperCons.BEGIN_BRACKET + SqlHelper.buildSelectSql((Select) value) + SqlHelperCons.END_BRACKET);
            }
        }
        whereMap.put(tableAlias + field, new ConditionInfo(sqlLogic, schema, tableAlias, field, value, sqlOperator));
        return this;
    }


    /**
     * where 条件 and 方法
     *
     * @param field
     * @param value
     * @return
     */
    public Condition_copy wAND(String field, Object value) {
        return wAND(field, value, SqlOperator.EQUAL_TO);
    }

    /**
     * where 条件 and 方法
     *
     * @param value
     * @return
     * @author Jovi
     */
    public Condition_copy wAND(Column column, Object value) {
        return wAND(column.getSchema(), column.getTableAlias(), column.name(), value, SqlOperator.EQUAL_TO);
    }

    /**
     * where 条件 and 方法
     *
     * @param field
     * @param value
     * @param sqlOperator
     * @return
     */
    public Condition_copy wAND(String field, Object value, SqlOperator sqlOperator) {
        return wAND("", "", field, value, sqlOperator);
    }

    /**
     * where 条件 and 方法
     *
     * @param column
     * @param value
     * @param sqlOperator
     * @return
     * @author Jovi
     */
    public Condition_copy wAND(Column column, Object value, SqlOperator sqlOperator) {
        return wAND(column.getSchema(), column.getTableAlias(), column.name(), value, sqlOperator);
    }

    /**
     * where 条件 and 方法
     *
     * @param schema
     * @param tableAlias
     * @param field
     * @param value
     * @param sqlOperator
     * @return
     */
    public Condition_copy wAND(String schema, String tableAlias, String field, Object value, SqlOperator sqlOperator) {
        return where(SqlLogic.AND, schema, tableAlias, field, value, sqlOperator);
    }

    /**
     * where 条件 or 方法
     *
     * @param field
     * @param value
     * @return
     */
    public Condition_copy wOR(String field, Object value) {
        return wOR(field, value, SqlOperator.EQUAL_TO);
    }

    /**
     * where 条件 or 方法
     *
     * @param column
     * @param value
     * @return
     * @author Jovi
     */
    public Condition_copy wOR(Column column, Object value) {
        return wOR(column.getSchema(), column.getTableAlias(), column.name(), value, SqlOperator.EQUAL_TO);
    }

    /**
     * where 条件 or 方法
     *
     * @param field
     * @param value
     * @param sqlOperator
     * @return
     */
    public Condition_copy wOR(String field, Object value, SqlOperator sqlOperator) {
        return wOR("", "", field, value, sqlOperator);
    }

    /**
     * where 条件 or 方法
     *
     * @param column
     * @param value
     * @param sqlOperator
     * @return
     */
    public Condition_copy wOR(Column column, Object value, SqlOperator sqlOperator) {
        return wOR(column.getSchema(), column.getTableAlias(), column.name(), value, sqlOperator);
    }

    /**
     * where 条件 or 方法
     *
     * @param schema
     * @param tableAlias
     * @param field
     * @param value
     * @param sqlOperator
     * @return
     */
    public Condition_copy wOR(String schema, String tableAlias, String field, Object value, SqlOperator sqlOperator) {
        return where(SqlLogic.OR, schema, tableAlias, field, value, sqlOperator);
    }

    /**
     * where 条件 andBracket 方法
     *
     * @param field
     * @param value
     * @return
     */
    public Condition_copy wANDBracket(String field, Object value) {
        return wANDBracket(field, value, SqlOperator.EQUAL_TO);
    }

    /**
     * where 条件 andBracket 方法
     *
     * @param column
     * @param value
     * @return
     * @author Jovi
     */
    public Condition_copy wANDBracket(Column column, Object value) {
        return wANDBracket(column.getSchema(), column.getTableAlias(), column.name(), value, SqlOperator.EQUAL_TO);
    }

    /**
     * where 条件 andBracket 方法
     *
     * @param field
     * @param value
     * @param sqlOperator
     * @return
     */
    public Condition_copy wANDBracket(String field, Object value, SqlOperator sqlOperator) {
        return wANDBracket("", "", field, value, sqlOperator);
    }

    /**
     * where 条件 andBracket 方法
     *
     * @param column
     * @param value
     * @param sqlOperator
     * @return
     */
    public Condition_copy wANDBracket(Column column, Object value, SqlOperator sqlOperator) {
        return wANDBracket(column.getSchema(), column.getTableAlias(), column.name(), value, sqlOperator);
    }

    /**
     * where 条件 andBracket 方法
     *
     * @param schema
     * @param tableAlias
     * @param field
     * @param value
     * @param sqlOperator
     * @return
     */
    public Condition_copy wANDBracket(String schema, String tableAlias, String field, Object value, SqlOperator sqlOperator) {
        return where(SqlLogic.ANDBracket, schema, tableAlias, field, value, sqlOperator);
    }

    /**
     * where 条件 orBracket 方法
     *
     * @param field
     * @param value
     * @return
     */
    public Condition_copy wORBracket(String field, Object value) {
        return wORBracket(field, value, SqlOperator.EQUAL_TO);
    }

    /**
     * where 条件 orBracket 方法
     *
     * @param column
     * @param value
     * @return
     * @author Jovi
     */
    public Condition_copy wORBracket(Column column, Object value) {
        return wORBracket(column.getSchema(), column.getTableAlias(), column.name(), value, SqlOperator.EQUAL_TO);
    }

    /**
     * where 条件 orBracket 方法
     *
     * @param field
     * @param value
     * @param sqlOperator
     * @return
     */
    public Condition_copy wORBracket(String field, Object value, SqlOperator sqlOperator) {
        return wORBracket("", "", field, value, sqlOperator);
    }

    /**
     * where 条件 orBracket 方法
     *
     * @param column
     * @param value
     * @param sqlOperator
     * @return
     */
    public Condition_copy wORBracket(Column column, Object value, SqlOperator sqlOperator) {
        return wORBracket(column.getSchema(), column.getTableAlias(), column.name(), value, sqlOperator);
    }

    /**
     * where 条件 orBracket 方法
     *
     * @param schema
     * @param tableAlias
     * @param field
     * @param value
     * @param sqlOperator
     * @return
     */
    public Condition_copy wORBracket(String schema, String tableAlias, String field, Object value, SqlOperator sqlOperator) {
        return where(SqlLogic.ORBracket, schema, tableAlias, field, value, sqlOperator);
    }

}
