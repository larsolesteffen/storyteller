package app.storyteller.storyteller;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by Jan on 03.06.2015.
 */
class AsyncTaskExample extends AsyncTask<Void, Integer, String> {

    @Override
    protected void onPreExecute(){
        Log.d("Asyntask", "On preExceute...");}

    protected String doInBackground(Void...arg0) {
        Log.d("Asyntask","On doInBackground...");
        for(int i=0; i<5; i++){
            Integer in = new Integer(i);
            publishProgress(i);
        }
        return "You are at PostExecute";}

    protected void onProgressUpdate(Integer...a){
        Log.d("Asyntask","You are in progress update ... " + a[0]);}

    protected void onPostExecute(String result) {
        Log.d("Asyntask",result); }
}
