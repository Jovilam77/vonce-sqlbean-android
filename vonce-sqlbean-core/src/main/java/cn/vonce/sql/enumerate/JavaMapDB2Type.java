package cn.vonce.sql.enumerate;

import cn.vonce.sql.bean.Alter;
import cn.vonce.sql.bean.ColumnInfo;
import cn.vonce.sql.bean.Common;
import cn.vonce.sql.bean.Table;
import cn.vonce.sql.config.SqlBeanDB;
import cn.vonce.sql.constant.SqlConstant;
import cn.vonce.sql.uitls.SqlBeanUtil;
import cn.vonce.sql.uitls.StringUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Java类型对应的Oracle类型枚举类
 *
 * @author Jovi
 * @email imjovi@qq.com
 * @date 2022/8/22 16:28
 */
public enum JavaMapDB2Type {

    INTEGER(new Class[]{int.class, Integer.class}), BIGINT(new Class[]{long.class, Long.class}), SMALLINT(new Class[]{boolean.class, Boolean.class, byte.class, Byte.class, short.class, Short.class}), REAL(new Class[]{float.class, Float.class}), DOUBLE(new Class[]{double.class, Double.class}), DECIMAL(new Class[]{BigDecimal.class}), CHAR(new Class[]{char.class, Character.class}), VARCHAR(new Class[]{String.class}), DATE(new Class[]{java.sql.Date.class}), TIME(new Class[]{java.sql.Time.class}), TIMESTAMP(new Class[]{java.sql.Timestamp.class, java.util.Date.class}), CLOB(new Class[]{java.sql.Clob.class}), BLOB(new Class[]{java.sql.Blob.class, Object.class});


    JavaMapDB2Type(Class<?>[] classes) {
        this.classes = classes;
    }

    private Class<?>[] classes;

    public static JavaMapDB2Type getType(Class<?> clazz) {
        for (JavaMapDB2Type javaType : values()) {
            for (Class<?> thisClazz : javaType.classes) {
                if (thisClazz == clazz) {
                    return javaType;
                }
            }
        }
        return null;
    }

    public static String getTypeName(Class<?> clazz) {
        return getType(clazz).name();
    }

