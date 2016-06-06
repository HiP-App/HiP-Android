/*
 * Copyright (c) 2016 History in Paderborn App - Universität Paderborn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.upb.hip.mobile.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import de.upb.hip.mobile.fragments.bottomsheetfragments.BottomSheetFragment;
import de.upb.hip.mobile.fragments.exhibitpagefragments.ExhibitPageFragment;
import de.upb.hip.mobile.fragments.exhibitpagefragments.ExhibitPageFragmentFactory;
import de.upb.hip.mobile.helpers.BottomSheetConfig;
import de.upb.hip.mobile.helpers.PixelDpConversion;
import de.upb.hip.mobile.models.Audio;
import de.upb.hip.mobile.models.exhibit.AppetizerPage;
import de.upb.hip.mobile.models.exhibit.Page;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

/** Coordinates subpages with details for an exhibit. */
public class ExhibitDetailsActivity extends AppCompatActivity {

    /** Stores the name of the current exhibit */
    private String exhibitName = "";

    /** Stores the pages for the current exhibit */
    private List<Page> exhibitPages = new LinkedList<>();

    /** Index of the page in the exhibitPages list that is currently displayed */
    private int currentPageIndex = 0;

    /** Menu for the toolbar, created in onCreateOptionsMenu */
    private Menu toolbarMenu;

    /** Indicates whether the audio action in the toolbar should be shown (true) or not (false) */
    private boolean showAudioAction = false;

    /** Indicates whether audio is currently played (true) or not (false) */
    private boolean isAudioPlaying = false;

    /** Indicates whether the audio toolbar is currently displayed (true) or not (false) */
    private boolean isAudioToolbarHidden = true;

    /** Extras contained in the Intent that started this activity */
    private Bundle extras = null;

    /** Stores the current action associated with the FAB */
    private BottomSheetConfig.FabAction fabAction;

    /** Reference to the BottomSheetFragment currently displayed */
    private BottomSheetFragment bottomSheetFragment = null;

    //logging
    public static final String TAG = "ExhibitDetailsActivity";

    // keys for saving/accessing the state
    public static final String INTENT_EXTRA_EXHIBIT_PAGES = "de.upb.hip.mobile.extra.exhibit_pages";
    public static final String INTENT_EXTRA_EXHIBIT_NAME = "de.upb.hip.mobile.extra.exhibit_name";

    private static final String KEY_EXHIBIT_NAME = "ExhibitDetailsActivity.exhibitName";
    private static final String KEY_EXHIBIT_PAGES = "ExhibitDetailsActivity.exhibitPages";
    private static final String KEY_CURRENT_PAGE_INDEX = "ExhibitDetailsActivity.currentPageIndex";
    private static final String KEY_AUDIO_PLAYING = "ExhibitDetailsActivity.isAudioPlaying";
    private static final String KEY_AUDIO_TOOLBAR_HIDDEN = "ExhibitDetailsActivity.isAudioToolbarHidden";
    private static final String KEY_EXTRAS = "ExhibitDetailsActivity.extras";

    // ui elements
    private FloatingActionButton fab;
    private View bottomSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout mRevealView;
    private ImageButton btnPlayPause;
    private ImageButton btnPreviousPage;
    private ImageButton btnNextPage;


    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putString(KEY_EXHIBIT_NAME, exhibitName);
        outState.putSerializable(KEY_EXHIBIT_PAGES, (Serializable) exhibitPages);
        outState.putInt(KEY_CURRENT_PAGE_INDEX, currentPageIndex);
        outState.putBoolean(KEY_AUDIO_PLAYING, isAudioPlaying);
        outState.putBoolean(KEY_AUDIO_TOOLBAR_HIDDEN, isAudioToolbarHidden);
        outState.putBundle(KEY_EXTRAS, extras);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibit_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            // activity re-creation because of device rotation, instant run, ...

