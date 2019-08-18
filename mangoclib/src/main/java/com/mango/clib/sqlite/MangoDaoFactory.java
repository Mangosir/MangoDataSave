package com.mango.clib.sqlite;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: mango
 * Time: 2019/8/16 22:39
 * Version:
 * Desc: 供开发者使用的操作类
 */
public class MangoDaoFactory {

    //数据库对象
    private SQLiteDatabase      mDatabase;
    private Map<Class,MangoDao> mCacheEntityDao = new HashMap<>();
    private Map<Class,MangoDao> mCacheClassDao  = new HashMap<>();

    private static MangoDaoFactory ourInstance ;

    public static MangoDaoFactory getDefault() {
        if (ourInstance == null) {
            synchronized (MangoDaoFactory.class) {
                if (ourInstance == null) {
                    ourInstance = new MangoDaoFactory();
                }
            }
        }
        return ourInstance;
    }

    private MangoDaoFactory() { }

    /**
     * 创建/打开数据库
     * @param dbFile 数据库文件保存位置
     */
    public void initDataBase(File dbFile){
        mDatabase = SQLiteDatabase.openOrCreateDatabase(dbFile,null);
        Log.i("MangoDaoFactory","mDatabase="+mDatabase);
    }

    /**
     * 根据实体对象映射表
     * @param entity
     * @param <T>
     * @return
     */
    public<T> MangoDao<T> getEntityDao(Class<T> entity) {
        MangoDao mangoDao = mCacheEntityDao.get(entity);
        if (mangoDao != null) {
            return mangoDao;
        }

        try {
            mangoDao = MangoDao.class.newInstance();
            if(mangoDao.init(mDatabase, entity)){
                mCacheEntityDao.put(entity,mangoDao);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return mangoDao;
    }

    /**
     * 根据实体对象映射表
     * @param entity
     * @param <T>
     * @return
     */
    public<T> MangoDao<T> getEntityDao(Class daoClazz , Class<T> entity) {
        MangoDao mangoDao = mCacheClassDao.get(daoClazz);
        Log.i("MangoDaoFactory","mangoDao="+mangoDao);
        if (mangoDao != null ) {
            return mangoDao;
        }

        try {
            mangoDao = (MangoDao) daoClazz.newInstance();
            if(mangoDao.init(mDatabase, entity)){
                mCacheClassDao.put(daoClazz,mangoDao);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return mangoDao;
    }

    public<T> void clearEntityDao(Class<T> entity){
        if (mCacheClassDao.containsKey(entity)) {
            mCacheClassDao.remove(entity);
        }
    }

    public void clear(){
        mCacheClassDao.clear();
    }


}
