package com.example.timo.hip;

import android.hardware.SensorManager;
import android.widget.Toast;

import com.wikitude.architect.ArchitectView;
import com.wikitude.architect.StartupConfiguration;

public class SampleCamActivity extends AbstractArchitectCamActivity {


    private final static String WIKITUDE_SDK_KEY = "Oy8rMEqP0zt8ww7V2N5X8OFIE7h8Kq/LKl3ovLB9NKJuGQsov4HOnnuZAHorw5XqhicwdfMxEQkyDcfZdNph/6qpCOikX1HCtuaw4AyacW2CxO0JxVpNjoe8X1dZRUCu+L3JzC945EtZt7R+u7dIMsNcvpNNyLPwniH2mAOhWHBTYWx0ZWRfX5Xi0rb2lYsZQ+j7lQvpd8H9lVCiNWm4b6Fp3mZetVu7vp9mw2+CpSWKtGgernYB+30POzrrRwVx0j89j7LNus0y/1A/OFF+Ln7iLcMloZZATMoLVLclW1AQxTboK0ZGRPSD1osvykAUY3dYftSsIKxytgRimNqdIQU5K2bwghlrYTX0W3Cgt3td59vFUCjXt+pMemFNx1t++p8oTWq8pDnqcDFeVHVkW1BL5GoRVw+vbL0gH0QihG2jtuvyd4642nFvV/pI+NHozpfF4NsOBQhiFgQ2TCh4Lo5jgZPY1htc6PePvAAjG7IeCZr0HuPR7zeMYj9CPCt9kkjNHE5GmPmcMpFumCY4aKJA6wS/yiBPDdV1mIzEX6RdoVInHlvE5ghiqOk7eIEeebCq0zFuXm6Gz0UMqIl9YOfZE1w4vgCAh9CT5FiID908+6DwrMeNPVYndEu+A15ECciNVm/sArShs/XApBiRUxsLJfh9Vxg6n5+1cAqzSYc=";
    /**
     * last time the calibration toast was shown, this avoids too many toast shown when compass needs calibration
     */
    private long lastCalibrationToastShownTimeMillis = System.currentTimeMillis();

    @Override
    public String getARchitectWorldPath() {
        return getIntent().getExtras().getString(
                ArExampleActivity.EXTRAS_KEY_ACTIVITY_ARCHITECT_WORLD_URL);
    }

    @Override
    public String getActivityTitle() {
        return (getIntent().getExtras() != null && getIntent().getExtras().get(
                ArExampleActivity.EXTRAS_KEY_ACTIVITY_TITLE_STRING) != null) ? getIntent()
                .getExtras().getString(ArExampleActivity.EXTRAS_KEY_ACTIVITY_TITLE_STRING)
                : "Test-World";
    }

    @Override
    public int getContentViewId() {
        return R.layout.sample_cam;
    }

    @Override
    public int getArchitectViewId() {
        return R.id.architectView;
    }

    @Override
    public String getWikitudeSDKLicenseKey() {
        return WIKITUDE_SDK_KEY;
    }

    @Override
    public ArchitectView.SensorAccuracyChangeListener getSensorAccuracyListener() {
        return new ArchitectView.SensorAccuracyChangeListener() {
            @Override
            public void onCompassAccuracyChanged( int accuracy ) {
				/* UNRELIABLE = 0, LOW = 1, MEDIUM = 2, HIGH = 3 */
                if ( accuracy < SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM && SampleCamActivity.this != null && !SampleCamActivity.this.isFinishing() && System.currentTimeMillis() - SampleCamActivity.this.lastCalibrationToastShownTimeMillis > 5 * 1000) {
                    Toast.makeText(SampleCamActivity.this, R.string.compass_accuracy_low, Toast.LENGTH_LONG).show();
                    SampleCamActivity.this.lastCalibrationToastShownTimeMillis = System.currentTimeMillis();
                }
            }
        };
    }


    @Override
    protected boolean hasIR() {
        return getIntent().getExtras().getBoolean(
                ArExampleActivity.EXTRAS_KEY_ACTIVITY_IR);
    }

    @Override
    protected StartupConfiguration.CameraPosition getCameraPosition() {
        return StartupConfiguration.CameraPosition.DEFAULT;
    }
}