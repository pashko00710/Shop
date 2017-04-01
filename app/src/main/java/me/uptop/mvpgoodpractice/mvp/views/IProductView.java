package me.uptop.mvpgoodpractice.mvp.views;

import me.uptop.mvpgoodpractice.data.storage.dto.ProductDto;

public interface IProductView extends IView {
    public void showProductView(ProductDto product);
    public void updateProductCountView(ProductDto product);
}
