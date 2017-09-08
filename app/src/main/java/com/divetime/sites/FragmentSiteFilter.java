package com.divetime.sites;

import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.divetime.LandingActivity;
import com.divetime.R;
import com.divetime.retrofit.RestHandler;
import com.divetime.retrofit.RetrofitListener;
import com.divetime.sites.adapter.SitesAdapter;
import com.divetime.sites.model.CityRequest;
import com.divetime.sites.model.SiteListingRequest;
import com.divetime.sites.model.SiteTypeMasterRequest;
import com.divetime.utils.Constants;
import com.divetime.utils.TrackApplication;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentSites#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSiteFilter extends Fragment implements View.OnClickListener,RetrofitListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private AutoCompleteTextView edt_city;
    Button btn_site_type,btn_site_depth,btn_apply,btn_city;
    RestHandler rest;
    ProgressDialog pDialog;
    SeekBar seekbar_from,seekbar_to;
    ArrayList<String> al_site_master_list=new ArrayList<>();

    ArrayList<String> al_city;
    ProgressBar city_prog_bar;

    CityRequest req_cityObj;

    Toolbar toolbar;
    TextView toolbar_header;
    int selected_site_type_id=0,seekbar_from_value=0,seekbar_to_value=0;
    SiteTypeMasterRequest site_type_obj;
    private OnFragmentInteractionListener mListener;
    ImageView img_back;
    private String selected_city="",selected_city_id="";

    public FragmentSiteFilter() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static FragmentSiteFilter newInstance(String param1, String param2) {
        FragmentSiteFilter fragment = new FragmentSiteFilter();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_sites_filter, container, false);

        btn_site_depth=(Button)view.findViewById(R.id.btn_site_depth);
        btn_site_type=(Button)view.findViewById(R.id.btn_site_type);
        btn_apply=(Button)view.findViewById(R.id.btn_apply);
        btn_city=(Button)view.findViewById(R.id.btn_city);

        rest=new RestHandler(getActivity(),this);
        btn_site_type.setOnClickListener(this);
        btn_site_depth.setOnClickListener(this);
        btn_apply.setOnClickListener(this);
        btn_city.setOnClickListener(this);

        pDialog= Constants.getDialog(getActivity());

        toolbar_header= (TextView)view.findViewById(R.id.toolbar_header);
        toolbar_header.setText("Filters");
         toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
