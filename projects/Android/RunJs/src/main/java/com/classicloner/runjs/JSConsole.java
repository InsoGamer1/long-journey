package com.classicloner.runjs;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.classicloner.runjs.MainActivity._DOWNLOAD_THESE_URLS;
import static com.classicloner.runjs.MainActivity.mWebView;
import static com.classicloner.runjs.Common.INCOGNITO_MODE;
import static com.classicloner.runjs.Common.READ_REQUEST_CODE;
import static com.classicloner.runjs.Common.appName;
import static com.classicloner.runjs.Common.cacheFile;
import static com.classicloner.runjs.Common.currentJsFilePath;
import static com.classicloner.runjs.Common.defaultDownloadFile;
import static com.classicloner.runjs.Common.downloadFile;
import static com.classicloner.runjs.Common.getPathfromExternal;
import static com.classicloner.runjs.Common.incognitoDownloadFile;
import static com.classicloner.runjs.Common.sdcardPath;


/**
 * Created by Raheman on 10/23/2016.
 */

public class JSConsole extends Activity {
    static EditText inputText;
    static EditText outputText;
    static EditText consoleText;
    static LinearLayout inputLinearBtns;
    static LinearLayout outputLinearBtns;
    static LinearLayout consoleLinearBtns;
    static JSONObject Cache;

