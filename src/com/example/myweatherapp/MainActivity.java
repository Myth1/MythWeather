package com.example.myweatherapp;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import db.AreaDB;
import model.City;
import model.Country;
import model.Province;
import util.HandlerUtil;
import util.HttpCallbackListener;
import util.HttpUtil;

public class MainActivity extends Activity {
	protected static final int PROVINCE = 0;
	protected static final int CITY = 1;
	protected static final int COUNTRY = 2;
	private ListView lv_listArea;
	private TextView tv_currentArea;
	private ProgressDialog progressDialog;
	private AreaDB areadb;
	
	private int currentLevel;
	private Province selectedProvince;
	private City selectedCity;
	private Country selectedCountry;
	private ArrayList<Province> provinces;
	private ArrayList<City> cities;
	private ArrayList<Country> countries;
	private ArrayList<String> datalist;
	private ArrayAdapter<String> adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choosearea);
		datalist = new ArrayList<String>();
		lv_listArea = (ListView) findViewById(R.id.lv_listArea);
		tv_currentArea = (TextView) findViewById(R.id.tv_currentArea);
		adapter = new ArrayAdapter<String>(this, R.layout.item, datalist);
		lv_listArea.setAdapter(adapter);
		areadb = AreaDB.getInstance(this);
		lv_listArea.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
				
				if(currentLevel == PROVINCE){
					selectedProvince = provinces.get(index);
					queryCity();
				}else if(currentLevel == CITY){
					selectedCity = cities.get(index);
					queryCountry();
				}else if(currentLevel == COUNTRY){
					selectedCountry = countries.get(index);
					Intent intent = new Intent();
					intent.setClass(MainActivity.this,Weather.class);
					intent.putExtra("code", selectedCountry.getCountry_code());
					startActivity(intent);
				}
				
			}
			
		});
		queryProvince();
		
	}
	
	private void queryProvince(){
		
		provinces =  areadb.loadProvince();
		if(provinces.size() > 0){
			datalist.clear();
			for(Province province : provinces){
				String province_name = province.getProvince_name();
				datalist.add(province_name);
			}
			adapter.notifyDataSetChanged();
			lv_listArea.setSelection(0);
			tv_currentArea.setText("中国");
			currentLevel = PROVINCE;
		}else{
			query4server(null,PROVINCE);
		}
		
	}
	private void queryCity(){
		
		cities =  areadb.loadCity(selectedProvince.getId());
		if(cities.size() > 0){
			datalist.clear();
			for(City city : cities){
				String city_name = city.getCity_name();
				datalist.add(city_name);
			}
			adapter.notifyDataSetChanged();
			lv_listArea.setSelection(0);
			tv_currentArea.setText(selectedProvince.getProvince_name());
			currentLevel = CITY;
		}else{
			query4server(selectedProvince.getProvince_code(),CITY);
		}
		
	}
	private void queryCountry(){
		
		countries =  areadb.loadCountry(selectedCity.getCity_id());
		if(countries.size() > 0){
			datalist.clear();
			for(Country country : countries){
				String country_name = country.getCountry_name();
				datalist.add(country_name);
			}
			adapter.notifyDataSetChanged();
			lv_listArea.setSelection(0);
			tv_currentArea.setText(selectedCity.getCity_name());
			currentLevel = COUNTRY;
		}else{
			query4server(selectedCity.getCity_code(),COUNTRY);
		}
		
	}

	private void query4server(String code, final int type) {
		String path = null;
		if(TextUtils.isEmpty(code)){
			path = "http://www.weather.com.cn/data/list3/city.xml";
		}else{
			path = "http://www.weather.com.cn/data/list3/city"+code+".xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(path, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				boolean result = false;
				if(type == PROVINCE){
					result = HandlerUtil.handleProvincesResponse(areadb, response);
				}else if(type == CITY){
					result = HandlerUtil.handleCitiesResponse(areadb, response, selectedProvince.getId());
				}else if(type == COUNTRY){
					result = HandlerUtil.handleCountiesResponse(areadb, response, selectedCity.getCity_id());
				}
				if(result){
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							closeProgressDialog();
							if(type == PROVINCE){
								queryProvince();
							}else if(type == CITY){
								queryCity();
							}else if(type == COUNTRY){
								queryCountry();
							}
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(MainActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
		
	
	}

	private void showProgressDialog() {
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("loading");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	private void closeProgressDialog(){
		if(progressDialog != null){
			progressDialog.dismiss();
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		
		if(currentLevel == PROVINCE ){
			queryCity();
		}else if(currentLevel == CITY){
			queryCountry();
		}else{
			finish();
		}
	}
}
