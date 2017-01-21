package kade_c.taskforge.utils;


import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

import kade_c.taskforge.R;

/**
 * Handles the tutorial
 */
public class Tutorial {
    private View view;
    private Activity activity;
    private Toolbar toolbar;

    public Tutorial(View view, Activity activity, Toolbar toolbar) {
        this.view = view;
        this.activity = activity;
        this.toolbar = toolbar;
        launch();
    }

    /**
     * Launches the tutorial sequence, explaining the app to the user.
     * Event creation -> List manipulation -> List creation
     * -> List deletion -> List navigation
     */
    private void launch() {
        new TapTargetSequence(activity)
                .targets(
                        TapTarget.forView(view.findViewById(R.id.fab), activity.getResources().getString(R.string.tutorial_fab_title),
                                activity.getResources().getString(R.string.tutorial_fab_description))
                                .outerCircleColor(R.color.red)          // Specify a color for the outer circle
                                .targetCircleColor(R.color.grey_100)    // Specify a color for the target circle
                                .titleTextSize(30)                      // Specify the size (in sp) of the title text
                                .titleTextColor(R.color.grey_100)       // Specify the color of the title text
                                .textColor(R.color.grey_100)            // Specify a color for both the title and description text
                                .textTypeface(Typeface.SANS_SERIF)      // Specify a typeface for the text
                                .drawShadow(true)                       // Whether to draw a drop shadow or not
                                .cancelable(true)                       // Whether tapping outside the outer circle dismisses the view
                                .tintTarget(true)                       // Whether to tint the target view's color
                                .transparentTarget(false)               // Specify whether the target is transparent (displays the content underneath)
                                .targetRadius(60),                      // Specify the target radius (in dp)
                        TapTarget.forView(view.findViewById(R.id.list), activity.getResources().getString(R.string.tutorial_list_title), activity.getResources().getString(R.string.tutorial_list_description))
                                .outerCircleColor(R.color.red)
                                .targetCircleColor(R.color.grey_100)
                                .titleTextSize(30)
                                .titleTextColor(R.color.grey_100)
                                .textColor(R.color.grey_100)
                                .textTypeface(Typeface.SANS_SERIF)
                                .drawShadow(true)
                                .cancelable(true)
                                .tintTarget(true)
                                .transparentTarget(false)
                                .targetRadius(60),
                        TapTarget.forToolbarMenuItem(toolbar, R.id.action_add_list, activity.getResources().getString(R.string.tutorial_list_creation_title), activity.getResources().getString(R.string.tutorial_list_creation_description))
                                .outerCircleColor(R.color.red)
                                .targetCircleColor(R.color.grey_100)
                                .titleTextSize(30)
                                .titleTextColor(R.color.grey_100)
                                .textColor(R.color.grey_100)
                                .textTypeface(Typeface.SANS_SERIF)
                                .drawShadow(true)
                                .cancelable(true)
                                .tintTarget(true)
                                .transparentTarget(false)
                                .targetRadius(60),
                        TapTarget.forToolbarMenuItem(toolbar, R.id.action_delete_list, activity.getResources().getString(R.string.tutorial_list_deletion_title), activity.getResources().getString(R.string.tutorial_list_deletion_description))
                                .outerCircleColor(R.color.red)
                                .targetCircleColor(R.color.grey_100)
                                .titleTextSize(30)
                                .titleTextColor(R.color.grey_100)
                                .textColor(R.color.grey_100)
                                .textTypeface(Typeface.SANS_SERIF)
                                .drawShadow(true)
                                .cancelable(true)
                                .tintTarget(true)
                                .transparentTarget(false)
                                .targetRadius(60),
                        TapTarget.forToolbarNavigationIcon(toolbar, activity.getResources().getString(R.string.tutorial_list_navigation_title), activity.getResources().getString(R.string.tutorial_list_navigation_description))
                                .textColor(R.color.grey_100))
                .start();
    }
}
