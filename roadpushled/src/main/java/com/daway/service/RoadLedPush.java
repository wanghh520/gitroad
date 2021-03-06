package com.daway.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.daway.utils.Utils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author Administrator
 *
 */
@Service
public class RoadLedPush {
	private static Utils utils = new Utils();
	int ledfalse = 0;
	
	@Async("taskExecutor")
	public void pushLedColor() {
		Properties serial;
		while(true) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
				Date nowtime = new Date(); 
				String now = sdf.format(nowtime);
				nowtime = sdf.parse(now);
				Date startTime1 = sdf.parse("00:00:00");
				Date endTime1 = sdf.parse("15:00:00");
				if(isEffectiveDate(nowtime,startTime1,endTime1)) {
					serial = utils.readProperties("application.properties");
					String centercoordinate = serial.getProperty("centercoordinate");
					String baiduak = serial.getProperty("baiduak1");
					String baiduurl = "http://api.map.baidu.com/traffic/v1/around?ak="+baiduak+"&center="+centercoordinate+"&radius=300&coord_type_input=gcj02&coord_type_output=gcj02";
					getRoadStatus(baiduurl);
				}
				Date startTime2 = sdf.parse("15:00:00");
				Date endTime2 = sdf.parse("24:00:00");
				if(isEffectiveDate(nowtime,startTime2,endTime2)) {
					serial = utils.readProperties("application.properties");
					String centercoordinate = serial.getProperty("centercoordinate");
					String baiduak = serial.getProperty("baiduak2");
					String baiduurl = "http://api.map.baidu.com/traffic/v1/around?ak="+baiduak+"&center="+centercoordinate+"&radius=300&coord_type_input=gcj02&coord_type_output=gcj02";
					getRoadStatus(baiduurl);
				}
				Thread.sleep(1000*60);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
	/**
     * ???????????????????????????[startTime, endTime]????????????????????????????????????
     * 
     * @param nowTime ????????????
     * @param startTime ????????????
     * @param endTime ????????????
     * @return
     * @author whh
     */
    public boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }
	
