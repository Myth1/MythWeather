package com.example.myweatherapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.test.PerformanceTestCase;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import model.WeatherData;
import util.HandlerUtil;
import util.HttpCallbackListener;
import util.HttpUtil;

public class Weather extends Activity {
	
	
	private TextView tv_city;
	private TextView tv_weather;
	private TextView tv_temp1;
	private TextView tv_temp2;
	private TextView tv_ptime;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather);
		
		tv_ptime = (TextView) findViewById(R.id.tv_ptime);
		tv_weather = (TextView) findViewById(R.id.tv_weather);
		tv_city = (TextView) findViewById(R.id.tv_city);
		tv_temp1 = (TextView) findViewById(R.id.tv_temp1);
		tv_temp2 = (TextView) findViewById(R.id.tv_temp2);
		
		Intent intent = getIntent();
		String countryCode = intent.getStringExtra("code");
		queryWeatherCode(countryCode);
		
		
	}
	
	private void queryWeather(String weatherCode2) {
		String address = "http://www.weather.com.cn/data/cityinfo/"+weatherCode2+".html";
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				HandlerUtil.handleWeatherResponse(Weather.this,response);
				Log.d("tag", "haha");
				
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						showWeather();
					}

					
				});
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(Weather.this, "加载失败", Toast.LENGTH_SHORT).show();
					}
				});
				
			}
		});
	}

	private void queryWeatherCode(String countryCode) {
		String address = "http://www.weather.com.cn/data/list3/city"+countryCode+".xml";
		
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(final String response) {
				if(!TextUtils.isEmpty(response)){
					String[] weatherCodes = response.split("\\|");
					if(weatherCodes != null && weatherCodes.length >0){
						String weatherCode = weatherCodes[1];
						queryWeather(weatherCode);
					}
				}
				
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(Weather.this, "同步失敗", Toast.LENGTH_SHORT).show();
					}
				});
				
			}
		});
	}
	private void showWeather() {
//		 SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Weather.this);
		 SharedPreferences sp = getSharedPreferences("config", 0);
		 String city = sp.getString("city", "");
		 String temp1 = sp.getString("temp1", "");
		 String temp2 = sp.getString("temp2", "");
		 String weather = sp.getString("weather", "");
		 String ptime = sp.getString("ptime", "");
		 tv_city.setText(city);
		 tv_temp1.setText(temp1);
		 tv_temp2.setText(temp2);
		 tv_weather.setText(weather);
		 tv_ptime.setText(ptime);
		  
	}

}