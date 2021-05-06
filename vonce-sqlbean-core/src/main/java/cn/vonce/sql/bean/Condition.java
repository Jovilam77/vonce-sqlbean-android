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
public class Condition extends Common {

    private String condition = "";//条件
    private Object[] agrs = null;
    private ListMultimap<String, ConditionInfo> conditionMap = LinkedListMultimap.create();//where条件包含的逻辑

    /**
     * 获取condition sql 内容
     *
     * @return
     */
    protected String getCondition() {
        return condition;
    }

    /**
     * 设置condition sql 内容
     *
     * @param condition
     */
    protected void setCondition(String condition) {
        this.condition = condition;
    }

    /**
     * 设置where sql 内容
     *
     * @param condition
     * @param args
     */
    protected void setCondition(String condition, Object... args) {
        this.condition = condition;
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
    public ListMultimap<String, ConditionInfo> getCondMap() {
        return conditionMap;
    }

    /**
     * 添加where条件
     *
     * @param field 字段
     * @param value 字段值
     */
    public Condition cond(String field, Object value) {
        return cond(field, value, SqlOperator.EQUAL_TO);
    }

    /**
     * 添加where条件
     *
     * @param column 字段信息
     * @param value  字段值
     * @author Jovi
     */
    public Condition cond(Column column, Object value) {
        return cond(SqlLogic.AND, column.getSchema(), column.getTableAlias(), column.name(), value, SqlOperator.EQUAL_TO);
    }


    /**
     * 添加where条件
     *
     * @param field       字段
     * @param value       字段值
     * @param sqlOperator 操作符
     * @return
     */
    public Condition cond(String field, Object value, SqlOperator sqlOperator) {
        return cond(SqlLogic.AND, "", "", field, value, sqlOperator);
    }

    /**
     * @param sqlLogic   该条件与下一条件之间的逻辑关系
     * @param tableAlias 表别名
     * @param field      字段
     * @param value      字段值
     * @return
     */
    public Condition cond(SqlLogic sqlLogic, String tableAlias, String field, Object value) {
        return cond(sqlLogic, "", tableAlias, field, value, SqlOperator.EQUAL_TO);
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
    public Condition cond(String tableAlias, String field, Object value, SqlOperator sqlOperator) {
        return cond(SqlLogic.AND, "", tableAlias, field, value, sqlOperator);
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
    public Condition cond(Column column, Object value, SqlOperator sqlOperator) {
        return cond(SqlLogic.AND, column.getSchema(), column.getTableAlias(), column.name(), value, sqlOperator);
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
    public Condition cond(SqlLogic sqlLogic, Column column, Object value, SqlOperator sqlOperator) {
        return cond(sqlLogic, column.getSchema(), column.getTableAlias(), column.name(), value, sqlOperator);
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
    public Condition cond(SqlLogic sqlLogic, String schema, String tableAlias, String field, Object value, SqlOperator sqlOperator) {
        if (value instanceof Select) {
            if (sqlOperator == SqlOperator.IN || sqlOperator == SqlOperator.NOT_IN) {
                value = new Original(SqlHelper.buildSelectSql((Select) value));
            } else {
                value = new Original(SqlHelperCons.BEGIN_BRACKET + SqlHelper.buildSelectSql((Select) value) + SqlHelperCons.END_BRACKET);
            }
        }
        conditionMap.put(tableAlias + field, new ConditionInfo(sqlLogic, schema, tableAlias, field, value, sqlOperator));
        return this;
    }


    /**
     * where 条件 and 方法
     *
     * @param field
     * @param value
     * @return
     */
    public Condition and(String field, Object value) {
        return and(field, value, SqlOperator.EQUAL_TO);
    }

    /**
     * where 条件 and 方法
     *
     * @param value
     * @return
     * @author Jovi
     */
    public Condition and(Column column, Object value) {
        return and(column.getSchema(), column.getTableAlias(), column.name(), value, SqlOperator.EQUAL_TO);
    }

    /**
     * where 条件 and 方法
     *
     * @param field
     * @param value
     * @param sqlOperator
     * @return
     */
    public Condition and(String field, Object value, SqlOperator sqlOperator) {
        return and("", "", field, value, sqlOperator);
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
    public Condition and(Column column, Object value, SqlOperator sqlOperator) {
        return and(column.getSchema(), column.getTableAlias(), column.name(), value, sqlOperator);
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
    public Condition and(String schema, String tableAlias, String field, Object value, SqlOperator sqlOperator) {
        return cond(SqlLogic.AND, schema, tableAlias, field, value, sqlOperator);
    }

    /**
     * where 条件 or 方法
     *
     * @param field
     * @param value
     * @return
     */
    public Condition or(String field, Object value) {
        return or(field, value, SqlOperator.EQUAL_TO);
    }

    /**
     * where 条件 or 方法
     *
     * @param column
     * @param value
     * @return
     * @author Jovi
     */
    public Condition or(Column column, Object value) {
        return or(column.getSchema(), column.getTableAlias(), column.name(), value, SqlOperator.EQUAL_TO);
    }

    /**
     * where 条件 or 方法
     *
     * @param field
     * @param value
     * @param sqlOperator
     * @return
     */
    public Condition or(String field, Object value, SqlOperator sqlOperator) {
        return or("", "", field, value, sqlOperator);
    }

    /**
     * where 条件 or 方法
     *
     * @param column
     * @param value
     * @param sqlOperator
     * @return
     */
    public Condition or(Column column, Object value, SqlOperator sqlOperator) {
        return or(column.getSchema(), column.getTableAlias(), column.name(), value, sqlOperator);
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
    public Condition or(String schema, String tableAlias, String field, Object value, SqlOperator sqlOperator) {
        return cond(SqlLogic.OR, schema, tableAlias, field, value, sqlOperator);
    }

    /**
     * where 条件 andBracket 方法
     *
     * @param field
     * @param value
     * @return
     */
    public Condition andBracket(String field, Object value) {
        return andBracket(field, value, SqlOperator.EQUAL_TO);
    }

    /**
     * where 条件 andBracket 方法
     *
     * @param column
     * @param value
     * @return
     * @author Jovi
     */
    public Condition andBracket(Column column, Object value) {
        return andBracket(column.getSchema(), column.getTableAlias(), column.name(), value, SqlOperator.EQUAL_TO);
    }

    /**
     * where 条件 andBracket 方法
     *
     * @param field
     * @param value
     * @param sqlOperator
     * @return
     */
    public Condition andBracket(String field, Object value, SqlOperator sqlOperator) {
        return andBracket("", "", field, value, sqlOperator);
    }

    /**
     * where 条件 andBracket 方法
     *
     * @param column
     * @param value
     * @param sqlOperator
     * @return
     */
    public Condition andBracket(Column column, Object value, SqlOperator sqlOperator) {
        return andBracket(column.getSchema(), column.getTableAlias(), column.name(), value, sqlOperator);
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
    public Condition andBracket(String schema, String tableAlias, String field, Object value, SqlOperator sqlOperator) {
        return cond(SqlLogic.ANDBracket, schema, tableAlias, field, value, sqlOperator);
    }

    /**
     * where 条件 orBracket 方法
     *
     * @param field
     * @param value
     * @return
     */
    public Condition orBracket(String field, Object value) {
        return orBracket(field, value, SqlOperator.EQUAL_TO);
    }

    /**
     * where 条件 orBracket 方法
     *
     * @param column
     * @param value
     * @return
     * @author Jovi
     */
    public Condition orBracket(Column column, Object value) {
        return orBracket(column.getSchema(), column.getTableAlias(), column.name(), value, SqlOperator.EQUAL_TO);
    }

    /**
     * where 条件 orBracket 方法
     *
     * @param field
     * @param value
     * @param sqlOperator
     * @return
     */
    public Condition orBracket(String field, Object value, SqlOperator sqlOperator) {
        return orBracket("", "", field, value, sqlOperator);
    }

    /**
     * where 条件 orBracket 方法
     *
     * @param column
     * @param value
     * @param sqlOperator
     * @return
     */
    public Condition orBracket(Column column, Object value, SqlOperator sqlOperator) {
        return orBracket(column.getSchema(), column.getTableAlias(), column.name(), value, sqlOperator);
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
    public Condition orBracket(String schema, String tableAlias, String field, Object value, SqlOperator sqlOperator) {
        return cond(SqlLogic.ORBracket, schema, tableAlias, field, value, sqlOperator);
    }

}
