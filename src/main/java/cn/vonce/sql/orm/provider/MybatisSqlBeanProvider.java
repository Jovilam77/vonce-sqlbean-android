package cn.vonce.sql.orm.provider;

import cn.vonce.sql.bean.Delete;
import cn.vonce.sql.bean.Paging;
import cn.vonce.sql.bean.Select;
import cn.vonce.sql.bean.Update;
import cn.vonce.sql.provider.SqlBeanProvider;

import java.util.Map;

/**
 * 通用的数据库操作sql语句生成
 *
 * @author Jovi
 * @version 1.0
 * @email 766255988@qq.com
 * @date 2018年5月15日下午2:23:47
 */
public class MybatisSqlBeanProvider extends SqlBeanProvider {

    /**
     * 根据id条件查询
     *
     * @param map
     * @return
     * @author Jovi
     * @date 2018年5月15日下午2:22:05
     */
    public String selectById(Map<String, Object> map) {
        return super.selectByIdSql((Class<?>) map.get("clazz"), map.get("id"));
    }

    /**
     * 根据条件查询
     *
     * @param map
     * @return
     * @author Jovi
     * @date 2018年5月15日下午2:21:33
     */
    public String selectByCondition(Map<String, Object> map) {
        Paging paging = null;
        if (map.containsKey("paging")) {
            paging = (Paging) map.get("paging");
        }
        Object[] args = null;
        if (map.containsKey("args")) {
            args = (Object[]) map.get("args");
        }
        return super.selectByConditionSql((Class<?>) map.get("clazz"), paging, (String) map.get("where"), args);
    }

    /**
     * 根据条件查询统计
     *
     * @param map
     * @return
     * @author Jovi
     * @date 2018年7月5日下午4:09:45
     */
    public String selectCountByCondition(Map<String, Object> map) {
        Object[] args = null;
        if (map.containsKey("args")) {
            args = (Object[]) map.get("args");
        }
        return super.selectCountByConditionSql((Class<?>) map.get("clazz"), (String) map.get("where"), args);
    }

    /**
     * 查询全部
     *
     * @param map
     * @return
     * @author Jovi
     * @date 2018年5月15日下午2:21:27
     */
    public String selectAll(Map<String, Object> map) {
        Paging paging = null;
        if (map.containsKey("paging")) {
            paging = (Paging) map.get("paging");
        }
        return super.selectAllSql((Class<?>) map.get("clazz"), paging);
    }

    /**
     * 根据自定义条件查询（可自动分页）
     *
     * @param map
     * @return
     * @author Jovi
     * @date 2018年5月15日下午2:21:05
     */
    public String select(Map<String, Object> map) {
        return super.selectSql((Class<?>) map.get("clazz"), (Select) map.get("select"));
    }

    /**
     * 根据自定义条件统计
     *
     * @param map
     * @return
     * @author Jovi
     * @date 2018年5月15日下午2:20:22
     */
    public String count(Map<String, Object> map) {
        return super.countSql((Class<?>) map.get("clazz"), (Select) map.get("select"));
    }

    /**
     * 根据id条件删除
     *
     * @param map
     * @return
     * @author Jovi
     * @date 2018年5月15日下午2:20:10
     */
    public String deleteById(Map<String, Object> map) {
        return super.deleteByIdSql((Class<?>) map.get("clazz"), map.get("id"));
    }

    /**
     * 根据条件删除
     *
     * @param map
     * @return
     * @author Jovi
     * @date 2018年5月15日下午2:19:59
     */
    public String deleteByCondition(Map<String, Object> map) {
        Object[] args = null;
        if (map.containsKey("args")) {
            args = (Object[]) map.get("args");
        }
        return super.deleteByConditionSql((Class<?>) map.get("clazz"), (String) map.get("where"), args);
    }

    /**
     * 删除
     *
     * @param map
     * @return
     * @author Jovi
     * @date 2019年1月12日下午2:19:59
     */
    public String delete(Map<String, Object> map) {
        return super.deleteSql((Class<?>) map.get("clazz"), (Delete) map.get("delete"), (boolean) map.get("ignore"));
    }

    /**
     * 逻辑删除
     *
     * @param map
     * @return
     * @author Jovi
     * @date 2019年6月6日下午16:41:20
     */
    public String logicallyDeleteById(Map<String, Object> map) {
        return super.logicallyDeleteByIdSql((Class<?>) map.get("clazz"), map.get("id"));
    }

    /**
     * 根据条件逻辑删除
     *
     * @param map
     * @return
     * @author Jovi
     * @date 2019年6月6日下午16:41:20
     */
    public String logicallyDeleteByCondition(Map<String, Object> map) {
        Object[] args = null;
        if (map.containsKey("args")) {
            args = (Object[]) map.get("args");
        }
        return super.logicallyDeleteByConditionSql((Class<?>) map.get("clazz"), (String) map.get("where"), args);
    }

    /**
     * 更新
     *
     * @param map
     * @return
     * @author Jovi
     * @date 2019年1月12日下午4:16:24
     */
    public String update(Map<String, Object> map) {
        return super.updateSql((Update) map.get("update"), (boolean) map.get("ignore"));
    }

    /**
     * 根据id条件更新
     *
     * @param map
     * @return
     * @author Jovi
     * @date 2018年5月15日下午2:19:24
     */
    public String updateById(Map<String, Object> map) {
        return super.updateByIdSql(map.get("bean"), map.get("id"), (boolean) map.get("updateNotNull"), (String[]) map.get("filterFields"));
    }

    /**
     * 根据实体类id条件更新
     *
     * @param map
     * @return
     * @author Jovi
     * @date 2018年5月15日下午2:19:24
     */
    public String updateByBeanId(Map<String, Object> map) {
        return super.updateByBeanIdSql(map.get("bean"), (boolean) map.get("updateNotNull"), (String[]) map.get("filterFields"));
    }

    /**
     * 根据条件更新
     *
     * @param map
     * @return
     * @author Jovi
     * @date 2018年5月15日下午2:18:03
     */
    public String updateByCondition(Map<String, Object> map) {
        Object[] args = null;
        if (map.containsKey("args")) {
            args = (Object[]) map.get("args");
        }
        return super.updateByConditionSql(map.get("bean"), (boolean) map.get("updateNotNull"), (String[]) map.get("filterFields"), (String) map.get("where"), args);
    }

    /**
     * 根据实体类字段条件更新
     *
     * @param map
     * @return
     * @author Jovi
     * @date 2018年5月15日下午2:16:36
     */
    public String updateByBeanCondition(Map<String, Object> map) {
        return super.updateByBeanConditionSql(map.get("bean"), (boolean) map.get("updateNotNull"), (String[]) map.get("filterFields"), (String) map.get("where"));
    }

    /**
     * 插入数据
     *
     * @param map
     * @return
     * @author Jovi
     * @date 2018年5月15日下午2:16:30
     */
    public String insertBean(Map<String, Object> map) {
        return super.insertBeanSql(map.get("beanList"));
    }

    /**
     * 插入数据
     *
     * @param map
     * @return
     * @author Jovi
     * @date 2018年5月15日下午2:16:30
     */
    public String insert(Map<String, Object> map) {
        return super.insertBeanSql(map.get("insert"));
    }

}
