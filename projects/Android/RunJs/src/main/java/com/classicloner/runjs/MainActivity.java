package com.classicloner.runjs;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.AbstractCursor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Visibility;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
import static android.webkit.WebSettings.LOAD_CACHE_ELSE_NETWORK;
import static com.classicloner.runjs.Common.GOT_PERMISSION_TO_WRITE;
import static com.classicloner.runjs.Common.INCOGNITO_MODE;
import static com.classicloner.runjs.Common.MY_WRITE_EXTERNAL_STORAGE_CODE;
import static com.classicloner.runjs.Common.READ_REQUEST_CODE;
import static com.classicloner.runjs.Common.appName;
import static com.classicloner.runjs.Common.appPath;
import static com.classicloner.runjs.Common.bookmarkFile;
import static com.classicloner.runjs.Common.cacheFile;
import static com.classicloner.runjs.Common.configFile;
import static com.classicloner.runjs.Common.defaultDownloadFile;
import static com.classicloner.runjs.Common.double_touch_js;
import static com.classicloner.runjs.Common.downloadFile;
import static com.classicloner.runjs.Common.getPathfromExternal;
import static com.classicloner.runjs.Common.historyCache;
import static com.classicloner.runjs.Common.incognitoDownloadFile;
import static com.classicloner.runjs.Common.long_touch_js;
import static com.classicloner.runjs.Common.offlineFolder;
import static com.classicloner.runjs.Common.scriptFile;
import static com.classicloner.runjs.Common.sdcardPath;
import static com.classicloner.runjs.Common.settingFile;
import static com.classicloner.runjs.Common.startupScript;

//import android.support.v7.widget.SearchView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    static WebView mWebView;
    ProgressBar progressBar;
    static String pageUrl = "https://www.google.com";//Default page
    static Common myfunctionList;
    final Context maincontext = this;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private Menu mMenu = null;
    static ArrayList<String> mResults;
    static ArrayList<String> mCompResults;
    static JSONObject myWebHistory;
    static JSONObject myConfigJson;
    static String lastLongRunScript;
    static String lastDoubleRunScript;
    static String desktopUA = "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0";
