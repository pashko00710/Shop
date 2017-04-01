package me.uptop.mvpgoodpractice.mvp.models;

public class RootModel extends AbstractModel {
    public RootModel() {
    }

    public void saveCartProductCounter(int count) {
        mDataManager.saveCartProductCounter(count);
    }

    public int loadCartProductCounter() {
        return mDataManager.loadCartProductCounter();
    }

    public int getBasketCounter() {
        return mDataManager.getPreferencesManager().getBasketCounter();
    }

    public void saveBasketCounter(int counter) {
        mDataManager.getPreferencesManager().saveBasketCounter(counter);
    }

}
