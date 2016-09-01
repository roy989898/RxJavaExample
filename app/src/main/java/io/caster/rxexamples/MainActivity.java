package io.caster.rxexamples;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Map;

import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static void showCustomDialog(String title, String message, Context activity) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setNegativeButton(activity.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getGistObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Gist>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

                Log.d("Rxjava",e+"");

            }

            @Override
            public void onNext(Gist gist) {
// Output
                for (Map.Entry<String, GistFile> entry : gist.files.entrySet()) {
                    Log.d(TAG, entry.getKey());
                    Log.d(TAG, "Length of file: " + entry.getValue().content.length());
                }
            }
        });



    }
    @Nullable
    private Gist getGist() throws IOException {
        OkHttpClient client = new OkHttpClient();

        // Go get this Gist: https://gist.github.com/donnfelker/db72a05cc03ef523ee74
        // via the GitHub API
        Request request = new Request.Builder()
                .url("https://api.github.com/gists/db72a05cc03ef523ee74")
                .build();


            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                Gist gist = new Gson().fromJson(response.body().charStream(), Gist.class);
                return gist;
            }

            return null;


    }


   public Observable<Gist> getGistObservable(){
       return Observable.defer(new Func0<Observable<Gist>>() {
           @Override
           public Observable<Gist> call() {
               try {
                   return Observable.just(getGist());
               } catch (IOException e) {
                  return Observable.error(e);
               }
           }
       });
   }

}
