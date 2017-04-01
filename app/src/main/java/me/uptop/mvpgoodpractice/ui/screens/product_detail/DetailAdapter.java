package me.uptop.mvpgoodpractice.ui.screens.product_detail;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.uptop.mvpgoodpractice.R;
import me.uptop.mvpgoodpractice.data.storage.realm.ProductRealm;
import me.uptop.mvpgoodpractice.di.DaggerService;
import me.uptop.mvpgoodpractice.flow.AbstractScreen;
import me.uptop.mvpgoodpractice.ui.screens.product_detail.comments.CommentsScreen;
import me.uptop.mvpgoodpractice.ui.screens.product_detail.description.DescriptionScreen;
import mortar.MortarScope;

public class DetailAdapter extends PagerAdapter {

    private static final int TABS_NUMBER = 2;

    private Context mContext;
    private ProductRealm mProductRealm;

    public DetailAdapter(Context context, ProductRealm productRealm) {
        mContext = context;
        mProductRealm = productRealm;
    }

    @Override
    public int getCount() {
        return TABS_NUMBER;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.detail_description);
            case 1:
                return mContext.getString(R.string.detail_comment);
            default:
                return super.getPageTitle(position);
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        AbstractScreen screen = null;


        View newView;
        // TODO: 19.12.2016 // FIXME: 19.12.2016

        switch (position) {
            case 0:
                screen = new DescriptionScreen(mProductRealm);
                break;
            case 1:
                screen = new CommentsScreen(mProductRealm);
                break;
            default:
                break;
        }

        MortarScope screenScope = createScreenScopeFromContext(container.getContext(), screen);
        Context screenContext = screenScope.createContext(container.getContext());
        newView = LayoutInflater.from(screenContext).inflate(screen.getLayoutResId(), container, false);
        container.addView(newView);

        return newView;
    }

    private MortarScope createScreenScopeFromContext(Context context, AbstractScreen screen) {
        MortarScope parentScope = MortarScope.getScope(context);
        MortarScope childScope = parentScope.findChild(screen.getScopeName());

        if(childScope == null) {
            Object screenComponent = screen.createScreenComponent(parentScope.getService(DaggerService.SERVICE_NAME));
            if(screenComponent == null) {
                throw new IllegalStateException("don't create screen component for" + screen.getScopeName());
            }

            childScope = parentScope.buildChild()
                    .withService(DaggerService.SERVICE_NAME, screenComponent)
                    .build(screen.getScopeName());
        }

        return childScope;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        MortarScope scope = MortarScope.getScope(((View)object).getContext());
        container.removeView((View)object);
//        scope.destroy();
    }
}
