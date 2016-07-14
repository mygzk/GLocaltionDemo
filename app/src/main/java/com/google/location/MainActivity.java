package com.google.location;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog dialog;
    private TextView tvLoaction;
    private LocationUtil mLocationUtil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLocationUtil = LocationUtil.getInstanse(this);
        tvLoaction = (TextView) findViewById(R.id.tv_location);

        mLocationUtil.setILocationNotify(new LocationUtil.ILocationNotify() {
            @Override
            public void locationResult(Location location) {
                tvLoaction.setText(tvLoaction.getText() + "\n" + location.toString());
                dialog.dismiss();
            }

            @Override
            public void locationFail(String error) {
                tvLoaction.setText(tvLoaction.getText() + "\n" + error);
                dialog.dismiss();
            }
        });

        mLocationUtil.startLocation();
        // 等待提示
        dialog = new ProgressDialog(this);
        dialog.setMessage("正在定位...");
        dialog.setCancelable(true);
        dialog.show();
    }


    @Override
    protected void onDestroy() {
        mLocationUtil.stopLocation();
        super.onDestroy();
    }
}
