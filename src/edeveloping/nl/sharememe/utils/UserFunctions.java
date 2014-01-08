package edeveloping.nl.sharememe.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;

public class UserFunctions {

    private jsonParser jsonParser;
    
    private static String apiRUL = "http://www.sharememeapp.com";


    public UserFunctions(){
        jsonParser = new jsonParser();
    }
    
    public JSONObject getMemeStuff(){
    

        JSONObject json = jsonParser.getJSONFromUrlWithOutParam(apiRUL+"/pull");

        return json;
    }

    public JSONObject getTrends(){

        JSONObject json = jsonParser.getJSONFromUrlWithOutParam(apiRUL+"/pull/trends/");

        return json;
    }
    
public JSONObject getTrendsDetail(String GroupName, int count){        
		
        JSONObject json = jsonParser.getJSONFromUrlWithOutParam(apiRUL+"/pull/tag.php?term="+GroupName+"&count="+count);

        return json;
    }


}