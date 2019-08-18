package com.mango.datasave;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mango.clib.sqlite.MangoDaoFactory;
import com.mango.clib.tools.FileStorageTools;
import com.mango.datasave.sql.MyUserDao;
import com.mango.datasave.sql.SQLiteDBHelper;
import com.mango.datasave.sql.User;
import com.mango.datasave.sql.UserDao;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = MainActivity.class.getSimpleName();

    TextView add;
    TextView get;
    TextView del;
    TextView update;
    ImageView view;

    private UserDao userDao;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = (ImageView) findViewById(R.id.view);
        add = (TextView) findViewById(R.id.add);
        get = (TextView) findViewById(R.id.get);
        del = (TextView) findViewById(R.id.del);
        update = (TextView) findViewById(R.id.update);
        add.setOnClickListener(this);
        get.setOnClickListener(this);
        del.setOnClickListener(this);
        update.setOnClickListener(this);

//        userDao = new UserDao(this);
//        moveDBFile();

//        MyApplication.getDaoSession().getUserDao().insert(new com.mango.datasave.entity.User());
//        MyApplication.getDaoSession().getUserDao().delete(new com.mango.datasave.entity.User());
    }


    private void moveDBFile(){
        boolean isDbFileExist = true;
        File dbFile;
        String path = "/Android/data/"+getPackageName()+"/database/";
        File parent = new File(FileStorageTools.getInstance(this).makeFilePath(path));
        if (!parent.exists()) {
            parent.mkdirs();
            isDbFileExist = false;
            dbFile = FileStorageTools.getInstance(this).makeFile(parent, SQLiteDBHelper.DATABASE_NAME+".db");
        } else {
            dbFile = FileStorageTools.getInstance(this).makeFile(parent, SQLiteDBHelper.DATABASE_NAME+".db");
            if (!dbFile.exists()) {
                isDbFileExist = false;
            }
        }
        if (!isDbFileExist) {
            FileStorageTools.getInstance(this).moveResourceFileToExternalStorage(dbFile,
                    FileStorageTools.getInstance(this).getStreamFromRaw(R.raw.mango));
        }
    }



    int uid = 356;
    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.get:

                MyUserDao myUserDao = (MyUserDao) MangoDaoFactory.getDefault().getEntityDao(MyUserDao.class, User.class);
                Log.e("MainActivity","myUserDao="+myUserDao.count());
                break;
            case R.id.add:

                MangoDaoFactory.getDefault().getEntityDao(User.class).insert(new User(++uid,"tom"+uid,"1"));

                break;
            case R.id.update:

                break;
            case R.id.del:
                long delete = MangoDaoFactory.getDefault().getEntityDao(User.class).delete(new User(uid));
                Log.i("MainActivity","delete="+delete);
                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
