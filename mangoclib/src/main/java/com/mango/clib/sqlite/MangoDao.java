package com.mango.clib.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.mango.clib.sqlite.annotation.FieldName;
import com.mango.clib.sqlite.annotation.Irrelevant;
import com.mango.clib.sqlite.annotation.Key;
import com.mango.clib.sqlite.annotation.NotNull;
import com.mango.clib.sqlite.annotation.Table;
import com.mango.clib.sqlite.annotation.Unique;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author: mango
 * Time: 2019/8/16 20:50
 * Version:
 * Desc: 所有dao的基类，封装业务操作
 */
public class MangoDao<T> implements IMangoDao<T> {

    /**
     * 数据库对象
     * */
    protected SQLiteDatabase mSqLiteDatabase;
    /**
     * 表名
     * */
    protected String mTableName;
    /**
     * 映射对象类型
    */
    private Class<T> mEntityClz;

    /**
     * key 表字段名
     * value 成员变量field
     */
    private Map<String,Field> mFieldName = new HashMap<>();

    /**
     * 使用代理模式 不能直接访问该对象
     */
    protected MangoDao(){}

    /**
     * 这里将初始化方法设置protected，不需要开发者调用
     * 要操作的表，字段等信息从注解中获取，避免开发者传入过多参数
     * @param sqLiteDatabase
     * @param entityClz
     * @return
     */
    protected boolean init(SQLiteDatabase sqLiteDatabase,Class<T> entityClz) {
        this.mSqLiteDatabase = sqLiteDatabase;
        this.mEntityClz = entityClz;
        //拿到表名
        mTableName = mEntityClz.getAnnotation(Table.class).value();
        if (TextUtils.isEmpty(mTableName)) {
            return false;
        }
        //如果数据库没创建或者没打开 直接返回
        if (mSqLiteDatabase == null || !mSqLiteDatabase.isOpen()) {
            return false;
        }
        /**
         * 接下来创建表 
         * 只知道表名 不知道字段名，需要通过注解获取
         */
        mSqLiteDatabase.execSQL(getCreateTableSql(entityClz));
        return true;
    }

    /**
     * 获取创表语句
     * @param entityClz
     * @return
     */
    private String getCreateTableSql(Class<T> entityClz){
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists ")
                .append(mTableName)
                .append(" (");

        StringBuilder sbUnique = new StringBuilder();

        Field[] declaredFields = entityClz.getDeclaredFields();
        if (declaredFields.length == 0) {
            throw new TableException("该映射对象无有效字段");
        }

        for (Field field : declaredFields) {

            //如果该字段只是作为普通属性，那就跳过
            Irrelevant irr = field.getAnnotation(Irrelevant.class);
            if (irr != null) {
                continue;
            }

            /**
             * 获取 表字段名称
             * 如果没有添加该字段，默认以属性名作为表字段名
             */
            String fieldName ;
            FieldName  name = field.getAnnotation(FieldName.class);
            if (name == null) {
                fieldName = field.getName();
            } else {
                fieldName = name.value();
            }

            //获取成员变量类型
            Class<?> type = field.getType();
            if (type == String.class) {
                sb.append(fieldName);
                sb.append(" TEXT");
                mFieldName.put(fieldName,field);
            } else if (type == int.class) {
                sb.append(fieldName);
                sb.append(" INTEGER");
                mFieldName.put(fieldName,field);
            } else if (type == long.class) {
                sb.append(fieldName);
                sb.append(" LONG");
                mFieldName.put(fieldName,field);
            } else if (type == double.class) {
                sb.append(fieldName);
                sb.append(" DOUBLE");
                mFieldName.put(fieldName,field);
            } else if (type == byte[].class) {
                sb.append(fieldName);
                //存放字节数组，比如将图片转成byte数组保存到数据库
                sb.append(" BLOB");
                mFieldName.put(fieldName,field);
            } else {
                continue;
            }

            //是否添加了主键注解
            Key key = field.getAnnotation(Key.class);
            if (key != null) {
                if (sb.toString().contains("primary")) {
                    throw new TableException("请勿添加多个主键");
                }
                if (type != int.class) {
                    throw new TableException("AUTOINCREMENT is only allowed on an INTEGER PRIMARY KEY");
                }
                sb.append(" primary key");
                if (key.autoincrement()) {
                    sb.append(" autoincrement");
                }
            }
            //该字段不允许为null
            NotNull notNull = field.getAnnotation(NotNull.class);
            if (notNull != null) {
                sb.append(" not null");
            }

            //字段要求唯一性
            Unique unique = field.getAnnotation(Unique.class);
            if (unique != null) {
                sbUnique.append(fieldName+",");
            }

            sb.append(",");

        }

        //去掉最后的逗号
        if (",".equals(String.valueOf(sbUnique.charAt(sbUnique.length() - 1)))) {
            sbUnique.deleteCharAt(sbUnique.length() - 1);
        }
        sb.append(" UNIQUE ("+ sbUnique.toString() + ")");

        sb.append(")");

        Log.i("MangoDao",""+sb.toString());
        return sb.toString();
    }