            exhibitName = savedInstanceState.getString(KEY_EXHIBIT_NAME);
            exhibitPages = (List<Page>) savedInstanceState.getSerializable(KEY_EXHIBIT_PAGES);
            currentPageIndex = savedInstanceState.getInt(KEY_CURRENT_PAGE_INDEX, 0);
            isAudioPlaying = savedInstanceState.getBoolean(KEY_AUDIO_PLAYING, false);
            isAudioToolbarHidden = true;
            extras = savedInstanceState.getBundle(KEY_EXTRAS);

        } else {
            // activity creation because of intent
            Intent intent = getIntent();
            extras = intent.getExtras();
            exhibitName = intent.getStringExtra(INTENT_EXTRA_EXHIBIT_NAME);
            exhibitPages = (List<Page>) intent.getSerializableExtra(INTENT_EXTRA_EXHIBIT_PAGES);
        }

        if (exhibitPages == null)
            throw new NullPointerException("exhibitPages cannot be null!");

        // set up bottom sheet behavior
        bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // toggle between expand / collapse , inform fragment
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    if (fabAction == BottomSheetConfig.FabAction.COLLAPSE)
                        setFabAction(BottomSheetConfig.FabAction.EXPAND);

                    if (bottomSheetFragment != null)
                        bottomSheetFragment.onBottomSheetCollapse();

                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    if (fabAction == BottomSheetConfig.FabAction.EXPAND)
                        setFabAction(BottomSheetConfig.FabAction.COLLAPSE);

                    if (bottomSheetFragment != null)
                        bottomSheetFragment.onBottomSheetExpand();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // intentionally left blank
            }
        });

        // audio toolbar
        mRevealView = (LinearLayout) findViewById(R.id.reveal_items);
        mRevealView.setVisibility(View.INVISIBLE);

        // display audio toolbar on savedInstanceState:
        // if (! isAudioToolbarHidden) showAudioToolbar();
        // does not work because activity creation has not been completed?!
        // see also: http://stackoverflow.com/questions/7289827/how-to-start-animation-immediately-after-oncreate

        // set up play / pause toggle
        btnPlayPause = (ImageButton) findViewById(R.id.btnPlayPause);
        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAudioPlaying) {
                    pauseAudioPlayback();
                    isAudioPlaying = false;
                } else {
                    startAudioPlayback();
                    isAudioPlaying = true;
                    btnPlayPause.setImageResource(android.R.color.transparent);
                }
                updatePlayPauseButtonIcon();
            }
        });

        updatePlayPauseButtonIcon();

        // set up CC button
        final ImageButton btnCaptions = (ImageButton) findViewById(R.id.btnCaptions);
        btnCaptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCaptions();
            }
        });


        // set up previous / next button
        btnPreviousPage = (ImageButton) findViewById(R.id.buttonPrevious);
        btnPreviousPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPreviousExhibitPage();
            }
        });

        btnNextPage = (ImageButton) findViewById(R.id.buttonNext);
        btnNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayNextExhibitPage();
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (fabAction) {
                    case NEXT:
                        displayNextExhibitPage();
                        break;
                    case COLLAPSE:
                        setFabAction(BottomSheetConfig.FabAction.EXPAND);
                        break;
                    case EXPAND:
                        setFabAction(BottomSheetConfig.FabAction.COLLAPSE);
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported FAB action!");
                }
            }
        });

        displayCurrentExhibitPage();

    }

    /** Displays the current exhibit page */
    public void displayCurrentExhibitPage() {
        if (currentPageIndex >= exhibitPages.size())
            throw new IndexOutOfBoundsException("currentPageIndex >= exhibitPages.size() !");

        if (!isAudioToolbarHidden)
            hideAudioToolbar(); // TODO: generalize to audio playing

        Page page = exhibitPages.get(currentPageIndex);

        // set previous & next button
        if (currentPageIndex == 0)
            btnPreviousPage.setVisibility(View.GONE);
        else
            btnPreviousPage.setVisibility(View.VISIBLE);

        if (currentPageIndex >= exhibitPages.size() - 1 || page instanceof AppetizerPage)
            btnNextPage.setVisibility(View.GONE);
        else
            btnNextPage.setVisibility(View.VISIBLE);


        // set page fragment
        ExhibitPageFragment pageFragment =
                ExhibitPageFragmentFactory.getFragmentForExhibitPage(page, exhibitName);

        if (pageFragment == null)
            throw new NullPointerException("pageFragment is null!");

        pageFragment.setArguments(extras);

        // TODO: this seems to take some time. would it help to do this in a separate thread?
        // remove old fragment and display new fragment
        if (findViewById(R.id.content_fragment_container) != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_fragment_container, pageFragment);
            transaction.commit();
        }

        // configure bottom sheet
        BottomSheetConfig config = pageFragment.getBottomSheetConfig();

        if (config == null)
            throw new RuntimeException("BottomSheetConfig cannot be null!");

        if (config.isDisplayBottomSheet()) {

            bottomSheet.setVisibility(View.VISIBLE);

            // configure peek height and max height
            int peekHeightInPixels = (int) PixelDpConversion.convertDpToPixel(config.getPeekHeight());
            bottomSheetBehavior.setPeekHeight(peekHeightInPixels);

            int maxHeightInPixels = (int) PixelDpConversion.convertDpToPixel(config.getMaxHeight());
            ViewGroup.LayoutParams params = bottomSheet.getLayoutParams();
            params.height = maxHeightInPixels;
            bottomSheet.setLayoutParams(params);

            // set content
            bottomSheetFragment = config.getBottomSheetFragment();

            if (bottomSheetFragment == null)
                throw new NullPointerException("sheetFragment is null!");

            // FIXME: adding the new fragment somehow fails if the BottomSheet is expanded
            // TODO: this seems to take some time. would it help to do this in a separate thread?
            // remove old fragment and display new fragment
            if (findViewById(R.id.bottom_sheet_fragment_container) != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.bottom_sheet_fragment_container, bottomSheetFragment);
                transaction.commit();
            }

            // configure FAB (includes expanded/collapsed state)
            setFabAction(config.getFabAction());

        } else {    // config.displayBottomSheet == false
            bottomSheet.setVisibility(View.GONE);
        }

        // display audio action only if the page provides audio
        if (page.getAudio() == null)
            displayAudioAction(false);
        else {
            displayAudioAction(true);

            // TODO: continue with handling the audio
        }
    }

    /** Displays the next exhibit page */
    public void displayNextExhibitPage() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        currentPageIndex++;
        updateAudioFile();

        displayCurrentExhibitPage();
    }

    /** Displays the previous exhibit page (for currentPageIndex > 0) */
    public void displayPreviousExhibitPage() {
        currentPageIndex--;
        if (currentPageIndex < 0) {
            return;
        }
        updateAudioFile();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        displayCurrentExhibitPage();
    }

    private void updateAudioFile(){
        stopAudioPlayback();
        mMediaPlayerService.setAudioFile(exhibitPages.get(currentPageIndex).getAudio());
        updatePlayPauseButtonIcon();
    }

    /**
     * Sets the action of the FAB. Adjusts the appearance (visibility and icon) of the FAB
     * and the state of the bottom sheet accordingly.
     *
     * @param action FAB action to set.
     */
    public void setFabAction(BottomSheetConfig.FabAction action) {

        fabAction = action;
        fab.setVisibility(View.VISIBLE);

        switch (action) {
            case NONE:
                fab.setVisibility(View.GONE);
                break;
            case NEXT:
                fab.setImageResource(R.drawable.ic_arrow_forward_48dp);
                break;
            case COLLAPSE:
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                fab.setImageResource(R.drawable.ic_expand_more_white_48dp);
                break;
            case EXPAND:
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                fab.setImageResource(R.drawable.ic_expand_less_white_48dp);
                break;
            default:
                throw new IllegalArgumentException("Unsupported FAB action!");
        }
    }

    /**
     * Shows the audio toolbar.
     *
     * @return true if the toolbar has been revealed, false otherwise.
     */
    private boolean showAudioToolbar() {
        // check only if mRevealView != null. If isAudioToolbarHidden == true is also checked,
        // the toolbar cannot be displayed on savedInstanceState
        if (mRevealView != null) {
            int cx = (mRevealView.getLeft() + mRevealView.getRight());
            int cy = mRevealView.getTop();
            int radius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

                SupportAnimator animator =
                        ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.setDuration(800);

                mRevealView.setVisibility(View.VISIBLE);
                animator.start();

            } else {
                Animator anim = android.view.ViewAnimationUtils
                        .createCircularReveal(mRevealView, cx, cy, 0, radius);
                mRevealView.setVisibility(View.VISIBLE);
                anim.start();
            }

            isAudioToolbarHidden = false;
            return true;
        }

        return false;
    }

    /**
     * Hides the audio toolbar.
     *
     * @return true if the audio toolbar was hidden, false otherwise
     */
    private boolean hideAudioToolbar() {
        if (mRevealView != null) {
            int cx = (mRevealView.getLeft() + mRevealView.getRight());
            int cy = mRevealView.getTop();
            int radius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

                SupportAnimator animator =
                        ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.setDuration(800);

                SupportAnimator animator_reverse = animator.reverse();
                animator_reverse.addListener(new SupportAnimator.AnimatorListener() {
                    @Override
                    public void onAnimationStart() {

                    }

                    @Override
                    public void onAnimationEnd() {
                        mRevealView.setVisibility(View.INVISIBLE);
                        isAudioToolbarHidden = true;
                    }

                    @Override
                    public void onAnimationCancel() {

                    }

                    @Override
                    public void onAnimationRepeat() {

                    }
                });
                animator_reverse.start();

            } else {
                Animator anim = android.view.ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, radius, 0);
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mRevealView.setVisibility(View.INVISIBLE);
                        isAudioToolbarHidden = true;
                    }
                });
                anim.start();

            }

            return true;
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_exhibit_details_menu_main, menu);
        this.toolbarMenu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_audio).setVisible(showAudioAction);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_audio:
                if (isAudioToolbarHidden)
                    showAudioToolbar();
                else
                    hideAudioToolbar();
                return true;

            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Starts the playback of the audio associated with the page. */
    private void startAudioPlayback() {
        Toast.makeText(this, R.string.audio_playing_indicator, Toast.LENGTH_SHORT).show();
        // TODO: integrate media player
        try {
            if(!mMediaPlayerService.getAudioFileIsSet()) {
                mMediaPlayerService.setAudioFile(exhibitPages.get(currentPageIndex).getAudio());
            }
            mMediaPlayerService.startSound();
        } catch(IllegalStateException e){
            isPlaying = false;
        } catch(NullPointerException e){
            isPlaying = false;
        } catch(Exception e){
            isPlaying = false;
        }
    }

    /** Pauses the playback of the audio. */
    private void pauseAudioPlayback() {
        Toast.makeText(this, R.string.audio_pausing_indicator, Toast.LENGTH_SHORT).show();
        // TODO: integrate media player
        try {
            mMediaPlayerService.pauseSound();
        } catch(IllegalStateException e){
        } catch(NullPointerException e){
        } catch(Exception e){
        }
        isAudioPlaying = false;
    }

    private void stopAudioPlayback(){
        try{
            mMediaPlayerService.stopSound();
        }catch(IllegalStateException e){
        } catch(NullPointerException e){
        } catch(Exception e) {
        }
        isAudioPlaying = false;

    }

    /** Updates the icon displayed in the Play/Pause button */
    private void updatePlayPauseButtonIcon() {
        // remove old image first
        btnPlayPause.setImageResource(android.R.color.transparent);

        if (isAudioPlaying)
            btnPlayPause.setImageResource(R.drawable.ic_pause_black_36dp);
        else
            btnPlayPause.setImageResource(R.drawable.ic_play_arrow_black_36dp);

    }

    /**
     * Modifies the visibility of the audio action in the toolbar.
     *
     * @param visible True indicates the audio action should be visible.
     */
    private void displayAudioAction(boolean visible) {
        showAudioAction = visible;
        invalidateOptionsMenu();
    }

    /** Shows the caption for the text that is currently read out */
    private void showCaptions() {
        // TODO: adapt this to retrieved data
        String caption = this.exhibitPages.get(this.currentPageIndex).getAudio().getCaption();
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.audio_toolbar_cc)
                .setMessage(caption)
                .setNegativeButton(getString(R.string.close), null);
        AlertDialog dialog = builder.show();
    }
}