//    static String mobileUA = "Mozilla/5.0 (Linux; Android 7.1.2; MotoG3 Build/N2G47O) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.83 Mobile Safari/537.36";
    static String mobileUA = System.getProperty("http.agent");
    static String _DOWNLOAD_THESE_URLS = "__download_these_urls__";


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myWebHistory = new JSONObject();
        myConfigJson = new JSONObject();
        myfunctionList = new Common(MainActivity.this);
        lastLongRunScript = "_some__#$Thing_";
        Uri data = getIntent().getData();
        if (data != null) {
            pageUrl = data.toString();
        } else {
            String lastLoaded = myfunctionList.readFromExtFile(bookmarkFile);
            if (lastLoaded != null){
                if (!lastLoaded.trim().isEmpty()) {
                    pageUrl = lastLoaded.trim();
                }
            }
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        mySwipeRefreshLayout.setDistanceToTriggerSync(200);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("SWIPE", "onRefresh called from SwipeRefreshLayout");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        mWebView.loadUrl(mWebView.getUrl());
                    }
                });

        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        final GestureDetector gestureDetector = new GestureDetector(this, new GestureListener(MainActivity.this));
        //WebChromeClient myChromeClient = new WebChromeClient(){};
        WebViewClient myBrowser = new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // once the page loading is finished
                progressBar.setVisibility(view.GONE);
                mySwipeRefreshLayout.setRefreshing(false);
                //view.loadUrl("javascript:MyAppJs.resize(document.body.getBoundingClientRect().height)");
                try {
                    myfunctionList.writeToExtFile(historyCache, myWebHistory.toString(2));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                loadStartupScript( );
                //handleLongTouchScript(url);
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(view.VISIBLE);
                pageUrl = url;
                if ( !INCOGNITO_MODE ) {
                    try {
                        myWebHistory.put(url, false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                progressBar.setVisibility(view.GONE);
                mySwipeRefreshLayout.setRefreshing(false);
                //Toast tst = Toast.makeText(MainActivity.this, "Error occurred while loading", Toast.LENGTH_SHORT);
                //tst.show();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (Uri.parse(url).getScheme().equals("http") || Uri.parse(url).getScheme().equals("https")) {
                    return false;
                }
                else{
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                }
            }
        };

        mWebView = (WebView) findViewById(R.id.activity_main_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(this, "MyAppJs");
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.getSettings().setCacheMode(LOAD_CACHE_ELSE_NETWORK);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setUserAgentString(mobileUA);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAllowContentAccess(true);
        mWebView.getSettings().setAllowFileAccessFromFileURLs(true);

        //mWebView.setOnTouchListener(new View.OnTouchListener() {});

        mWebView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(final String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                if (url.startsWith("content://"))
                    return;
                //Toast.makeText(MainActivity.this, mimetype, Toast.LENGTH_SHORT).show();

                String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);
                if ( fileName.endsWith(".bin")){
                    fileName = fileName.replace(".bin", ".jpg");
                    //read from javascript download_dict[url]
                    final String finalFileName = fileName;
                    mWebView.evaluateJavascript("( function(){return guessFileName('"+url+"');} )()" , new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            if ( s == "")
                                s = finalFileName;
                            else
                                s = s.replace("\"" , "");
                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            if ( INCOGNITO_MODE) {
                                defaultDownloadFile = incognitoDownloadFile;
                            }
                            else {
                                request.allowScanningByMediaScanner();
                                defaultDownloadFile = downloadFile;
                            }
                            request.setDescription(appName);
                            String internalDownloadPath = getPathfromExternal(defaultDownloadFile);

                            if ( myfunctionList.isExist(sdcardPath+"/"+internalDownloadPath+"/"+ s)) {
                                myfunctionList.deleteFile( sdcardPath+"/"+internalDownloadPath+"/"+ s);
                                //Toast.makeText(MainActivity.this, fileName +" ALREADY exists!!", Toast.LENGTH_SHORT).show();
                            }
                            Log.d("DOWNLOAD:downpath" , internalDownloadPath);
                            request.setDestinationInExternalPublicDir(internalDownloadPath, s);
                            DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                            dm.enqueue(request);
                            //Toast.makeText(MainActivity.this,s , Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    fileName = fileName.replace(".bin", ".jpg");
                    //for downloading directly through download manager
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    if ( INCOGNITO_MODE) {
                        defaultDownloadFile = incognitoDownloadFile;
                    }
                    else {
                        request.allowScanningByMediaScanner();
                        defaultDownloadFile = downloadFile;
                    }
                    request.setDescription(appName);
                    String internalDownloadPath = getPathfromExternal(defaultDownloadFile);

                    if ( myfunctionList.isExist(sdcardPath+"/"+internalDownloadPath+"/"+fileName)) {
                        myfunctionList.deleteFile( sdcardPath+"/"+internalDownloadPath+"/"+fileName);
                        //Toast.makeText(MainActivity.this, fileName +" ALREADY exists!!", Toast.LENGTH_SHORT).show();
                    }
                    Log.d("DOWNLOAD:downpath" , internalDownloadPath);
                    request.setDestinationInExternalPublicDir(internalDownloadPath, fileName);
                    DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    dm.enqueue(request);
                }



            }
        });

        mWebView.setWebViewClient(myBrowser);
        //mWebView.setWebChromeClient( myChromeClient );
        mWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
        mWebView.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (b) {
                            if (mMenu != null) {
                                SearchView searchView = (SearchView) mMenu.findItem(R.id.action_search).getActionView();
                                if (searchView != null) {
                                    searchView.onActionViewCollapsed();
                                }
                            }
                        }
                    }
                }
        );

