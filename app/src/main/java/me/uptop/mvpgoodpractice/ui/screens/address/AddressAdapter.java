package me.uptop.mvpgoodpractice.ui.screens.address;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import me.uptop.mvpgoodpractice.R;
import me.uptop.mvpgoodpractice.data.storage.dto.UserAddressDto;

import static com.facebook.stetho.inspector.network.ResponseHandlingInputStream.TAG;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressHolder> {

    private List<UserAddressDto> addressList  = new ArrayList<>();

    class AddressHolder extends RecyclerView.ViewHolder {
        public EditText house;
        public EditText commentHouse;
//        public EditText accountWork;
//        public EditText accountCommentWork;
        public AddressHolder(View v) {
            super(v);
            house = (EditText) v.findViewById(R.id.account_house);
            commentHouse = (EditText) v.findViewById(R.id.account_comment_house);
//            accountWork = (EditText) v.findViewById(R.id.account_work);
//            accountCommentWork = (EditText) v.findViewById(R.id.account_comment_work);
        }


    }



    public void removeItem(int position) {
        addressList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, addressList.size());
    }

    public void addItem(UserAddressDto userAddressDto) {
        addressList.add(userAddressDto);
        notifyDataSetChanged();
    }

    public void reloadAdapter() {
        addressList.clear();
        notifyDataSetChanged();
    }

    @Override
    public AddressHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.screen_address_item, parent, false);
        AddressHolder vh = new AddressHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(AddressHolder holder, int position) {
        UserAddressDto address = addressList.get(position);
        Log.e(TAG, "onBindViewHolder: "+address.getAppartament());
        holder.house.setText(address.getHouse());
        holder.commentHouse.setText((address.getComment()));
//        holder.accountWork.setText(address.get);
    }

    @Override
    public int getItemCount() {
        return this.addressList.size();
    }

}