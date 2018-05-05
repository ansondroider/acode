package com.anson.acode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.provider.DocumentFile;
import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;

/**
 * Created by anson on 18-4-30.
 * for TF card.
 */

public class TFCardUtils {
    final static String TAG = "TFCardUtils";
    private static Context mContext;
    private static String tfRoot;
    private static final String PREF_FAKE_TF_DIR = "tf_fake_dir";
    private static final String PREF_TF_DIR = "tf_dir";
    public static void init(Context ctx){
        mContext = ctx;
        tfRoot = PreferenceUtils.getStringFromDefault(ctx, PREF_TF_DIR, "");
    }

    public static void getWritePermissionAboveAndroidM(Activity activity, String targetDevice){
        Intent intent = new Intent("android.intent.action.OPEN_DOCUMENT_TREE");
        if(!TextUtils.isEmpty(targetDevice)) {
            //intent.setData(Uri.fromFile(new File(targetDevice)));
            PreferenceUtils.setStringToDefault(activity, PREF_TF_DIR, targetDevice);
            tfRoot = targetDevice;
        }
        activity.startActivityForResult(intent, 0xfff0);
    }

    public static boolean onGetWritePermissionResult(Activity activity, int reqCode, int resultCode, Intent result){
        if(reqCode == 0xfff0){
            Uri treeUri;
            if (resultCode == Activity.RESULT_OK) {
                // Get Uri from Storage Access Framework.
                treeUri = result.getData();
                if(treeUri == null)return false;
                ALog.i(TAG, "treeUri = " + treeUri.toString());
                // Persist URI in shared preference so that you can use it later.
                // Use your own framework here instead of PreferenceUtil.
                PreferenceUtils.setStringToDefault(activity, PREF_FAKE_TF_DIR, treeUri.toString());

                // Persist access permissions.
                final int takeFlags = result.getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                activity.getContentResolver().takePersistableUriPermission(treeUri, takeFlags);
                return true;
            }
        }
        return false;
    }

    public static boolean isTfCardFile(File f){
        return f.getAbsolutePath().startsWith(tfRoot);
    }

    public static boolean deleteFile(File f) {
        return deleteFile(f.getAbsolutePath());
    }
    public static boolean deleteFile(String path) {
        DocumentFile df = getDocumentFilePath(path);
        if(df != null){
            DocumentFile[] dfs = df.listFiles();
            if(dfs != null && dfs.length > 0)return false;
            return df.delete();
        }

        return false;
    }

    public static String getFakeRootDir(Context ctx){
        return PreferenceUtils.getStringFromDefault(ctx, PREF_TF_DIR, "");
    }
    /*public static boolean copyFile(final File source, final File target) {
        FileInputStream inStream = null;
        OutputStream outStream = null;
        //FileChannel inChannel = null;
        //FileChannel outChannel = null;
        try {
            inStream = new FileInputStream(source);
            if (isWritable(target)) {
                // standard way
                outStream = new FileOutputStream(target);
                //inChannel = inStream.getChannel();
                //outChannel = ((FileOutputStream) outStream).getChannel();
                //inChannel.transferTo(0, inChannel.size(), outChannel);
                //outStream = null;
            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                // Storage Access Framework
                DocumentFile targetDocument = getDocumentFilePath(target.getAbsolutePath());
                if (targetDocument != null) {
                    outStream = mContext.getContentResolver().openOutputStream(targetDocument.getUri());
                }
            } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                // Workaround for Kitkat ext SD card
                Uri uri = getUriFromFile(context, target.getAbsolutePath());
                outStream = context.getContentResolver().openOutputStream(uri);
            } else {
                return false;
            }

            if (outStream != null) {
                // Both for SAF and for Kitkat, write to output stream.
                byte[] buffer = new byte[4096]; // MAGIC_NUMBER
                int bytesRead;
                while ((bytesRead = inStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            ALog.e("Error when copying file from " + source.getAbsolutePath() + " to " + target.getAbsolutePath());
            return false;
        } finally {
            try {
                if(inStream != null)inStream.close();
                if(outStream != null)outStream.close();
                //if(inChannel != null)inChannel.close();
                //if(outChannel != null)outChannel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }*/

