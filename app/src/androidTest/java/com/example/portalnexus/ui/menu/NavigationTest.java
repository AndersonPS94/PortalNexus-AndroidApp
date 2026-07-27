package com.example.portalnexus.ui.menu;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.portalnexus.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class NavigationTest {

    @Rule
    public ActivityScenarioRule<MenuActivity> activityRule = new ActivityScenarioRule<>(MenuActivity.class);

    @Test
    public void openCharactersListTest() {
        onView(withId(R.id.cardCharacters)).perform(click());
        onView(withId(R.id.rvCharacters)).check(matches(isDisplayed()));
    }

    @Test
    public void openEmployeesListTest() {
        onView(withId(R.id.cardEmployees)).perform(click());
        onView(withId(R.id.rvEmployees)).check(matches(isDisplayed()));
    }
}
