package com.divetime.settings.adapter;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.divetime.R;
import com.divetime.login.LoginActivity;
import com.divetime.login.model.LogoutRequest;
import com.divetime.retrofit.RestHandler;
import com.divetime.retrofit.RetrofitListener;
import com.divetime.settings.FragmentChangePassword;
import com.divetime.settings.FragmentCms;
import com.divetime.settings.FragmentNotificationSettings;
import com.divetime.settings.FragmentPrivacy;
import com.divetime.settings.FragmentSettings;
import com.divetime.settings.FragmentSuggestions;
import com.divetime.utils.Constants;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by webskitters on 3/29/2017.
 */

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> implements RetrofitListener {

    private ProgressDialog pDialog;
    Context mContext;
    View itemView;
    RestHandler rest;
    private AppInviteDialog mInvititeDialog;
    private List<String> mDataArray;
    Activity activity;
    public static CallbackManager callbackManager;

    public SettingsAdapter(List<String> dataset, Context con) {
        if(con!=null) {
            mDataArray = dataset;
            mContext = con;
            rest = new RestHandler(con, this);
            pDialog = new ProgressDialog(con);
            pDialog.setMessage(con.getString(R.string.dialog_msg));
            pDialog.setCancelable(false);
        }
    }

    @Override
    public int getItemCount() {
        if (mDataArray == null)
            return 0;
        return mDataArray.size();
    }

    @Override
    public SettingsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_settings, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_item.setText(mDataArray.get(position));

//        if(Constants.getUserType(mContext).equalsIgnoreCase(Constants.registration_type_fb))
//        holder.img_icon.setImageResource(icons[position+1]);
//        else
            holder.img_icon.setImageResource(icons[position]);

//        if(position%2==0)
//            itemView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
//        else
//            itemView.setBackgroundColor(mContext.getResources().getColor(R.color.setting_row_grey));


//        holder.mTextCity.setText(mDataArray.get(position).getHomeCity());
    }

    @Override
    public void onSuccess(Call call, Response response, String method) {

        if(pDialog.isShowing())
            pDialog.dismiss();

        if(response!=null && response.code()==200){

            if (method.equalsIgnoreCase("logout")) {
                String fcm_token="";

                LogoutRequest req_Obj = (LogoutRequest) response.body();
                if(!req_Obj.getResult().getError()) {
                    Toast.makeText(mContext, req_Obj.getResult().getMessage(), Toast.LENGTH_LONG).show();

                    SharedPreferences prefs = mContext.getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);

//                    prefs.edit().putBoolean(Constants.strShPrefUserloginStatus,false).commit();

                    fcm_token=prefs.getString(Constants.strShPrefFCMTokenID,"");

                    String user=Constants.getUserType(mContext);
                    Log.d("Ddd",user);

                    if(Constants.getUserType(mContext).equalsIgnoreCase("fb")){

                        Constants.disconnectFromFacebook();

                    }

                    SharedPreferences.Editor editor = mContext.getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE).edit();
                    editor.clear().commit();
                    prefs = mContext.getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);
                    prefs.edit().putString(Constants.strShPrefFCMTokenID,fcm_token).commit();


                    Intent intent=new Intent(mContext, LoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mContext.startActivity(intent);
                    ((Activity)mContext).finish();
//                    if(posi_del!=-1)
//                        removeAt(posi_del);
                }
                else
                    Toast.makeText(mContext,req_Obj.getResult().getMessage(),Toast.LENGTH_LONG).show();

            }

        }
        else{
            try {
                response.errorBody().string();
//                label.setText(response.code()+" "+response.message());
//                showAlertDialog("Alert",response.code()+" "+response.message());
            } catch (IOException e) {
//                showAlertDialog("Alert","Server Response Error..");
                e.printStackTrace();
            }
            catch(NullPointerException e){
//                showAlertDialog("Alert","Server Response Error..");
            }
        }

    }

    @Override
    public void onFailure(String errorMessage) {

        if(pDialog.isShowing())
            pDialog.dismiss();

        Constants.showAlert(mContext,errorMessage);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_item)
        TextView tv_item;

        @Bind(R.id.img_icon)
        ImageView img_icon;

        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String item=mDataArray.get(getAdapterPosition());

                    if(item.equalsIgnoreCase("logout")){

                        showConfirmDialog();
                    }
                    else if(item.equalsIgnoreCase("Privacy Policy"))
                    {

                        FragmentTransaction ft = ((Activity)mContext).getFragmentManager().beginTransaction();

                        ft.replace(R.id.contentContainer, FragmentCms.newInstance("privacy-policy",""));
                        ft.addToBackStack(new FragmentCms().getClass().getName());
                        ft.commit();
                    }
                    else if(item.equalsIgnoreCase("Terms of Services"))
                    {

                        FragmentTransaction ft = ((Activity)mContext).getFragmentManager().beginTransaction();

                        ft.replace(R.id.contentContainer, FragmentCms.newInstance("terms-conditions",""));
                        ft.addToBackStack(new FragmentCms().getClass().getName());
                        ft.commit();
                    }

                    else if(item.equalsIgnoreCase("Change Password"))
                    {

                        FragmentTransaction ft = ((Activity)mContext).getFragmentManager().beginTransaction();

                        ft.replace(R.id.contentContainer, FragmentChangePassword.newInstance("",""));
                        ft.addToBackStack(new FragmentChangePassword().getClass().getName());
                        ft.commit();
                    }
                    else if(item.equalsIgnoreCase("My Settings"))
                    {

                        FragmentTransaction ft = ((Activity)mContext).getFragmentManager().beginTransaction();

                        ft.replace(R.id.contentContainer, FragmentPrivacy.newInstance("",""));
                        ft.addToBackStack(new FragmentChangePassword().getClass().getName());
                        ft.commit();
                    }

                    else if(item.equalsIgnoreCase("Contact Us/Suggestions"))
                    {

                        FragmentTransaction ft = ((Activity)mContext).getFragmentManager().beginTransaction();

                        ft.replace(R.id.contentContainer, FragmentSuggestions.newInstance("",""));
                        ft.addToBackStack(new FragmentSuggestions().getClass().getName());
                        ft.commit();
                    }
                    else if (item.equalsIgnoreCase("Invite Facebook Friends")){
//                        FragmentSettings.getFbFriends();
                        activity = (Activity) itemView.getContext();
//                        inviteFbFriens(activity);
                        openDialogInvite((Activity) itemView.getContext());
                    }
                    else if (item.equalsIgnoreCase("Notification Settings")){
//                        FragmentSettings.getFbFriends();
                        FragmentTransaction ft = ((Activity)mContext).getFragmentManager().beginTransaction();

                        ft.replace(R.id.contentContainer, FragmentNotificationSettings.newInstance("",""));
                        ft.addToBackStack(new FragmentNotificationSettings().getClass().getName());
                        ft.commit();
                    }


                }
            });

        }
    }

    private void logout()
    {

        pDialog.show();
        rest.makeHttpRequest(rest.retrofit.create(RestHandler.RestInterface.class).
                logout(Constants.getUserId(mContext)),"logout");

    }

    private void showConfirmDialog()
    {
        //super.onBackPressed();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

        // set title
        alertDialogBuilder.setTitle("Alert");

        // set dialog message
        alertDialogBuilder
                .setMessage("Do you want to Logout ?")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
//                        MainActivity.this.finish();
                        logout();
                    }
                })
                .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void inviteFbFriens(Activity activity){
        String appLinkUrl, previewImageUrl;

        callbackManager = CallbackManager.Factory.create();

        mInvititeDialog = new AppInviteDialog(activity);
        mInvititeDialog.registerCallback(callbackManager,
                new FacebookCallback<AppInviteDialog.Result>() {

                    @Override
                    public void onSuccess(AppInviteDialog.Result result) {
                        Constants.showAlert(mContext, "Invitation sent successfully.");
                    }

                    @Override
                    public void onCancel() {
                        Log.d("BadgeRemoveResult", "Cancelled");
                        Constants.showAlert(mContext, "Cancelled.");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d("BadgeRemoveResult", "Error " + exception.getMessage());
                        Constants.showAlert(mContext, "Unexpected error ocuured.");
                    }
                });

//        appLinkUrl = "https://fb.me/1836682009929921";

        appLinkUrl = "https://fb.me/217036362133389";
        previewImageUrl = "https://fb.me/217036362133389";

        if (AppInviteDialog.canShow()) {
            AppInviteContent content = new AppInviteContent.Builder()
                    .setApplinkUrl(appLinkUrl)
                    .setPreviewImageUrl(previewImageUrl)
                    .build();
            AppInviteDialog.show((Activity)mContext, content);
        }
    }

    public void openDialogInvite(final Activity activity)
    {
        String appLinkUrl, previewImageUrl;

        appLinkUrl = "https://fb.me/1936150383333112";
        previewImageUrl = "https://fb.me/1936150383333112";

        if (AppInviteDialog.canShow())
        {
            AppInviteContent content = new AppInviteContent.Builder()
                    .setApplinkUrl(appLinkUrl)
                    .setPreviewImageUrl(previewImageUrl)
                    .build();

            AppInviteDialog appInviteDialog = new AppInviteDialog(activity);
            callbackManager = CallbackManager.Factory.create();
            appInviteDialog.registerCallback(callbackManager, new FacebookCallback<AppInviteDialog.Result>()
            {
                @Override
                public void onSuccess(AppInviteDialog.Result result)
                {
                    Constants.showAlert(mContext, "Invitation sent successfully.");
                }

                @Override
                public void onCancel()
                {
                    Constants.showAlert(mContext, "Cancelled.");
                }

                @Override
                public void onError(FacebookException e)
                {
                    Constants.showAlert(mContext, "Unexpected error ocuured.");
                }
            });

            appInviteDialog.show(content);
        }
    }

    private int[] icons =new int[]{

            R.drawable.pwd_ico,
            R.drawable.ico_settings_fb,
            R.drawable.ico_settings1,
            R.drawable.ico_settings_notificatons,
            R.drawable.ico_settings_privacy,
            R.drawable.ico_settings_terms_cond,
            R.drawable.ico_settings_contactus,
            R.drawable.ico_settings_logout,
    };

}
