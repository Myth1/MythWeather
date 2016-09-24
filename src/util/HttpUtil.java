package util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.util.Log;

public class HttpUtil {
	public static void sendHttpRequest(final String path,final HttpCallbackListener listener){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				HttpURLConnection conn = null;
				try {
					URL url = new URL(path);
					conn = (HttpURLConnection) url.openConnection();
					conn.setConnectTimeout(5000);
					conn.setRequestMethod("GET");
					int code = conn.getResponseCode();
					if(code == 200){
						InputStream is = conn.getInputStream();
						BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
						StringBuilder response = new StringBuilder();
						String line =null;
						while( (line = bufferedReader.readLine()) != null){
							response.append(line);
						}
						if(listener != null){
							Log.wtf("tag", "wtf");
							listener.onFinish(response.toString());
						}
					}
					
				} catch (Exception e) {
					if(listener != null){
						listener.onError(e);
					}
				}finally{
					if(conn != null){
						conn.disconnect();
					}
				}
			}
		}){}.start();
	}
	
	
	
	
	
	
	
	
}
