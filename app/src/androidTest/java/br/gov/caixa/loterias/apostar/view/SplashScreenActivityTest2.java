package br.gov.caixa.loterias.apostar.view;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.gov.caixa.loterias.apostar.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SplashScreenActivityTest2 {

    @Rule
    public ActivityTestRule<SplashScreenActivity> mActivityTestRule = new ActivityTestRule<>( SplashScreenActivity.class );

    @Test
    public void splashScreenActivityTest2() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep( 2000 );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton = onView(
                allOf( withId( android.R.id.button1 ), withText( "Sim" ),
                        childAtPosition(
                                childAtPosition(
                                        withId( R.id.buttonPanel ),
                                        0 ),
                                3 ) ) );
        appCompatButton.perform( scrollTo(), click() );

        ViewInteraction appCompatButton2 = onView(
                allOf( withId( R.id.buttonPularIntroducao ), withText( "Pular introdução" ),
                        childAtPosition(
                                withParent( withId( R.id.vpPager ) ),
                                4 ),
                        isDisplayed() ) );
        appCompatButton2.perform( click() );

        ViewInteraction viewPager = onView(
                allOf( withId( R.id.vpPager ),
                        childAtPosition(
                                childAtPosition(
                                        withClassName( is( "android.support.constraint.ConstraintLayout" ) ),
                                        0 ),
                                0 ),
                        isDisplayed() ) );
        viewPager.perform( swipeLeft() );

        ViewInteraction appCompatButton3 = onView(
                allOf( withId( R.id.buttonAposteAgora ), withText( "Aposte agora" ),
                        childAtPosition(
                                childAtPosition(
                                        withClassName( is( "android.widget.FrameLayout" ) ),
                                        0 ),
                                1 ),
                        isDisplayed() ) );
        appCompatButton3.perform( click() );

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText( "Child at position " + position + " in parent " );
                parentMatcher.describeTo( description );
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches( parent )
                        && view.equals( ((ViewGroup) parent).getChildAt( position ) );
            }
        };
    }
}
