package cn.vonce.sql.bean;

import cn.vonce.sql.uitls.SqlBeanUtil;

public class SpecialCondition extends Condition {

    private Table table = new Table();

    /**
     * 获取table
     *
     * @return
     */
    public Table getTable() {
        return table;
    }

    /**
     * 设置table
     *
     * @param name
     */
    public void setTable(String name) {
        this.table.setName(name);
        this.table.setAlias(name);
    }

    /**
     * 设置table
     *
     * @param name
     * @param aliasName
     */
    public void setTable(String name, String aliasName) {
        this.table.setName(name);
        this.table.setAlias(aliasName);
    }

    /**
     * 设置table
     *
     * @param name
     * @param aliasName
     */
    public void setTable(String schema, String name, String aliasName) {
        this.table.setSchema(schema);
        this.table.setName(name);
        this.table.setAlias(aliasName);
    }

    /**
     * 设置table
     *
     * @param table
     */
    public void setTable(Table table) {
        this.table = table;
    }

    /**
     * 设置table sql 内容
     *
     * @param clazz 表对应的实体类
     */
    public void setTable(Class<?> clazz) {
        this.table = SqlBeanUtil.getTable(clazz);
    }

}
