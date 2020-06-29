package cn.vonce.sql.bean;


/**
 * 用于返回数据的结果集(Result Set)
 *
 * @author jovi
 * @version 1.0
 * @email 766255988@qq.com
 * @date 2017年5月24日上午11:32:38
 */
public class RS<T> {

    private Integer code;
    private String msg;
    private T data;


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    @Override
    public String toString() {
        return "RS{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
