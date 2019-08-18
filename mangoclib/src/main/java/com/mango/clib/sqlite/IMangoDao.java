package com.mango.clib.sqlite;

/**
 * Author: mango
 * Time: 2019/8/16 20:46
 * Version:
 * Desc: 定义所有的数据库操作
 */
public interface IMangoDao<T> {

    /**
     * 数据插入操作
     * @param entity 插入对象
     * @return
     */
    long insert(T entity);

    /**
     * 输入更新操作
     * @param entity
     * @return
     */
    long update(T entity);

    /**
     * 数据删除操作
     * @param entity
     * @return
     */
    long delete(T entity);

    T select(T entity);

}
