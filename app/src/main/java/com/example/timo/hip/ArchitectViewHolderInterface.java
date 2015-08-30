package com.example.timo.hip;

import com.wikitude.architect.ArchitectView.SensorAccuracyChangeListener;

public interface ArchitectViewHolderInterface {

	/**
	 * path to the architect-file (AR-Experience HTML) to launch
	 * @return
	 */
	String getARchitectWorldPath();
	
	/**
	 * @return layout id of your layout.xml that holds an ARchitect View, e.g. R.layout.camview
	 */
	int getContentViewId();
	
	/**
	 * @return Wikitude SDK license key, checkout www.wikitude.com for details
	 */
	String getWikitudeSDKLicenseKey();
	
	/**
	 * @return layout-id of architectView, e.g. R.id.architectView
	 */
	int getArchitectViewId();

	/**
	 * @return Implementation of Sensor-Accuracy-Listener. That way you can e.g. show prompt to calibrate compass
	 */
	SensorAccuracyChangeListener getSensorAccuracyListener();
}
