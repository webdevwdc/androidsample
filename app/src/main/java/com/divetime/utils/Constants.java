package com.divetime.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by android on 24/5/17.
 */

public class Constants {
    public static final String TAG_FRAGMENT_ME = "fragment_home" ;

    // Live Server
    public static String base_url="http://api.divetimeapp.com/api/v2/";
    public static String image_base_url="http://divetimeapp.com/uploads/";

//
    public static String articles_thumb_image_url =image_base_url+"articles/thumbs/";
    public static String articles_full_image_url =image_base_url+"articles/";

    public static String contents_thumb_image_url =image_base_url+"contents/thumbs/";


    public static String site_image=image_base_url+"sites/thumbs/";

    public static String SHARED_PREF_NAME="DiveTimeData";

    public static String registration_type_nrml="Normal";
    public static String registration_type_fb="Fb";
    public static String device_type="android";

    public static String strShPrefUserType="user_type";
    public static String strShPrefRegistrationType="registration_type";
    public static String strShPrefUserLocationSet="No";
    public static String strShPrefUserloginStatus="login_status";
    public static String strShPrefUserFirstName="first_name";
    public static String strShPrefUserLastName="last_name";
    public static String strShPrefUserEmail="email";
    public static String strShPrefUserZip="zip_code";
    public static String strShPrefUserCity="city";
    public static String strShPrefUserLanguage="language_id";
    public static String strShPrefUserLanguageTEXTS="language_texts";
    public static String strShPrefUserLRegType="registration_type";
    public static String strShPrefUserID="id";
    public static String strShPrefUserShortBio="short_bio";
    public static String strShPrefProfilePic="profile_pic";
    public static String strShPrefPhoneNo="phone_no";

    public static String strShPrefUserTag="user_tag";

    public static String strShPrefUserNationalityID="nationality_id";
    public static String strShPrefUserLatitude="user_latitude";
    public static String strShPrefUserLongitude="user_longitude";
    public static String strShPrefFCMTokenID="fcm_token";

    public static String strShPrefMeetupBadges="meetup_badges_count";
    public static String strShPrefBulletinBadges="bulletin_badges_count";
    public static String strShPrefConnectionBadges="connection_badges_count";
    public static String strShPrefChatupBadges="chat_badges_count";


    public static String strShPrefUserToChat="to_user_id";
    public static String strShPrefUserFromChat="from_user_id";

    public static String strShPrefUserFBID="fb_id";
    public static String strShPrefAddress="adress";

    public static int PlacePicker_Requeest=007;


    public static String web_date_format="yyyy-MM-dd HH:mm:ss";
    public static String app_display_date_format="MM/dd/yy";
    public static String app_display_time_format="hh:mm a";


    public static String app_bulletin_display_date_format="dd MMM yyyy";

    public static String PRIVACY_PUBLIC="public";
    public static String PRIVACY_PRIVATE="private";

    public static String LIKE_YES="Yes";
    public static String LIKE_NO="No";

    public static String web_date_only_format="yyyy-MM-dd";
    public static String web_time_only_format="HH:mm:ss";


    public static String ACCEPT="Accept";
    public static String REJECT="Reject";
    public static String to_user_id="";


    public static void showAlert(Context con, String msg)
    {

        if(con==null)
            return;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(con);

        // set title
        alertDialogBuilder.setTitle("Alert");

        // set dialog message
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(false)
                .setNegativeButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public static boolean emailValidator(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static String getFCMToken(Context con)
    {

        if(con==null)
            return "";

        String fcm="";
        SharedPreferences prefs = con.getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);
        fcm = prefs.getString(Constants.strShPrefFCMTokenID, "");

        return fcm;
    }

    public static ProgressDialog getDialog(Context mContext)
    {

        ProgressDialog progressDialog;
        progressDialog=new ProgressDialog(mContext);

        progressDialog.setMessage("Please wait..");
        progressDialog.setCancelable(false);

        return progressDialog;
    }

    public static boolean getLoggedinStatus(Context con)
    {
        boolean status=false;

        SharedPreferences prefs = con.getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);
        status = prefs.getBoolean(Constants.strShPrefUserloginStatus, false);

        return status;

    }

    public static String getUserType(Context con)
    {
        String type="";
        SharedPreferences prefs = con.getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);
        type = prefs.getString(Constants.strShPrefRegistrationType, "");

        return type;

    }

    public static int getUserId(Context con)
    {
        if(con==null)
            return 0;

        int id=0;

        SharedPreferences prefs = con.getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);
        id = prefs.getInt(Constants.strShPrefUserID, 0);

        return id;

    }


    public static String changeDateFormat(String inp_date,String inpFormat,String outFormat) {

        String outputDateStr = "";
        DateFormat outputFormat = new SimpleDateFormat(outFormat);
        DateFormat inputFormat = new SimpleDateFormat(inpFormat);
//        String inputDateStr= null;
        try {
//            inputDateStr = "2013-06-24";

            Date date = inputFormat.parse(inp_date);
            outputDateStr = outputFormat.format(date);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return outputDateStr;
    }

    public static void disconnectFromFacebook() {

        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }

        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {

                LoginManager.getInstance().logOut();

            }
        }).executeAsync();
    }

    public static String getUserName(Context con)
    {
        String firstName="", lastName="";
        SharedPreferences prefs = con.getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);
        firstName = prefs.getString(Constants.strShPrefUserFirstName, "");
        lastName = prefs.getString(Constants.strShPrefUserLastName,"");
        return firstName+" "+lastName;
    }


    public static void setImage(Context mContext, ImageView image,boolean isThumbImage,String image_name,final ProgressBar progressBar)
    {

     /* Glide.with(mContext)
                .load(image_name)
                .fitCenter()
                .into(image);*/

        Glide.with(mContext).load(image_name).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                if(progressBar!=null)
                progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                if(progressBar!=null)
                progressBar.setVisibility(View.GONE);
                return false;
            }
        }).into(image);
    }

}
