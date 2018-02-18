package com.classicloner.runjs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import static android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY;
import static com.classicloner.runjs.MainActivity.myfunctionList;


/**
 * Created by Raheman on 6/11/2017.
 */

public class MyFunctions extends Activity implements View.OnTouchListener {
    public static final int WRITE_REQUEST_CODE = 43;
    public String mimeType = "application/*";
    public static int GOT_PERMISSION_TO_WRITE = 0;
    public static final int MY_WRITE_EXTERNAL_STORAGE_CODE = 1;
    public static boolean INCOGNITO_MODE = false;

    public static String sdcardPath = Environment.getExternalStorageDirectory().toString();
    public static String appName = "RunJs";
    public static String appPath = sdcardPath+"/"+appName;

    static String scriptFile =sdcardPath+"/"+appName+"/Scripts";
    static String offlineFolder =sdcardPath+"/"+appName+"/SavedPages";
    static String settingFile =sdcardPath+"/"+appName+"/Settings";
    static String downloadFile =sdcardPath+"/"+appName+"/Downloads";
    static String defaultDownloadFile =downloadFile;
    static String incognitoDownloadFile =sdcardPath+"/"+appName+"/Downloads/.incognito";

    static String configFile = sdcardPath+"/"+appName+"/Settings/Config.txt";
    static String cacheFile = sdcardPath+"/"+appName+"/Settings/.Cache";
    static String bookmarkFile = sdcardPath+"/"+appName+"/Settings/.bookmarks";
    static String historyCache = sdcardPath+"/"+appName+"/Settings/.history";

    public static String double_touch_js = scriptFile+"/double_touch.js";
    public static String long_touch_js = scriptFile+"/long_touch.js";

    public static String currentJsFile = "Unsaved.js";
    public static String currentJsFilePath = "";
    public static final int READ_REQUEST_CODE = 42;
    public static final int LONG_TOUCH_REQUEST_CODE = 40;
    public static final int DOUBLE_TOUCH_REQUEST_CODE = 41;
    public static final int LONG_TOUCH = 0;
    public static final int DOUBLE_TOUCH = 1;

    public static String resetConfig = LONG_TOUCH+":"+double_touch_js+"\n"+LONG_TOUCH+":"+long_touch_js+"\n";
    public static String long_touch_script ;
    public static String double_touch_script ;
    public static Context main_Activity_Context;