    public static OutputStream getFileOutputStream(File dst) throws FileNotFoundException {
        if(dst != null){
            String path = dst.getAbsolutePath();
            DocumentFile df = getDocumentFilePath(path);
            if(df != null){
                return mContext.getContentResolver().openOutputStream(df.getUri());
            }
        }
        return null;
    }

    /**
     * Check is a file is writable. Detects write issues on external SD card.
     *
     * @param file The file
     * @return true if the file is writable.
     */
    public static boolean isWritable(final File file) {
        String path = file.getAbsolutePath();
        if(path.startsWith(Environment.getExternalStorageDirectory().getAbsolutePath()))return true;

        if(file.canWrite() && file.exists()){
            return true;
        }else{
            return file.exists() || createDocumentFolder(path);
        }
    }

    public static boolean isWritable(final String path) {
        return isWritable(new File(path));
    }


  /*  public static boolean canAccess(Context context, Uri treeUri){
        context.getContentResolver().takePersistableUriPermission(treeUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION |
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    }*/
    public static DocumentFile  getDocumentFilePath(String path) {
        String fakeSd = PreferenceUtils.getStringFromDefault(mContext, PREF_FAKE_TF_DIR, "");
        //log("getDocumentFilePath", "path(" + path + ")");
        if(!TextUtils.isEmpty(fakeSd)) {
            DocumentFile sdRoot = DocumentFile.fromTreeUri(mContext, Uri.parse(fakeSd));
            //log("getDocumentFilePath", sdRoot.getName() + ":" + fakeSd);
            String sdDir = PreferenceUtils.getStringFromDefault(mContext, PREF_TF_DIR, "");
            String[] parts = path.replace(sdDir, "").split("/");
            DocumentFile lastDoc = sdRoot;
            for (int i = 0; i < parts.length; i++) {
                //log("getDocumentFilePath", i + "= " + parts[i]);
                if(TextUtils.isEmpty(parts[i]))continue;
                DocumentFile targetDoc = lastDoc.findFile(parts[i]);
                if(targetDoc == null) {
                    if (i < parts.length - 1) {
                        //log("getDocumentFilePath", "create directory");
                        lastDoc = lastDoc.createDirectory(parts[i]);
                    } else {
                        String fileName = parts[i];
                        String mineType = FileUtils.getFileMineTypeWithMimeTypeMap(new File(path));

                        //log("getDocumentFilePath", "create file mineType(" + mineType + ")");
                        targetDoc = lastDoc.createFile(mineType.split("/")[0], fileName);
                        //log("getDocumentFilePath", "targetDoc(" + targetDoc.getUri().toString() + ")");
                        return targetDoc;
                    }
                }else{
                    //log("getDocumentFilePath", "Exists: targetDoc(" + targetDoc.getUri().toString() + ")");
                    lastDoc = targetDoc;
                    if(i == parts.length - 1)return targetDoc;
                }
            }
        }
        return null;
    }

    static void log(String method, String l){
        ALog.i(TAG, method + " " + l);
    }

    public static boolean createDocumentFolder(String folderWithoutRoot) {
        String fakeSd = PreferenceUtils.getStringFromDefault(mContext, PREF_FAKE_TF_DIR, "");
        if(!TextUtils.isEmpty(fakeSd)) {
            DocumentFile docRoot = DocumentFile.fromTreeUri(mContext, Uri.parse(fakeSd));
            ALog.i(TAG, "createDocumentFolder " + docRoot.getName() + ":" + fakeSd);

            String[] parts = folderWithoutRoot.split("/");
            DocumentFile lastDoc = docRoot;
            for (String part : parts) {
                if(TextUtils.isEmpty(part))continue;
                DocumentFile folder = lastDoc.findFile(part);
                if (folder == null) {
                    lastDoc = lastDoc.createDirectory(part);
                } else {
                    lastDoc = folder;
                }
            }
            ALog.i(TAG, "createDocumentFolder finish " + folderWithoutRoot);
            return true;
        }
        return false;
    }

}
