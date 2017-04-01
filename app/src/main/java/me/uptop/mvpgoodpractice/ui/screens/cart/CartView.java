package me.uptop.mvpgoodpractice.ui.screens.cart;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;
import me.uptop.mvpgoodpractice.R;
import me.uptop.mvpgoodpractice.data.storage.realm.OrdersRealm;
import me.uptop.mvpgoodpractice.di.DaggerService;
import me.uptop.mvpgoodpractice.mvp.views.AbstractView;

public class CartView extends AbstractView<CartScreen.CartPresenter> {
    @BindView(R.id.cart_list)
    RecyclerView mOrdersList;
    @BindView(R.id.amount_value)
    TextView amountValue;
    @BindView(R.id.discount_value)
    TextView discountValue;
    @BindView(R.id.total_value)
    TextView totalValue;

    CartAdapter mAdapter;
    int discount, total, amount;

    public CartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override
    protected void initDagger(Context context) {
        DaggerService.<CartScreen.Component>getDaggerComponent(context).inject(this);
    }

    @Override
    public boolean viewOnBackPressed() {
        return false;
    }

    public void showCartList(RealmResults<OrdersRealm> allOrders) {
        mAdapter = new CartAdapter(getContext(), allOrders, listener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mOrdersList.setLayoutManager(layoutManager);
        mOrdersList.setAdapter(mAdapter);
    }

    CartAdapter.CartViewHolder.OnClickListener listener = new CartAdapter.CartViewHolder.OnClickListener() {
        @Override
        public void onImageClick(OrdersRealm order) {
            mPresenter.onProductImageClick(order);
        }

        @Override
        public void onDeleteProduct(OrdersRealm order) {
            mPresenter.onDeleteProduct(order);
        }
    };


    public void initPrice(RealmResults<OrdersRealm> orders) {
        total = 0;
        amount = 0;
        for(OrdersRealm order:orders) {
            if(!order.isStatusPurchase()) {
                if(order.getCount()>0) {
                    total += order.getPrice()*order.getCount();
                } else {
                    total += order.getPrice();
                }
                amount += 1;
            }
        }
        if(total > 3000) {
            discount = (int) (total*0.15);
            discountValue.setText(String.valueOf(discount));
            totalValue.setText(String.valueOf(total-discount));
        } else {
            discount = 0;
            discountValue.setText(String.valueOf(discount));
            totalValue.setText(String.valueOf(total));
        }
        amountValue.setText(String.valueOf(amount));
    }
}