    public MyFunctions(Context context) {
        main_Activity_Context = context;
    }
    public  void createFile(String mimeType, String fileName , Context fromContext) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);

        // Filter to only show results that can be "opened", such as
        // a file (as opposed to a list of contacts or timezones).
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Create a file with the requested MIME type.
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        ((Activity) fromContext).startActivityForResult(intent, WRITE_REQUEST_CODE);
    }

    public void alterDocument(Uri uri , String jsContent , Context fromContext) {
        try {
            ParcelFileDescriptor pfd = fromContext.getContentResolver().
                    openFileDescriptor(uri, "w");
            FileOutputStream fileOutputStream =
                    new FileOutputStream(pfd.getFileDescriptor());
            fileOutputStream.write((jsContent).getBytes());
            Log.i("CHECK", "data: " + jsContent);
            // Let the document provider know you're done by closing the stream.
            fileOutputStream.close();
            pfd.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    public void performFileSearch(Context fromContext , int CODE) {
        Log.d("PERFORM", "SEARCH");
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent( Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        //intent.setDataAndType(Uri.parse(scriptFile), "resource/folder");
        intent.setDataAndType(Uri.parse(scriptFile), mimeType);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        //intent.setType(mimeType);

        ((Activity) fromContext).startActivityForResult(intent, CODE);
    }



    public String readTextFromUri(Uri uri,Context fromContext) throws IOException {
        InputStream inputStream = fromContext.getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line+"\n");
        }
        inputStream.close();
        return stringBuilder.toString();
    }

    public void hideKeyBoard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), HIDE_IMPLICIT_ONLY );
        }
    }

    public String readFromFile(String filename , Context context) {
        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString+"\n");
                }
                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return ret;
    }

    public void writeToFile(String filename , String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public void writeToExtFile( String filename , String data){
        FileOutputStream fos;
        byte[] datax = data.getBytes();
        try {
            fos = new FileOutputStream(filename);
            fos.write(datax);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String readFromExtFile( String filename ){
        createFileInExternal(filename);
        File file = new File (filename);
        if (!file.exists ()) {
            return null;
        }
        FileInputStream fis = null;
        StringBuffer fileContent = new StringBuffer("");
        Integer n;
        try {
            fis = new FileInputStream(filename);
            byte[] buffer = new byte[1024];
            while ((n = fis.read(buffer)) != -1)
            {
                fileContent.append(new String(buffer, 0, n));
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return  fileContent.toString();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    public boolean deleteFile(String filename) {
        return new File(filename).delete();
    }

    public String createFolderInExternal(String foldername){
        File tempFolder = new File(foldername);
        if ( !tempFolder.exists() ) {
            tempFolder.mkdirs();
        }
        return  tempFolder.getPath();
    }

    public boolean isExist(String filepath) {
        return new File(filepath).exists();
    }
    public String createFileInExternal(String filename ){
        File file = new File (filename);
        if (!file.exists ()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.getPath();
    }

    public static void load_scripts(){
        long_touch_script = myfunctionList.readFromExtFile(long_touch_js);
        if ( long_touch_script!=null && long_touch_script.isEmpty()){//file exists but empty
            long_touch_script = "" +
                    " var  obj=document.elementFromPoint(_x,_y);\n" +
                    " if ( obj.parentNode ){\n" +
                    "       tag = 'video';\n" +
                    "       if ( obj.parentNode.getElementsByTagName(tag).length == 0 )\n" +
                    "           tag = 'img';\n" +
                    "       if ( srcNode = obj.parentNode.getElementsByTagName(tag)[0] )\n" +
                    "           var src = srcNode.src; \n" +
                    "       else\n" +
                    "           var src = null;\n" +
                    " }\n" +
                    "\n" +
                    " else{\n" +
                    "       if ( srcNode = obj.getElementsByTagName(tag)[0] )\n" +
                    "           var src = srcNode.src; \n" +
                    "       else\n" +
                    "           var src = null;\n" +
                    " }\n" +
                    " var str ='null';\n" +
                    " if ( src ){\n" +
                    "   filterkeyImage = \".jpg\" ;\n" +
                    "   filterkeyVideo = \".mp4\" ;\n" +
                    "   if ( src.indexOf(filterkeyImage) != -1 || src.indexOf(filterkeyVideo) != -1){\n" +
                    "        str = \"Starting download: \"+src ;" +
                    "        var link = document.createElement(\"a\");\n" +
                    "        link.href = src.split(\"?\")[0];\n" +
                    "        link.download = src.split(\"?\")[0].split(\"/\").pop();\n" +
                    "        link.style.display = \"none\";\n" +
                    "        var evt = new MouseEvent(\"click\", {\n" +
                    "            \"view\": window,\n" +
                    "            \"bubbles\": true,\n" +
                    "            \"cancelable\": true\n" +
                    "        });\n" +
                    "        document.body.appendChild(link);\n" +
                    "        link.dispatchEvent(evt);\n" +
                    "        document.body.removeChild(link);\n" +
                    "   }\n" +
                    " }" +
                    "return str;";
                    myfunctionList.writeToExtFile(long_touch_js , long_touch_script);
        }
        double_touch_script = myfunctionList.readFromExtFile(double_touch_js);

        if ( double_touch_script==null){
            double_touch_script = "";
            //myfunctionList.writeToExtFile(double_touch_js , double_touch_script);
        }
    }

    public static void load_scripts_for_touch_event(String content , int TOUCH_CODE){
        if ( TOUCH_CODE == LONG_TOUCH_REQUEST_CODE) {
            long_touch_script = content;
            if (long_touch_script.isEmpty()) {
                long_touch_script = "" +
                        " var  obj=document.elementFromPoint(_x,_y);\n" +
                        " if ( obj.parentNode ){\n" +
                        "       tag = 'video';\n" +
                        "       if ( obj.parentNode.getElementsByTagName(tag).length == 0 )\n" +
                        "           tag = 'img';\n" +
                        "       if ( srcNode = obj.parentNode.getElementsByTagName(tag)[0] )\n" +
                        "           var src = srcNode.src; \n" +
                        "       else\n" +
                        "           var src = null;\n" +
                        " }\n" +
                        "\n" +
                        " else{\n" +
                        "       if ( srcNode = obj.getElementsByTagName(tag)[0] )\n" +
                        "           var src = srcNode.src; \n" +
                        "       else\n" +
                        "           var src = null;\n" +
                        " }\n" +
                        " var str ='null';\n" +
                        " if ( src ){\n" +
                        "   filterkeyImage = \".jpg\" ;\n" +
                        "   filterkeyVideo = \".mp4\" ;\n" +
                        "   if ( src.indexOf(filterkeyImage) != -1 || src.indexOf(filterkeyVideo) != -1){\n" +
                        "        str = \"Starting download: \"+src ;" +
                        "        var link = document.createElement(\"a\");\n" +
                        "        link.href = src.split(\"?\")[0];\n" +
                        "        link.download = src.split(\"?\")[0].split(\"/\").pop();\n" +
                        "        link.style.display = \"none\";\n" +
                        "        var evt = new MouseEvent(\"click\", {\n" +
                        "            \"view\": window,\n" +
                        "            \"bubbles\": true,\n" +
                        "            \"cancelable\": true\n" +
                        "        });\n" +
                        "        document.body.appendChild(link);\n" +
                        "        link.dispatchEvent(evt);\n" +
                        "        document.body.removeChild(link);\n" +
                        "   }\n" +
                        " }" +
                        "return str;";
            }

        }
        else if ( TOUCH_CODE == DOUBLE_TOUCH_REQUEST_CODE) {
            double_touch_script = content;
            if ( double_touch_script.isEmpty()){
                double_touch_script = "";
            }

        }
    }

    public static  String getPathfromExternal( String externalPath){
        return externalPath.replace(sdcardPath+"/" , "");
    }


    /*
    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }
    */
}


