package cn.vonce.sql.android.mapper;

import android.database.Cursor;

/**
 * 行数据映射
 *
 * @param <T>
 */
public interface RowMapper<T> {

    T mapRow(Cursor cursor, int index);

}
