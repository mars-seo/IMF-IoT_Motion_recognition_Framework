package Motion.server;

import Motion.MotionCheck;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UltrasonicResource extends CoapResource {

	private static final Logger logger = LoggerFactory.getLogger(UltrasonicResource.class);
	
	
	private static UltrasonicResource instance;
	public static double ultraDistance;
	
	public UltrasonicResource() throws Exception {
		super("ultrasonic");
		instance = this;
		
	}

	public static UltrasonicResource getInstance() {

		return instance;
	}

	@Override
	public void handleGET(CoapExchange exchange) {

	}

	@Override
	public void handlePOST(CoapExchange exchange) {
		//{"sensor":"ultrasonic","distance":"100"} 이런식으로
	//{"sensor":"status"} 이런식으로 요청

		try{
		String requestJson = exchange.getRequestText();
		JSONObject requestJsonObject = new JSONObject(requestJson);
		String sensor = requestJsonObject.getString("sensor");
		if (sensor.equals("ultrasonic")) {
			double distance= Double.parseDouble(requestJsonObject.getString("distance"));
			ultraDistance=distance;
			MotionCheck.ultrasonicAddData(distance);
			
		}else if (sensor.equals("status")) {

		}
		JSONObject responseJsonObject = new JSONObject();
		responseJsonObject.put("result", "success");
		responseJsonObject.put("distance", String.valueOf(ultraDistance));
		String responseJson = responseJsonObject.toString();
		exchange.respond(responseJson);
		}catch(Exception e){
		JSONObject responseJsonObject = new JSONObject();
		responseJsonObject.put("result", "fail");
		String responseJson = responseJsonObject.toString();
		exchange.respond(responseJson);
		}

	}

}