package me.uptop.mvpgoodpractice.data.network;

import java.util.List;

import me.uptop.mvpgoodpractice.data.network.req.UserLoginReq;
import me.uptop.mvpgoodpractice.data.network.req.UserSignInReq;
import me.uptop.mvpgoodpractice.data.network.res.AvatarUrlRes;
import me.uptop.mvpgoodpractice.data.network.res.FbProfileRes;
import me.uptop.mvpgoodpractice.data.network.res.ProductRes;
import me.uptop.mvpgoodpractice.data.network.res.TwProfileRes;
import me.uptop.mvpgoodpractice.data.network.res.UserRes;
import me.uptop.mvpgoodpractice.data.network.res.VkProfileRes;
import me.uptop.mvpgoodpractice.data.network.res.models.AddCommentRes;
import me.uptop.mvpgoodpractice.data.network.res.models.Comments;
import me.uptop.mvpgoodpractice.utils.ConstantManager;
import okhttp3.MultipartBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

public interface RestService {
    @GET("products")
    Observable<Response<List<ProductRes>>> getProductResObs (@Header(ConstantManager.IF_MODIFIED_SINCE_HEADER) String lastEntityUpdate);

    @POST("products/{id}/comments")
    Observable<Comments> sendCommentToServer(@Path("id") String id,
                                        @Body AddCommentRes post);

    @Multipart
    @POST("avatar")
    Observable<AvatarUrlRes> uploadUserAvatar(@Part MultipartBody.Part file);

    @POST("login")
    Observable<Response<UserRes>> loginUser(@Body UserLoginReq userLoginReq);

    @GET
    Observable<VkProfileRes> signInVk(@Url String baseVkApiUrl, @Query("access_token") String vkToken);

    @POST("socialLogin")
    Observable<Response<UserRes>> socialSignIn(@Body UserSignInReq userSocReq);

    @GET
    Observable<FbProfileRes> getFbProfile(@Url String baseUrl, @Query("access_token") String accessToken);

    @GET
    Observable<TwProfileRes> getTwProfile(@Url String baseUrl, @Query("access_token") String accessToken);
}
