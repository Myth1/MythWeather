package db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import model.City;
import model.Country;
import model.Province;

public class AreaDB {
	private static AreaDB areadb;
	SQLiteDatabase db;
	private AreaDB(Context context){
		AreaOpenHelper areaop = new AreaOpenHelper(context, "area.db", null, 1);
		db = areaop.getWritableDatabase();
	}
	
	public synchronized static AreaDB getInstance(Context context){
		if(areadb == null){
			areadb = new AreaDB(context);
		}
		
		return areadb;
		
	} 
	
	public ArrayList<Province> loadProvince() {
		ArrayList<Province> provinces = new ArrayList<Province>();
		Cursor cursor = db.query("province", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				Province province = new Province();
//				province.setId(cursor.getInt(0));
				province.setId(cursor.getInt(cursor.getColumnIndex("_id")));
				province.setProvince_code(cursor.getString(cursor.getColumnIndex("province_code")));
				province.setProvince_name(cursor.getString(cursor.getColumnIndex("province_name")));
				provinces.add(province);
			}while(cursor.moveToNext());
		}
		if(cursor != null){
			cursor.close();
		}
		return provinces;
	}
	
	public void addProvince(Province province){
		if(province != null){
			ContentValues values = new ContentValues();
			values.put("province_code", province.getProvince_code());
			values.put("province_name", province.getProvince_name());
			
			db.insert("province", null, values );
		}
	}
	
	public ArrayList<City> loadCity(int province_id){
		ArrayList<City> cities = new ArrayList<City>();
		Cursor cursor = db.query("city", null, "province_id=?", new String[]{String.valueOf(province_id)}, null, null, null);
		if(cursor.moveToFirst()){
			do{
				City city = new City();
				city.setCity_id(cursor.getInt(cursor.getColumnIndex("_id")));
				city.setCity_code(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setCity_name(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setProvince_id(province_id);
				cities.add(city);
				
			}while(cursor.moveToNext());
		}
		if(cursor != null){
			cursor.close();
		}
		
		return cities;
		
	}
	
	public void addCity(City city){
		ContentValues values = new ContentValues();
		values.put("city_code", city.getCity_code());
		values.put("city_name", city.getCity_name());
		values.put("province_id", city.getProvince_id());
		db.insert("city", null, values);
	}
	
	
	public ArrayList<Country> loadCountry(int city_id){
		ArrayList<Country> countries = new ArrayList<Country>();
		Cursor cursor = db.query("country", null, "city_id=?", new String[]{String.valueOf(city_id)}, null, null, null);
		if(cursor.moveToFirst()){
			do{
				Country country = new Country();
				country.setCountry_id(cursor.getInt(cursor.getColumnIndex("_id")));
				country.setCountry_code(cursor.getString(cursor.getColumnIndex("country_code")));
				country.setCountry_name(cursor.getString(cursor.getColumnIndex("country_name")));
				country.setCity_id(city_id);
				countries.add(country);
				
			}while(cursor.moveToNext());
		}
		if(cursor != null){
			cursor.close();
		}
		
		return countries;
		
	}
	
	public void addCountry(Country country){
		ContentValues values = new ContentValues();
		values.put("country_code", country.getCountry_code());
		values.put("country_name",country.getCountry_name());
		values.put("city_id", country.getCity_id());
		db.insert("country", null, values);
	}
	
}