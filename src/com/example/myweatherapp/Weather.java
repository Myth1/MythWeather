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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import model.WeatherData;
import util.HandlerUtil;
import util.HttpCallbackListener;
import util.HttpUtil;

public class Weather extends Activity implements OnClickListener {
	
	
	private static final int  COUNTRYCODE = 0;
	private static final int WEATHERCODE = 1;
	private TextView tv_city;
	private TextView tv_home;
	private TextView tv_update;
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
		tv_home = (TextView) findViewById(R.id.tv_home);
		tv_update = (TextView) findViewById(R.id.tv_update);
		tv_home.setOnClickListener(this);
		tv_update.setOnClickListener(this);
		Intent intent = getIntent();
		String countryCode = intent.getStringExtra("code");
		queryWeatherCode(countryCode);
		
		
	}
	private void query4weather(final String address,final int type){
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				if(type == COUNTRYCODE){
					if(!TextUtils.isEmpty(response)){
						String[] weatherCodes = response.split("\\|");
						if(weatherCodes != null && weatherCodes.length >0){
							String weatherCode = weatherCodes[1];
							queryWeather(weatherCode);
						}
					}

					
				}else if(type == WEATHERCODE){

					HandlerUtil.handleWeatherResponse(Weather.this,response);
					Log.d("tag", "haha");
					
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							showWeather();
						}
						
					});
				}
				
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
	private void queryWeather(String weatherCode2) {
		String address = "http://www.weather.com.cn/data/cityinfo/"+weatherCode2+".html";
		query4weather(address, WEATHERCODE);
	}

	private void queryWeatherCode(String countryCode) {
		String address = "http://www.weather.com.cn/data/list3/city"+countryCode+".xml";
		query4weather(address, COUNTRYCODE);
		
		
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
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.tv_home:
			Intent intent = new Intent();
			intent.setClass(this, MainActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			break;
		case R.id.tv_update:
			SharedPreferences sp = getSharedPreferences("config", 0);
			String weathercode = sp.getString("weathercode", "");
			if(TextUtils.isEmpty(weathercode)){
				queryWeather(weathercode);
			}
			break;

		default:
			break;
		}
	}

}