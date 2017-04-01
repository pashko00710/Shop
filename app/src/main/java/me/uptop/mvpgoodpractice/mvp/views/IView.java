package me.uptop.mvpgoodpractice.mvp.views;

import android.content.Context;

public interface IView {
    boolean viewOnBackPressed();
    Context getContext();
}
