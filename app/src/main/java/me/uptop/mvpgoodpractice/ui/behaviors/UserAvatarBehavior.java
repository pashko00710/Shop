package me.uptop.mvpgoodpractice.ui.behaviors;


import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import java.util.List;

public class UserAvatarBehavior extends CoordinatorLayout.Behavior<ImageView> {

    private static final ThreadLocal<Matrix> sMatrix = new ThreadLocal<>();
    private static final ThreadLocal<RectF> sRectF = new ThreadLocal<>();

    private WindowInsetsCompat mLastInsets;

    private Rect mTmpRect;

    public UserAvatarBehavior() {
        super();
    }

    public UserAvatarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent,
                                   ImageView child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, ImageView child,
                                          View dependency) {
        if (dependency instanceof AppBarLayout) {
            // If we're depending on an AppBarLayout we will show/hide it automatically
            // if the FAB is anchored to the AppBarLayout
            updateFabVisibility(parent, (AppBarLayout) dependency, child);
        }
        return false;
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, ImageView child,
                                 int layoutDirection) {
        // First, let's make sure that the visibility of the View is consistent
        final List<View> dependencies = parent.getDependencies(child);
        for (int i = 0, count = dependencies.size(); i < count; i++) {
            final View dependency = dependencies.get(i);
            if (dependency instanceof AppBarLayout
                    && updateFabVisibility(parent, (AppBarLayout) dependency, child)) {
                break;
            }
        }
        // Now let the CoordinatorLayout lay out the View
        parent.onLayoutChild(child, layoutDirection);

        return true;
    }

    private boolean updateFabVisibility(CoordinatorLayout parent,
                                        AppBarLayout appBarLayout, ImageView child) {
        final CoordinatorLayout.LayoutParams lp =
                (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        if (lp.getAnchorId() != appBarLayout.getId()) {
            // The anchor ID doesn't match the dependency, so we won't automatically
            // show/hide the View
            return false;
        }
        if (mTmpRect == null) {
            mTmpRect = new Rect();
        }

        // First, let's get the visible rect of the dependency
        final Rect rect = mTmpRect;

        mTmpRect.set(0, 0, appBarLayout.getWidth(), appBarLayout.getHeight());
        offsetDescendantRect(parent, appBarLayout, mTmpRect);

        if (rect.bottom <= getMinimumHeightForVisibleOverlappingContent(appBarLayout)) {
            // If the anchor's bottom is below the seam, we'll animate our View out
            child.setVisibility(View.GONE);
        } else {
            // Else, we'll animate our View back in
            child.setVisibility(View.VISIBLE);
        }
        return true;
    }

    private void offsetDescendantRect(ViewGroup group, View child, Rect rect) {
        Matrix m = sMatrix.get();
        if (m == null) {
            m = new Matrix();
            sMatrix.set(m);
        } else {
            m.reset();
        }

        offsetDescendantMatrix(group, child, m);

        RectF rectF = sRectF.get();
        if (rectF == null) {
            rectF = new RectF();
            sRectF.set(rectF);
        }
        rectF.set(rect);
        m.mapRect(rectF);
        rect.set((int) (rectF.left + 0.5f), (int) (rectF.top + 0.5f),
                (int) (rectF.right + 0.5f), (int) (rectF.bottom + 0.5f));
    }

    private void offsetDescendantMatrix(ViewParent target, View view, Matrix m) {
        final ViewParent parent = view.getParent();
        if (parent instanceof View && parent != target) {
            final View vp = (View) parent;
            offsetDescendantMatrix(target, vp, m);
            m.preTranslate(-vp.getScrollX(), -vp.getScrollY());
        }

        m.preTranslate(view.getLeft(), view.getTop());

        if (!view.getMatrix().isIdentity()) {
            m.preConcat(view.getMatrix());
        }
    }

    private int getMinimumHeightForVisibleOverlappingContent(AppBarLayout appBarLayout) {
        final int topInset = getTopInset();
        final int minHeight = ViewCompat.getMinimumHeight(appBarLayout);
        if (minHeight != 0) {
            // If this layout has a min height, use it (doubled)
            return (minHeight * 2) + topInset;
        }

        // Otherwise, we'll use twice the min height of our last child
        final int childCount = appBarLayout.getChildCount();
        final int lastChildMinHeight = childCount >= 1
                ? ViewCompat.getMinimumHeight(appBarLayout.getChildAt(childCount - 1)) : 0;
        if (lastChildMinHeight != 0) {
            return (lastChildMinHeight * 2) + topInset;
        }

        // If we reach here then we don't have a min height explicitly set. Instead we'll take a
        // guess at 1/3 of our height being visible
        return appBarLayout.getHeight() / 3;
    }

    private int getTopInset() {
        return mLastInsets != null ? mLastInsets.getSystemWindowInsetTop() : 0;
    }
}
