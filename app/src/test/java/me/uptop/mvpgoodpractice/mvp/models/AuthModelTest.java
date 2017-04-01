package me.uptop.mvpgoodpractice.mvp.models;

import com.birbit.android.jobqueue.JobManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import me.uptop.mvpgoodpractice.data.managers.DataManager;
import me.uptop.mvpgoodpractice.data.network.req.UserLoginReq;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AuthModelTest {
    private AuthModel model;
    @Mock
    DataManager mockDataManager;
    @Mock
    JobManager mockJobManager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        model = new AuthModel(mockDataManager, mockJobManager);
    }

    @Test
    public void isAuthUser() throws Exception {
        model.isAuthUser();
        verify(mockDataManager, only()).isAuthUser();
    }

//    @Test
//    public void saveAuthToken() throws Exception {
//
//    }

    @Test
    public void loginUser() throws Exception {
        model.loginUser("any@mail.ru", "password");
        verify(mockDataManager, only()).loginUser(any(UserLoginReq.class));
    }

}