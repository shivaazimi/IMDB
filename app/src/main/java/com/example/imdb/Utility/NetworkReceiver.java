package com.example.imdb.Utility;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.imdb.R;

public class NetworkReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int status = NetworkUtil.getConnectivityStatusString(context);
        if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
            Toast.makeText(context, "no internet connection", Toast.LENGTH_LONG);
            show(context);
        } else if (status == NetworkUtil.TYPE_MOBILE){
            Toast.makeText(context, "mobile data", Toast.LENGTH_LONG).show();
        }

    }

    public void show (Context context){
        AlertDialog.Builder builder =new AlertDialog.Builder(context);
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(context.getResources().getLayout(R.layout.internet_connection),null);
        builder.setView(view);
//        builder.setTitle("No internet Connection");
//        builder.setMessage("Please turn on internet connection to continue");
        builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
