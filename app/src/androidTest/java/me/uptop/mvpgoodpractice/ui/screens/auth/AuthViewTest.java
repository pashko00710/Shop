package me.uptop.mvpgoodpractice.ui.screens.auth;

import android.support.design.widget.TextInputLayout;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import me.uptop.mvpgoodpractice.R;
import me.uptop.mvpgoodpractice.ui.activities.SplashActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressKey;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasFocus;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.view.KeyEvent.KEYCODE_ENTER;
import static org.hamcrest.Matchers.not;

public class AuthViewTest {
    private static final String TEST_EMAIL_VALID = "anyEmail@yandex.ru";
    private static final String TEST_EMAIL_INVALID = "anyEmail@y";
    private static final String TEST_HINT_VALID = "Введите email";
    private static final String TEST_HINT_INVALID = "Введите email адрес в правильном формате: email@example.com";
    @Rule
    public ActivityTestRule<SplashActivity> mActivityTestRule = new ActivityTestRule<SplashActivity>(SplashActivity.class);
    private ViewInteraction mLoginBtn;
    private ViewInteraction mShowCatalogBtn;
    private ViewInteraction mLoginInput;
    private ViewInteraction mLoginWrapper ;
    private ViewInteraction mPasswordInput;

    @Before
    public void setup() {
//        mLoginBtn = onView(Matchers.allOf(withId(R.id.login_btn), withText("Войти")));
        mLoginBtn = onView(withId(R.id.login_btn));
        mShowCatalogBtn = onView(withId(R.id.show_catalog_btn));
        mLoginInput = onView(withId(R.id.login_email_et));
        mLoginWrapper = onView(withId(R.id.login_email_wrap));
        mPasswordInput = onView(withId(R.id.login_password_et));
    }

    @Test
    public void click_on_login_HIDE_SHOW_CATALOG_BTN() throws Exception {
        mLoginBtn.perform(click());
        mShowCatalogBtn.check(matches(not(isDisplayed())));
    }

    @Test
    public void login_state_back_pressed_LOGIN_CARD_NOT_DISPLAY() throws Exception {
        mLoginBtn.perform(click());
        pressBack();
        mShowCatalogBtn.check(matches(isDisplayed()));
    }

    @Test
    public void input_valid_mail_password_VALID_HINT() throws Exception {
        mLoginBtn.perform(click());
        mLoginInput.perform(typeTextIntoFocusedView(TEST_EMAIL_VALID));
        mLoginInput.check(matches(withText(TEST_EMAIL_VALID)));

        mLoginWrapper.check(matches(withHintTextInputLayout(TEST_HINT_VALID)));
        mLoginInput.perform(pressKey(KEYCODE_ENTER));
        mPasswordInput.check(matches(hasFocus()));
        mPasswordInput.perform(typeTextIntoFocusedView("anyPassword"));
        mPasswordInput.check(matches(withText("anyPassword")));
    }

    @Test
    public void input_valid_mail_password_INVALID_HINT() throws Exception {
        mLoginBtn.perform(click());
        mLoginInput.perform(typeTextIntoFocusedView(TEST_EMAIL_INVALID));
        mLoginInput.check(matches(withText(TEST_EMAIL_INVALID)));

        mLoginInput.perform(pressKey(KEYCODE_ENTER));
        mPasswordInput.check(matches(hasFocus()));
        mPasswordInput.perform(typeTextIntoFocusedView("anyPassword"));
        mPasswordInput.check(matches(withText("anyPassword")));
    }

    public static Matcher<View> withHintTextInputLayout(final String expectedHint) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                if(!(item instanceof TextInputLayout)) {
                    return false;
                }
                String hint = ((TextInputLayout) item).getHint().toString();
                return expectedHint.equals(hint);
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }
}