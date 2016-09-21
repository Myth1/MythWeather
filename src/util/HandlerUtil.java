package util;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import db.AreaDB;
import model.City;
import model.Country;
import model.Province;

public class HandlerUtil {
	public synchronized static boolean handleProvincesResponse(AreaDB db,String response){
		
		if(!TextUtils.isEmpty(response)){
			String[] provinces = response.split(",");
			if(provinces != null && provinces.length > 0){
				for (String province : provinces) {
					String[] strings = province.split("\\|");
					Province province2 = new Province();
					province2.setProvince_code(strings[0]);
					province2.setProvince_name(strings[1]);
					db.addProvince(province2);
				}
				return true;
			}
		}
		
		return false;
	}
	public synchronized static boolean handleCitiesResponse(AreaDB db,String response,int provinceid){
		
		if(!TextUtils.isEmpty(response)){
			String[] cities = response.split(",");
			if(cities != null && cities.length > 0){
				for (String city : cities) {
					String[] strings = city.split("\\|");
					City city2 = new City();
					city2.setCity_code(strings[0]);
					city2.setCity_name(strings[1]);
					city2.setProvince_id(provinceid);
					db.addCity(city2);
				}
				return true;
			}
		}
		
		return false;
	}
	public synchronized static boolean handleCountiesResponse(AreaDB db,String response,int cityid){
		
		if(!TextUtils.isEmpty(response)){
			String[] counties = response.split(",");
			if(counties != null && counties.length > 0){
				for (String country : counties) {
					String[] strings = country.split("\\|");
					Country country2 = new Country();
					country2.setCountry_code(strings[0]);
					country2.setCountry_name(strings[1]);
					country2.setCity_id(cityid);
					db.addCountry(country2);
				}
				return true;
			}
		}
		
		return false;
	}
	public static void handleWeatherResponse(Context context, String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject jsonInfo = jsonObject.getJSONObject("weatherinfo");
			String city = jsonInfo.getString("city");
			String temp1 = jsonInfo.getString("temp1");
			String temp2 = jsonInfo.getString("temp2");
			String weather = jsonInfo.getString("weather");
			String ptime = jsonInfo.getString("ptime");
			saveWeatherInfo(context,city,temp1,temp2,weather,ptime);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void saveWeatherInfo(Context context, String city, String temp1, String temp2, String weather,
			String ptime) {
		Editor edit = context.getSharedPreferences("config", 0).edit();
//		edit.putBoolean("city_selected", true);
		edit.putString("city", city);
		edit.putString("temp1", temp1);
		edit.putString("temp2", temp1);
		edit.putString("weather", weather);
		edit.putString("ptime", ptime);
		edit.commit();
	}
}