    public boolean fromOnAcitivityResult = true;
    static int isError;
    Common myfunctionList ;
    final Context jscontext = this;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jsconsole);
        myfunctionList = new Common(JSConsole.this);

        inputText = (LinedEditText)findViewById(R.id.input);
        outputText = (EditText)findViewById(R.id.output);
        consoleText = (EditText)findViewById(R.id.console);
        outputText.setShowSoftInputOnFocus(false);//block keyboard on focus
        consoleText.setShowSoftInputOnFocus(false);

        inputLinearBtns = (LinearLayout) findViewById(R.id.action_input);
        outputLinearBtns = (LinearLayout) findViewById(R.id.action_output);
        consoleLinearBtns = (LinearLayout) findViewById(R.id.action_console);

        Cache = new JSONObject();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Object jsData = extras.get("jsData");
            if ( jsData.toString().contains("__NOJSDATA__")){
                setInputText();
            }
            else{
                inputText.setText(jsData.toString());
            }
        }

        inputText.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if( b) {
                            inputLinearBtns.setVisibility(view.VISIBLE);
                            try {
                                inputText.setSelection(Cache.getInt("cursor"));
                            } catch (JSONException e) {
                            }
                        }
                        else {
                            inputLinearBtns.setVisibility(view.GONE);
                        }
                    }
                }
        );
        outputText.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if( b) {
                            outputLinearBtns.setVisibility(view.VISIBLE);
                        }
                        else {
                            outputLinearBtns.setVisibility(view.GONE);
                        }
                    }
                }
        );
        consoleText.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if( b) {
                            consoleLinearBtns.setVisibility(view.VISIBLE);
                        }
                        else {
                            consoleLinearBtns.setVisibility(view.GONE);
                        }
                    }
                }
        );

    }

    public void setWindowParams() {
        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wlp.dimAmount = (float) 1;
        wlp.format = PixelFormat.TRANSLUCENT;
        wlp.alpha = (float) 0.5;
        wlp.x = 200;
        wlp.y = 200;
        window.setAttributes(wlp);
    }

    public void clearActions( View view){
        ClipboardManager myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        ClipData myClip;
        switch (view.getId()) {
            case R.id.clr_console:
                consoleText.setText("");
                break;
            case R.id.clr_input:
                inputText.setText("");
                break;
            case R.id.clr_output:
                outputText.setText("");
                break;
            case R.id.copy_console:
                myClip = ClipData.newPlainText("text", consoleText.getText());
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(JSConsole.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                break;
            case R.id.copy_input:
                myClip = ClipData.newPlainText("text", inputText.getText());
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(JSConsole.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                break;
            case R.id.copy_output:
                myClip = ClipData.newPlainText("text", outputText.getText());
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(JSConsole.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                break;
        }
    }
    public void editActions(View view){
        Toast ts;
        myfunctionList.hideKeyBoard(view);

        switch (view.getId()) {
            case R.id.run:
                if ( inputText.getText().length()==0) {
                    ts = Toast.makeText(JSConsole.this, "Empty JS data can't be run", Toast.LENGTH_SHORT);
                    ts.show();
                    return;
                }
                mWebView.evaluateJavascript(inputText.getText().toString() , new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        outputText.setTextColor(Color.BLUE);
                        outputText.setText( outputText.getText()+"\n"+s);
                    }
                });
                mWebView.setWebChromeClient(new WebChromeClient() {
                    public boolean onConsoleMessage(ConsoleMessage cm) {
                        Log.d("error" , cm.message()+"\n@ "+cm.lineNumber()+" of "+cm.sourceId());
                        consoleText.setTextColor(Color.RED);
                        consoleText.setText( consoleText.getText()+"\n"+myfunctionList.currentJsFile+":"+cm.lineNumber() +":\t" + cm.message());
                        String consoleMessage = cm.message();
                        if( consoleMessage.contains(_DOWNLOAD_THESE_URLS)) {
                            try {
                                JSONObject consoleJson = new JSONObject(consoleMessage);//browser history is empty
                                if ( consoleJson.has(_DOWNLOAD_THESE_URLS)){
                                    JSONArray downloadUrlObjects = consoleJson.getJSONArray(_DOWNLOAD_THESE_URLS);
                                    JSONObject tempObj ;
                                    String url;
                                    String fileName;
                                    for ( int i=0; i<downloadUrlObjects.length();i++) {
                                        tempObj = downloadUrlObjects.getJSONObject(i);
                                        url = tempObj.getString("src");
                                        fileName = tempObj.getString("name");
                                        Log.d(_DOWNLOAD_THESE_URLS , url );

                                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                        if (INCOGNITO_MODE) {
                                            defaultDownloadFile = incognitoDownloadFile;
                                        } else {
                                            request.allowScanningByMediaScanner();
                                            defaultDownloadFile = downloadFile;
                                        }
                                        request.setDescription(appName);
                                        String internalDownloadPath = getPathfromExternal(defaultDownloadFile);

                                        if (myfunctionList.isExist(sdcardPath + "/" + internalDownloadPath + "/" + fileName)) {
                                            myfunctionList.deleteFile(sdcardPath + "/" + internalDownloadPath + "/" + fileName);
                                            //Toast.makeText(MainActivity.this, fileName +" ALREADY exists!!", Toast.LENGTH_SHORT).show();
                                        }
                                        Log.d("DOWNLOAD:downpath", internalDownloadPath);
                                        request.setDestinationInExternalPublicDir(internalDownloadPath, fileName);
                                        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                        dm.enqueue(request);
                                    }
                                }
                            } catch (JSONException e) {
                                Toast.makeText(JSConsole.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                        return true;
                    }
                });
                break;

            case R.id.save:
                if ( !currentJsFilePath.equals("")) {
                    myfunctionList.writeToExtFile(currentJsFilePath, inputText.getText().toString());
                    Toast.makeText(JSConsole.this, "file saved :" + currentJsFilePath, Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(JSConsole.this, "Error in saving file", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.save_as:
                if ( inputText.getText().length()==0) {
                    ts = Toast.makeText(JSConsole.this, "Empty JS data can't be saved", Toast.LENGTH_SHORT);
                    ts.show();
                    return;
                }
                myfunctionList.createFile(myfunctionList.mimeType, "File.js" ,jscontext);
                break;

            case R.id.clear:
                //inputText.setText("");
                outputText.setText("");
                consoleText.setText("");
                break;
            case R.id.load:
                Toast.makeText(JSConsole.this, "Pick the javascript file ", Toast.LENGTH_SHORT).show();
                myfunctionList.performFileSearch(jscontext , READ_REQUEST_CODE);
                break;
            case R.id.toggle:
                if ( findViewById(R.id.console_wrapper).getVisibility() == View.GONE) {
                    findViewById(R.id.console_wrapper).setVisibility(View.VISIBLE);
                    findViewById(R.id.output_wrapper).setVisibility(View.VISIBLE);
                }else {
                    findViewById(R.id.console_wrapper).setVisibility(View.GONE);
                    findViewById(R.id.output_wrapper).setVisibility(View.GONE);
                }
                break;
            case R.id.read_exit:
                if ( findViewById(R.id.read_exit).getVisibility() == View.VISIBLE) {
                    inputText.setEnabled(true);
                    findViewById(R.id.console_wrapper).setVisibility(View.VISIBLE);
                    findViewById(R.id.jsButtons_parent).setVisibility(View.VISIBLE);
                    findViewById(R.id.output_wrapper).setVisibility(View.VISIBLE);
                    findViewById(R.id.read_exit).setVisibility(View.GONE);
                }
                break;
            case R.id.read:
                if ( findViewById(R.id.read_exit).getVisibility() == View.GONE) {
                    findViewById(R.id.read_exit).setVisibility(View.VISIBLE);
                    inputText.setEnabled(false);
                    findViewById(R.id.console_wrapper).setVisibility(View.GONE);
                    findViewById(R.id.output_wrapper).setVisibility(View.GONE);
                    findViewById(R.id.jsButtons_parent).setVisibility(View.GONE);

                }
                break;

        }

        //something TODO
    }
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == myfunctionList.WRITE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                myfunctionList.currentJsFile = uri.getPath().split("/")[uri.getPath().split("/").length-1];
                Log.i("CHECK1", "Uri: " + myfunctionList.currentJsFile);
                String jsContent= inputText.getText().toString();
                //Log.i("CHECK2", "data: " + jsContent);
                myfunctionList.alterDocument(uri , jsContent , jscontext);
            }
            else{
                Log.i("CHECK", "Nothing is selected");
            }
        }
        else if (requestCode == myfunctionList.READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                currentJsFilePath = getPathFromUri( uri);
                Log.i("CHECK0", "Uri: " + uri.getPath());
                Log.i("CHECK0", "Uripath: " + currentJsFilePath);
                try {
                    String jsContent ;
                    jsContent = myfunctionList.readTextFromUri(uri , jscontext);
                    inputText.setText(jsContent.toString());
                    fromOnAcitivityResult = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                Log.i("CHECK", "Nothing is selected");
            }
        }
    }

    String getPathFromUri(Uri sourceuri )
    {
        if ( sourceuri != null ){
            String path = sourceuri.getPath();
            String[] temp = path.split(":");
            if ( temp.length==1 )
                return temp[0];
            else if ( temp.length>1 ){
                String decider = temp[0];
                String sdCardName = "primary";
                if (decider.contains(sdCardName))
                    return Environment.getExternalStorageDirectory().getPath()+"/"+temp[1];
                else{
                    Toast.makeText(JSConsole.this, "Use Save As option for ExtCard files", Toast.LENGTH_SHORT).show();
                    return "";
                }
            }
        }
        return "";
    }



    @Override
    public void onResume(){
        super.onResume();
        // put your code here...
        if ( !fromOnAcitivityResult ) {
            setInputText();
        }
    }

    @Override
    public  void onDestroy(){
        super.onDestroy();
        try {
            Cache.put("content" , inputText.getText().toString());
            Cache.put("cursor" , inputText.getSelectionStart());
            myfunctionList.writeToExtFile(cacheFile , Cache.toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void setInputText(){
        String initData = "(function(){\n\tsrc = document.URL;\n\treturn src.toString();\n})();";
        String CacheText = myfunctionList.readFromExtFile(cacheFile);
        if (CacheText != null){
            CacheText = CacheText.trim();
            if (!CacheText.isEmpty()) {
                try {
                    Cache = new JSONObject(CacheText);
                    String fileData = Cache.getString("content");
                    if( fileData!=null && !fileData.isEmpty()){
                        inputText.setText(fileData);
                        inputText.setSelection(Cache.getInt("cursor"));
                    }else{
                        inputText.setText(initData);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}