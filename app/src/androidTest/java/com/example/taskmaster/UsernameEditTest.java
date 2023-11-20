//package com.example.taskmaster;
//
//
//import static androidx.test.espresso.Espresso.onView;
//import static androidx.test.espresso.action.ViewActions.click;
//import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
//import static androidx.test.espresso.action.ViewActions.replaceText;
//import static androidx.test.espresso.assertion.ViewAssertions.matches;
//import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
//import static androidx.test.espresso.matcher.ViewMatchers.withId;
//import static androidx.test.espresso.matcher.ViewMatchers.withParent;
//import static androidx.test.espresso.matcher.ViewMatchers.withText;
//import static org.hamcrest.Matchers.allOf;
//
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewParent;
//
//import androidx.test.espresso.ViewInteraction;
//import androidx.test.ext.junit.rules.ActivityScenarioRule;
//import androidx.test.ext.junit.runners.AndroidJUnit4;
//import androidx.test.filters.LargeTest;
//
//import org.hamcrest.Description;
//import org.hamcrest.Matcher;
//import org.hamcrest.TypeSafeMatcher;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//@LargeTest
//@RunWith(AndroidJUnit4.class)
//public class UsernameEditTest {
//
//    @Rule
//    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
//            new ActivityScenarioRule<>(MainActivity.class);
//
//    @Test
//    public void usernameEditTest() {
//        ViewInteraction appCompatImageButton = onView(
//                allOf(withId(R.id.settingsImageButton),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(android.R.id.content),
//                                        0),
//                                6),
//                        isDisplayed()));
//        appCompatImageButton.perform(click());
//
//        ViewInteraction appCompatEditText = onView(
//                allOf(withId(R.id.UsernameEditText), withText("No username Found"),
//                        childAtPosition(
//                                allOf(withId(R.id.SettingsActivity),
//                                        childAtPosition(
//                                                withId(android.R.id.content),
//                                                0)),
//                                3),
//                        isDisplayed()));
//        appCompatEditText.perform(replaceText("Balqees"));
//
//        ViewInteraction appCompatEditText2 = onView(
//                allOf(withId(R.id.UsernameEditText), withText("Balqees"),
//                        childAtPosition(
//                                allOf(withId(R.id.SettingsActivity),
//                                        childAtPosition(
//                                                withId(android.R.id.content),
//                                                0)),
//                                3),
//                        isDisplayed()));
//        appCompatEditText2.perform(closeSoftKeyboard());
//
//        ViewInteraction materialButton = onView(
//                allOf(withId(R.id.saveUserSettingsButton), withText("Save"),
//                        childAtPosition(
//                                allOf(withId(R.id.SettingsActivity),
//                                        childAtPosition(
//                                                withId(android.R.id.content),
//                                                0)),
//                                4),
//                        isDisplayed()));
//        materialButton.perform(click());
//
//        ViewInteraction appCompatEditText3 = onView(
//                allOf(withId(R.id.UsernameEditText), withText("Balqees"),
//                        childAtPosition(
//                                allOf(withId(R.id.SettingsActivity),
//                                        childAtPosition(
//                                                withId(android.R.id.content),
//                                                0)),
//                                3),
//                        isDisplayed()));
//        appCompatEditText3.perform(click());
//
//        ViewInteraction editText = onView(
//                allOf(withId(R.id.UsernameEditText), withText("Balqees"),
//                        withParent(allOf(withId(R.id.SettingsActivity),
//                                withParent(withId(android.R.id.content)))),
//                        isDisplayed()));
//        editText.check(matches(withText("Balqees")));
//
//        ViewInteraction editText2 = onView(
//                allOf(withId(R.id.UsernameEditText), withText("Balqees"),
//                        withParent(allOf(withId(R.id.SettingsActivity),
//                                withParent(withId(android.R.id.content)))),
//                        isDisplayed()));
//        editText2.check(matches(withText("Balqees")));
//    }
//
//    private static Matcher<View> childAtPosition(
//            final Matcher<View> parentMatcher, final int position) {
//
//        return new TypeSafeMatcher<View>() {
//            @Override
//            public void describeTo(Description description) {
//                description.appendText("Child at position " + position + " in parent ");
//                parentMatcher.describeTo(description);
//            }
//
//            @Override
//            public boolean matchesSafely(View view) {
//                ViewParent parent = view.getParent();
//                return parent instanceof ViewGroup && parentMatcher.matches(parent)
//                        && view.equals(((ViewGroup) parent).getChildAt(position));
//            }
//        };
//    }
//}
