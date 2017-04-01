package me.uptop.mvpgoodpractice.resources;

import android.view.MenuItem;

import me.uptop.mvpgoodpractice.R;
import me.uptop.mvpgoodpractice.data.network.req.UserLoginReq;
import me.uptop.mvpgoodpractice.data.network.res.UserRes;
import me.uptop.mvpgoodpractice.data.storage.dto.UserInfoDto;
import me.uptop.mvpgoodpractice.mvp.presenters.MenuItemHolder;

import static org.mockito.Mockito.mock;

public class StubEntityFactory {

    @SuppressWarnings("unchecked")
    public static <T> T makeStub(Class<T> stubEntityClass) {
        switch (stubEntityClass.getSimpleName()) {
            case "UserRes":
                return (T) new UserRes("58711631a242690011b1b26d", "Макеев Михаил","https://pp.userapi.com/c313129/v313129097/80ff/5U-iWkuFxEM.jpg","wegfvw;edcnw'lkedm93847983yuhefoij32lkml'kjvj30fewoidvn","89179711111", null);
            case "UserLoginReq":
                return (T) new UserLoginReq("anymail@mail.ru", "password");
            case "UserInfoDto":
                return (T) new UserInfoDto("Макеев Михаил","89179711111", "https://pp.userapi.com/c313129/v313129097/80ff/5U-iWkuFxEM.jpg");
            case "MenuItemHolder":
                return (T) new MenuItemHolder("Редактировать", R.drawable.ic_account_circle_black_24dp, mock(MenuItem.OnMenuItemClickListener.class));
            default:
                return null;
        }
    }
}
