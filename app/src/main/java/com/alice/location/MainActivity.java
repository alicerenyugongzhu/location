package com.alice.location;

import android.app.Service;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.BDNotifyListener;//假如用到位置提醒功能，需要import该类
import com.baidu.location.Poi;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    BDNotifyListener mNotifyer;
    TextView locate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        locate = (TextView)findViewById(R.id.location);

        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        initLocation();
        //mLocationClient.start();
        //int a = mLocationClient.requestLocation();
        //Log.d("alice_debug", "Result is " + a);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Start location ", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                // mLocationClient.removeNotifyEvent(mNotifyer);
                mLocationClient.start();
            }
        });
      //  mLocationClient.start();
        //位置提醒相关代码
        mNotifyer = new NotifyLister();
       // mNotifyer.SetNotifyLocation(31.300015, 121.551288, 3000, "gps");//4个参数代表要位置提醒的点的坐标，具体含义依次为：纬度，经度，距离范围，坐标系类型(gcj02,gps,bd09,bd09ll)
        mNotifyer.SetNotifyLocation(131.300015, 121.551288, 3000, "gps");
        mLocationClient.registerNotify(mNotifyer);
        //取消位置提醒
        //mLocationClient.removeNotifyEvent(mNotifyer);
        //mLocationClient.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            mLocationClient.stop();
            locate.setText("The answer is true. Marlin is a fool. :) Have fun");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=30000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    public class NotifyLister extends BDNotifyListener {
        public void onNotify(BDLocation mlocation, float distance){
            Vibrator vb = (Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
            //取消位置提醒
            vb.vibrate(1000);//振动提醒已到设定位置附近
        }
    }
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            Log.d("alice_debug", "I am in the callback");
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            //Log.d("alice_debug", sb.toString());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            //Log.d("alice_debug", sb.toString());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            //Log.d("alice_debug", sb.toString());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            //Log.d("alice_debug",sb.toString());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            //Log.d("alice_debug", sb.toString());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                //Log.d("alice_debug", sb.toString());
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                //  Log.d("alice_debug", sb.toString());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                // Log.d("alice_debug", sb.toString());
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                //  Log.d("alice_debug", sb.toString());
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //   Log.d("alice_debug", "SB" + sb);
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");
                //   Log.d("alice_debug", sb.toString());

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                // Log.d("alice_debug",sb.toString());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                //    Log.d("alice_debug", sb.toString());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
                //   Log.d("alice_debug", sb.toString());
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
                // Log.d("alice_debug", sb.toString());
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                //    Log.d("alice_debug", sb.toString());
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
                //  Log.d("alice_debug", sb.toString());
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                //     Log.d("alice_debug", "SB" + sb.toString());
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            // Log.d("alice_debug", "SB" + sb.toString());
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            //Log.d("alice_debug", sb.toString());

            locate.setText(sb.toString());
        }
    }
}
