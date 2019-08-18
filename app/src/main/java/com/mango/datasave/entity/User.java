package com.mango.datasave.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Author: mango
 * Time: 2019/8/15 16:56
 * Version:
 * Desc:
 * @Entity：告诉GreenDao该对象为实体，只有被@Entity注解的Bean类才能被dao类操作
 *
 * @Id：对象的Id，也是主键，必须使用Long类型作为Entity的Id，否则会报错。(autoincrement=true)表示主键会自增，如果false就会使用旧值 。
 *
 * @Property：可以自定义字段名，默认是使用字段名，注意外键不能使用该属性
 *
 * @NotNull：表当前列不能为空
 *
 * @Unique：该属性值必须在数据库中是唯一值
 *
 * @Generated：编译后自动生成的构造函数、方法等的注解，提示构造函数、方法等不能被修改
 *
 * @Transient：使用该注解的属性不会被存入数据库的字段中，只是作为一个普通的java类字段
 */
@Entity
public class User {

    @Id(autoincrement = true)
    private Long id;

    @NotNull
    @Unique
    private String name;

    @Property(nameInDb = "userage")
    private int age;

    @Transient
    private String work;

    @Generated(hash = 955858333)
    public User(Long id, @NotNull String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    @Generated(hash = 586692638)
    public User() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return this.age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
