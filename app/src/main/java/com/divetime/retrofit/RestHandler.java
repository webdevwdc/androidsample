package com.divetime.retrofit;

import android.content.Context;


import com.divetime.R;
import com.divetime.charters.model.CharterDetailsRequest;
import com.divetime.charters.model.CharterListingRequest;
import com.divetime.charters.model.PayPalRequest;
import com.divetime.charters.model.PaymentRequest;
import com.divetime.home.model.DashboardRequest;
import com.divetime.home.model.LocationUpdateRequest;
import com.divetime.login.model.ForgotPasswordRequest;
import com.divetime.login.model.LoginRequest;
import com.divetime.login.model.LogoutRequest;
import com.divetime.settings.model.ChangePasswordRequest;
import com.divetime.settings.model.GetUserSettingsRequest;
import com.divetime.settings.model.NotiSettingsRequest;
import com.divetime.settings.model.PrivacyPolicyRequest;
import com.divetime.settings.model.SetUserSettingsRequest;
import com.divetime.settings.model.SuggestionRequest;
import com.divetime.signup.model.SignupRequest;
import com.divetime.sites.model.CityRequest;
import com.divetime.sites.model.SiteDetailsRequest;
import com.divetime.sites.model.SiteListingRequest;
import com.divetime.sites.model.SiteTypeMasterRequest;
import com.divetime.utils.Constants;

import java.io.IOException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by Uzibaba on 1/8/2017.
 */

public class RestHandler {

   public Retrofit retrofit;
    RetrofitListener retroListener;
    String method_name;
    Context mContext;

    public RestHandler(Context con, RetrofitListener retroListener ){

        this.retroListener=retroListener;
        mContext=con;

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.base_url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public interface RestInterface {

       /* @GET("languages")
        Call<LanguageRequest> getLanguageList();

        @FormUrlEncoded
        @POST("nationalities")
        Call<NationalityRequest> getNationalityList(@Field("keyword") String keyword);

        @FormUrlEncoded
        @POST("cities")
        Call<CityRequest> getCityList(@Field("keyword") String keyword);*/

        @FormUrlEncoded
        @POST("suggestions")
        Call<SuggestionRequest>
        postSuggestions(@Field("user_id") int user_id,@Field("comment") String comment);

        @FormUrlEncoded
        @POST("appsetting/insert")
        Call<SetUserSettingsRequest>
        userSettings(@Field("user_id") int user_id,@Field("mile") String mile);


        @GET("appsetting/{user_id}")
        Call<GetUserSettingsRequest> getSettings(@Path("user_id") int user_id);

        @FormUrlEncoded
        @POST("user/signup")
        Call<SignupRequest> register(@Field("token_id") String token_id, @Field("facebookId") String facebookId,
                                     @Field("first_name") String first_name, @Field("last_name") String last_name,
                                     @Field("email") String email, @Field("password") String password,
                                     @Field("registration_type") String registration_type,
                                     @Field("device_type") String device_type,@Field("register_done") int register_done,
                                     @Field("address") String address,
                                     @Field("latitude") String latitude,
                                     @Field("longitude") String longitude);

        @FormUrlEncoded
        @POST("user/signin")
        Call<LoginRequest> login(@Field("token_id") String token_id,
                                 @Field("email") String email, @Field("password") String password,
                                 @Field("device_type") String device_type,
                                 @Field("address") String address,
                                 @Field("latitude") String latitude,
                                 @Field("longitude") String longitude);

        @GET("user/logout/{user_id}")
        Call<LogoutRequest> logout(@Path("user_id") int user_id);

        @GET("site-type")
        Call<SiteTypeMasterRequest> getSiteType();

        @GET("cms/{cms_type}")
        Call<PrivacyPolicyRequest> getPriavcyPolicy(@Path("cms_type") String type);

        @FormUrlEncoded
        @POST("trip/all/{user_id}")
        Call<CharterListingRequest> diveWhen(@Path("user_id") int user_id,
                                    @Field("dive_date") String dive_date);

        @GET("user/dashboard/{user_id}")
        Call<DashboardRequest> getDashboard(@Path("user_id") int user_id);

        @POST("trip/all/{user_id}")
        Call<CharterListingRequest> getCharterList(@Path("user_id") int user_id);

        @GET("trip/{trip_id}/{user_id}")
        Call<CharterDetailsRequest> getCharterDetails(@Path("trip_id")
                                                              int trip_id, @Path("user_id") int user_id);

        @POST("site/all/{user_id}")
        Call<SiteListingRequest> getSiteListing(@Path("user_id") int user_id);

        @GET("site/{site_id}/{user_id}")
        Call<SiteDetailsRequest> getSiteDetails(@Path("site_id")
                                                              int site_id, @Path("user_id") int user_id);

        @FormUrlEncoded
        @POST("site/all/{user_id}")
        Call<SiteListingRequest> getFilteredSites(@Path("user_id") int user_id,
                                             @Field("site_type") int site_type,
                                                  @Field("from_depth") int from_depth,
                                                  @Field("to_depth") int to_depth,
                                                  @Field("city") String city);

        @GET("site/all_charter/{site_id}/{user_id}")
        Call<CharterListingRequest> getSiteWiseCharterListing(@Path("site_id") int site_id,@Path("user_id") int user_id);

        @FormUrlEncoded
        @POST("user/change_password")
        Call<ChangePasswordRequest> changePassword(@Field("id") int id,
                                                   @Field("old_password") String old_password,
                                                   @Field("new_password") String new_password);

        @FormUrlEncoded
        @POST("user/forgot_password")
        Call<ForgotPasswordRequest> forgotPwd(@Field("email") String email);

        @FormUrlEncoded
        @POST("cities")
        Call<CityRequest> getCityList(@Field("keyword") String keyword);

        @FormUrlEncoded
        @PUT("user/location_update/{user_id}")
        Call<LocationUpdateRequest> locationUpdate(@Path("user_id") int user_id,
                                                   @Field("address") String address,
                                                   @Field("latitude") String latitude,
                                                   @Field("longitude") String longitude);

        @FormUrlEncoded
        @POST("dopayment")
        Call<PaymentRequest>
        doPayment(@Field("user_id") int user_id,@Field("dive_id") int dive_id,@Field("price") String price);

        @FormUrlEncoded
        @POST("notification/insert")
        Call<NotiSettingsRequest> notiSettings(@Field("user_id") int user_id,
                                               @Field("trip_cancel") String trip_cancel,
                                               @Field("discount_dive") String discount_dive,
                                               @Field("event") String event);

        @FormUrlEncoded
        @POST("checkpayment")
        Call<PayPalRequest> callService(@Field("paykey") String paykey,
                                        @Field("urltype") String urltype);
    }

    public void makeHttpRequest(Call call,String method)
    {
        this.method_name =method;
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                // do something with the response
//                if(mContext instanceof  Activity && !((Activity)mContext).isFinishing())
                retroListener.onSuccess(call,response, method_name);
            }

            @Override
            public void onFailure(Call call, Throwable t) {

                    if (t instanceof NoRouteToHostException) {
                        retroListener.onFailure(mContext.getString(R.string.server_unreachable));
                    } else if (t instanceof SocketTimeoutException) {
                        retroListener.onFailure(mContext.getString(R.string.timed_out));
                    } else if (t instanceof IOException) {
                        retroListener.onFailure(mContext.getString(R.string.no_internet));
                    } else
                        retroListener.onFailure(t.getMessage());
                }



        });
    }

}
