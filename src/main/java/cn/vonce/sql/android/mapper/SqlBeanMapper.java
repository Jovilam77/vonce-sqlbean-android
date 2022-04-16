package cn.vonce.sql.android.mapper;


import android.database.Cursor;

import cn.vonce.sql.annotation.SqlJoin;
import cn.vonce.sql.bean.ColumnInfo;
import cn.vonce.sql.bean.TableInfo;
import cn.vonce.sql.constant.SqlConstant;
import cn.vonce.sql.uitls.DateUtil;
import cn.vonce.sql.uitls.ReflectUtil;
import cn.vonce.sql.uitls.SqlBeanUtil;
import cn.vonce.sql.uitls.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SqlBean 结果映射
 *
 * @author Jovi
 */
public class SqlBeanMapper<T> implements RowMapper<T> {

    public Class<?> clazz;
    public Class<?> returnType;

    public SqlBeanMapper(Class<?> clazz, Class<?> returnType) {
        this.clazz = clazz;
        this.returnType = returnType;
    }

    @Override
    public T mapRow(Cursor cursor, int index) {
        Object object = null;
        if (cursor.moveToNext()) {
            if (returnType.getName().equals(ColumnInfo.class.getName()) || returnType.getName().equals(TableInfo.class.getName())) {
                return (T) beanHandleResultSet(returnType, cursor);
            }
            if (SqlBeanUtil.isBaseType(returnType.getName())) {
                return (T) baseHandleResultSet(cursor, returnType);
            }
            if (SqlBeanUtil.isMap(returnType.getName())) {
                return (T) mapHandleResultSet(cursor);
            }
            return (T) beanHandleResultSet(clazz, cursor);
        }
        return (T) object;
    }

    /**
     * 基础对象映射
     *
     * @param cursor
     * @return
     */
    public Object baseHandleResultSet(Cursor cursor, Class<?> returnType) {
        Object value = null;
        value = getValue(cursor.getType(0), 0, cursor);
        if (value != null && !value.getClass().getName().equals(returnType.getName())) {
            value = getValueConvert(returnType.getName(), value);
        }
        if (value == null || value.equals("null")) {
            value = getDefaultValueByColumnType(cursor.getType(0));
        }
        return value;
    }

    /**
     * map对象映射
     *
     * @param cursor
     * @return
     */
    public Object mapHandleResultSet(Cursor cursor) {
        Map<String, Object> map = new HashMap<>();
        for (int i = 1; i <= cursor.getColumnCount(); i++) {
            Object value = getValue(cursor.getType(i), i, cursor);
            if (value == null || value.equals("null")) {
                value = getDefaultValueByColumnType(cursor.getType(i));
            }
            map.put(cursor.getColumnName(i), value);
        }
        return map;
    }

