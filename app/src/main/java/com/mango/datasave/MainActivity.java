package com.mango.datasave;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mango.datasave.sql.SQLiteDBHelper;
import com.mango.datasave.sql.UserDao;
import com.mango.datasave.tools.DisplayTools;
import com.mango.datasave.tools.FileStorageTools;

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

        userDao = new UserDao(this);
        moveDBFile();
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

    int uid = 5;
    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.get:

                break;
            case R.id.add:

                startActivity(new Intent(this,ItemPictureAct.class));

                break;
            case R.id.update:
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                Bitmap value = BitmapFactory.decodeResource(getResources(), R.mipmap.img_nav_01,options);
                view.setImageBitmap(value);
                int height = value.getHeight();
                int width = value.getWidth();
                Bitmap.Config config = value.getConfig();
                String name = config.name();
                int size = value.getAllocationByteCount();
                float density = DisplayTools.getDensity(this);
                int densityDpi = DisplayTools.getDensityDpi(this);
                Log.e(TAG,"height="+height+",width="+width+",name="+name+",size="+size+",density="+density+",densityDpi="+densityDpi);
                Log.e(TAG,"view Width="+view.getWidth()+",Height="+view.getHeight());
                break;
            case R.id.del:

                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
