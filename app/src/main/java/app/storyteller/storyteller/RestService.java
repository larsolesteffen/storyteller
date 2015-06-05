package app.storyteller.storyteller;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Jan on 01.06.2015.
 */
public class RestService extends AsyncTask {

    private Context _context;
    private URL _url;
    private HttpURLConnection _connection = null;
    private String _requestType;
    private String _rootUrl = "http://janz93.koding.io/storyteller/api/";
    private HashMap _postParams = new HashMap();

    public RestService(Context context) {
        this._context = context;
    }

    private boolean _hasInternetConnection() {
        //Create object for ConnectivityManager class which returns network related info
        ConnectivityManager connectivity = (ConnectivityManager) _context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        //If connectivity object is not null
        if (connectivity != null) {
            //Get network info - WIFI internet access
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (info != null) {
                //Look for whether device is currently connected to WIFI network
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }

    private String _getText(InputStream in) {
        String text = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            text = sb.toString();
        } catch (Exception ex) {

        } finally {
            try {

                in.close();
            } catch (Exception ex) {
            }
        }
        return text;
    }

//    private

    @Override
    protected Object doInBackground(Object[] params) {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) this._url.openConnection();
            Log.i("RequestType before:", urlConnection.getRequestMethod());
            urlConnection.setRequestMethod(this._requestType);
            Log.i("RequestType after:", urlConnection.getRequestMethod());
            Log.i("url", urlConnection.getURL().toString());
            if (this._postParams != null && !this._postParams.isEmpty()) {
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Content-Type", "application/x-ww-form-urlencoded");
                OutputStream os = urlConnection.getOutputStream();

                Iterator it = this._postParams.entrySet().iterator();
                String postParams = "";
                Integer count = 0;
                Uri.Builder builder = new Uri.Builder();
                while (it.hasNext()) {
                    HashMap.Entry pair = (HashMap.Entry)it.next();
//                    if (count > 0) {
//                        postParams += "&";
//                    }
//                    postParams += pair.getKey() + "=" + URLEncoder.encode(pair.getValue().toString(), "UTF-8");
//                    it.remove();
//                    count++;
                    builder.appendQueryParameter(pair.getKey().toString(), pair.getValue().toString());
                }
                String query = builder.build().getEncodedQuery();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                Log.d("Postparams", query);
                writer.write(query);
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            Log.i("Restservice", "Error");
            e.printStackTrace();
        }
        try {
            InputStream in = null;
            if (urlConnection.getResponseCode() == 200) {
                in = new BufferedInputStream(urlConnection.getInputStream());
            } else {
                in = new BufferedInputStream(urlConnection.getErrorStream());
            }

            Log.i("ResponceMessage", urlConnection.getResponseMessage());
            String responseStr = this._getText(in);
            Log.d("RestService", responseStr);
            try {
                JSONObject jsonResponse = new JSONObject(responseStr);
                JSONObject jsonError = jsonResponse.getJSONObject("message");
                Log.i("Json Error", jsonError.getString("error"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setGet(String url) {
        try {
            this._url = new URL(this._rootUrl + url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        this._requestType = "GET";
    }

    public void setPost(String url, HashMap postParams) {
        try {
            this._url = new URL(this._rootUrl + url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        this._requestType = "POST";
        this._setPostParams(postParams);
    }

    private void _setPostParams(HashMap postParams) {
        this._postParams = postParams;
    }

}