    /**
     * bean对象映射处理
     *
     * @param cursor
     * @param clazz
     * @return
     */
    public Object beanHandleResultSet(Class<?> clazz, Cursor cursor) {
        List<String> columnNameList = Arrays.asList(cursor.getColumnNames());
        Object bean = null;
        try {
            bean = clazz.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        String tableAlias = SqlBeanUtil.getTable(clazz).getAlias();
        List<Field> fieldList = SqlBeanUtil.getBeanAllField(clazz);
        for (Field field : fieldList) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            SqlJoin sqlJoin = field.getAnnotation(SqlJoin.class);
            String fieldName = field.getName();
            if (sqlJoin != null) {
                if (sqlJoin.isBean()) {
                    Class<?> subClazz = field.getType();
                    Object subBean = ReflectUtil.instance().newObject(subClazz);
                    //获取表的别名，先是获取别名，获取不到就会获取表名
                    String subTableAlias = SqlBeanUtil.getTable(subClazz).getAlias();
                    //如果在SqlBeanJoin中设置了表名，那么优先使用该表名，如果有多个联表查询的对象需要连接同一张表的，那么需要保证表名一致
                    if (StringUtil.isNotEmpty(sqlJoin.table())) {
                        subTableAlias = sqlJoin.table();
                    }
                    //如果在SqlBeanJoin中设置了别名，那么优先使用该别名，如果有多个联表查询的对象需要连接同一张表的，那么需要保证别名一致
                    if (StringUtil.isNotEmpty(sqlJoin.tableAlias())) {
                        subTableAlias = sqlJoin.tableAlias();
                    }
                    Field[] subFields = subClazz.getDeclaredFields();
                    for (Field subField : subFields) {
                        if (Modifier.isStatic(subField.getModifiers())) {
                            continue;
                        }
                        String subFieldName = subField.getName();
                        subFieldName = subTableAlias + SqlConstant.UNDERLINE + subFieldName;
                        setFieldValue(subBean, subField, subFieldName, cursor);
                    }
                    ReflectUtil.instance().set(bean.getClass(), bean, fieldName, subBean);
                    continue;
                } else {
                    String subTableAlias = sqlJoin.table();
                    if (StringUtil.isNotEmpty(sqlJoin.tableAlias())) {
                        subTableAlias = sqlJoin.tableAlias();
                    }
                    setFieldValue(bean, field, subTableAlias + SqlConstant.UNDERLINE + fieldName, cursor);
                }
            } else {
                //优先使用 表别名+字段名才方式匹配
                String newFieldName = tableAlias + SqlConstant.UNDERLINE + fieldName;
                if (!columnNameList.contains(newFieldName)) {
                    //其次通过驼峰转下划线方式匹配
                    newFieldName = StringUtil.humpToUnderline(fieldName);
                    if (!columnNameList.contains(newFieldName)) {
                        //再其次通过字段名匹配
                        newFieldName = fieldName;
                    }
                }
                setFieldValue(bean, field, newFieldName, cursor);
            }
        }
        return bean;
    }

    /**
     * 字段赋值
     *
     * @param obj
     * @param field
     * @param fieldName
     * @param cursor
     */
    public void setFieldValue(Object obj, Field field, String fieldName, Cursor cursor) {
        Object value = getValue(field.getType().getName(), fieldName, cursor);
        if (value == null || value.equals("null")) {
            value = getDefaultValue(field.getType().getName());
        }
        ReflectUtil.instance().set(obj.getClass(), obj, field.getName(), value);
    }

    /**
     * 获取该字段对应的值
     *
     * @param fieldType
     * @param fieldName
     * @param cursor
     * @return
     */
    public Object getValue(String fieldType, String fieldName, Cursor cursor) {
        Object value = null;
        int index = cursor.getColumnIndex(fieldName);
        if (index == -1) {
            return null;
        }
        switch (fieldType) {
            case "byte":
            case "java.lang.Byte":
                value = Byte.parseByte(cursor.getShort(index) + "");
                break;
            case "short":
            case "java.lang.Short":
                value = cursor.getShort(index);
                break;
            case "int":
            case "java.lang.Integer":
                value = cursor.getInt(index);
                break;
            case "float":
            case "java.lang.Float":
                value = cursor.getFloat(index);
                break;
            case "double":
            case "java.lang.Double":
                value = cursor.getDouble(index);
                break;
            case "long":
            case "java.lang.Long":
                value = cursor.getLong(index);
                break;
            case "boolean":
            case "java.lang.Boolean":
                short bool = cursor.getShort(index);
                if (bool > 0) {
                    value = true;
                } else {
                    value = false;
                }
                break;
            case "char":
            case "java.lang.Character":
                value = cursor.getString(index);
                if (StringUtil.isNotEmpty(value)) {
                    value = value.toString().charAt(0);
                }
                break;
            case "java.lang.String":
                value = cursor.getString(index);
                break;
            case "java.sql.Date":
                value = new java.sql.Date(cursor.getLong(index));
                break;
            case "java.sql.Time":
                value = new java.sql.Time(cursor.getLong(index));
                break;
            case "java.sql.Timestamp":
                value = new java.sql.Timestamp(cursor.getLong(index));
                break;
            case "java.util.Date":
                //先取得long类型，转String之后如果长度是10或13那么则为时间戳
                long timestamp = cursor.getLong(index);
                if (timestamp != 0) {
                    String stringTimestamp = timestamp + "";
                    if (stringTimestamp.length() == 10 || stringTimestamp.length() == 13) {
                        value = new java.util.Date(timestamp);
                    } else {
                        value = DateUtil.stringToDate(cursor.getString(index));
                    }
                }
                break;
            case "java.math.BigDecimal":
                value = new BigDecimal(cursor.getDouble(index));
                break;
            default:
                value = cursor.getBlob(index);
                break;
        }
        return value;

    }

    /**
     * 获取该字段对应的值
     *
     * @param jdbcType
     * @param index
     * @param cursor
     * @return
     */
    public Object getValue(int jdbcType, int index, Cursor cursor) {
        Object value = null;
        switch (jdbcType) {
            case Cursor.FIELD_TYPE_INTEGER:
                value = cursor.getLong(index);
                break;
            case Cursor.FIELD_TYPE_FLOAT:
                value = cursor.getDouble(index);
                break;
            case Cursor.FIELD_TYPE_STRING:
                value = cursor.getString(index);
                break;
            case Cursor.FIELD_TYPE_BLOB:
                value = cursor.getBlob(index);
                break;
        }
        return value;

    }

    /**
     * 获取基本类型默认值
     *
     * @param typeName
     * @return
     */
    public static Object getDefaultValue(String typeName) {
        Object value = null;
        switch (typeName) {
            case "byte":
            case "java.lang.Byte":
                value = new Byte("0");
                break;
            case "short":
            case "java.lang.Short":
                value = new Short("0");
                break;
            case "int":
            case "java.lang.Integer":
                value = 0;
                break;
            case "long":
            case "java.lang.Long":
                value = 0L;
                break;
            case "float":
            case "java.lang.Float":
                value = 0F;
                break;
            case "double":
            case "java.lang.Double":
                value = 0D;
                break;
            case "char":
            case "java.lang.Char":
                value = '\u0000';
                break;
            case "boolean":
            case "java.lang.Boolean":
                value = false;
                break;
        }
        return value;
    }

    /**
     * 获取基本类型默认值
     *
     * @param jdbcType
     * @return
     */
    public static Object getDefaultValueByColumnType(int jdbcType) {
        Object value = null;
        switch (jdbcType) {
            case Cursor.FIELD_TYPE_INTEGER:
                value = 0;
                break;
            case Cursor.FIELD_TYPE_FLOAT:
                value = 0f;
                break;
            case Cursor.FIELD_TYPE_STRING:
            case Cursor.FIELD_TYPE_BLOB:
                value = null;
                break;
        }
        return value;
    }

    /**
     * 获取转换后的值
     *
     * @param typeName
     * @param value
     * @return
     */
    public static Object getValueConvert(String typeName, Object value) {
        Object newValue = value;
        switch (typeName) {
            case "byte":
            case "java.lang.Byte":
                newValue = new Byte(value.toString());
                break;
            case "short":
            case "java.lang.Short":
                newValue = new Short(value.toString());
                break;
            case "int":
            case "java.lang.Integer":
                newValue = new Integer(value.toString());
                break;
            case "long":
            case "java.lang.Long":
                newValue = new Long(value.toString());
                break;
            case "float":
            case "java.lang.Float":
                newValue = new Float(value.toString());
                break;
            case "double":
            case "java.lang.Double":
                newValue = new Double(value.toString());
                break;
            case "boolean":
            case "java.lang.Boolean":
                newValue = new Boolean(value.toString());
                break;
            case "char":
            case "java.lang.Character":
                newValue = value.toString().charAt(0);
                break;
            case "java.lang.String":
                newValue = value.toString();
                break;
        }
        return newValue;
    }

}
