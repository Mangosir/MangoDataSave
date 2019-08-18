package com.mango.clib.sqlite.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: mango
 * Time: 2019/8/16 21:10
 * Version:
 * Desc: 将对象映射到某个表 只有被@Table注解的Bean类才能被dao类操作
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    /**
     * 声明表名
     * @return
     */
    String value();
}
