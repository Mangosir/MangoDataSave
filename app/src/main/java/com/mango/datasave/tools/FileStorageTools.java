package com.mango.datasave.tools;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


/**
 * @Description TODO(文件操作辅助类)
 * @author cxy
 * @Date 2018/10/30 16:26
 */
public class FileStorageTools {

    private String TAG = FileStorageTools.class.getSimpleName();

    private WeakReference<Context> mContext ;
    private static FileStorageTools instance;

    private FileStorageTools(Context context){
        mContext = new WeakReference<>(context);
    }
    public static FileStorageTools getInstance(Context context){
        if(instance == null){
            instance = new FileStorageTools(context);
        }
        return instance;
    }

    //获取ram可用内存
    public String getRAMAvailMem(){
        ActivityManager am=(ActivityManager)mContext.get().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return reviseFileSize(mi.availMem);
    }

    //获取ram总内存
    public String getRAMTotalMem(){
        ActivityManager am=(ActivityManager)mContext.get().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return reviseFileSize(mi.totalMem);
    }

    //获取sd卡总大小
    public String getSDTotalSize(){
        if(isSdCardMount()){
            File file= Environment.getExternalStorageDirectory();
            StatFs statFs=new StatFs(file.getPath());
            long blockSize=statFs.getBlockSizeLong();
            long totalBlocks=statFs.getBlockCountLong();
            return reviseFileSize(totalBlocks*blockSize);
        }else {
            return null;
        }
    }

    //获取sd卡可用大小
    public String getSDAvailableSize(){
        if(isSdCardMount()){
            File file= Environment.getExternalStorageDirectory();
            StatFs statFs=new StatFs(file.getPath());
            long blockSize=statFs.getBlockSizeLong();
            long availableBlocks=statFs.getFreeBlocksLong();
            return reviseFileSize(availableBlocks*blockSize);
        }else {
            return null;
        }
    }


    /**================================================内部存储操作=================================================================**/

