package com.mango.datasave.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @Description TODO(提供对user的操作)
 * @author cxy
 * @Date 2018/11/5 14:50
 */
public class UserDao extends BaseDao{

    private String TAG = UserDao.class.getSimpleName();

    public UserDao(Context context) {
        super(context);
    }

    public void addUser(User user){
        SQLiteDatabase database = mHelper.getWritableDatabase();
        String sql = "insert into " + SQLiteDBHelper.TABLE_USER + "(uid,name,sex,role) values(?,?,?,?)";
        database.execSQL(sql,new String[]{user.getUid(),user.getName(),user.getSex(),user.getRole()});
        database.close();
    }

    /**
     *
     * @param user
     * @return 插入的新纪录的行号，即是第多少行
     */
    public long addUserValues(User user){
        SQLiteDatabase database = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("uid",user.getUid());
        values.put("name",user.getName());
        values.put("sex",user.getSex());
        values.put("role",user.getRole());
        long rowNum = database.insert(SQLiteDBHelper.TABLE_USER,null,values);
        database.close();
        return rowNum;
    }

    public void updateUser(String uid, String name, String sex){
        SQLiteDatabase database = mHelper.getWritableDatabase();
        String sql = "update " + SQLiteDBHelper.TABLE_USER + " set name = ? where uid = ? and sex = ?";
        database.execSQL(sql,new String[]{name,uid,sex});
        database.close();
    }

    public void updateMoreUser(String uid, String uid2, String uid3, String name){
        SQLiteDatabase database = mHelper.getWritableDatabase();
        String sql = "update " + SQLiteDBHelper.TABLE_USER + " set name = ? where uid in (?,?,?)";
        database.execSQL(sql,new String[]{name,uid,uid2,uid3});
        database.close();
    }

    /**
     *
     * @param sex
     * @param name
     * @return 受影响的行数总和
     */
    public int updateUserValues(String sex, String name){
        SQLiteDatabase database = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name",name);
        int rowNum = database.update(SQLiteDBHelper.TABLE_USER,values,"sex = ?",new String[]{sex});
        database.close();
        return rowNum;
    }

    public void delUser(String uid){
        SQLiteDatabase database = mHelper.getWritableDatabase();
        String sql = "delete from " + SQLiteDBHelper.TABLE_USER + " where uid = ?";
        database.execSQL(sql,new String[]{uid});
        database.close();
    }

    public void delAllUser(){
        SQLiteDatabase database = mHelper.getWritableDatabase();
        String sql = "delete from " + SQLiteDBHelper.TABLE_USER ;
        database.execSQL(sql);
        database.close();
    }

    /**
     *
     * @param sex
     * @return  受影响的行数总和
     */
    public int delUserValues(String sex){
        SQLiteDatabase database = mHelper.getWritableDatabase();
        int rowNum = database.delete(SQLiteDBHelper.TABLE_USER,"sex = ?",new String[]{sex});
        database.close();
        return rowNum;
    }

    /**
     *
     * @return 受影响的行数总和
     */
    public int delALLUserValues(){
        SQLiteDatabase database = mHelper.getWritableDatabase();
        int rowNum = database.delete(SQLiteDBHelper.TABLE_USER,null,null);
        database.close();
        return rowNum;
    }

    public User getUser(String uid){
        User user = new User();
        SQLiteDatabase database = mHelper.getWritableDatabase();
        String sql = "select * from "+ SQLiteDBHelper.TABLE_USER + " where uid = ?";
        Cursor cursor = database.rawQuery(sql,new String[]{uid});
        while (cursor.moveToNext()) {
            user.setUid(cursor.getInt(cursor.getColumnIndex("uid"))+"");
            user.setName(cursor.getString(cursor.getColumnIndex("name")));
            user.setSex(cursor.getString(cursor.getColumnIndex("sex")));
            user.setRole(cursor.getString(cursor.getColumnIndex("role")));
        }
        cursor.close();
        database.close();
        return user;
    }


}
