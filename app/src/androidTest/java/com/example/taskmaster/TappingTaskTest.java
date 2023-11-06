package com.example.taskmaster;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class TappingTaskTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void tappingTaskTest() {
        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.settingsImageButton),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                6),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.UsernameEditText), withText("No username Found"),
                        childAtPosition(
                                allOf(withId(R.id.SettingsActivity),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                3),
                        isDisplayed()));
        appCompatEditText.perform(longClick());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.UsernameEditText), withText("No username Found"),
                        childAtPosition(
                                allOf(withId(R.id.SettingsActivity),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                3),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("Balqees"));

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.UsernameEditText), withText("Balqees"),
                        childAtPosition(
                                allOf(withId(R.id.SettingsActivity),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                3),
                        isDisplayed()));
        appCompatEditText3.perform(closeSoftKeyboard());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.saveUserSettingsButton), withText("Save"),
                        childAtPosition(
                                allOf(withId(R.id.SettingsActivity),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                4),
                        isDisplayed()));
        materialButton.perform(click());

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withId(R.id.backButton), withContentDescription("Back"),
                        childAtPosition(
                                allOf(withId(R.id.SettingsActivity),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.AddTaskButton), withText("Add Task"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        materialButton2.perform(click());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.taskTileEditText), withText("My Task"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatEditText4.perform(longClick());

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.taskTileEditText), withText("My Task"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatEditText5.perform(replaceText("Exercise"));

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.taskTileEditText), withText("Exercise"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatEditText6.perform(closeSoftKeyboard());

        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.taskDescriptionEditText), withText("Do Something"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                5),
                        isDisplayed()));
        appCompatEditText7.perform(longClick());

        ViewInteraction appCompatEditText8 = onView(
                allOf(withId(R.id.taskDescriptionEditText), withText("Do Something"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                5),
                        isDisplayed()));
        appCompatEditText8.perform(replaceText("push ups "));

        ViewInteraction appCompatEditText9 = onView(
                allOf(withId(R.id.taskDescriptionEditText), withText("push ups "),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                5),
                        isDisplayed()));
        appCompatEditText9.perform(closeSoftKeyboard());

//        pressBack();
        ViewInteraction materialButton3 = onView(
                allOf(withId(R.id.saveTaskButton), withText("Save"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                11),
                        isDisplayed()));
        materialButton3.perform(click());

        ViewInteraction appCompatImageButton3 = onView(
                allOf(withId(R.id.backButton), withContentDescription("Back"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                9),
                        isDisplayed()));
        appCompatImageButton3.perform(click());

        ViewInteraction materialButton4 = onView(
                allOf(withId(R.id.AllTaskButton), withText("All Tasks"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                5),
                        isDisplayed()));
        materialButton4.perform(click());

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.TaskListRecyclerView),
                        childAtPosition(
                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                4)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        onView(withId(R.id.TaskTitleTextView)).check(matches(withText("Exercise")));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
