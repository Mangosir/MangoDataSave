package com.mango.datasave.sql;

import com.mango.clib.sqlite.annotation.FieldName;
import com.mango.clib.sqlite.annotation.Irrelevant;
import com.mango.clib.sqlite.annotation.Key;
import com.mango.clib.sqlite.annotation.NotNull;
import com.mango.clib.sqlite.annotation.Table;
import com.mango.clib.sqlite.annotation.Unique;

/**
 * @Description TODO()
 * @author cxy
 * @Date 2018/11/5 14:53
 */
@Table("mango_user")
public class User {

    @FieldName("uid")
    @Key
    private int uid;

    @NotNull
    @Unique
    private String name;

    private String sex;

    @Irrelevant
    private String temp;

    public User() {
    }

    public User(int uid) {
        this.uid = uid;
    }

    public User(String name, String sex) {
        this.name = name;
        this.sex = sex;
    }

    public User(int uid, String name, String sex) {
        this.uid = uid;
        this.name = name;
        this.sex = sex;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

}
