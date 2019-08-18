package com.mango.clib.sqlite.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: mango
 * Time: 2019/8/16 21:10
 * Version:
 * Desc: 将对象属性映射到表字段
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldName {
    /**
     * 声明字段名
     * @return
     */
    String value();
}
