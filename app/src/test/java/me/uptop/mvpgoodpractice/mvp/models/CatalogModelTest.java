package me.uptop.mvpgoodpractice.mvp.models;

import com.birbit.android.jobqueue.JobManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import me.uptop.mvpgoodpractice.data.managers.DataManager;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

public class CatalogModelTest {
    private CatalogModel model;
    @Mock
    DataManager mockDataManager;
    @Mock
    JobManager mockJobManager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        model = new CatalogModel(mockDataManager, mockJobManager);
    }

    @Test
    public void isAuthUser() throws Exception {
        //given


        //when
        model.isUserAuth();

        //then
        verify(mockDataManager, only()).isAuthUser();
    }
//
//    @Test
//    public void loginUser() throws Exception {
//        model.loginUser("any@mail.ru", "password");
//        verify(mockDataManager, only()).loginUser(any(UserLoginReq.class));
//    }



}