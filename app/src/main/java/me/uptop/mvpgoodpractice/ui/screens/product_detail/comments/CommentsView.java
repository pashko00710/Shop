package me.uptop.mvpgoodpractice.ui.screens.product_detail.comments;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import me.uptop.mvpgoodpractice.R;
import me.uptop.mvpgoodpractice.di.DaggerService;
import me.uptop.mvpgoodpractice.mvp.views.AbstractView;

public class CommentsView extends AbstractView<CommentsScreen.CommentsPresenter> {
    @BindView(R.id.comments_list)
    RecyclerView commentList;

    @Inject
    CommentsScreen.CommentsPresenter mPresenter;

    private CommentsAdapter mAdapter = new CommentsAdapter(getContext());

    public CommentsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initDagger(Context context) {
        DaggerService.<CommentsScreen.Component>getDaggerComponent(context).inject(this);
    }


    @Override
    public boolean viewOnBackPressed() {
        return false;
    }

    public CommentsAdapter getAdapter() {
        return mAdapter;
    }

    public void initView() {
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        commentList.setLayoutManager(llm);
        commentList.setAdapter(mAdapter);
    }

    @OnClick(R.id.fab_comment_add)
    public void commentAdd() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_comment_add, null);
        RatingBar addCommentRatingBar = (RatingBar) view.findViewById(R.id.add_comment_ratingbar);
        TextInputEditText addCommentEditText = (TextInputEditText) view.findViewById(R.id.comment_message);

        dialogBuilder.setTitle(R.string.account_editing)
                .setView(view)
                .setPositiveButton("Ok", (dialogInterface, i) -> {

                        mPresenter.sendToServer(addCommentRatingBar.getRating(), addCommentEditText.getText().toString());

                    dialogInterface.cancel();
                })
                .setNegativeButton("Nope", (dialogInterface, i) -> dialogInterface.cancel())
                .show();
    }
}