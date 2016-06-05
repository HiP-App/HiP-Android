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

package de.upb.hip.mobile.fragments.exhibitpagefragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.upb.hip.mobile.activities.R;
import de.upb.hip.mobile.fragments.bottomsheetfragments.SimpleBottomSheetFragment;
import de.upb.hip.mobile.helpers.BottomSheetConfig;
import de.upb.hip.mobile.helpers.DrawView;
import de.upb.hip.mobile.models.exhibit.ImagePage;
import de.upb.hip.mobile.models.exhibit.Page;

/**
 * A fragment for displaying an image with selectable areas
 */
public class ImagePageExhibitFragment extends ExhibitPageFragment {

    private ImagePage page;

    private DrawView drawView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_exhibitpage_image, container, false);

        drawView = (DrawView) v.findViewById(R.id.fragment_exhibitpage_image_imageview);
        drawView.setImageDrawable(this.page.getImage().getDawableImage(getContext()));
        if (page.getAreas() != null) {
            drawView.getRectangles().addAll(page.getAreas());
        } else {
            //There are no areas to highlight, don't show button
            Button button = (Button) v.findViewById(R.id.fragment_exhibitpage_image_button);
            button.setVisibility(View.INVISIBLE);
        }
        drawView.setOriginalImageDimensions(page.getImage().getImageDimensions());

        initListeners(v);

        return v;
    }

    @Override
    public BottomSheetConfig getBottomSheetConfig() {
        SimpleBottomSheetFragment bottomSheetFragment = new SimpleBottomSheetFragment();
        bottomSheetFragment.setTitle(page.getImage().getTitle());
        bottomSheetFragment.setDescription(page.getImage().getDescription());
        return new BottomSheetConfig.Builder().displayBottomSheet(true)
                .bottomSheetFragment(bottomSheetFragment).getBottomSheetConfig();
    }

    @Override
    public void setPage(Page page) {
        this.page = (ImagePage) page;
    }

    private double[] getImageScalingFactor() {
        int[] originalImageDimensions = page.getImage().getImageDimensions();
        double widthScalingFactor = ((double) originalImageDimensions[0]) / ((double) drawView.getWidth());
        double heightScalingFactor = ((double) originalImageDimensions[1]) / ((double) drawView.getHeight());
        return new double[]{widthScalingFactor, heightScalingFactor};
    }

    private void initListeners(View v) {
        drawView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!(event.getAction() == MotionEvent.ACTION_DOWN) || page.getAreas() == null) {
                    //Only do something when the user actually pressed down and there are actually
                    //areas that the user can press
                    return false;
                }
                int x = (int) (((double) event.getX()) * getImageScalingFactor()[0]);
                int y = (int) (((double) event.getY()) * getImageScalingFactor()[1]);
                for (int i = 0; i < page.getAreas().size(); i++) {
                    ImagePage.Rectangle rect = page.getAreas().get(i);
                    if (x >= rect.getX1() && x <= rect.getX2() && y >= rect.getY1() && y <= rect.getY2()) {
                        //We hit an rectangle, display further information about it
                        new AlertDialog.Builder(getContext())
                                .setTitle(getString(R.string.information))
                                .setMessage(page.getTexts().get(i))
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Do nothing, dialogue will just close
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .show();
                        return true;
                    }
                }
                return false;
            }
        });

        Button button = (Button) v.findViewById(R.id.fragment_exhibitpage_image_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setDrawOnImage(!drawView.isDrawOnImage());
                drawView.invalidate();

            }
        });
    }
}
