package com.raspoid;

import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.i2c.I2CFactory;
import com.raspoid.Tools;
import com.raspoid.MPU6050;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.json.JSONObject;

/**
 * Example of use of an MPU6050.
 *
 * @see MPU6050
 *
 * @author Julien Louette &amp; Ga&euml;l Wittorski
 * @version 1.0
 */
public class MPU6050Example {

	public static String ipAdress = "192.168.3.133";
	public static CoapClient coapClient;
	public static CoapResponse coapResponse;
	public static JSONObject jsonObject;
	public static String json;
	public static TouchSwitch ts;

	/**
	 * Private constructor to hide the implicit public one.
	 */
	private MPU6050Example() {
		coapClient = new CoapClient();
		
	}

	/**
	 * Command-line interface.
	 *
	 * @param args unused here.
	 */
	public static void main(String[] args) throws I2CFactory.UnsupportedBusNumberException {
		MPU6050 mpu6050 = new MPU6050();
		ts = new TouchSwitch(RaspiPin.GPIO_01);
		
		mpu6050.startUpdatingThread();

		while (true) {
			Tools.log("-----------------------------------------------------");
//
//			// Accelerometer
//			Tools.log("Accelerometer:");
//			double[] accelAngles = mpu6050.getAccelAngles();
//			Tools.log("\t" + MPU6050.xyzValuesToString(MPU6050.angleToString(accelAngles[0]),
//							MPU6050.angleToString(accelAngles[1]), MPU6050.angleToString(accelAngles[2])));
//
//			double[] accelAccelerations = mpu6050.getAccelAccelerations();
//			Tools.log("\tAccelerations: " + MPU6050.xyzValuesToString(MPU6050.accelToString(accelAccelerations[0]),
//							MPU6050.accelToString(accelAccelerations[1]), MPU6050.accelToString(accelAccelerations[2])));
//
//			// Gyroscope
//			Tools.log("Gyroscope:");
//			double[] gyroAngles = mpu6050.getGyroAngles();
//			Tools.log("\t" + MPU6050.xyzValuesToString(MPU6050.angleToString(gyroAngles[0]),
//							MPU6050.angleToString(gyroAngles[1]), MPU6050.angleToString(gyroAngles[2])));
//
//			double[] gyroAngularSpeeds = mpu6050.getGyroAngularSpeeds();
//			Tools.log("\t" + MPU6050.xyzValuesToString(MPU6050.angularSpeedToString(gyroAngularSpeeds[0]),
//							MPU6050.angularSpeedToString(gyroAngularSpeeds[1]), MPU6050.angularSpeedToString(gyroAngularSpeeds[2])));
//
//			// Filtered angles
			Tools.log("Filtered angles:");
			double[] filteredAngles = mpu6050.getFilteredAngles();
			if(filteredAngles[2]<0){
				filteredAngles[2]=180+filteredAngles[2];
			}else if(filteredAngles[2]==0){
				filteredAngles[2]=180;
			}else{
				filteredAngles[2]=180+filteredAngles[2];
			}
			
			Tools.log("\t" + MPU6050.xyzValuesToString(MPU6050.angleToString(filteredAngles[0]),
							MPU6050.angleToString(filteredAngles[1]), MPU6050.angleToString(filteredAngles[2])));
			mouseMove(filteredAngles[0], filteredAngles[1],filteredAngles[2]);
			
			button();

			Tools.sleepMilliseconds(100);
		}

	}

	public static void mouseMove(double x, double y,double z) {
		double roll = x;
		double pitch = y;
		double yaw = z;
//		
//		if(yaw<0){
//			yaw=360+yaw;
//		}
//        
		jsonObject = new JSONObject();
		jsonObject.put("sensor", "gyroscope");
		jsonObject.put("yawAngle", String.valueOf(yaw));
		jsonObject.put("rollAngle", String.valueOf(roll));
		jsonObject.put("pitchAngle", String.valueOf(pitch));
		json = jsonObject.toString();

		coapClient = new CoapClient();
		coapClient.setURI("coap://" + ipAdress + "/gyroscope");
		coapResponse = coapClient.post(json, MediaTypeRegistry.APPLICATION_JSON);
		coapClient.shutdown();
	}
	
	public static void button(){
		
		jsonObject = new JSONObject();
		jsonObject.put("sensor", "touch");
		jsonObject.put("status", ts.getStatus());
		json = jsonObject.toString();

		coapClient = new CoapClient();
		coapClient.setURI("coap://" + ipAdress + "/button");
		coapResponse = coapClient.post(json, MediaTypeRegistry.APPLICATION_JSON);
		coapClient.shutdown();
	}

}
