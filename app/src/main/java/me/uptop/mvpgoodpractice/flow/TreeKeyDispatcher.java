package me.uptop.mvpgoodpractice.flow;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import java.util.Collections;
import java.util.Map;

import flow.Direction;
import flow.Dispatcher;
import flow.KeyChanger;
import flow.State;
import flow.Traversal;
import flow.TraversalCallback;
import flow.TreeKey;
import me.uptop.mvpgoodpractice.R;
import me.uptop.mvpgoodpractice.mortar.ScreenScoper;
import me.uptop.mvpgoodpractice.utils.ViewHelper;

public class TreeKeyDispatcher implements Dispatcher, KeyChanger {

    public static final String TAG = "TreeKeyDispatcher";

    Activity mActivity;
    private Object inKey;

    @Nullable
    private Object outKey;

    private FrameLayout mRootFrame;

    public TreeKeyDispatcher(Activity activity) {
        mActivity = activity;
    }


    @Override
    public void dispatch(Traversal traversal, TraversalCallback callback) {
        Map<Object, Context> contexts;
        State inState = traversal.getState(traversal.destination.top());
        inKey = inState.getKey();
        State outState = traversal.origin == null ? null : traversal.getState(traversal.origin.top());
        outKey = outState == null ? null : outState.getKey();

        mRootFrame = (FrameLayout) mActivity.findViewById(R.id.root_frame);

        if(inKey.equals(outKey)) {
            callback.onTraversalCompleted();
            return;
        }

        if(inKey instanceof TreeKey) {
            // TODO: 26.11.16 implement treekey case
        }

        // TODO: 26.11.16 mortar context for screen
        Context flowContext = traversal.createContext(inKey, mActivity);
        Context mortarContext = ScreenScoper.getScreenScope((AbstractScreen) inKey).createContext(flowContext);
        contexts = Collections.singletonMap(inKey, mortarContext);

        changeKey(outState, inState, traversal.direction, contexts, callback);
    }

    @Override
    public void changeKey(@Nullable State outgoingState, State incomingState, Direction direction, Map<Object,
            Context> incomingContexts, TraversalCallback callback) {

        Context context = incomingContexts.get(inKey);
        //save prev View

        if (outgoingState != null) {
            outgoingState.save(mRootFrame.getChildAt(0));
        }

        //create new View
        Screen screen;
        screen = inKey.getClass().getAnnotation(Screen.class);

        if (screen == null) {
            throw new IllegalStateException("@Screen annotation is missing 2: screen " + ((AbstractScreen) inKey).getScopeName());
        } else {

            int layout = screen.value();

            LayoutInflater inflater = LayoutInflater.from(context);
            View newView = inflater.inflate(layout, mRootFrame, false);
            View oldView = mRootFrame.getChildAt(0);

            //restore State to new View
            incomingState.restore(newView);

            // T000: 21.11.2016 unregister screen scope

            //delete old View
            Log.e(TAG, "changeKey: "+mRootFrame);
            if(outKey != null && !(inKey instanceof TreeKey)) {
                ((AbstractScreen)outKey).unregisterScope();
            }

//            if (mRootFrame.getChildAt(0) != null) {
//                mRootFrame.removeView(mRootFrame.getChildAt(0));
//            }


            mRootFrame.addView(newView);

            ViewHelper.waitForMeasure(newView, new ViewHelper.OnMeasureCallback() { //дожидаемся когда станут известны размеры View которая придет
                @Override
                public void onMeasure(View view, int width, int height) {
                    runAnimation(mRootFrame, oldView, newView, direction, new TraversalCallback() { //запускаем анимацию
                        @Override
                        public void onTraversalCompleted() {
                            //анимация окончена делаем что то, что необходимо.. например удаляем область видимости Mortar
                            callback.onTraversalCompleted();
                        }
                    });
                }
            });

//            callback.onTraversalCompleted();
        }
    }

    private void runAnimation(FrameLayout container, View from, View to, Direction direction, TraversalCallback traversalCallback) {
       Animator animator = createAnimation(from, to, direction); //создаем анимацию
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(from != null) {
                    container.removeView(from); //удаляем view из контейнера по окончанию анимации
                }
                traversalCallback.onTraversalCompleted(); //вызываем колбэк успешного окончания перехода, в котором выше происходит очистка области видимости
            }
        });

        animator.setInterpolator(new FastOutLinearInInterpolator()); //устанавливаем временную функцию
        animator.start();
    }

    @NonNull
    private Animator createAnimation(@Nullable View from, View to, Direction direction) {
        int fromTranslation, toTranslation;
        boolean backward = direction == Direction.BACKWARD;

        AnimatorSet set = new AnimatorSet();

        if (from != null) {
            fromTranslation = backward ? from.getWidth() : -from.getWidth(); //если движемся по истории назад то смещение по Х положительное, вперед - отрицательное
            final ObjectAnimator  outAnimation = ObjectAnimator.ofFloat(from, "translationX", fromTranslation); //анимируем смещение по Х
            set.play(outAnimation);
        }
        toTranslation = backward ? -to.getWidth() : to.getWidth();
        final ObjectAnimator  inAnimation = ObjectAnimator.ofFloat(to, "translationX", toTranslation, 0);
        set.play(inAnimation);

        return set;
    }
}