    /**
     * 获取表数据列表的SQL
     * select tabname AS \"name\" from syscat.tables where tabschema = current schema and tabname = 'TEST'
     *
     * @param sqlBeanDB
     * @param schema
     * @param tableName
     * @return
     */
    public static String getTableListSql(SqlBeanDB sqlBeanDB, String schema, String tableName) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT name, remarks ");
        sql.append("FROM sysibm.systables ");
        sql.append("WHERE type = 'T' ");
        sql.append("AND creator = ");
        if (StringUtil.isNotEmpty(schema)) {
            sql.append("'" + schema + "'");
        } else {
            sql.append("current user");
        }
        if (StringUtil.isNotEmpty(tableName)) {
            sql.append(" AND name = '" + tableName + "'");
        }
        return sql.toString();
    }

    /**
     * 获取列数据列表的SQL
     *
     * @param sqlBeanDB
     * @param tableName
     * @return
     */
    public static String getColumnListSql(SqlBeanDB sqlBeanDB, String schema, String tableName) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT col.column_id AS cid, col.column_name AS name, col.data_type AS type, ");
        sql.append("(CASE col.nullable WHEN 'N' THEN '1' ELSE '0' END) AS notnull, col.data_default AS dflt_value, ");
        sql.append("(CASE uc1.constraint_type WHEN 'P' THEN '1' ELSE '0' END) AS pk, ");
        sql.append("(CASE uc2.constraint_type WHEN 'R' THEN '1' ELSE '0' END) AS fk, ");
        sql.append("(CASE WHEN col.data_type = 'FLOAT' OR col.data_type = 'DOUBLE' OR col.data_type = 'DECIMAL' OR col.data_type = 'NUMBER' THEN col.data_precision ELSE col.char_length END) AS length, ");
        sql.append("col.data_scale AS scale, ");
        sql.append("user_col_comments.comments AS remarks ");
        sql.append("FROM user_tab_columns col ");
        sql.append("LEFT JOIN user_cons_columns ucc ON ucc.table_name = col.table_name AND ucc.column_name = col.column_name AND ucc.position IS NOT NULL ");
        sql.append("LEFT JOIN user_constraints uc1 ON uc1.constraint_name = ucc.constraint_name AND uc1.constraint_type = 'P' ");
        sql.append("LEFT JOIN user_constraints uc2 ON uc2.constraint_name = ucc.constraint_name AND uc2.constraint_type = 'R' ");
        sql.append("INNER JOIN user_col_comments ON user_col_comments.table_name = col.table_name AND user_col_comments.column_name = col.column_name ");
        sql.append("WHERE col.table_name = '");
        sql.append(tableName);
        sql.append("'");
        return sql.toString();
    }

    /**
     * 更改表结构
     *
     * @param alterList
     * @return
     */
    public static List<String> alterTable(List<Alter> alterList) {
        List<String> sqlList = new ArrayList<>();
        String transferred = SqlBeanUtil.getTransferred(alterList.get(0));
        StringBuffer sql = new StringBuffer();
        StringBuffer remarksSql = new StringBuffer();
        for (int i = 0; i < alterList.size(); i++) {
            Alter alter = alterList.get(i);
            if (alter.getType() == AlterType.ADD) {
                sql.append(SqlConstant.ALTER_TABLE);
                sql.append(getFullName(alter, alter.getTable()));
                sql.append(SqlConstant.ADD);
                sql.append(SqlBeanUtil.addColumn(alter, alter.getColumnInfo(), alter.getAfterColumnName()));
                remarksSql.append(addRemarks(alter, transferred));
            } else if (alter.getType() == AlterType.CHANGE) {
                sql.append(changeColumn(alter));
                sql.append(SqlConstant.SEMICOLON);
                //先改名后修改信息
                StringBuffer modifySql = modifyColumn(alter);
                if (modifySql.length() > 0) {
                    sql.append(SqlConstant.ALTER_TABLE);
                    sql.append(modifySql);
                }
                remarksSql.append(addRemarks(alter, transferred));
            } else if (alter.getType() == AlterType.MODIFY) {
                sql.append(SqlConstant.ALTER_TABLE);
                sql.append(modifyColumn(alter));
                remarksSql.append(addRemarks(alter, transferred));
            } else if (alter.getType() == AlterType.DROP) {
                sql.append(SqlConstant.ALTER_TABLE);
                sql.append(getFullName(alter, alter.getTable()));
                sql.append(SqlConstant.DROP);
                sql.append(SqlConstant.COLUMN);
                sql.append(SqlConstant.BEGIN_SQUARE_BRACKETS);
                sql.append(alter.getColumnInfo().getName());
                sql.append(SqlConstant.END_SQUARE_BRACKETS);
            }
            sql.append(SqlConstant.SPACES);
            sql.append(SqlConstant.SEMICOLON);
        }
        sqlList.add(sql.toString());
        sqlList.add(remarksSql.toString());
        sqlList.add(recast(alterList.get(0)));
        return sqlList;
    }

    /**
     * 获取全名
     *
     * @param common
     * @param table
     * @return
     */
    private static String getFullName(Common common, Table table) {
        String transferred = SqlBeanUtil.getTransferred(common);
        boolean toUpperCase = SqlBeanUtil.isToUpperCase(common);
        StringBuffer sql = new StringBuffer();
        if (StringUtil.isNotBlank(table.getSchema())) {
            sql.append(transferred);
            sql.append(toUpperCase ? table.getSchema().toUpperCase() : table.getSchema());
            sql.append(transferred);
            sql.append(SqlConstant.POINT);
        }
        sql.append(transferred);
        sql.append(toUpperCase ? table.getName().toUpperCase() : table.getName());
        sql.append(transferred);
        sql.append(SqlConstant.SPACES);
        return sql.toString();
    }

    /**
     * 更改列信息
     *
     * @param alter
     * @return
     */
    private static StringBuffer modifyColumn(Alter alter) {
        StringBuffer modifySql = new StringBuffer();
        modifySql.append(getFullName(alter, alter.getTable()));
        modifySql.append(SqlConstant.ALTER);
        modifySql.append(SqlConstant.COLUMN);
        ColumnInfo columnInfo = alter.getColumnInfo();
        JdbcType jdbcType = JdbcType.getType(columnInfo.getType());
        if ((columnInfo.getNotnull() != null && columnInfo.getNotnull()) || columnInfo.getPk()) {
            modifySql.append("SET ");
            modifySql.append(SqlConstant.NOT_NULL);
        }
        if (columnInfo.getLength() != null && columnInfo.getLength() > 0) {
            modifySql.append("SET ");
            modifySql.append(SqlConstant.BEGIN_BRACKET);
            //字段长度
            modifySql.append(columnInfo.getLength());
            if (jdbcType.isFloat()) {
                modifySql.append(SqlConstant.COMMA);
                modifySql.append(columnInfo.getScale() == null ? 0 : columnInfo.getScale());
            }
            modifySql.append(SqlConstant.END_BRACKET);
        }
        return modifySql;
    }

    /**
     * 更改字段名
     *
     * @param alter
     * @return
     */
    private static String changeColumn(Alter alter) {
        StringBuffer changeSql = new StringBuffer();
        changeSql.append(SqlConstant.ALTER_TABLE);
        changeSql.append(getFullName(alter, alter.getTable()));
        changeSql.append(SqlConstant.RENAME);
        changeSql.append(SqlConstant.COLUMN);
        changeSql.append(alter.getOldColumnName());
        changeSql.append(SqlConstant.TO);
        changeSql.append(alter.getColumnInfo().getName());
        return changeSql.toString();
    }

    /**
     * 增加列备注
     *
     * @param item
     * @param transferred
     * @return
     */
    private static String addRemarks(Alter item, String transferred) {
        StringBuffer remarksSql = new StringBuffer();
        if (StringUtil.isNotBlank(item.getColumnInfo().getRemarks())) {
            remarksSql.append(SqlConstant.COMMENT);
            remarksSql.append(SqlConstant.ON);
            remarksSql.append(SqlConstant.COLUMN);
            remarksSql.append(getFullName(item, item.getTable()));
            remarksSql.append(SqlConstant.POINT);
            remarksSql.append(transferred);
            remarksSql.append(item.getColumnInfo().getName());
            remarksSql.append(transferred);
            remarksSql.append(SqlConstant.IS);
            remarksSql.append(SqlConstant.SINGLE_QUOTATION_MARK);
            remarksSql.append(item.getColumnInfo().getRemarks());
            remarksSql.append(SqlConstant.SINGLE_QUOTATION_MARK);
        }
        return remarksSql.toString();
    }

    /**
     * 重组
     *
     * @param item
     * @return
     */
    private static String recast(Alter item) {
        StringBuffer recastSql = new StringBuffer();
        recastSql.append("CALL SYSPROC.ADMIN_CMD");
        recastSql.append(SqlConstant.BEGIN_BRACKET);
        recastSql.append("'REORG TABLE ");
        recastSql.append(getFullName(item, item.getTable()));
        recastSql.append("'");
        recastSql.append(SqlConstant.END_BRACKET);
        return recastSql.toString();
    }

}
