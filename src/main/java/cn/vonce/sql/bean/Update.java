package cn.vonce.sql.bean;

import java.io.Serializable;

/**
 * 更新
 *
 * @author Jovi
 * @version 1.0
 * @email 766255988@qq.com
 * @date 2017年8月18日上午9:00:19
 */
public class Update extends Condition implements Serializable {

    private boolean updateNotNull = true;//默认只更新不为空的字段
    private Object updateBean = null;//更新的实体对象
    private String[] filterFields = null;//需要过滤的字段

    /**
     * 设置是否只更新不为null的字段
     *
     * @return
     */
    public boolean isUpdateNotNull() {
        return updateNotNull;
    }

    /**
     * 设置是否只更新不为null的字段
     *
     * @param updateNotNull
     */
    public void setUpdateNotNull(boolean updateNotNull) {
        this.updateNotNull = updateNotNull;
    }

    /**
     * 获取更新实体类
     *
     * @return
     */
    public Object getUpdateBean() {
        return updateBean;
    }

    /**
     * 设置更新实体类
     *
     * @param updateBean
     */
    public void setUpdateBean(Object updateBean) {
        this.updateBean = updateBean;
    }

    /**
     * 获取过滤的字段
     *
     * @return
     */
    public String[] getFilterFields() {
        return filterFields;
    }

    /**
     * 设置过滤的字段
     *
     * @param filterField
     */
    public void setFilterFields(String... filterField) {
        this.filterFields = filterField;
    }


}