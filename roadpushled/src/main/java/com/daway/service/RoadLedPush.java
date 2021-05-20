/**
 * 
 */
package com.daway.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
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
import org.springframework.web.client.RestTemplate;

import com.daway.utils.Utils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author Administrator
 *
 */
public class RoadLedPush {
	private static Utils utils = new Utils();
	
	@Test
	public void test() {
		getRoadStatus();
	}
	
	public void getRoadStatus() {
		Properties serial;
		try { 
			serial = utils.readProperties("application.properties");
			String centercoordinate = serial.getProperty("centercoordinate");
			String baiduurl = "http://api.map.baidu.com/traffic/v1/around?ak=7T5CaExBDqUj0lYZd0BT9oG2Z5MYiTp9&center="+centercoordinate+"&radius=500&coord_type_input=gcj02&coord_type_output=gcj02";
			String baidudata = sendGet(baiduurl);
			
			baidudata = "{\r\n" + 
					"	\"status\": 0,\r\n" + 
					"	\"message\": \"成功\",\r\n" + 
					"	\"description\": \"该区域整体轻微拥堵。快速内环西线：南向北,从五家浜桥到五家桥拥堵；北向南,从五家桥到明秀桥行驶缓慢。青祁路：南向北,从五家浜桥到五家桥拥堵。青祁隧道：南向北,从青祁路到五家桥拥堵。\",\r\n" + 
					"	\"evaluation\": {\r\n" + 
					"		\"status\": 2,\r\n" + 
					"		\"status_desc\": \"轻微拥堵\"\r\n" + 
					"	},\r\n" + 
					"	\"road_traffic\": [{\r\n" + 
					"		\"road_name\": \"UNKNOW\"\r\n" + 
					"	},\r\n" + 
					"	{\r\n" + 
					"		\"road_name\": \"北华巷路\"\r\n" + 
					"	},\r\n" + 
					"	{\r\n" + 
					"		\"road_name\": \"北华路\"\r\n" + 
					"	},\r\n" + 
					"	{\r\n" + 
					"		\"road_name\": \"唐巷路\"\r\n" + 
					"	},\r\n" + 
					"	{\r\n" + 
					"		\"road_name\": \"建筑路\"\r\n" + 
					"	},\r\n" + 
					"	{\r\n" + 
					"		\"congestion_sections\": [{\r\n" + 
					"			\"congestion_distance\": 760,\r\n" + 
					"			\"speed\": 21.58,\r\n" + 
					"			\"status\": 3,\r\n" + 
					"			\"congestion_trend\": \"WORSE\",\r\n" + 
					"			\"section_desc\": \"南向北,从五家浜桥到五家桥\",\r\n" + 
					"			\"congection_trend\": null\r\n" + 
					"		},\r\n" + 
					"		{\r\n" + 
					"			\"congestion_distance\": 2990,\r\n" + 
					"			\"speed\": 33.04,\r\n" + 
					"			\"status\": 2,\r\n" + 
					"			\"congestion_trend\": \"BETTER\",\r\n" + 
					"			\"section_desc\": \"北向南,从五家桥到明秀桥\",\r\n" + 
					"			\"congection_trend\": null\r\n" + 
					"		}],\r\n" + 
					"		\"road_name\": \"快速内环西线\"\r\n" + 
					"	},\r\n" + 
					"	{\r\n" + 
					"		\"road_name\": \"溪南路\"\r\n" + 
					"	},\r\n" + 
					"	{\r\n" + 
					"		\"road_name\": \"稻香路\"\r\n" + 
					"	},\r\n" + 
					"	{\r\n" + 
					"		\"road_name\": \"蓝庭路\"\r\n" + 
					"	},\r\n" + 
					"	{\r\n" + 
					"		\"road_name\": \"邱巷路\"\r\n" + 
					"	},\r\n" + 
					"	{\r\n" + 
					"		\"congestion_sections\": [{\r\n" + 
					"			\"congestion_distance\": 760,\r\n" + 
					"			\"speed\": 21.58,\r\n" + 
					"			\"status\": 3,\r\n" + 
					"			\"congestion_trend\": \"WORSE\",\r\n" + 
					"			\"section_desc\": \"南向北,从五家浜桥到五家桥\",\r\n" + 
					"			\"congection_trend\": null\r\n" + 
					"		}],\r\n" + 
					"		\"road_name\": \"青祁路\"\r\n" + 
					"	},\r\n" + 
					"	{\r\n" + 
					"		\"congestion_sections\": [{\r\n" + 
					"			\"congestion_distance\": 510,\r\n" + 
					"			\"speed\": 24.14,\r\n" + 
					"			\"status\": 3,\r\n" + 
					"			\"congestion_trend\": \"WORSE\",\r\n" + 
					"			\"section_desc\": \"南向北,从青祁路到五家桥\",\r\n" + 
					"			\"congection_trend\": null\r\n" + 
					"		}],\r\n" + 
					"		\"road_name\": \"青祁隧道\"\r\n" + 
					"	},\r\n" + 
					"	{\r\n" + 
					"		\"road_name\": \"青莲路\"\r\n" + 
					"	}]\r\n" + 
					"}";
			System.out.println(baidudata);
			JSONObject json0 = JSONObject.fromObject(baidudata);
			String status = json0.getString("status");
			System.out.println(status);
			JSONArray road_traffic = json0.getJSONArray("road_traffic");
			Map<String,String> map = new HashMap<String,String>();
			for(int i=0;i<road_traffic.size();i++) {
				JSONObject json1 = (JSONObject) road_traffic.get(i);
				String road_name = json1.getString("road_name");
				System.out.println(road_name);
				
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
						System.out.println(congestion_distance+"-"+speed+"-"+status2+"-"+congestion_trend+"-"+section_desc);
						String direction = section_desc.substring(0, section_desc.indexOf(","));
						map.put(direction, status2);
					}
				}
			}
			
