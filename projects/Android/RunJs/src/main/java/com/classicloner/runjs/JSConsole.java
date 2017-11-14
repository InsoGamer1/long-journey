package com.classicloner.runjs;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;

import static com.classicloner.runjs.MainActivity.mWebView;
import static com.classicloner.runjs.MyFunctions.READ_REQUEST_CODE;
import static com.classicloner.runjs.MyFunctions.cacheFile;


/**
 * Created by Raheman on 10/23/2016.
 */


public class JSConsole extends Activity {
    static EditText inputText;
    static EditText outputText;
    static EditText consoleText;
    static LinearLayout inputLinearBtns;
    static LinearLayout outpuLinearBtns;
    static LinearLayout consoleLinearBtns;
    public boolean fromOnAcitivityResult = true;
    static int isError;
    MyFunctions myfunctionList ;
    final Context jscontext = this;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jsconsole);

        inputText = (EditText)findViewById(R.id.input);
        outputText = (EditText)findViewById(R.id.output);
        consoleText = (EditText)findViewById(R.id.console);
        outputText.setShowSoftInputOnFocus(false);//block keyboard on focus
        consoleText.setShowSoftInputOnFocus(false);

        inputLinearBtns = (LinearLayout) findViewById(R.id.action_input);
        outpuLinearBtns = (LinearLayout) findViewById(R.id.action_output);
        consoleLinearBtns = (LinearLayout) findViewById(R.id.action_console);

        myfunctionList = new MyFunctions(JSConsole.this);

        inputText.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if( b) {
                            inputLinearBtns.setVisibility(view.VISIBLE);
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
                            outpuLinearBtns.setVisibility(view.VISIBLE);
                        }
                        else {
                            outpuLinearBtns.setVisibility(view.GONE);
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

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Object jsData = extras.get("jsData");
            if ( jsData.toString().contains("__NOJSDATA__")){
                String initData = "(function(){\n\tsrc = document.URL;\n\treturn src.toString();\n})();";
                String fileData = myfunctionList.readFromExtFile(cacheFile);
                if( !fileData.isEmpty()){
                    initData = fileData;
                }
                inputText.setText(initData);
            }
            else{
                inputText.setText(jsData.toString());
            }
        }
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
                        return true;
                    }
                });
                break;

            case R.id.save:
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
                Log.i("CHECK2", "data: " + jsContent);
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
                Log.i("CHECK0", "Uri: " + uri.getPath());
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



    @Override
    public void onResume(){
        super.onResume();
        // put your code here...
        if ( !fromOnAcitivityResult ) {
            String CacheText;
            CacheText = myfunctionList.readFromExtFile(cacheFile);
            if( CacheText!= null)
                inputText.setText(CacheText);
            fromOnAcitivityResult = false;
        }
    }

    @Override
    public  void onDestroy(){
        super.onDestroy();
        myfunctionList.writeToExtFile(cacheFile , inputText.getText().toString());
    }
}