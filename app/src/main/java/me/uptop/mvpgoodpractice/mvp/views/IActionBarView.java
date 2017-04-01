package me.uptop.mvpgoodpractice.mvp.views;

import android.support.v4.view.ViewPager;

import java.util.List;

import me.uptop.mvpgoodpractice.mvp.presenters.MenuItemHolder;

public interface IActionBarView {
    void setTitle(CharSequence title);
    void setVisible(boolean visible);
    void setBackArrow(boolean enabled);
    void setMenuItem(List<MenuItemHolder> items);
    void setTabLayout(ViewPager pager);
    void removeTabLayout();
}