    /**
     * 插入数据
     * @param entity 插入对象
     * @return
     */
    @Override
    public long insert(T entity) {
        if (mSqLiteDatabase == null) {
            return -1;
        }
        Map<String,String> value = bindValue(entity);
        ContentValues values = getContentValues(value);
        long result = mSqLiteDatabase.insert(mTableName, null, values);
        return result;
    }

    @Override
    public long update(T entity) {
        if (mSqLiteDatabase == null) {
            return -1;
        }
        return 0;
    }

    @Override
    public long delete(T entity) {
        if (mSqLiteDatabase == null) {
            return -1;
        }
        String key = getKey();
        if (TextUtils.isEmpty(key)) {
            throw new TableException("未找到对应的主键");
        }
        String value = getValue(entity,key);
        if (TextUtils.isEmpty(value)) {
            throw new TableException("主键对应的值不能为null");
        }
        return mSqLiteDatabase.delete(mTableName,key + " = ?",new String[]{value});
    }

    @Override
    public T select(T entity) {
        if (mSqLiteDatabase == null) {
            return null;
        }
        List<String> unique = getUnique();
        if (unique.size() == 0) {
            throw new TableException("未找到具有唯一性的字段");
        }
        StringBuilder sb = new StringBuilder();
        String[] values = new String[unique.size()];
        for (int i=0; i<unique.size(); i++) {
            String value = getValue(entity, unique.get(i));
            values[i] = value;
            sb.append(unique.get(i) + " = ? and ");
        }

        Cursor cursor = mSqLiteDatabase.query(mTableName, null, sb.toString(), values, null, null, null);
        while (cursor.moveToNext()) {

        }
        return null;
    }

    public void delete(T... entity){
        if (entity == null) {
            return;
        }

        for (T t : entity) {
            delete(t);
        }
    }

    public long delete(String value){
        if (mSqLiteDatabase == null) {
            return -1;
        }
        String key = getKey();
        if (TextUtils.isEmpty(key)) {
            throw new TableException("未找到对应的主键");
        }
        return mSqLiteDatabase.delete(mTableName,key + " = ?",new String[]{value});
    }

    private List<String> getUnique(){
        List uniqueKey = new ArrayList();
        Iterator<Field> iterator = mFieldName.values().iterator();
        while (iterator.hasNext()) {
            Field field = iterator.next();
            Unique unique = field.getAnnotation(Unique.class);
            if (unique != null) {
                FieldName  name = field.getAnnotation(FieldName.class);
                if (name == null) {
                    uniqueKey.add( field.getName());
                } else {
                    uniqueKey.add(name.value());
                }
                break;
            }
        }
        return uniqueKey;
    }

    private String getKey(){
        String keyValue = null;
        Iterator<Field> iterator = mFieldName.values().iterator();
        while (iterator.hasNext()) {
            Field field = iterator.next();
            Key key = field.getAnnotation(Key.class);
            if (key != null) {
                FieldName  name = field.getAnnotation(FieldName.class);
                if (name == null) {
                    keyValue = field.getName();
                } else {
                    keyValue = name.value();
                }
                break;
            }
        }
        return keyValue;
    }

    private String getValue(T entity,String key){
        Field field = mFieldName.get(key);
        field.setAccessible(true);
        try {
            Object o = field.get(entity);
            String value = o.toString();
            return value;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ContentValues getContentValues(Map<String, String> value) {
        ContentValues values = new ContentValues();
        Set<String> keys = value.keySet();
        Iterator<String> iterator = keys.iterator();
        while(iterator.hasNext()){
            String key = iterator.next();
            String s = value.get(key);
            if (s != null) {
                values.put(key,s);
            }
        }
        return values;
    }

    private Map<String, String> bindValue(T entity) {

        Map<String,String> valueMap = new HashMap<>();
        Iterator<Field> iterator = mFieldName.values().iterator();
        while (iterator.hasNext()) {
            //遍历每个成员变量
            Field field = iterator.next();
            Irrelevant irr = field.getAnnotation(Irrelevant.class);
            if (irr != null) {
                continue;
            }
            field.setAccessible(true);
            try {
                //拿到变量值
                Object o = field.get(entity);
                if (o == null) {
                    continue;
                }
                String value = o.toString();
                NotNull notNull = field.getAnnotation(NotNull.class);
                if (notNull != null && TextUtils.isEmpty(value)) {
                    throw new TableException("非空字段必须要赋值");
                }

                String fieldName ;
                FieldName  name = field.getAnnotation(FieldName.class);
                if (name == null) {
                    fieldName = field.getName();
                } else {
                    fieldName = name.value();
                }
                valueMap.put(fieldName,value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }


        }
        return valueMap;
    }


}
