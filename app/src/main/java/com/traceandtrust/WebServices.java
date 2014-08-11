package com.traceandtrust;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by Joey on 7/23/2014.
 */
public class WebServices {
    //JsonObject Parser
    public static Map<String,String> parse(JSONObject json , Map<String,String> out) throws JSONException {
        @SuppressWarnings("unchecked")
        Iterator<String> keys = json.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            String val = null;
            try {
                JSONObject value = json.getJSONObject(key);
                parse(value, out);
            } catch (Exception e) {
                val = json.getString(key);
            }

            if (val != null) {
                out.put(key, val);
            }
        }
        return out;
    }
    //Networking Connection Checking Method
    public static boolean isNetworkAvailable(Context c) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //WEB SERVICE CALL - SEARCH FILTER HIT
    public static JSONObject getSearchFilterPOSTMethod(String url)
    {
        String  sourceString = "";
        JSONObject json = null ;
        final HttpClient httpClient = new DefaultHttpClient();

        // Creating HTTP Post
        final HttpPost httpPost = new HttpPost(url);

        try
        {
            HttpResponse response = httpClient.execute(httpPost);

            // writing response to log
            Log.e("Http Response:", response.toString());
            if(response!=null)
            {
                HttpEntity resEntityGet = response.getEntity();
                sourceString= new String(EntityUtils.toString(resEntityGet));
                Log.e("InputStream",""+sourceString);
                try {
                    json = new JSONObject(sourceString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            Log.e("UnsupportedEncodingException",""+ e);
        }
        catch (ClientProtocolException e)
        {
            // writing exception to log
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // writing exception to log
            e.printStackTrace();
        }
        return json;
    }

    //General Alert Box displays error alert
    public static void alertbox(String title, String msg,Context context)
    {
        try{
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle(title);
            alert.setMessage(msg);
            alert.setCancelable(false);
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int whichButton)
                {
                    dialog.cancel();
                }
            });
            AlertDialog showAlert = alert.create();
            showAlert.show();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
