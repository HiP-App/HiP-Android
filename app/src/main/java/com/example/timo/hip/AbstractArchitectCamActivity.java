package com.example.timo.hip;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.wikitude.architect.ArchitectView;
import com.wikitude.architect.ArchitectView.SensorAccuracyChangeListener;
import com.wikitude.architect.StartupConfiguration;
import com.wikitude.architect.StartupConfiguration.CameraPosition;

import java.io.IOException;

/**
 * Abstract activity which handles live-cycle events.
 * Feel free to extend from this activity when setting up your own AR-Activity 
 *
 */
public abstract class AbstractArchitectCamActivity extends Activity implements ArchitectViewHolderInterface{

	/**
	 * holds the Wikitude SDK AR-View, this is where camera, markers, compass, 3D models etc. are rendered
	 */
	protected ArchitectView architectView;
	
	/**
	 * sensor accuracy listener in case you want to display calibration hints
	 */
	protected SensorAccuracyChangeListener sensorAccuracyListener;

	/** Called when the activity is first created. */
	@SuppressLint("NewApi")
	@Override
	public void onCreate( final Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		/* pressing volume up/down should cause music volume changes */
		this.setVolumeControlStream( AudioManager.STREAM_MUSIC );

		/* set samples content view */
		this.setContentView( this.getContentViewId() );
		
		this.setTitle( this.getActivityTitle() );
		
		/*  
		 *	this enables remote debugging of a WebView on Android 4.4+ when debugging = true in AndroidManifest.xml
		 *	If you get a compile time error here, ensure to have SDK 19+ used in your ADT/Eclipse.
		 *	You may even delete this block in case you don't need remote debugging or don't have an Android 4.4+ device in place.
		 *	Details: https://developers.google.com/chrome-developer-tools/docs/remote-debugging
		 */
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
		    if ( 0 != ( getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE ) ) {
		        WebView.setWebContentsDebuggingEnabled(true);
		    }
		}

		/* set AR-view for life-cycle notifications etc. */
		this.architectView = (ArchitectView)this.findViewById( this.getArchitectViewId()  );

		/* pass SDK key if you have one, this one is only valid for this package identifier and must not be used somewhere else */
		final StartupConfiguration config = new StartupConfiguration( this.getWikitudeSDKLicenseKey(), this.getFeatures(), this.getCameraPosition() );

		try {
			/* first mandatory life-cycle notification */
			this.architectView.onCreate( config );
		} catch (RuntimeException rex) {
			this.architectView = null;
			Toast.makeText(getApplicationContext(), "can't create Architect View", Toast.LENGTH_SHORT).show();
			Log.e(this.getClass().getName(), "Exception in ArchitectView.onCreate()", rex);
		}

		// set accuracy listener if implemented, you may e.g. show calibration prompt for compass using this listener
		this.sensorAccuracyListener = this.getSensorAccuracyListener();
	}

	protected abstract CameraPosition getCameraPosition();

	private int getFeatures() {
		return (hasIR() ? StartupConfiguration.Features.Tracking2D : 0);
	}

	protected abstract boolean hasIR();

	@Override
	protected void onPostCreate( final Bundle savedInstanceState ) {
		super.onPostCreate(savedInstanceState);
		
		if ( this.architectView != null ) {
			
			// call mandatory live-cycle method of architectView
			this.architectView.onPostCreate();
			
			try {
				// load content via url in architectView, ensure '<script src="architect://architect.js"></script>' is part of this HTML file, have a look at wikitude.com's developer section for API references
				this.architectView.load( this.getARchitectWorldPath() );
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		// call mandatory live-cycle method of architectView
		if ( this.architectView != null ) {
			this.architectView.onResume();
			
			// register accuracy listener in architectView, if set
			if (this.sensorAccuracyListener!=null) {
				this.architectView.registerSensorAccuracyChangeListener(this.sensorAccuracyListener);
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		// call mandatory live-cycle method of architectView
		if ( this.architectView != null ) {
			this.architectView.onPause();
			
			// unregister accuracy listener in architectView, if set
			if ( this.sensorAccuracyListener != null ) {
				this.architectView.unregisterSensorAccuracyChangeListener( this.sensorAccuracyListener );
			}
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		// call mandatory live-cycle method of architectView
		if ( this.architectView != null ) {
			this.architectView.onDestroy();
		}
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		if ( this.architectView != null ) {
			this.architectView.onLowMemory();
		}
	}

	/**
	 * title shown in activity
	 * @return
	 */
	public abstract String getActivityTitle();
	
	/**
	 * path to the architect-file (AR-Experience HTML) to launch
	 * @return
	 */
	@Override
	public abstract String getARchitectWorldPath();
	
	/**
	 * @return layout id of your layout.xml that holds an ARchitect View, e.g. R.layout.camview
	 */
	@Override
	public abstract int getContentViewId();
	
	/**
	 * @return Wikitude SDK license key, checkout www.wikitude.com for details
	 */
	@Override
	public abstract String getWikitudeSDKLicenseKey();
	
	/**
	 * @return layout-id of architectView, e.g. R.id.architectView
	 */
	@Override
	public abstract int getArchitectViewId();
	
	/**
	 * @return Implementation of Sensor-Accuracy-Listener. That way you can e.g. show prompt to calibrate compass
	 */
	@Override
	public abstract SensorAccuracyChangeListener getSensorAccuracyListener();
}