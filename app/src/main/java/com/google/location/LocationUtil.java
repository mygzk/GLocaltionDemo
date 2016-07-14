package com.google.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;

/**
 * Created by guozhk on 16-7-14.
 * 使用google api 定位
 */
public class LocationUtil {
    private static final long updateLocationTime = 3;
    private static final long updateLocationDistance = 0;
    private static LocationUtil mLocationUtil;
    private LocationManager mLocationManager;
    private Context mContext;
    //定位方式　
    private String provider;

    private ILocationNotify mILocationNotify;

    private static final int LOCATION_SUSS = 0;
    private static final int LOCATION_FAIL = 1;

    //是否则定位一次
    private boolean onlyOnce = true;


    private Handler mHandler = new Handler(Looper.getMainLooper());


    public synchronized static LocationUtil getInstanse(Context context) {
        if (mLocationUtil == null) {
            synchronized (LocationUtil.class) {
                if (mLocationUtil == null) {
                    mLocationUtil = new LocationUtil(context.getApplicationContext());
                }
            }
        }
        return mLocationUtil;
    }

    private LocationUtil(Context context) {
        mContext = context;
        initLocation(context);

    }


    private void initLocation(Context context) {
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        // 获得最好的定位效果
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //海拔
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        // 使用省电模式
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        // 获得当前的位置提供者
        provider = mLocationManager.getBestProvider(criteria, true);
    }

    public void startLocation() {
        startLocation(true);
    }

    public void startLocation(boolean once) {
        if (checkPermission()) {
            setOnlyOnce(once);
            mLocationManager
                    .requestLocationUpdates(provider, updateLocationTime, updateLocationDistance,
                            mLocationListener);
        }
    }

    public void stopLocation() {
        if (checkPermission()) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }


    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            if (mILocationNotify != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mILocationNotify.locationResult(location);
                    }
                });

            }
            if (onlyOnce) {
                stopLocation();
            }
        }

        @Override
        public void onStatusChanged(final String provider, int status, Bundle extras) {
           /* if (mILocationNotify != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mILocationNotify.locationFail("onStatusChanged provider:" + provider);
                    }
                });

            }*/
        }

        @Override
        public void onProviderEnabled(final String provider) {

           /* if (mILocationNotify != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mILocationNotify.locationFail("onProviderEnabled provider:" + provider);
                    }
                });

            }
*/
        }

        @Override
        public void onProviderDisabled(final String provider) {
       /*     if (mILocationNotify != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mILocationNotify.locationFail("onProviderDisabled provider:" + provider);
                    }
                });

            }*/

        }
    };


    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            if (mILocationNotify != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mILocationNotify.locationFail("没有权限");
                    }
                });

            }
            return false;
        }
        return true;
    }

    public void setOnlyOnce(boolean onlyOnce) {
        this.onlyOnce = onlyOnce;
    }

    public void setILocationNotify(ILocationNotify mILocationNotify) {
        this.mILocationNotify = mILocationNotify;
    }

    interface ILocationNotify {

        void locationResult(Location location);

        void locationFail(String error);
    }

}
