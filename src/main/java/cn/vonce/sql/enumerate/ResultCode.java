package cn.vonce.sql.enumerate;

/**
 * 服务器返回状态
 * 
 * @author Jovi
 * @email 766255988@qq.com
 * @version 1.0
 * @date 2017年11月27日下午3:33:09
 */
public enum ResultCode {

	SUCCESS(200), BAD(400), UNAUTHORIZED(401), OTHERS(402), PARAMETER(405), ERROR(500);

	private int code;

	private ResultCode() {

	}

	ResultCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return this.code;
	}

}
