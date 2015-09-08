package com.example.timo.hip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.wikitude.architect.ArchitectView;
import com.wikitude.architect.StartupConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArExampleActivity extends Activity {

    public static final String EXTRAS_KEY_ACTIVITY_TITLE_STRING = "activityTitle";
    public static final String EXTRAS_KEY_ACTIVITY_ARCHITECT_WORLD_URL = "activityArchitectWorldUrl";
    public static final String EXTRAS_KEY_ACTIVITY_IR = "activityIr";

    public final String className = "com.example.timo.hip.SampleCamActivity";

    private Map<Integer, List<SampleMeta>> samples;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        setContentView(R.layout.ar_example);

        // ensure to clean cache when it is no longer required
        ArExampleActivity.deleteDirectoryContent ( ArchitectView.getCacheDirectoryAbsoluteFilePath(this) );

        this.getSamples();
    }

    protected final void getSamples() {
        boolean includeIR = (ArchitectView.getSupportedFeaturesForDevice(getApplicationContext()) & StartupConfiguration.Features.Tracking2D) != 0;
        samples = new HashMap<Integer, List<SampleMeta>>();
        if(includeIR){
            String assetOne = "Image$Recognition_Image$With$Labels";
            String assetTwo = "Image$Recognition_Labels$With$OnClick";
            String assetThree = "Image$Recognition_Image$Overlay";
            SampleMeta sampleMetaOne = new SampleMeta(assetOne, true);
            SampleMeta sampleMetaTwo = new SampleMeta(assetTwo, true);
            SampleMeta sampleMetaThree = new SampleMeta(assetThree, true);
            samples.put(0, new ArrayList<SampleMeta>());
            samples.get(0).add(sampleMetaOne);
            samples.put(1, new ArrayList<SampleMeta>());
            samples.get(1).add(sampleMetaTwo);
            samples.put(2, new ArrayList<SampleMeta>());
            samples.get(2).add(sampleMetaThree);
        }
    }

    /**
     * deletes content of given directory
     * @param path
     */
    private static void deleteDirectoryContent(final String path) {
        try {
            final File dir = new File (path);
            if (dir.exists() && dir.isDirectory()) {
                final String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {
                    new File(dir, children[i]).delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startArExample(View view) {

        final List<SampleMeta> activitiesToLaunch = samples.get(0);

        final SampleMeta meta = activitiesToLaunch.get(0);

        String newActivityTitle = ( meta.sampleName.replace("$", " "));
        String newActivityUrl = meta.path;
        boolean newActivitieIr = meta.hasIr;

        try {

            final Intent intent = new Intent(this, Class.forName(className));
            intent.putExtra(EXTRAS_KEY_ACTIVITY_TITLE_STRING, newActivityTitle);
            intent.putExtra(EXTRAS_KEY_ACTIVITY_ARCHITECT_WORLD_URL, "samples"
                    + File.separator + newActivityUrl
                    + File.separator + "index.html");
            intent.putExtra(EXTRAS_KEY_ACTIVITY_IR, newActivitieIr);

			/* launch activity */
            this.startActivity(intent);

        } catch (Exception e) {
			/*
			 * may never occur, as long as all SampleActivities exist and are
			 * listed in manifest
			 */
            Toast.makeText(this, className + "\nnot defined/accessible",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void startArExampleWithOnClick(View view) {

        final List<SampleMeta> activitiesToLaunch = samples.get(1);

        final SampleMeta meta = activitiesToLaunch.get(0);

        String newActivityTitle = ( meta.sampleName.replace("$", " "));
        String newActivityUrl = meta.path;
        boolean newActivitieIr = meta.hasIr;

        try {

            final Intent intent = new Intent(this, Class.forName(className));
            intent.putExtra(EXTRAS_KEY_ACTIVITY_TITLE_STRING, newActivityTitle);
            intent.putExtra(EXTRAS_KEY_ACTIVITY_ARCHITECT_WORLD_URL, "samples"
                    + File.separator + newActivityUrl
                    + File.separator + "index.html");
            intent.putExtra(EXTRAS_KEY_ACTIVITY_IR, newActivitieIr);

			/* launch activity */
            this.startActivity(intent);

        } catch (Exception e) {
			/*
			 * may never occur, as long as all SampleActivities exist and are
			 * listed in manifest
			 */
            Toast.makeText(this, className + "\nnot defined/accessible",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void startArExampleWithOverlay(View view) {

        final List<SampleMeta> activitiesToLaunch = samples.get(2);

        final SampleMeta meta = activitiesToLaunch.get(0);

        String newActivityTitle = ( meta.sampleName.replace("$", " "));
        String newActivityUrl = meta.path;
        boolean newActivitieIr = meta.hasIr;

        try {

            final Intent intent = new Intent(this, Class.forName(className));
            intent.putExtra(EXTRAS_KEY_ACTIVITY_TITLE_STRING, newActivityTitle);
            intent.putExtra(EXTRAS_KEY_ACTIVITY_ARCHITECT_WORLD_URL, "samples"
                    + File.separator + newActivityUrl
                    + File.separator + "index.html");
            intent.putExtra(EXTRAS_KEY_ACTIVITY_IR, newActivitieIr);

			/* launch activity */
            this.startActivity(intent);

        } catch (Exception e) {
			/*
			 * may never occur, as long as all SampleActivities exist and are
			 * listed in manifest
			 */
            Toast.makeText(this, className + "\nnot defined/accessible",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private static class SampleMeta {

        final String path, categoryName, sampleName;
        final boolean hasIr;

        public SampleMeta(String path, boolean hasIr) {
            super();
            this.path = path;
            this.hasIr = hasIr;
            if (path.indexOf("_")<0) {
                throw new IllegalArgumentException("all files in asset folder must be folders and define category and subcategory as predefined (with underscore)");
            }
            this.categoryName = path.substring(0, path.indexOf("_"));
            path = path.substring(path.indexOf("_")+1);
            this.sampleName = path;
        }

        @Override
        public String toString() {
            return "categoryName:" + this.categoryName + ", sampleName: " + this.sampleName + ", path: " + this.path;
        }
    }
}