	public void getRoadStatus(String baiduurl) {
		Properties serial;
		try { 
			serial = utils.readProperties("application.properties");
			String baidudata = sendGet(baiduurl);
//			System.out.println(baidudata);
			JSONObject json0 = JSONObject.fromObject(baidudata);
			String status = json0.getString("status");
//			System.out.println(status);
			JSONArray road_traffic = json0.getJSONArray("road_traffic");
			Map<String,String> map = new HashMap<String,String>();
			for(int i=0;i<road_traffic.size();i++) {
				JSONObject json1 = (JSONObject) road_traffic.get(i);
				String road_name = json1.getString("road_name");
//				System.out.println(road_name);
				
				boolean iss = json1.has("congestion_sections");
				if(iss) {
					JSONArray congestion_sections = json1.getJSONArray("congestion_sections");
					for(int j=0;j<congestion_sections.size();j++) {
						JSONObject json2 = (JSONObject) congestion_sections.get(j);
						String congestion_distance = json2.getString("congestion_distance");
						String speed = json2.getString("speed");
						String status2 = json2.getString("status");
						String congestion_trend = json2.getString("congestion_trend");
						String section_desc = json2.getString("section_desc");
//						System.out.println(congestion_distance+"-"+speed+"-"+status2+"-"+congestion_trend+"-"+section_desc);
						String direction = section_desc.substring(0, section_desc.indexOf(","));
						map.put(direction, status2);
					}
				}
			}
			
			String situation1 = map.get("?????????");
			String situation2 = map.get("?????????");
			String situation3 = map.get("?????????");
			String situation4 = map.get("?????????");
			
			String leftcolor = "green";
			String rightcolor = "green";
			String centernorthcolor = "green";
			String centersouthcolor = "green";
			
			if(situation1!=null && Integer.parseInt(situation1)>2) {
				centernorthcolor = "red";
			}else if(situation1!=null && Integer.parseInt(situation1)==2) {
				centernorthcolor = "yellow";
			}
			
			if(situation2!=null && Integer.parseInt(situation2)>2) {
				leftcolor = "red";
			}else if(situation2!=null && Integer.parseInt(situation2)==2) {
				leftcolor = "yellow";
			}
			
			if(situation3!=null && Integer.parseInt(situation3)>2) {
				rightcolor = "red";
			}else if(situation3!=null && Integer.parseInt(situation3)==2) {
				rightcolor = "yellow";
			}
			
			if(situation4!=null && Integer.parseInt(situation4)>2) {
				centersouthcolor = "red";
			}else if(situation4!=null && Integer.parseInt(situation4)==2) {
				centersouthcolor = "yellow";
			}
			
			//??????????????????
			String data = getJsonData(leftcolor,rightcolor,centernorthcolor);
			if(!data.equals(Utils.northdata)) {
				String northledip = serial.getProperty("northledip");
				String url = "http://" + northledip + "/api/program/Multi-Line.vsn";
				ledPush(data,url);
				
				if(ledfalse==0) {
					Utils.northdata = data;
				}
			}
			
			//??????????????????
			String southdata = getJsonData(rightcolor,leftcolor,centersouthcolor);
			if(!southdata.equals(Utils.southdata)) {
				
				String southledip = serial.getProperty("southledip");
				String url = "http://" + southledip + "/api/program/Multi-Line.vsn";
				ledPush(data,url);
				
				if(ledfalse==0) {
					Utils.southdata = southdata;
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * led????????? @Title: ledPush @Description: TODO @param: @param data @param: @param
	 * url @return: void @throws
	 */
	public void ledPush(String data, String url) {
		try {
			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			requestFactory.setConnectTimeout(2000);// ????????????
			requestFactory.setReadTimeout(2000);
			RestTemplate restTemplate = new RestTemplate(requestFactory);

			List<HttpMessageConverter<?>> converterList = restTemplate.getMessageConverters();
			HttpMessageConverter<?> converter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
			converterList.add(0, converter);
			restTemplate.setMessageConverters(converterList);

			HttpHeaders headers = new HttpHeaders();
			MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
			headers.setContentType(type);
			headers.add("Accept", MediaType.APPLICATION_JSON.toString());

			HttpEntity<String> formEntity = new HttpEntity<String>(data, headers);
			// String result = restTemplate.getForObject(url, String.class);
			String result = restTemplate.postForObject(url, formEntity, String.class);
			System.out.println(url + "LED?????????=============" + url);
//			System.out.println("result:" + result);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(url + "??????LED???????????????=============");
			ledfalse = 1;
		}
	}
	
	/**
	 * ????????????led??????json?????? @Title: sendLedJson @Description: TODO @param: @param
	 * context @param: @return @return: String @throws
	 */
	public String sendLedJson(String context) {
		Properties serial;
		JSONObject result = new JSONObject();
		try {
			serial = utils.readProperties("application.properties");
			String pedestrians_i_width = serial.getProperty("pedestrians_i_width");
			String pedestrians_i_height = serial.getProperty("pedestrians_i_height");
			String pedestrians_l_lfHeight = serial.getProperty("pedestrians_l_lfHeight");
			String pedestrians_l_lfWeight = serial.getProperty("pedestrians_l_lfWeight");
			String pedestrians_l_lfFaceName = new String(
					serial.getProperty("pedestrians_l_lfFaceName").getBytes("ISO-8859-1"), "UTF-8");
			String pedestrians_i_TextColor = serial.getProperty("pedestrians_i_TextColor");
			String pedestrians_r_X = serial.getProperty("pedestrians_r_X");
			String pedestrians_r_Y = serial.getProperty("pedestrians_r_Y");
			String pedestrians_r_width = serial.getProperty("pedestrians_r_width");
			String pedestrians_r_height = serial.getProperty("pedestrians_r_height");

			JSONObject Program = new JSONObject();

			JSONObject user1 = new JSONObject();

			JSONObject information = new JSONObject();
			information.put("Width", pedestrians_i_width);
			information.put("Height", pedestrians_i_height);

			user1.put("Information", information);

			JSONObject position_LatLon = new JSONObject();
			position_LatLon.put("lfHeight", pedestrians_l_lfHeight);
			position_LatLon.put("lfWeight", pedestrians_l_lfWeight);
			position_LatLon.put("lfFaceName", pedestrians_l_lfFaceName);
			JSONObject posoffsetLL = new JSONObject();
			posoffsetLL.put("Type", "4");
			posoffsetLL.put("TextColor", pedestrians_i_TextColor);
			posoffsetLL.put("Text", context);
			posoffsetLL.put("IsScroll", "0");
			posoffsetLL.put("Speed", 92);
			posoffsetLL.put("LogFont", position_LatLon);

			JSONArray itemsarray = new JSONArray();
			itemsarray.add(0, posoffsetLL);

			JSONObject elevation = new JSONObject();
			elevation.put("X", pedestrians_r_X);
			elevation.put("Y", pedestrians_r_Y);
			elevation.put("Width", pedestrians_r_width);
			elevation.put("Height", pedestrians_r_height);

			JSONObject posarray = new JSONObject();
			posarray.put("Layer", 1);
			posarray.put("Rect", elevation);
			posarray.put("Items", itemsarray);

			JSONArray regionsarray = new JSONArray();
			regionsarray.add(0, posarray);

			JSONObject regions = new JSONObject();
			regions.put("Regions", regionsarray);

			JSONArray pages = new JSONArray();
			pages.add(0, regions);

			user1.put("Pages", pages);

			Program.element("Program", user1);
			result.element("Programs", Program);
//			System.out.println(result.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result.toString();
	}
	
	 public static String sendGet(String urlNameString) {
	        String result = "";
	        BufferedReader in = null;
	        try {
	            URL realUrl = new URL(urlNameString);
	            // ?????????URL???????????????
	            URLConnection connection = realUrl.openConnection();
	            // ???????????????????????????
	            connection.setRequestProperty("accept", "*/*");
	            connection.setRequestProperty("connection", "Keep-Alive");
	            connection.setRequestProperty("user-agent",
	                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
	            // ?????????????????????
	            connection.connect();
	            // ???????????????????????????
//	            Map<String, List<String>> map = connection.getHeaderFields();
	            // ??????????????????????????????
//	            for (String key : map.keySet()) {
//	                System.out.println(key + "--->" + map.get(key));
//	            }
	            // ?????? BufferedReader??????????????????URL?????????
	            in = new BufferedReader(new InputStreamReader(
	                    connection.getInputStream()));
	            String line;
	            while ((line = in.readLine()) != null) {
	                result += line;
	            }
	        } catch (Exception e) {
	            System.out.println("??????GET?????????????????????" + e);
	            e.printStackTrace();
	        }
	        // ??????finally?????????????????????
	        finally {
	            try {
	                if (in != null) {
	                    in.close();
	                }
	            } catch (Exception e2) {
	                e2.printStackTrace();
	            }
	        }
	        return result;
	    }
	 
	 public String getJsonData(String leftcolor,String rightcolor,String centercolor) {
		 String data = "{\r\n" + 
		 		"  \"Programs\": {\r\n" + 
		 		"    \"Program\": {\r\n" + 
		 		"		\"Information\":{  \r\n" + 
		 		"			\"Width\":\"256\",\r\n" + 
		 		"			\"Height\":\"256\"\r\n" + 
		 		"			},\r\n" + 
		 		"      \"Pages\": [{\r\n" + 
		 		"          \"Name\":\"Page 1\",\r\n" + 
		 		"          \"Regions\": [\r\n" + 
		 		"              {\r\n" + 
		 		"			    \"Layer\":1,\r\n" + 
		 		"                \"Rect\": {\r\n" + 
		 		"                  \"X\": \"0\",\r\n" + 
		 		"                  \"Y\": \"0\",\r\n" + 
		 		"                  \"Width\": \"232\",\r\n" + 
		 		"                  \"Height\": \"32\"\r\n" + 
		 		"                },\r\n" + 
		 		"                \"Items\": [{\r\n" + 
		 		"                    \"Type\": \"4\",\r\n" + 
		 		"                    \"BackColor\": "+leftcolor+",\r\n" + 
		 		"                    \"TextColor\":"+leftcolor+",\r\n" + 
		 		"                    \"Text\" : \"\",\r\n" + 
		 		"                    \"LogFont\":{\r\n" + 
		 		"						\"lfHeight\":\"32\",\r\n" + 
		 		"						\"lfWeight\":\"0\",\r\n" + 
		 		"					    \"lfFaceName\":\"??????\"\r\n" + 
		 		"					}\r\n" + 
		 		"				}]\r\n" + 
		 		"              },{\r\n" + 
		 		"			    \"Layer\":2,\r\n" + 
		 		"                \"Rect\": {\r\n" + 
		 		"                  \"X\": \"0\",\r\n" + 
		 		"                  \"Y\": \"32\",\r\n" + 
		 		"                  \"Width\": \"232\",\r\n" + 
		 		"                  \"Height\": \"32\"\r\n" + 
		 		"                },\r\n" + 
		 		"                \"Items\": [{\r\n" + 
		 		"                    \"Type\": \"4\",\r\n" + 
		 		"                    \"BackColor\": "+rightcolor+",\r\n" + 
		 		"                    \"TextColor\":"+rightcolor+",\r\n" + 
		 		"                    \"Text\" : \"\",\r\n" + 
		 		"                    \"LogFont\":{\r\n" + 
		 		"						\"lfHeight\":\"32\",\r\n" + 
		 		"						\"lfWeight\":\"0\",\r\n" + 
		 		"					    \"lfFaceName\":\"??????\"\r\n" + 
		 		"					}\r\n" + 
		 		"				}]\r\n" + 
		 		"              },{\r\n" + 
		 		"			    \"Layer\":3,\r\n" + 
		 		"                \"Rect\": {\r\n" + 
		 		"                  \"X\": \"0\",\r\n" + 
		 		"                  \"Y\": \"76\",\r\n" + 
		 		"                  \"Width\": \"232\",\r\n" + 
		 		"                  \"Height\": \"32\"\r\n" + 
		 		"                },\r\n" + 
		 		"                \"Items\": [{\r\n" + 
		 		"                    \"Type\": \"4\",\r\n" + 
		 		"                    \"BackColor\": "+centercolor+",\r\n" + 
		 		"                    \"TextColor\":"+centercolor+",\r\n" + 
		 		"                    \"Text\" : \"\",\r\n" + 
		 		"                    \"LogFont\":{\r\n" + 
		 		"						\"lfHeight\":\"32\",\r\n" + 
		 		"						\"lfWeight\":\"0\",\r\n" + 
		 		"					    \"lfFaceName\":\"??????\"\r\n" + 
		 		"					}\r\n" + 
		 		"				}]\r\n" + 
		 		"              }\r\n" + 
		 		"            ]\r\n" + 
		 		"        }]\r\n" + 
		 		"    }\r\n" + 
		 		"  }\r\n" + 
		 		"}";
		 return data;
	 }
	 
}
