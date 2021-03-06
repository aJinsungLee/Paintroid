/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.espresso.util;

import android.app.Activity;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.view.View;
import android.widget.LinearLayout;

import com.getkeepsafe.taptargetview.TapTarget;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.WelcomeActivity;
import org.catrobat.paintroid.intro.TapTargetBase;
import org.catrobat.paintroid.intro.TapTargetBottomBar;
import org.catrobat.paintroid.intro.TapTargetTopBar;
import org.catrobat.paintroid.intro.helper.WelcomeActivityHelper;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.ToolType;
import org.hamcrest.Matchers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.catrobat.paintroid.intro.TapTargetBase.getToolTypeFromView;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getDescendantView;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.shouldStartSequence;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.waitMillis;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.isNotVisible;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;


public class IntroUtils {
    public final static int animationDelay = 500;
    public final static String TT_CLASS_NAME = "com.getkeepsafe.taptargetview.TapTargetView";

    public static int numberOfVisibleChildren(LinearLayout layout) {
        int count = 0;
        for (int i = 0; i < layout.getChildCount(); i++) {
            if (layout.getChildAt(i).getVisibility() == View.VISIBLE) {
                count++;
            }
        }
        return count;
    }

    public static List<ToolType> getToolTypesFromView(LinearLayout layout) {
        List<ToolType> toolTypeList = new ArrayList<>();

        for (int i = 0; i < layout.getChildCount(); i++) {
            View view = layout.getChildAt(i);
            ToolType toolType = getToolTypeFromView(view);
            if (toolType == null) {
                continue;
            }
            toolTypeList.add(toolType);
        }

        return toolTypeList;
    }

    public static void introClickToolAndCheckView(ToolType toolType, IntroSlide introSlide) {
        ViewInteraction tapTargetViewInteraction;
        ViewInteraction buttonViewInteraction;
        ViewInteraction fadeViewInteraction;

        buttonViewInteraction = onView(allOf(withId(toolType.getToolButtonID()),
                isDescendantOfA(ViewMatchers.withId(introSlide.getToolBarResourceId()))));

        waitMillis(animationDelay);

        fadeViewInteraction = onView(ViewMatchers.withId(introSlide.getFadeViewResourceId()))
                .check(matches(isDisplayed()));

        if (introSlide == IntroSlide.Tools)
            buttonViewInteraction.perform(scrollTo());

        buttonViewInteraction
                .check(matches(isClickable()))
                .perform(click());
        tapTargetViewInteraction = onView(allOf(withClassName(Matchers.is(TT_CLASS_NAME))));
        tapTargetViewInteraction.check(matches(isDisplayed()));
        fadeViewInteraction.check(matches(not(isDisplayed())));
        tapTargetViewInteraction.perform(click()).check(isNotVisible());
        fadeViewInteraction.check(matches(isDisplayed()));
    }

    public static int getExpectedRadiusForTapTarget(TapTargetBase tapTargetTopBar) throws NoSuchFieldException, IllegalAccessException {
        Context context = InstrumentationRegistry.getTargetContext();
        int radiusOffset = (int) PrivateAccess.getMemberValue(TapTargetBase.class, tapTargetTopBar, "RADIUS_OFFSET");
        float dimension = context.getResources().getDimension(R.dimen.top_bar_height);
        return WelcomeActivityHelper.calculateTapTargetRadius(dimension, context, radiusOffset);
    }

    public static LinearLayout getBottomBarFromToolSlide(Activity activity) {
        return (LinearLayout) getDescendantView(R.id.intro_tools_bottom_bar, R.id.tools_layout, activity);
    }

    public static LinearLayout getTopBarFromPossibilitiesSlide(Activity activity) {
        return (LinearLayout) getDescendantView(R.id.intro_possibilites_topbar, R.id.layout_top_bar, activity);
    }

    public static TapTargetBottomBar getTapTargetBottomBar(Activity activity) {
        LinearLayout targetItemView = getBottomBarFromToolSlide(activity);
        final View fadeView = activity.findViewById(R.id.intro_tools_textview);
        TapTargetBottomBar tapTargetBottomBar = new TapTargetBottomBar(targetItemView, fadeView, (WelcomeActivity) activity, R.id.intro_tools_bottom_bar);
        return tapTargetBottomBar;
    }

    public static TapTargetTopBar getTapTargetTopBar(Activity activity) throws NoSuchFieldException, IllegalAccessException {
        final View fadeView = activity.findViewById(R.id.intro_possibilities_textview);
        LinearLayout targetItemView = getTopBarFromPossibilitiesSlide(activity);
        TapTargetTopBar tapTargetTopBar = new TapTargetTopBar(targetItemView, fadeView, (WelcomeActivity) activity, R.id.intro_possibilities_bottom_bar);
        shouldStartSequence(tapTargetTopBar, false);
        return tapTargetTopBar;
    }

    public static HashMap<ToolType, TapTarget> getMapFromTapTarget(TapTargetBase tapTarget) throws NoSuchFieldException, IllegalAccessException {
        Object o = PrivateAccess.getMemberValue(TapTargetBase.class, tapTarget, "tapTargetMap");
        assertThat("tapTarget member is not a HashMap", o, instanceOf(HashMap.class));

        return (HashMap<ToolType, TapTarget>) o;
    }

    public static int getExpectedRadius(TapTargetBase tapTargetTopBar) throws NoSuchFieldException, IllegalAccessException {
        Context context = InstrumentationRegistry.getContext();
        int radiusOffset = (int) PrivateAccess.getMemberValue(TapTargetBase.class, tapTargetTopBar, "RADIUS_OFFSET");
        float dimension = context.getResources().getDimension(R.dimen.top_bar_height);
        return WelcomeActivityHelper.calculateTapTargetRadius(dimension, context, radiusOffset);
    }

    public static int numberOfVisibleChildern(LinearLayout layout) {
        int count = 0;
        for(int i = 0; i < layout.getChildCount(); i++) {
            if(layout.getChildAt(i).getVisibility() == View.VISIBLE) {
                count++;
            }
        }

        return count;
    }

    public enum IntroSlide {
        Tools(R.id.intro_tools_textview, R.id.intro_tools_bottom_bar),
        Possibilities(R.id.intro_possibilities_textview, R.id.intro_possibilites_topbar);

        int fadeViewResourceId;
        int toolBarResourceId;

        IntroSlide(int fadeViewResourceId, int toolBarResourceId) {
            this.fadeViewResourceId = fadeViewResourceId;
            this.toolBarResourceId = toolBarResourceId;
        }

        public int getFadeViewResourceId() {
            return fadeViewResourceId;
        }

        public int getToolBarResourceId() {
            return toolBarResourceId;
        }

    }
}
