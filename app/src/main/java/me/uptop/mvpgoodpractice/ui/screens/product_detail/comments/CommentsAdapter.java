package me.uptop.mvpgoodpractice.ui.screens.product_detail.comments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import me.uptop.mvpgoodpractice.R;
import me.uptop.mvpgoodpractice.data.storage.dto.CommentDto;
import me.uptop.mvpgoodpractice.di.DaggerService;
import me.uptop.mvpgoodpractice.utils.CircularTransformation;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder> {

    private List<CommentDto> mCommentList = new ArrayList<>();
    private Context context;

    @Inject
    Picasso mPicasso;

    public CommentsAdapter(Context context) {
        this.context = context;
    }

    @Override
    public CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentsViewHolder(view);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        DaggerService.<CommentsScreen.Component>getDaggerComponent(recyclerView.getContext()).inject(this);
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(CommentsViewHolder holder, int position) {
        CommentDto comment = mCommentList.get(position);
        String urlAvatar = comment.getAvatar();
        if(urlAvatar == null || urlAvatar.isEmpty()) {
            urlAvatar = "http://skill-branch.ru/img/app/avatar-1.png";
        }

        mPicasso.load(urlAvatar)
                .error(R.drawable.ic_account_circle_black_24dp)
                .transform(new CircularTransformation())
                .into(holder.mUserAvatar);
        holder.mUserNameComment.setText(comment.getUserName());
        holder.mCommentText.setText(comment.getComment());
        holder.mRatingComment.setRating(comment.getRating());
        holder.mCommentDate.setText(comment.getDate());
    }

    public void addItem(CommentDto commentDto) {
        mCommentList.add(commentDto);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }

    public void reloadAdapter(List<CommentDto> commentDtos) {
        mCommentList.clear();
        mCommentList = commentDtos;
        notifyDataSetChanged();
    }

    public class CommentsViewHolder extends RecyclerView.ViewHolder {

        private ImageView mUserAvatar;
        private TextView mUserNameComment;
        private TextView mCommentText;
        private RatingBar mRatingComment;
        private TextView mCommentDate;

        public CommentsViewHolder(View itemView) {
            super(itemView);
            mUserAvatar = (ImageView) itemView.findViewById(R.id.comment_avatar);
            mUserNameComment = (TextView) itemView.findViewById(R.id.comment_user_name);
            mCommentText = (TextView) itemView.findViewById(R.id.comment_text);
            mRatingComment = (RatingBar) itemView.findViewById(R.id.comment_rating);
            mCommentDate = (TextView) itemView.findViewById(R.id.comment_date);
        }
    }
}
