package com.classicloner.runjs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.widget.Scroller;
import android.widget.Toast;

import static com.classicloner.runjs.MainActivity.mWebView;
import static com.classicloner.runjs.MyFunctions.double_touch_script;
import static com.classicloner.runjs.MyFunctions.long_touch_script;

public class GestureListener extends GestureDetector.SimpleOnGestureListener {
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    float xPos1;
    float yPos1;
    private Context context;
    public String console_data="";
    AlertDialog.Builder alertDialogBuilder;
    Scroller mScroller;

    public GestureListener(Context current){
        this.context = current;
        alertDialogBuilder = new AlertDialog.Builder(current);
        mScroller= new Scroller(context);
    }


    @Override
    public void onLongPress(MotionEvent event) {
        float density = context.getResources().getDisplayMetrics().density; //Screen density
        xPos1 = event.getX() / density;  //Must be divided by the density of the screen
        yPos1 = event.getY() / density;
        clickImage(xPos1 , yPos1 , long_touch_script , "LONG");
        super.onLongPress(event);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        super.onDown(event);
        return true;
        /*
        Toast.makeText(context, "Single Tap" , Toast.LENGTH_SHORT);
        clickImage(0 , 0 , double_touch_script , "SINGLE");
        */
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        float density = context.getResources().getDisplayMetrics().density; //Screen density
        xPos1 = event.getX() / density;  //Must be divided by the density of the screen
        yPos1 = event.getY() / density;
        clickImage(xPos1 , yPos1 , double_touch_script , "DOUBLE");
        super.onDoubleTap(event);
        return true;
    }



    private void clickImage(float touchX, float touchY , String js_data , final String event) {
        final String function_data ;
        final Toast console_toast = new Toast(context);

        function_data = "(\n" +
                    "function(){\n" +
                    "    _x = "+touchX+"; \n" +
                    "    _y = "+touchY+"; \n" +
                    js_data+
                    "    console.log('_EOS_');\n" +
                    "})();";

        // executing the javascript
        mWebView.evaluateJavascript(function_data, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
                if( s.contains("null") )
                    return;
                console_toast.makeText(context,s , Toast.LENGTH_SHORT).show();
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            public boolean onConsoleMessage(ConsoleMessage cm) {
                if( cm.message().contains("_EOS_")){
                    console_toast.makeText(context,console_data , Toast.LENGTH_SHORT).show();
                    console_data = "";
                }
                else {
                    console_data += cm.message();
                }
                return true;
            }
        });
    }


}
