package cn.vonce.sql.bean;

import cn.vonce.sql.define.ColumnFunction;
import cn.vonce.sql.uitls.LambdaUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 更新
 *
 * @author Jovi
 * @version 1.0
 * @email 766255988@qq.com
 * @date 2017年8月18日上午9:00:19
 */
public class Update<T> extends CommonCondition<Update<T>> implements Serializable {

    public Update() {
        super();
        super.setReturnObj(this);
    }

    /**
     * 更新的实体对象
     */
    private T updateBean = null;
    /**
     * 需要过滤的字段
     */
    private String[] filterFields = null;
    /**
     * 默认只更新不为空的字段
     */
    private boolean updateNotNull = true;
    /**
     * 是否使用乐观锁
     */
    private boolean optimisticLock = false;

    /**
     * 更新的字段列表
     */
    private List<SetInfo> setInfoList = new ArrayList<>();

    public T getUpdateBean() {
        return updateBean;
    }

    public void setUpdateBean(T updateBean) {
        this.updateBean = updateBean;
    }

    public String[] getFilterFields() {
        return filterFields;
    }

    public void setFilterFields(String... filterFields) {
        this.filterFields = filterFields;
    }

    public boolean isUpdateNotNull() {
        return updateNotNull;
    }

    public void setUpdateNotNull(boolean updateNotNull) {
        this.updateNotNull = updateNotNull;
    }

    public boolean isOptimisticLock() {
        return optimisticLock;
    }

    public void setOptimisticLock(boolean optimisticLock) {
        this.optimisticLock = optimisticLock;
    }

    public List<SetInfo> getSetInfoList() {
        return setInfoList;
    }

    public void setSetInfoList(List<SetInfo> setInfoList) {
        this.setInfoList = setInfoList;
    }

    public Update<T> set(String columnName, Object value) {
        setInfoList.add(new SetInfo(columnName, value));
        return this;
    }

    public Update<T> set(String tableAlias, String columnName, Object value) {
        setInfoList.add(new SetInfo(tableAlias, columnName, value));
        return this;
    }

    public Update<T> set(Column column, Object value) {
        setInfoList.add(new SetInfo(column.getTableAlias(), column.getName(), value));
        return this;
    }

    public <R> Update<T> set(ColumnFunction<T, R> columnFunction, Object value) {
        Column column = LambdaUtil.getColumn(columnFunction);
        setInfoList.add(new SetInfo(column.getTableAlias(), column.getName(), value));
        return this;
    }

    public Update<T> setAdd(String columnName, Object value1, Object value2) {
        setInfoList.add(new SetInfo(SetInfo.Operator.ADDITION, columnName, value1, value2));
        return this;
    }

    public Update<T> setAdd(String tableAlias, String columnName, Object value1, Object value2) {
        setInfoList.add(new SetInfo(SetInfo.Operator.ADDITION, tableAlias, columnName, value1, value2));
        return this;
    }

    public Update<T> setAdd(Column column, Object value1, Object value2) {
        setInfoList.add(new SetInfo(SetInfo.Operator.ADDITION, column.getTableAlias(), column.getName(), value1, value2));
        return this;
    }

    public <R> Update<T> setAdd(ColumnFunction<T, R> columnFunction, Object value1, Object value2) {
        Column column = LambdaUtil.getColumn(columnFunction);
        setInfoList.add(new SetInfo(SetInfo.Operator.ADDITION, column.getTableAlias(), column.getName(), value1, value2));
        return this;
    }

    public <R> Update<T> setAdd(ColumnFunction<T, R> columnFunction, ColumnFunction<T, R> value1, Object value2) {
        Column column = LambdaUtil.getColumn(columnFunction);
        setInfoList.add(new SetInfo(SetInfo.Operator.ADDITION, column.getTableAlias(), column.getName(), value1, value2));
        return this;
    }

    public Update<T> setSub(String columnName, Object value1, Object value2) {
        setInfoList.add(new SetInfo(SetInfo.Operator.SUBTRACT, columnName, value1, value2));
        return this;
    }

    public Update<T> setSub(String tableAlias, String columnName, Object value1, Object value2) {
        setInfoList.add(new SetInfo(SetInfo.Operator.SUBTRACT, tableAlias, columnName, value1, value2));
        return this;
    }

    public Update<T> setSub(Column column, Object value1, Object value2) {
        setInfoList.add(new SetInfo(SetInfo.Operator.SUBTRACT, column.getTableAlias(), column.getName(), value1, value2));
        return this;
    }

    public <R> Update<T> setSub(ColumnFunction<T, R> columnFunction, Object value1, Object value2) {
        Column column = LambdaUtil.getColumn(columnFunction);
        setInfoList.add(new SetInfo(SetInfo.Operator.SUBTRACT, column.getTableAlias(), column.getName(), value1, value2));
        return this;
    }

    public <R> Update<T> setSub(ColumnFunction<T, R> columnFunction, ColumnFunction<T, R> value1, Object value2) {
        Column column = LambdaUtil.getColumn(columnFunction);
        setInfoList.add(new SetInfo(SetInfo.Operator.SUBTRACT, column.getTableAlias(), column.getName(), value1, value2));
        return this;
    }

}