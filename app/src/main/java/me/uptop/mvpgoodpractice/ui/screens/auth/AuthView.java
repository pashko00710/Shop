package me.uptop.mvpgoodpractice.ui.screens.auth;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import flow.Flow;
import me.uptop.mvpgoodpractice.R;
import me.uptop.mvpgoodpractice.di.DaggerService;
import me.uptop.mvpgoodpractice.mvp.views.IAuthView;
import me.uptop.mvpgoodpractice.utils.BounceInterpolator;
import me.uptop.mvpgoodpractice.utils.ViewHelper;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AuthView extends RelativeLayout implements IAuthView {
    private static final String TAG = "AuthView";

    public static final int LOGIN_STATE = 0;
    public static final int IDLE_STATE = 1;
    private final Transition mBounds;
    private final Transition mFade;
    private final Animator scaleAnimator;

    private int mEmailTextColor= Color.BLACK;
    private int mPasswordTextColor= Color.BLACK;
    private Animation mAnimation;

    private AuthScreen mAuthScreen;

    @BindView(R.id.relative_container)
    RelativeLayout mRelativeLayout;

    @BindView(R.id.auth_card)
    CardView mAuthCard;

    @BindView(R.id.app_name_txt)
    TextView mAppName;

    @BindView(R.id.login_email_et)
    EditText mEmailText;

    @BindView(R.id.login_password_et)
    EditText mPasswordText;

    @BindView(R.id.show_catalog_btn)
    Button mShowCatalogBtn;

    @BindView(R.id.login_btn)
    Button mLoginBtn;

    @BindView(R.id.vk_btn)
    ImageButton mVkBtn;

    @BindView(R.id.fb_btn)
    ImageButton mFbBtn;

    @BindView(R.id.tw_btn)
    ImageButton mTwBtn;

    @BindView(R.id.login_email_wrap)
    TextInputLayout mEmailWrap;

    @BindView(R.id.login_password_wrap)
    TextInputLayout mPasswordWrap;

    @BindView(R.id.enter_pb)
    ProgressBar mEnterProgressBar;

    @BindView(R.id.panel_wrapper)
    FrameLayout panelWrapper;


    @Inject
    AuthScreen.AuthPresenter mAuthPresenter;
    private int mDen;

    @BindView(R.id.logo_img)
    ImageView mLogo;
    private Subscription animObs;

    public AuthView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode()) {
            mAuthScreen = Flow.getKey(this);
            DaggerService.<AuthScreen.Component>getDaggerComponent(context).inject(this);
        }

        mBounds = new ChangeBounds();
        mFade = new Fade();
        mDen = ViewHelper.getDensity(context);
        scaleAnimator = AnimatorInflater.loadAnimator(getContext(), R.anim.logo_scale_anim);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(!isInEditMode()) {
            mAuthPresenter.takeView(this);
        }

        mEmailTextColor = mEmailText.getCurrentTextColor();
        mPasswordTextColor = mPasswordText.getCurrentTextColor();

        //adding fonts
        Typeface myFontCondensed = Typeface.createFromAsset(getContext().getAssets(), "fonts/PTBebasNeueBook.ttf");
        Typeface myFontBold = Typeface.createFromAsset(getContext().getAssets(), "fonts/PTBebasNeueRegular.ttf");
        mAppName.setTypeface(myFontBold);

        //adding animation to social buttons
        mAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
        mVkBtn.setAnimation(mAnimation);
        mFbBtn.setAnimation(mAnimation);
        mTwBtn.setAnimation(mAnimation);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(!isInEditMode()) {
            mAuthPresenter.dropView(this);
            animObs.unsubscribe();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        if(!isInEditMode()) {
            showViewFromState();
            startLogoAnim();
        }

        mEmailText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAuthPresenter.onEmailChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mPasswordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAuthPresenter.onPasswordChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //adding animation to panel
        LayoutTransition layoutTransition = new LayoutTransition();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
        }
        this.setLayoutTransition(layoutTransition);
    }

    private void showViewFromState() {
        if(!isIdle()) {
            showLoginState();
        } else {
            showIdleState();
        }
    }

    private void showLoginState() {
        CardView.LayoutParams cardParams = (CardView.LayoutParams) mAuthCard.getLayoutParams(); //получаем текущие параметры макета
        cardParams.height = LinearLayout.LayoutParams.MATCH_PARENT; //устанавливаем высоты на высоту родителя
        mAuthCard.setLayoutParams(cardParams); //устанавливаем параметры(requestLayout inside)
        mAuthCard.getChildAt(0).setVisibility(VISIBLE); //input wrapper делаем видимым
        mAuthCard.setCardElevation(4 * mDen); //устанавливаем подъем карточки авторизации
        mShowCatalogBtn.setClickable(false); //отключаю кликабельность кнопки входа в каталог
        mShowCatalogBtn.setVisibility(GONE); //скрываем кнопку
        mAuthScreen.setCustomState(LOGIN_STATE); //устанавливаем STATE LOGIN
//        mAuthCard.setVisibility(VISIBLE);
//        mShowCatalogBtn.setVisibility(GONE);
//        mLoginBtn.setEnabled(false);
    }

    private void showIdleState() {
        CardView.LayoutParams cardParams = (CardView.LayoutParams) mAuthCard.getLayoutParams();
        cardParams.height = 44*mDen;
        mAuthCard.setLayoutParams(cardParams);
        mAuthCard.getChildAt(0).setVisibility(INVISIBLE);
        mAuthCard.setCardElevation(0f);
        mShowCatalogBtn.setClickable(true);
        mShowCatalogBtn.setVisibility(VISIBLE);
        mAuthScreen.setCustomState(IDLE_STATE);
//        mAuthCard.setVisibility(GONE);
//        mShowCatalogBtn.setVisibility(VISIBLE);
//        mLoginBtn.setEnabled(true);
    }


    //for tests
    public boolean getEmailOk() {
        return mAuthPresenter.getEmailOk();
    }

    public boolean getPasswordOk() {
        return mAuthPresenter.getPasswordOk();
    }

    //


    //region ============================= Events =============================

    @OnClick(R.id.login_btn)
    void loginClick() {
        mAuthPresenter.clickOnLogin();
    }

    @OnClick(R.id.fb_btn)
    void fbClick() {
        mAuthPresenter.clickOnFb();
    }

    @OnClick(R.id.tw_btn)
    void twitterClick() {
        mAuthPresenter.clickOnTwitter();
    }
    @OnClick(R.id.vk_btn)
    void vkClick() {
        mAuthPresenter.clickOnVk();
    }
    @OnClick(R.id.show_catalog_btn)
    void showCatalogClick() {
        mAuthPresenter.clickOnShowCatalog();
    }

    //endregion

    //region ============================= IAuthView =============================

    @Override
    public void errorAuthForm() {
        Animation myAnim = AnimationUtils.loadAnimation(getContext(), R.anim.error);
        mAuthCard.startAnimation(myAnim);
    }

    @Override
    public void btnAnimation(View view, Button button) {
        final Animation myAnim = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);

        // Use bounce interpolator with amplitude 0.2 and frequency 20
        BounceInterpolator interpolator = new BounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);

        button.startAnimation(myAnim);
    }

    //Анимации для кнопок авторизации
    @Override
    public void btnAnimation(View view, ImageButton imageButton) {
        final Animation myAnim = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);

        // Use bounce interpolator with amplitude 0.2 and frequency 20
        BounceInterpolator interpolator = new BounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);

        imageButton.startAnimation(myAnim);
    }

    @Override
    public void showLoginBtn() {
        mLoginBtn.setVisibility(VISIBLE);
    }

    @Override
    public void hideLoginBtn() {
        mLoginBtn.setVisibility(GONE);
    }

    @Override
    public String getEmail() {
        return mEmailText.getText().toString();
    }

    @Override
    public String getPassword() {
        return mPasswordText.getText().toString();
    }

    @Override
    public void setNonAcceptableEmail() {
        mEmailText.setTextColor(Color.RED);
        mLoginBtn.setEnabled(false);
    }

    @Override
    public void setNonAcceptablePassword() {
        mPasswordText.setTextColor(Color.RED);
        mLoginBtn.setEnabled(false);
    }

    @Override
    public void setAcceptableEmail() {
        mEmailWrap.setErrorEnabled(false);
        mEmailText.setTextColor(mEmailTextColor);
        if (!mPasswordWrap.isErrorEnabled() && mPasswordText.getText()!=null && mPasswordText.getText().length()>0) mLoginBtn.setEnabled(true);
    }

    @Override
    public void setAcceptablePassword() {
        mPasswordWrap.setErrorEnabled(false);
        mPasswordText.setTextColor(mPasswordTextColor);
        if (!mEmailWrap.isErrorEnabled() && mEmailText.getText()!=null && mEmailText.getText().length()>0) mLoginBtn.setEnabled(true);
    }

    @Override
    public void setWrongEmailError() {
        //Внимательно здесь
        mEmailWrap.setErrorEnabled(true);
        mEmailWrap.setError(getResources().getString(R.string.email_input_error));
        mLoginBtn.setEnabled(false);
    }

    @Override
    public void setWrongPasswordError() {
        mPasswordWrap.setErrorEnabled(true);
        mPasswordWrap.setError(getResources().getString(R.string.password_input_error));
        mLoginBtn.setEnabled(false);
    }

    @Override
    public void removeWrongEmailError() {
        mEmailWrap.setErrorEnabled(false);
    }

    @Override
    public void removeWrongPasswordError() {
        mPasswordWrap.setErrorEnabled(false);
    }

    @Override
    public void showCatalogScreen() {
        mAuthPresenter.clickOnShowCatalog();
    }

    @Override
    public String getUserEmail() {
        return String.valueOf(mEmailText.getText());
    }

    @Override
    public String getUserPassword() {
        return String.valueOf(mPasswordText.getText());
    }

    @Override
    public boolean isIdle() {
        return mAuthScreen.getCustomState() == IDLE_STATE;
    }