    /**
     * 获取文件内容
     * @param fileName 内部存储中文件名
     * @return 按行读取文件内容
     */
    public List<String> getStringFromInternalStorage(String fileName){

        List<String> content = new ArrayList<>();
        InputStream is = null;
        BufferedReader br = null;
        try {
            is = mContext.get().openFileInput(fileName);
            br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            String line ;
            while ( (line = br.readLine()) != null){
                content.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {

            closeInputStream(is);
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return content;
    }


    /**
     * 获取内部存储文件数据
     * @param fileName 内部存储中文件名
     * @return 返回文件二进制数据，以便传输
     */
    public byte[] getDataFromInternalStorage(String fileName){

        BufferedInputStream bis = null;
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
            fis = mContext.get().openFileInput(fileName);
            bis = new BufferedInputStream(fis);
            bos = new ByteArrayOutputStream();
            byte[] buff = new byte[8*1024];
            int len ;
            while ((len = bis.read(buff)) != -1){
                bos.write(buff,0,len);
            }
            return bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            closeInputStream(fis,bis);
            closeOutputStream(bos);
        }
        return null;
    }

    /**
     * 保存字符串数据到内部存储
     * @param content 保存的内容
     * @param fileName 文件名
     * @param mode 访问模式
     */
    public void putStringToInternalStorage(String content, String fileName, int mode){

        FileOutputStream fos = null;
        try {
            fos = mContext.get().openFileOutput(fileName,mode);
            fos.write(content.getBytes());
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            closeOutputStream(fos);
        }
    }

    /**
     * 保存数据到内部存储
     * @param content 保存的内容
     * @param fileName 文件名
     * @param mode 访问模式
     */
    public void putDataToInternalStorage(byte[] content, String fileName, int mode){

        FileOutputStream fos = null;
        try {
            fos = mContext.get().openFileOutput(fileName,mode);
            fos.write(content);
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            closeOutputStream(fos);
        }
    }

    /**
     * 将内容保存到内部存储的缓存目录 尽量别将文件保存在这里，内存有限
     * @param content
     * @param fileName
     * @param append 是否追加到文件尾部
     */
    public void putInternalStorageCache(String content, String fileName, boolean append){

        FileOutputStream fos = null;
        try {
            File cache = new File(mContext.get().getCacheDir(),fileName);
            fos = new FileOutputStream(cache,append);
            fos.write(content.getBytes());
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            closeOutputStream(fos);
        }
    }

    /**====================================================外部存储操作=======================================================**/

    /**====================================构建目录=================================================**/

    /**
     * 获取公共目录
     * @param type DIRECTORY_MUSIC 音乐类型
     *             DIRECTORY_RINGTONES 铃声类型
     *             DIRECTORY_PODCASTS 播客音频类型
     *             DIRECTORY_ALARMS 闹钟提示音类型
     *             DIRECTORY_NOTIFICATIONS 通知提示音类型
     *             DIRECTORY_PICTURES 图片类型
     *             DIRECTORY_MOVIES 电影类型
     *             DIRECTORY_DOWNLOADS 下载文件类型
     *             DIRECTORY_DCIM 相机照片类型
     *             DIRECTORY_DOCUMENTS 文档类型
     * @return 相应类型目录文件 例如/storage/sdcard0/Music
     */
    public File getExternalStoragePublicDirectory(String type){
        File file = Environment.getExternalStoragePublicDirectory(type);
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }

    /**
     * 获取私有文件目录
     * @param type DIRECTORY_MUSIC 音乐类型
     *             DIRECTORY_PODCASTS 播客音频类型
     *             DIRECTORY_RINGTONES 铃声类型
     *             DIRECTORY_ALARMS 闹钟提示音类型
     *             DIRECTORY_NOTIFICATIONS 通知提示音类型
     *             DIRECTORY_PICTURES 图片类型
     *             DIRECTORY_MOVIES 电影类型
     * @return 相应类型目录文件 例如/storage/sdcard0/Android/data/com.mango.datasave/files/Music
     */
    public File getExternalStoragePrivateDirectory(String type){
        File file = mContext.get().getExternalFilesDir(type);
        return file;
    }

    /**
     * 获取私有缓存目录
     * @return /storage/sdcard0/Android/data/com.mango.datasave/cache
     */
    public File getExternalStoragePrivateCache(){
        File file = mContext.get().getExternalCacheDir();
        return file;
    }

    /**
     * 构建文件目录
     * @param path 例如 /file/movie
     * @return 返回完整目录 /storage/sdcard0/file/movie
     */
    public String makeFilePath(String path){
        if (StringTools.isEmpty(path)) throw new NullPointerException("path cant be null");
        return Environment.getExternalStorageDirectory().getAbsolutePath() + path;
    }


    /**
     * 创建文件
     * @param base
     * @param fileName
     * @return
     */
    public File makeFile(File base, String fileName){
        if (StringTools.isEmpty(fileName)) throw new NullPointerException("fileName cant be null");
        if(fileName.indexOf(File.separator) < 0){
            return new File(base,fileName);
        }
        throw new IllegalArgumentException(
                "File " + fileName + " contains a path separator");
    }

    /**====================================保存数据=================================================**/

    /**
     * 将内容写到外部存储文件
     * @param content 内容
     * @param parent 目标文件父目录
     * @param fileName 文件名
     * @param append 内容是追加到文件末尾还是覆盖
     */
    public void putStringToExternalStorage(String content, File parent, String fileName, boolean append){

        FileOutputStream fos = null;

        File file = makeFile(parent,fileName);
        try {
            fos = new FileOutputStream(file,append);
            fos.write(content.getBytes());
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            closeOutputStream(fos);
        }
    }

    /**
     * 将内容写到外部存储文件
     * @param parent 目标文件父目录
     * @param fileName 文件名
     * @param content 内容
     *                @String.getBytes()
     */
    public void putDataToExternalStorage(File parent, String fileName, byte[] content){

        FileOutputStream fos = null;
        try {
            File file = makeFile(parent,fileName);
            fos = new FileOutputStream(file);
            fos.write(content);
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            closeOutputStream(fos);
        }
    }

    /**
     * 将流数据保存到外部存储文件
     * @param parent 目标文件父目录
     * @param fileName 文件名
     * @param is 流
     */
    public File putStreamToExternalStorage(File parent, String fileName, InputStream is){

        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        File file = null;
        try {
            file = makeFile(parent,fileName);
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(new FileOutputStream(file));
            byte[] buff = new byte[8*1024];
            int len;
            while ( (len = bis.read(buff)) != -1) {
                bos.write(buff,0,len);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            if (file != null) {
                file.delete();
            }
            e.printStackTrace();
        }finally {
            closeInputStream(is,bis);
            closeOutputStream(bos);
        }
        return file;
    }

    /**
     * 将bitmap保存到外部存储文件
     * @param parent 目标文件父目录
     * @param fileName 文件名
     * @param bitmap 图片
     */
    public void putBitmapToExternalStorage(File parent, String fileName, Bitmap bitmap){

        BufferedOutputStream bos = null;

        File bit = makeFile(parent,fileName);
        if (bit.exists()) {
            bit.delete();
        }
        try {
            bos = new BufferedOutputStream(new FileOutputStream(bit));
            if (fileName.contains(".png") || fileName.contains(".PNG")) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            } else {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            }
            bos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            if (bit != null) {
                bit.delete();
            }
            e.printStackTrace();
        }finally {
            closeOutputStream(bos);
        }

    }


    /**====================================读取数据=================================================**/

    /**
     * 读取外部文件数据
     * @param path 文件路径
     * @param isModifiTime 是否更新文件信息中的更新时间
     * @return 文件的字节数组及文件对象
     */
    public Object[] getDataFromExternalStorage(String path, boolean isModifiTime){

        Object[] objects = new Object[2];
        byte[] data = null;

        File file = new File(path);
        if (!file.exists()) return objects;

        if (isModifiTime) {
            file.setLastModified(System.currentTimeMillis());
        }

        BufferedInputStream bis = null;
        ByteArrayOutputStream bos = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            bos = new ByteArrayOutputStream();
            byte[] buff = new byte[8*1024];
            int len;
            while ((len = bis.read(buff)) != -1) {
                bos.write(buff,0,len);
            }
            data = bos.toByteArray();
            bos .flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            closeInputStream(bis);
            closeOutputStream(bos);
        }

        objects[0] = data;
        objects[1] = file;
        return objects;
    }

    /**
     * 按行读取文件内容
     * @param path 文件路径
     * @return
     */
    public List<String> getStringFromExternalStorage(String path){

        File file = new File(path);
        if (!file.exists()) return null;

        List<String> data = new ArrayList<>();
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            isr = new InputStreamReader(new FileInputStream(file),"utf-8");
            br = new BufferedReader(isr);
            data.add(br.readLine());
            isr.close();
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**====================================文件拷贝操作=================================================**/

    /**
     * 单个文件复制
     * @param oldFile 原文件目录
     * @param newFile 新文件
     */
    public void copyFile(String oldFile, File newFile){

        File oldF = new File(oldFile);
        if(!oldF.exists()) return;

        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(oldF);
            fos = new FileOutputStream(newFile);
            byte[] buff = new byte[8*1024];
            int len;
            while ((len = fis.read(buff)) != -1) {
                fos.write(buff,0,len);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            closeInputStream(fis);
            closeOutputStream(fos);
        }
    }


    public void copyDirectory(String oldPath, String newPath){

    }

    /**====================================资源文件操作=================================================**/

    /**
     * 获取raw目录下文件数据流 调用 getDataFromRaw(R.raw.mango)
     * @param resourceID R.raw.mango
     * @return
     */
    public InputStream getStreamFromRaw(int resourceID){

        InputStream in = null;
        try {
            in = mContext.get().getResources().openRawResource(resourceID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return in;
    }

    /**
     * 获取assert目录下文件数据流
     * 调用getDataFromAssets("mango.txt")
     * 如果多层目录就要带上父级目录 getStreamFromAssets("today/day.txt")
     * @param fileName 文件全名，包括后缀
     * @return 数据流
     */
    public InputStream getStreamFromAssets(String fileName){
        InputStream in = null;
        try {
            in = mContext.get().getResources().getAssets().open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return in;
    }

    /**
     * 从资源目录下读取数据
     * @param is 数据流
     * @return
     */
    public byte[] getDataFromResource(InputStream is){

        if(is == null) return null;

        try {
            int lenght = is.available();
            byte[]  buffer = new byte[lenght];
            //将文件中的数据读到byte数组中
            is.read(buffer);
            is.close();
            return buffer;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将资源文件拷贝到外部存储
     * @param file 输出目标文件
     * @param is 资源文件流
     */
    public  void moveResourceFileToExternalStorage(File file, InputStream is){

        if(is == null) return;
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            byte[] buffer = new byte[2*1024];
            int len;
            while ((len = is.read(buffer)) != -1){
                os.write(buffer, 0, len);
            }
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            closeInputStream(is);
            closeOutputStream(os);
        }

    }

    /**====================================文件普通操作=================================================**/

    /**
     * 存放文件遍历数据
     * 切记每次遍历前清空此list
     */
    private List<File> fList = new ArrayList<>();

    public void clearFlist(){
        fList.clear();
    }

    /**
     * 给文件重命名
     * @param oldPath 原文件
     * @param newPath 新文件
     * @return
     */
    public boolean renameFile(String oldPath, String newPath){
        File oldFile = new File(oldPath);
        File newFile = new File(newPath);
        return oldFile.renameTo(newFile);
    }


    /**
     * 遍历目录
     * @param path 文件夹目录
     * @return
     */
    public List<File> listFile(String path){

        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        File[] data = file.listFiles();
        if(data == null) return null;

        for(int i=0; i<data.length; i++){
            File child = data[i];
            if (child.isFile()) {
                fList.add(child);
            } else {
                listFile(child.getAbsolutePath());
            }
        }
        return fList;
    }

    /**
     * 对文件按修改时间或者文件大小进行升序排列
     * @param files 文件集合
     * @param timeOrSize true time; false size
     * @return 排序后的文件数组
     */
    public File[] sortFile(List<File> files, final boolean timeOrSize){

        if (files == null || files.size() == 0) return null;

        File[] fData = new File[files.size()];
        fData = files.toArray(fData);
        Arrays.sort(fData, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                long l1 = 0l;
                long l2 = 0l;
                if (o1 == null) {
                    l1 = 0;
                }
                if (o2 == null) {
                    l2 = 0;
                }
                long result ;
                if (timeOrSize) {
                    result = l1 - l2;
                } else {
                    result = l1 - l2;
                }
                if (result > 0) {
                    return 1;
                } else if (result == 0) {
                    return 0;
                }
                return -1;
            }
        });

        return fData;
    }

    /**
     * 删除文件
     * @param path
     */
    public void delFile(String path){

        List<File> file = listFile(path);
        if(file == null)return;
        for(int i=0; i<file.size(); i++){
            file.get(i).delete();
        }
    }

    /**
     * 获取文件的文件名(不包括扩展名)
     */
    public String getFileNameWithoutExtension(String path) {
        if(path == null) {
            return null;
        }
        int separatorIndex = path.lastIndexOf(File.separator);
        if(separatorIndex < 0) {
            separatorIndex = 0;
        }
        int dotIndex = path.lastIndexOf(".");
        if(dotIndex < 0) {
            dotIndex = path.length();
        } else if(dotIndex < separatorIndex) {
            dotIndex = path.length();
        }
        return path.substring(separatorIndex + 1, dotIndex);
    }

    /**
     * 获取文件名
     */
    public String getFileName(String path) {
        if(path == null) {
            return null;
        }
        int separatorIndex = path.lastIndexOf(File.separator);
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
    }


    public void closeInputStream(InputStream... is){

        for (int i=0; i<is.length; i++){
            if (is[i] != null) {
                try {
                    is[i].close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void closeOutputStream(OutputStream... os){

        for (int i=0; i<os.length; i++){
            if (os[i] != null) {
                try {
                    os[i].close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 判断sd卡是否处于就绪状态 可读可写
     * MEDIA_UNKNOWN：未知状态
     * MEDIA_REMOVED：移除状态（外部存储不存在）
     * MEDIA_UNMOUNTED：未装载状态（外部存储存在但是没有装载）
     * MEDIA_CHECKING：磁盘检测状态
     * MEDIA_NOFS：外部存储存在，但是磁盘为空或使用了不支持的文件系统
     * MEDIA_MOUNTED：就绪状态（可读、可写）
     * MEDIA_MOUNTED_READ_ONLY：只读状态
     * MEDIA_SHARED：共享状态（外部存储存在且正通过USB共享数据）
     * MEDIA_BAD_REMOVAL：异常移除状态（外部存储还没有正确卸载就被移除了）
     * MEDIA_UNMOUNTABLE：不可装载状态（外部存储存在但是无法被装载，一般是磁盘的文件系统损坏造成的）
     * @return
     */
    public boolean isSdCardMount(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }


    public String reviseFileSize(long size){
        String str="KB";
        float reviseSize = 0f;
        if(size>1024){
            reviseSize = size/1024f;
            if(reviseSize>1024){
                str="M";
                reviseSize = reviseSize/1024f;
                if (reviseSize>1024) {
                    str="G";
                    reviseSize = reviseSize/1024f;
                }
            }
        }

        DecimalFormat formatter=new DecimalFormat();
        formatter.setGroupingSize(3);
        String result = formatter.format(reviseSize) + str;
        return result;
    }

}
