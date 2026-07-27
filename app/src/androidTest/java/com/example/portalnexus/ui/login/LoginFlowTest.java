package com.example.portalnexus.ui.login;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.portalnexus.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class LoginFlowTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void loginVisibilityTest() {
        onView(withId(R.id.txtLoginTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.editEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.editPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.btnLogin)).check(matches(isDisplayed()));
    }

    @Test
    public void loginInvalidEmailTest() {
        onView(withId(R.id.editEmail)).perform(typeText("invalid-email"), closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());
        
        // O toast ou mensagem de erro deve aparecer. 
        // Como o LoginViewModel emite erro, verificamos se o login NÃO mudou de tela.
        onView(withId(R.id.txtLoginTitle)).check(matches(isDisplayed()));
    }

    @Test
    public void loginSuccessFlowTest() {
        onView(withId(R.id.editEmail)).perform(typeText("admin@teste.com"), closeSoftKeyboard());
        onView(withId(R.id.editPassword)).perform(typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());

        // Aguarda transição para o Menu (simulado pelo delay no ViewModel)
        // Em testes instrumentados reais com delay, usaríamos IdlingResource.
        // Para este desafio, vamos apenas garantir que a intenção de navegação esteja lá.
        try { Thread.sleep(2000); } catch (InterruptedException e) { }
        
        onView(withId(R.id.txtMenuTitle)).check(matches(isDisplayed()));
    }
}