//        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        getSiteType();

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
      /*  if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.btn_site_type:

                showAlert();

                break;

            case R.id.btn_site_depth:

                showDepthAlert();
                break;

            case R.id.btn_apply:

                if(selected_site_type_id==0 && seekbar_to_value==0 && selected_city.equalsIgnoreCase(""))
                    Constants.showAlert(getActivity(),"Please select options.");
                else
                getFilteredSiteType();

                break;

            case R.id.btn_city:

                showCityDialog();

                break;
        }
    }

    private void getSiteType()
    {
        if(pDialog!=null)
        pDialog.show();
        rest.makeHttpRequest(rest.retrofit.create(RestHandler.RestInterface.class).
                getSiteType(),"site_type");
    }

    private void getFilteredSiteType()
    {
        if(pDialog!=null)
            pDialog.show();
        rest.makeHttpRequest(rest.retrofit.create(RestHandler.RestInterface.class).
                getFilteredSites(Constants.getUserId(getActivity()),
                        selected_site_type_id,seekbar_from_value,seekbar_to_value,selected_city),"filtered_site_type");
    }

    @Override
    public void onSuccess(Call call, Response response, String method) {

        if(pDialog.isShowing())
            pDialog.dismiss();

        if(response!=null && response.code()==200){

            if(method.equalsIgnoreCase("site_type"))
            {
                site_type_obj=(SiteTypeMasterRequest)response.body();
                if(!site_type_obj.getResult().getError() && site_type_obj.getResult().getData()!=null)
                {
                    al_site_master_list.clear();
                    for(int i=0;i<site_type_obj.getResult().getData().size();i++)
                        al_site_master_list.add(site_type_obj.getResult().getData().get(i).getName());
                }
                else{
//                    Toast.makeText(SignupActivity.this,signup_obj.getResult().getMessage(),Toast.LENGTH_LONG).show();
//                    Constants.showAlert(getActivity(),signup_obj.getResult().getMessage());
                }
            }
            else  if(method.equalsIgnoreCase("filtered_site_type"))
            {
                SiteListingRequest sites_obj=(SiteListingRequest)response.body();

                if(!sites_obj.getResult().getError())
                {
                    TrackApplication.getInstance().setFiltered_site_listing(sites_obj);

                    ((LandingActivity)getActivity()).replaceFragment( FragmentSites.newInstance("yes",""));

                }
                else{
                    Constants.showAlert(getActivity(),sites_obj.getResult().getMessage());
                }
            }
            else if (method.equalsIgnoreCase("getCity")) {
                city_prog_bar.setVisibility(View.INVISIBLE);
                req_cityObj = (CityRequest) response.body();
                if(!req_cityObj.getResult().getError())
//                    Log.d("Ddsad","");
                    setCity(req_cityObj);
                else {
                    selected_city="";
                }
                //Constants.showAlert(this,req_cityObj.getResult().getMessage());
//                    Toast.makeText(SignupActivity.this,"Server Response Error.. ",Toast.LENGTH_LONG).show();

            }

         }
        else{
            try {
                Constants.showAlert(getActivity(),response.code()+" "+response.message());
            } catch (Exception e) {
//                showAlertDialog("Alert","Server Response Error..");
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onFailure(String errorMessage) {
        if(pDialog!=null && pDialog.isShowing())
            pDialog.dismiss();

        Constants.showAlert(getActivity(),errorMessage);

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    private void setCity(CityRequest req_data)
    {
        if(al_city ==null)
            al_city =new ArrayList<>(req_data.getResult().getData().size());
        else
            al_city.clear();

        for(int i=0;i<req_data.getResult().getData().size();i++)
            al_city.add(req_data.getResult().getData().get(i).getName());

        ArrayAdapter adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1, al_city);

        edt_city.setAdapter(adapter);
        edt_city.setThreshold(3);
        edt_city.showDropDown();


        if(al_city.size()==0)
            selected_city="";
    }


    @Override
    public void onResume() {
        super.onResume();

        getActivity().findViewById(R.id.img_btm_home).setSelected(false);
        getActivity().findViewById(R.id.img_btm_charters).setSelected(false);
        getActivity().findViewById(R.id.img_btm_sites).setSelected(true);
        getActivity().findViewById(R.id.img_btm_boats).setSelected(false);
//        getActivity().findViewById(R.id.btm_meetups).setSelected(false);

        getActivity().findViewById(R.id.tv_btm_home).setSelected(false);
        getActivity().findViewById(R.id.tv_btm_charters).setSelected(false);
        getActivity().findViewById(R.id.tv_btm_sites).setSelected(true);
        getActivity().findViewById(R.id.tv_btm_boats).setSelected(false);
    }


    private void getCityListing(CharSequence s) {

        rest.makeHttpRequest(rest.retrofit.create(RestHandler.RestInterface.class).
                getCityList(s.toString()),"getCity");
        city_prog_bar.setVisibility(View.VISIBLE);

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {


        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.home_menu, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // TODO put your code here to respond to the button tap
                Toast.makeText(getActivity(), "Settings!", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showCityDialog()
    {

       /* final Dialog dialog = new Dialog(getActivity(),R.style.FullyTranslucent);
        Window window = dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_city);

        ProgressBar myProgressBar=(ProgressBar)dialog.findViewById(R.id.pro);

        getActivity().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        edt_city=(AutoCompleteTextView)dialog.findViewById(R.id.edt_city);
//        city_prog_bar=(ProgressBar)dialog.findViewById(R.id.city_prog_bar);

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

//        InputMethodManager imm = (InputMethodManager)   getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        edt_city.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.length()>2) {
//

                    if (edt_city.isPerformingCompletion()) {
                        // An item has been selected from the list. Ignore.
                        return;
                    }
                    else
                        getCityListing(s);

                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        edt_city.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {
                selected_city= parent.getItemAtPosition(pos).toString();
                selected_city_id=req_cityObj.getResult().getData().get(pos).getId()+"";

                btn_city.setText("City: "+selected_city);

                dialog.dismiss();
            }
        });

        dialog.show();*/

        final AlertDialog alertDialog;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(),R.style.FullscreenTheme);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_city, null);
        dialogBuilder.setView(dialogView);

        city_prog_bar=(ProgressBar)dialogView.findViewById(R.id.city_prog_bar);
        edt_city=(AutoCompleteTextView)dialogView.findViewById(R.id.edt_city);
        img_back=(ImageView)dialogView.findViewById(R.id.img_back);

        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        alertDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        edt_city.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.length()>2) {
//

                    if (edt_city.isPerformingCompletion()) {
                        return;
                    }
                    else
                        getCityListing(s);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        edt_city.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {
                selected_city= parent.getItemAtPosition(pos).toString();
                selected_city_id=req_cityObj.getResult().getData().get(pos).getId()+"";

                btn_city.setText("City: "+selected_city);

                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }


    public void showAlert()
    {

        final Dialog dialog = new Dialog(getActivity(),R.style.FullyTranslucent);
        Window window = dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_site_type);
        getActivity().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        ListView listView=(ListView) dialog.findViewById(R.id.list_site_type);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.custom_listview_item, android.R.id.text1, al_site_master_list);

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getActivity(),"You selected : "+al_site_master_list.get(position) ,Toast.LENGTH_SHORT).show();
                btn_site_type.setText("Site Type:\n"+al_site_master_list.get(position));
                selected_site_type_id=site_type_obj.getResult().getData().get(position).getId();
                dialog.dismiss();
            }
        });


        // Assign adapter to ListView
        listView.setAdapter(adapter);


//        dialog.setCancelable(false);
//        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void showDepthAlert()
    {

        final Dialog dialog = new Dialog(getActivity(),R.style.FullyTranslucent);
        Window window = dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_site_depth);
        getActivity().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        final TextView tv_from_val=(TextView)dialog.findViewById(R.id.tv_from_val);
        final TextView tv_to_val=(TextView)dialog.findViewById(R.id.tv_to_val);
        final Button btn_done=(Button)dialog.findViewById(R.id.btn_done);


        seekbar_from=(SeekBar)dialog.findViewById(R.id.seekbar_from);
        seekbar_to=(SeekBar)dialog.findViewById(R.id.seekbar_to);

        seekbar_from.setProgress(seekbar_from_value);
        seekbar_to.setProgress(seekbar_to_value);

        tv_from_val.setText(seekbar_from_value+"\'");
        tv_to_val.setText(seekbar_to_value+"\'");

        seekbar_from.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_from_val.setText(progress+"\'");
                seekbar_from_value=progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        seekbar_to.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_to_val.setText(progress+"\'");
                seekbar_to_value=progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(seekbar_to.getProgress() > seekbar_from.getProgress()) {
                    btn_site_depth.setText("Depth: \n" + tv_from_val.getText().toString() + " - " + tv_to_val.getText().toString());
                    dialog.dismiss();
                }
                else
                    Constants.showAlert(getActivity(),"From depth should be less than To depth");

            }
        });

//        dialog.setCancelable(false);
        dialog.show();
    }
}
