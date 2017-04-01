package me.uptop.mvpgoodpractice.data.network.error;

import retrofit2.Response;

public class ErrorUtils {
//    public static ApiError parseError(Response<?> response) {
//        ApiError error;
//
//        try {
//            error = (ApiError) DataManager.getInstance()
//                    .getRetrofit()
//            .responseBodyConverter(ApiError.class, ApiError.class.getAnnotations())
//            .convert(response.errorBody());
//        } catch (IOException e) {
//            e.printStackTrace();
//            return new ApiError();
//        }
//        return error;
//    }

    public static ApiError parseError(Response<?> response) {
        // TODO: 21.03.2017 correct parse error (without retrofit dependency)
        return new ApiError(response.code());
    }
}