			String situation1 = map.get("南向北");
			String situation2 = map.get("东向西");
			String situation3 = map.get("西向东");
			
			System.out.println(situation1);
			System.out.println(situation2);
			System.out.println(situation3);
			
			if(situation1!=null && Integer.parseInt(situation1)>2) {
				
			}else if(situation1!=null && Integer.parseInt(situation1)==2) {
				
			}
			
			if(situation2!=null && Integer.parseInt(situation2)>2) {
				
			}else if(situation2!=null && Integer.parseInt(situation2)==2) {
				
			}
			
			if(situation3!=null && Integer.parseInt(situation3)>2) {
				
			}else if(situation3!=null && Integer.parseInt(situation3)==2) {
				
			}
			
			
//			String ledjson = sendLedJson("ssss");
//			String ledip = serial.getProperty("ledip");
//			String url = "http://" + ledip + "/api/program/Multi-Line.vsn";
//			ledPush(ledjson,url);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * led屏推送 @Title: ledPush @Description: TODO @param: @param data @param: @param
	 * url @return: void @throws
	 */
	public void ledPush(String data, String url) {
		try {
			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			requestFactory.setConnectTimeout(2000);// 设置超时
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
			System.out.println(url + "LED屏推送=============" + url);
			System.out.println("result:" + result);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(url + "推送LED屏推送报错=============");
		}
	}
	
	/**
	 * 创建信息led推送json数据 @Title: sendLedJson @Description: TODO @param: @param
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
	            // 打开和URL之间的连接
	            URLConnection connection = realUrl.openConnection();
	            // 设置通用的请求属性
	            connection.setRequestProperty("accept", "*/*");
	            connection.setRequestProperty("connection", "Keep-Alive");
	            connection.setRequestProperty("user-agent",
	                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
	            // 建立实际的连接
	            connection.connect();
	            // 获取所有响应头字段
//	            Map<String, List<String>> map = connection.getHeaderFields();
	            // 遍历所有的响应头字段
//	            for (String key : map.keySet()) {
//	                System.out.println(key + "--->" + map.get(key));
//	            }
	            // 定义 BufferedReader输入流来读取URL的响应
	            in = new BufferedReader(new InputStreamReader(
	                    connection.getInputStream()));
	            String line;
	            while ((line = in.readLine()) != null) {
	                result += line;
	            }
	        } catch (Exception e) {
	            System.out.println("发送GET请求出现异常！" + e);
	            e.printStackTrace();
	        }
	        // 使用finally块来关闭输入流
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
	 
	 
}