//    @Override
//    public void setCustomState(int state) {
//        mAuthScreen.setCustomState(state);
//        showViewFromState();
//    }

    @Override
    public boolean viewOnBackPressed() {
        if(!isIdle()) {
//            setCustomState(IDLE_STATE);
            showIdleWithAnim();
            return true;
        } else {
            return false;
        }
    }


    //endregion

//    public void showError(Throwable e) {
//        if (BuildConfig.DEBUG) {
//            showMessage(e.getMessage());
//            e.printStackTrace();
//        } else {
//            showMessage(getString(R.string.unknown_error));
//            //todo:send error stacktrace to crashlytics
//        }
//
//    }

    @OnClick(R.id.logo_img)
    public void test() {
        // TODO: 13.02.17 start if invalid input variables login or password
//        invalidLoginAnimation();
//        showLoginWithAnim();
    }

    //region =========================== Animation =====================

    private void invalidLoginAnimation() {
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.anim.invalid_field_animator);
        set.setTarget(mAuthCard);
        set.start();
    }

    public void showLoginWithAnim() {
        TransitionSet set = new TransitionSet();
        set.addTransition(mBounds) //анимируем положение и границы (высоты элемента и подъем)
                 .addTransition(mFade) //анимируем прозрачность (видимость элементов)
                .setDuration(300)
                .setInterpolator(new FastOutSlowInInterpolator()) //устанавливаем временную функцию
                .setOrdering(TransitionSet.ORDERING_SEQUENTIAL); //устанавливаем последовательность проигрывания анимаций при переходе

        TransitionManager.beginDelayedTransition(panelWrapper, set);

        showLoginState();
    }

    public void showIdleWithAnim() {
        TransitionSet set = new TransitionSet();
        Transition fade = new Fade();
        fade.addTarget(mAuthCard.getChildAt(0)); // анимация исчезновения для инпутов

        set.addTransition(fade)
                .addTransition(mBounds) //анимируем положение и границы (высоты элемента и подъем)
                .addTransition(mFade) //анимируем прозрачность (видимость элементов)
                .setDuration(450)
                .setInterpolator(new FastOutSlowInInterpolator()) //устанавливаем временную функцию
                .setOrdering(TransitionSet.ORDERING_SEQUENTIAL); //устанавливаем последовательность проигрывания анимаций при переходе

        TransitionManager.beginDelayedTransition(panelWrapper, set);

        showIdleState();
    }


    private void startLogoAnim() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AnimatedVectorDrawable avd = (AnimatedVectorDrawable) mLogo.getDrawable();
            scaleAnimator.setTarget(mLogo );

            animObs = Observable.interval(6000, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> {
                        scaleAnimator.start();
                        avd.start();
                    });

//            avd.start();
        }
    }

    //endregion

}
