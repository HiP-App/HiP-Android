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

package de.upb.hip.mobile.helpers;


import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides functionality to parse a {@link TextView} for footnote markdown and replace the
 * markdown with clickable numbers which show the footnote text on click using a SnackBar.
 *
 * Footnotes are defined using {@code <fn>...</fn>}, where "..." is replaced by the footnote text
 */
public class ClickableFootnotes {

    /** Helper class to represent footnote */
    static class Footnote {
        /** The number of the footnote */
        private int number = -1;

        /** The footnote text */
        private String text = "default";

        /** The position of the footnote in the text */
        private int startIndex = -1;

        /** Sets the attributes */
        public Footnote(int number, String text, int startIndex) {
            this.number = number;
            this.text = text;
            this.startIndex = startIndex;
        }
    }

    /** Text that indicates the start of a footnote. */
    public static final String FOOTNOTE_START = "<fn>";

    /** Text that indicates the end of a footnote. */
    public static final String FOOTNOTE_END = "</fn>";


    /**
     * Parses the text of the specified TextView and replaces footnote markup (format see above)
     * with clickable footnotes which display the footnote text as a Toast message on click.
     *
     * @param tv TextView where Footnote markdown should be converted to clickable footnotes.
     */
    public static void createFootnotes(TextView tv) {
        if (tv == null || tv.getText().length() == 0)
            return;

        String text = tv.getText().toString();

        Pattern pattern = Pattern.compile(FOOTNOTE_START + ".+?" + FOOTNOTE_END);
        Matcher matcher = pattern.matcher(text);

        int number = 0; // footnote numbering

        List<Footnote> footnotes = new LinkedList<>();

        // collect footnotes and replace them with a consecutive number
        while (matcher.find()) {
            number++;

            String match = matcher.group();

            // get footnote text
            String footnote = match;
            footnote = footnote.replace(FOOTNOTE_START, "");
            footnote = footnote.replace(FOOTNOTE_END, "");

            // store footnote
            footnotes.add(new Footnote(number, footnote, matcher.start()));

            // replace footnote markup with consecutive number
            text = text.replace(match, "[" + number + "]"); // TODO: only 1st is clickable
            matcher = pattern.matcher(text);  // working with new text to get correct start indices
        }

        SpannableString str = new SpannableString(text);
        addClickableFootnotes(str, footnotes);

        tv.setText(str);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        tv.setHighlightColor(Color.TRANSPARENT);
    }


    /**
     * Adds the list of clickable {@link Footnote} instances to the specified
     * {@link SpannableString} in form of {@link ClickableSpan}s.
     *
     * @param text      Where the footnotes are added
     * @param footnotes footnotes to add
     */
    private static void addClickableFootnotes(SpannableString text, List<Footnote> footnotes) {

        for (Footnote fn : footnotes) {
            if (fn == null)
                continue;

            text.setSpan(
                    convertFootnoteToClickableSpan(fn),
                    fn.startIndex,
                    fn.startIndex + Integer.toString(fn.number).length() + 2, // +2 for brackets
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

    }


    /**
     * Converts a {@link Footnote} instance into a {@link ClickableSpan} instance that can be added
     * to a {@link SpannableString}. On click, the footnote text is displayed in a Toast message.
     * TODO: change to SnackBar
     *
     * @param fn Footnote to convert.
     *
     * @return The ClickableSpan instance corresponding to the footnote (null, if the specified
     * Footnote instance is null))
     */
    private static ClickableSpan convertFootnoteToClickableSpan(final Footnote fn) {

        if (fn == null)
            return null;

        return new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Toast.makeText(textView.getContext(), fn.text, Toast.LENGTH_LONG).show();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

    }

}
