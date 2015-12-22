import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;

public class Httphandler {
    
    public String sendGet(String url) throws Exception {
    	URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
    	try{			
			// optional default is GET
			con.setRequestMethod("GET");
			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);
	
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			System.out.println(response.toString());
			return response.toString();
			//print result
    	}catch(Exception ex){
    		BufferedReader in = new BufferedReader(
	  		        new InputStreamReader(con.getErrorStream()));
	  		String inputLine;
	  		StringBuffer response = new StringBuffer();
	
	  		while ((inputLine = in.readLine()) != null) {
	  			response.append(inputLine);
	  		}
	  		in.close();
	  		
	  		//print result
	  		System.out.println(response.toString());
	  		return response.toString();
    	}
		

	}
    
    
	public String sendPost(String url,JSONObject requestJson) throws Exception {
		System.out.println(url+"   "+requestJson.toJSONString());
    	HttpClient httpClient = HttpClientBuilder.create().build();
    	try {
            HttpPost request = new HttpPost(url);
            StringEntity params =new StringEntity(requestJson.toJSONString(),ContentType.create("application/json"));
            request.addHeader("content-type", "application/json");
            request.setEntity(params); 
            HttpResponse response = httpClient.execute(request);
            return EntityUtils.toString(response.getEntity());          
            // handle response here...
        }catch (Exception ex) {
            // handle exception here
        	return "failed";
        }
    	
	}
    
    
    public String sendDelete(String url) throws Exception {

  		URL obj = new URL(url);
  		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

  		//add reuqest header
  		con.setRequestMethod("DELETE");
  		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
  		int responseCode = con.getResponseCode();
  		System.out.println("\nSending 'DELETE' request to URL : " + url);
  		System.out.println("Response Code : " + responseCode);
  		
  		try{
	  		BufferedReader in = new BufferedReader(
	  		        new InputStreamReader(con.getInputStream()));
	  		String inputLine;
	  		StringBuffer response = new StringBuffer();
	
	  		while ((inputLine = in.readLine()) != null) {
	  			response.append(inputLine);
	  		}
	  		in.close();
	  		
	  		//print result
	  		System.out.println(response.toString());
	  		return response.toString();
  		}catch(Exception ex){
  			BufferedReader in = new BufferedReader(
	  		        new InputStreamReader(con.getErrorStream()));
	  		String inputLine;
	  		StringBuffer response = new StringBuffer();
	
	  		while ((inputLine = in.readLine()) != null) {
	  			response.append(inputLine);
	  		}
	  		in.close();
	  		
	  		//print result
	  		System.out.println(response.toString());
	  		return response.toString();
  		}
  	}
    
    
    
    public String sendPut(String url,JSONObject requestJson) throws Exception {

    	HttpClient httpClient = HttpClientBuilder.create().build();
    	try {
    		HttpPut request = new HttpPut(url);
            StringEntity params =new StringEntity(requestJson.toJSONString(),ContentType.create("application/json"));
            request.addHeader("content-type", "application/json");
            request.setEntity(params); 
            HttpResponse response = httpClient.execute(request);
            return EntityUtils.toString(response.getEntity());
            // handle response here...
        }catch (Exception ex) {
            // handle exception here
        	return "failed";
        }
  	}
    
    
}
