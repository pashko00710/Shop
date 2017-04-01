package me.uptop.mvpgoodpractice.mvp.views;

import me.uptop.mvpgoodpractice.data.storage.dto.DescriptionDto;

public interface IDescView extends IView {
    public void initView(DescriptionDto descriptionDto);
}