//        mWebView.setOnScrollChangeListener(
//                new View.OnScrollChangeListener() {
//                    @Override
//                    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                        if ( (oldScrollY-scrollY) > 10 ){//scroll down
//                            Log.d("SCROLL" , "down");
////                            getSupportActionBar().show();
//                        }else if ( (oldScrollY-scrollY) <  -10 ){//scroll up
//                            Log.d("SCROLL" , "up");
////                            getSupportActionBar().hide();
//                        }
//                    }
//                }
//        );

        mWebView.loadUrl(pageUrl);
        // init stuff
        askForPermission();

        //Creates required folder : part of init 1
        myfunctionList.createFolderInExternal(appPath );//app path
        myfunctionList.createFolderInExternal(scriptFile );//Scripts folder
        myfunctionList.createFolderInExternal(settingFile );//Scripts folder

        File isAssetAlreadyCopied = new File (scriptFile+"/.done");
        if (!isAssetAlreadyCopied.exists ()) {
            Log.d("ASSETS" , "COPYING FOR THE FIRST AND LAST TIME");
            copyAssets(appName + "/Scripts", scriptFile);
            copyAssets(appName + "/Settings", settingFile);
            String configContent = "{'startup':['"+startupScript+"']}";
            myfunctionList.writeToExtFile( configFile , configContent);
            myfunctionList.createFileInExternal( scriptFile+"/.done");
        }else{
            Log.d("ASSETS" , "Already at the right place");
        }
        myfunctionList.createFolderInExternal(offlineFolder );//Savedpages folder
        myfunctionList.createFolderInExternal(settingFile );//Settings folder
        myfunctionList.createFolderInExternal(downloadFile );//Downloads folder
        myfunctionList.createFolderInExternal(defaultDownloadFile );//Downloads folder
        myfunctionList.createFolderInExternal(incognitoDownloadFile );//Downloads folder
        //Creates required files : part of init 2
        myfunctionList.createFileInExternal(configFile);
        myfunctionList.createFileInExternal(cacheFile);
        myfunctionList.createFileInExternal(bookmarkFile);
        myfunctionList.createFileInExternal(historyCache);
        //Creates required files : part of init 3
        myfunctionList.createFileInExternal(double_touch_js);
        myfunctionList.createFileInExternal(long_touch_js);

        String configText = myfunctionList.readFromExtFile(configFile);
        if (configText != null){
            configText = configText.trim();
            if (!configText.isEmpty()) {
                try {
                    myConfigJson = new JSONObject(configText);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        myfunctionList.load_scripts();//loads script for long and double touch
        //loads the saved history into app
        String savedHistory = myfunctionList.readFromExtFile(historyCache);
        if (savedHistory != null){
            savedHistory = savedHistory.trim();
            if (!savedHistory.isEmpty()) {
                try {
                    myWebHistory = new JSONObject(savedHistory);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        // End of init
    }

    public void loadStartupScript(){
        JSONArray startupScripList;
        String script_name = "";
        String scriptCode;
        String filename;
        if ( myConfigJson==null || myConfigJson.length()==0)
            return;
        try {
            startupScripList = myConfigJson.getJSONArray("startup");
            if (startupScripList == null)
                return ;
            for ( int i=0; i<startupScripList.length();i++){
                filename = startupScripList.getString(i);
                script_name = new File(filename).getName().replace(".js","");
                Log.d("SCRIPT_CODE" , filename);
                Log.d("SCRIPT_CODE" , script_name);
                scriptCode = myfunctionList.readFromExtFile(filename);
                if ( scriptCode !=null && !scriptCode.isEmpty()) {
                    String encoded = Base64.encodeToString(scriptCode.getBytes() , Base64.NO_WRAP);
                    String injectScriptCode = "(\n" +
                            "function (script_name,waitingSecs=1000){//1 sec waits after loads \n" +
                            "    console.log( \"................injecting .................\");\n" +
                            "    var d=document;\n" +
                            "    var timeOutfn;\n" +
                            "    var script_id = script_name+\"_id\";\n" +
                            "    if(!d.getElementById(script_id)){\n" +
                            "        var s=d.createElement('script');\n" +
                            "        s.type=\"text/javascript\";\n" +
                            "        s.id=script_id;\n" +
                            "        s.innerHTML = window.atob('"+ encoded+"');\n" +
                            "        d.head.appendChild(s);\n" +
                            "        console.log( \"all good things to those who waits\" );\n" +
                            "        setTimeout(function(){\n" +
                            "            console.log( script_name +\" is now loaded!!\" );\n" +
                            "            return ;\n" +
                            "        }, waitingSecs); // wait for a sec\n" +
                            "    }\n" +
                            "    else{\n" +
                            "        console.log( script_name+\" is already loaded\" );\n" +
                            "        return;\n" +
                            "    }\n" +
                            "})( '"+script_name+"');";
                    Log.d("SCRIPT_CODE" , injectScriptCode);
                    mWebView.evaluateJavascript(injectScriptCode , new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            //Toast.makeText(MainActivity.this,s , Toast.LENGTH_SHORT).show();
                        }
                    });
                    mWebView.setWebChromeClient(new WebChromeClient() {
                        String console_data = "";
                        public boolean onConsoleMessage(ConsoleMessage cm) {
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
                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }
                            else {
                                if (consoleMessage.contains("_EOS_")) {
                                    Toast.makeText(MainActivity.this, console_data, Toast.LENGTH_SHORT).show();
                                    console_data = "";
                                } else {
                                    console_data += consoleMessage;
                                }
                            }
                            Log.d("error" , consoleMessage+"\n@ "+cm.lineNumber()+" of "+cm.sourceId());
                            return true;
                        }
                    });
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }



    }
    public void handleLongTouchScript( String url) throws JSONException {//decides the download folder and long_press script
        if (url.contains(lastLongRunScript))
            return;
        String filename = null;
        String jsContent ;

        Pattern pattern = Pattern.compile("([^\\./]+\\.com)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find())
        {
            String domain_url = matcher.group(1).replace("www.","");//with .com/.in ex:facebook.com
            String url_folder = domain_url.split("\\.")[0];// ex:facebook
            lastLongRunScript = url_folder;
            //download in a particular folder ( site-specific )
            //downloadFile = downloadFile + "/"+url_folder;
            filename = "LONG_TOUCH_"+lastLongRunScript+"_SCRIPT.js";
        }
        else {
            return;
        }
        if ( filename == null )
            return;
        filename = scriptFile+"/"+filename;
        Log.d("FILE1 _CHECK" , filename);
        jsContent = myfunctionList.readFromExtFile( filename);
        if ( jsContent == null ) {
            Log.d("FILE2 _CHECK not found" , filename + " for "+url);
            //download in a particular folder ( non site-specific )
            //downloadFile = downloadFile+"/Others";
            //Toast.makeText(MainActivity.this, filename, Toast.LENGTH_SHORT).show();
            return;
        }
        myfunctionList.load_scripts_for_touch_event( jsContent , myfunctionList.LONG_TOUCH_REQUEST_CODE);
        Toast.makeText(MainActivity.this, "loaded for "+lastLongRunScript, Toast.LENGTH_SHORT).show();
    }

    protected  void onNewIntent( Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
        // code goes here

        Uri data = getIntent().getData();
        if (data != null) {
            pageUrl = data.toString();
            Log.d("DeepLink" , "load url : "+pageUrl);
            mWebView.loadUrl(pageUrl);
        }

    }
    /*********************** Start: Hard keys handlers     *****************************/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) { //if back key is pressed
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            else {
                mWebView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Toast ts;
        switch (item.getItemId()) {
            case R.id.action_reload:
                //Toast.makeText(MainActivity.this, "Reload", Toast.LENGTH_SHORT).show();
                mWebView.reload();
                return true;
            case R.id.action_settings:
                Toast.makeText(MainActivity.this, "Setting", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_cancel:
                mWebView.stopLoading();
                return true;
            case R.id.action_forward:
                if ( mWebView.canGoForward())
                    mWebView.goForward();
                return true;
            case R.id.nav_share:
                Toast.makeText(MainActivity.this, "nav_share", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.nav_send:
                Toast.makeText(MainActivity.this, "nav_send", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.savePage:
                mWebView.saveWebArchive(offlineFolder , true , new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        if ( s == null)
                            Toast.makeText(MainActivity.this, "Error in saving file", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(MainActivity.this, "Saved to :"+s, Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
            case R.id.clearHistory:
                myWebHistory = new JSONObject();//browser history is empty
                myfunctionList.writeToExtFile(historyCache , "");
                Toast.makeText(MainActivity.this, "Cleared!", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.bookmark:
                Toast.makeText(MainActivity.this, "Bookmarks", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.incognito:
                if ( item.isCheckable() ) {
                    item.setChecked(!item.isChecked());
                    INCOGNITO_MODE = item.isChecked();
                    if ( INCOGNITO_MODE ) {
                        Toast.makeText(MainActivity.this, "INCOGNITO: ON", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MainActivity.this, "INCOGNITO: OFF", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            case R.id.desktopView:
                if ( item.isCheckable() ) {
                    item.setChecked(!item.isChecked());
                    boolean isDesktop = item.isChecked();
                    if ( isDesktop ) {
                        mWebView.getSettings().setUserAgentString(desktopUA);
                    }else{
                        mWebView.getSettings().setUserAgentString(mobileUA);
                    }
                    //Toast.makeText(this , mWebView.getSettings().getUserAgentString() , Toast.LENGTH_SHORT).show();
                    mWebView.reload();
                }
                return true;
            case R.id.floating_console_toggle:
                bringUpConsole("__NOJSDATA__" , true);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            MainActivity.this.finish();
        }
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if ( !mWebView.canGoForward()){
            MenuItem forward_item = (MenuItem)findViewById(R.id.action_forward);
            if ( forward_item!=null )
                forward_item.setEnabled(false);
        }
        return super.onMenuOpened(featureId, menu);
    }

    /*********************** Done: Hard keys handlers   *****************************/



    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.d( "menu " , "menu" );

        getMenuInflater().inflate(R.menu.main, menu);
        this.mMenu = menu;
        final SearchView searchView = (SearchView)menu.findItem(R.id.action_search).getActionView();
        searchView.setSuggestionsAdapter(new SearchSuggestionsAdapter(this));
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener()
        {
            @Override
            public boolean onSuggestionClick(int position)
            {
                pageUrl = mCompResults.get(position);
                mWebView.loadUrl(pageUrl);
                searchView.onActionViewCollapsed();
                return true;
            }

            @Override
            public boolean onSuggestionSelect(int position)
            {
                return false;
            }
        });
        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        //searchView.setIconified(true);
                        //searchView.clearFocus();
                        searchView.onActionViewCollapsed();
                        query = query.trim();
                        pageUrl = "https://www.google.co.in/search?q="+query.toString();
                        if( ( query.startsWith("http") || query.startsWith("file://") )&& !query.contains(" ,") )
                            pageUrl = query;
                        Log.d("QUERY" , pageUrl);
                        mWebView.loadUrl(pageUrl);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                }
        );
        searchView.setOnSearchClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        searchView.setQuery(mWebView.getUrl() , false);
                    }
                }
        );
        return true;
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_console) {
            bringUpConsole("__NOJSDATA__");
            return true;
        } else if (id == R.id.nav_load) {
            Toast.makeText(MainActivity.this, "Pick the javascript or .js file ", Toast.LENGTH_SHORT).show();
            myfunctionList.performFileSearch(maincontext , READ_REQUEST_CODE);
        } else if (id == R.id.nav_share) {
            Toast.makeText(MainActivity.this, "Share ", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_send) {
            Toast.makeText(MainActivity.this, "Send ", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_exit) {
            MainActivity.this.finish();
        }
//        } else if (id == R.id.config_long) {
//            Toast.makeText(MainActivity.this, "Pick the javascript or .js file ", Toast.LENGTH_SHORT).show();
//            myfunctionList.performFileSearch(maincontext , LONG_TOUCH_REQUEST_CODE);
//        } else if (id == R.id.config_double) {
//            Toast.makeText(MainActivity.this, "Pick the javascript or .js file ", Toast.LENGTH_SHORT).show();
//            myfunctionList.performFileSearch(maincontext , DOUBLE_TOUCH_REQUEST_CODE);
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean bringUpConsole(String jsData ){
        Intent intent = new Intent(MainActivity.this,JSConsole.class);
        intent.putExtra("url", "https://www.instagram.com/accounts/login");
        intent.putExtra("jsData", jsData);
        intent.putExtra("FLOAT", false);
        startActivity(intent);
        return true;
    }

    public boolean bringUpConsole(String jsData , boolean isFloat){
        Intent intent = new Intent(MainActivity.this,JSConsole.class);
        intent.putExtra("url", "https://www.instagram.com/accounts/login");
        intent.putExtra("jsData", jsData);
        intent.putExtra("FLOAT", isFloat);
//        intent.setFlags(
//                Intent.FLAG_ACTIVITY_NEW_TASK
//        );
        intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT |
                        Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                        );
        startActivity(intent);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i("CHECK", "Uri: " + uri.toString());
                try {
                    String jsContent ;
                    jsContent = myfunctionList.readTextFromUri(uri , maincontext);
                    if ( requestCode == myfunctionList.READ_REQUEST_CODE ) {
                        myfunctionList.currentJsFile = uri.getLastPathSegment();
                        bringUpConsole(jsContent);
                    }else if ( requestCode == myfunctionList.LONG_TOUCH_REQUEST_CODE ) {
                        myfunctionList.load_scripts_for_touch_event( jsContent , myfunctionList.LONG_TOUCH_REQUEST_CODE);
                        Toast.makeText(MainActivity.this, "long touch configured", Toast.LENGTH_SHORT).show();
                    }else if ( requestCode == myfunctionList.DOUBLE_TOUCH_REQUEST_CODE ) {
                        myfunctionList.load_scripts_for_touch_event( jsContent , myfunctionList.DOUBLE_TOUCH_REQUEST_CODE);
                        Toast.makeText(MainActivity.this, "double touch configured", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                Log.i("CHECK", "Nothing is selected");
            }
        }
    }

    public  void askForPermission(){
        /******************* Start: Request Permission Handling ********************************/
        // Check for the permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // get the permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                String explanationStr = "This app need WRITE permisson to store the data in your phone";
                new android.app.AlertDialog.Builder(this).setTitle("Request").setMessage(explanationStr).setPositiveButton(android.R.string.ok, null).setCancelable(false).create().show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_WRITE_EXTERNAL_STORAGE_CODE);
            }
        } else {
            Log.d("WRITE_PERM", "Permission is already granted");
            GOT_PERMISSION_TO_WRITE = 1;
        }
        /******************* Done: Request Permission Handling ********************************/
    }

    public void copyAssets(String assetname,  String destinationDir){
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list(assetname);
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        if ( files != null){
            for( String filename: files){
                Log.d( "ASSET" , "Copying file : " + filename );
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = assetManager.open(assetname+"/"+filename);
                    File outFile = new File(destinationDir, filename);
                    out = new FileOutputStream(outFile);
                    copyFile(in, out);
                } catch(IOException e) {
                    Log.e("tag", "Failed to copy asset file: " + filename, e);
                }
                finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            // NOOP
                        }
                    }
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            // NOOP
                        }
                    }
                }
            }

        }
    }

    public void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public  void onDestroy(){
        super.onDestroy();
        if ( !INCOGNITO_MODE ) {
            myfunctionList.writeToExtFile(bookmarkFile, mWebView.getUrl() + "\n");
        }
        try {
            myfunctionList.writeToExtFile(historyCache, myWebHistory.toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @JavascriptInterface
    public void resize(final float height) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWebView.setLayoutParams(new LinearLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, (int) (height * getResources().getDisplayMetrics().density)));
            }
        });
    }


    public static class SearchSuggestionsAdapter extends SimpleCursorAdapter {
        private static final String[] mFields = {"_id", "result"};
        private static final String[] mVisible = {"result"};
        private static final int[] mViewIds = {android.R.id.text1};


        public SearchSuggestionsAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_1, null, mVisible, mViewIds, 0);
        }

        @Override
        public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
            return new SuggestionsCursor(constraint);
        }

        private static class SuggestionsCursor extends AbstractCursor {
            public SuggestionsCursor(CharSequence constraint) {
                mResults=new ArrayList<String>();
                mCompResults=new ArrayList<String>();
                if ( constraint!= null) {
                    String matchedUrl;
                    int startIndex;
                    Iterator<String> tempList = myWebHistory.keys();
                    while( tempList.hasNext()) {
                        matchedUrl = tempList.next();
                        startIndex =  matchedUrl.toLowerCase().indexOf(constraint.toString().toLowerCase());
                        if ( startIndex!= -1 ) {
                            mCompResults.add(matchedUrl);
                            //mResults.add(matchedUrl.substring(startIndex));
                            mResults.add(matchedUrl);
                        }
                    }
                }
            }


            @Override
            public int getCount() {
                return mResults.size();
            }

            @Override
            public String[] getColumnNames() {
                return mFields;
            }

            @Override
            public long getLong(int column) {
                if (column == 0) {
                    return getPosition();
                }
                throw new UnsupportedOperationException("unimplemented");
            }

            @Override
            public String getString(int column) {
                if (column == 1) {
                    return mResults.get(getPosition());
                }
                throw new UnsupportedOperationException("unimplemented");
            }

            @Override
            public short getShort(int column) {
                throw new UnsupportedOperationException("unimplemented");
            }

            @Override
            public int getInt(int column) {
                throw new UnsupportedOperationException("unimplemented");
            }

            @Override
            public float getFloat(int column) {
                throw new UnsupportedOperationException("unimplemented");
            }

            @Override
            public double getDouble(int column) {
                throw new UnsupportedOperationException("unimplemented");
            }

            @Override
            public boolean isNull(int column) {
                return false;
            }
        }
    }
}

/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */