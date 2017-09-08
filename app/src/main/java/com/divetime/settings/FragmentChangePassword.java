package com.divetime.settings;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.divetime.R;
import com.divetime.retrofit.RestHandler;
import com.divetime.retrofit.RetrofitListener;
import com.divetime.settings.model.ChangePasswordRequest;
import com.divetime.utils.Constants;

import retrofit2.Call;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentChangePassword#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentChangePassword extends Fragment implements View.OnClickListener,RetrofitListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RestHandler rest;
    LinearLayout btn_submit;
    ProgressDialog pDialog;

    Toolbar toolbar;
    TextView toolbar_header;
    EditText edt_old_pwd,edt_new_pwd,edt_cnf_new_pwd;
    private OnFragmentInteractionListener mListener;

    public FragmentChangePassword() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static FragmentChangePassword newInstance(String param1, String param2) {
        FragmentChangePassword fragment = new FragmentChangePassword();
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

        View view= inflater.inflate(R.layout.fragment_change_password, container, false);
        init(view);

        return view;
    }

    private void init(View view)
    {
        toolbar_header= (TextView)view.findViewById(R.id.toolbar_header);
        toolbar_header.setText("Change Password");
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        btn_submit=(LinearLayout)view.findViewById(R.id.btn_submit);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        edt_cnf_new_pwd=(EditText)view.findViewById(R.id.edt_cnf_new_pwd) ;
        edt_new_pwd=(EditText)view.findViewById(R.id.edt_new_pwd) ;
        edt_old_pwd=(EditText)view.findViewById(R.id.edt_old_pwd) ;

        pDialog=Constants.getDialog(getActivity());

        rest=new RestHandler(getActivity(),this);

//        setSupportActionBar(toolbar);
        btn_submit.setOnClickListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
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
            case R.id.btn_submit:

                if(validate())
                    submitRequest();

                break;
        }
    }

    @Override
    public void onSuccess(Call call, Response response, String method) {

        if(pDialog!=null && pDialog.isShowing())
            pDialog.dismiss();

        if(response!=null && response.code()==200){

            if(method.equalsIgnoreCase("change_pwd"))
            {

                ChangePasswordRequest change_pwd_obj=(ChangePasswordRequest)response.body();

                if(!change_pwd_obj.getResult().getError())
                {
                    Toast.makeText(getActivity(), change_pwd_obj.getResult().getMessage(), Toast.LENGTH_LONG).show();
                    getActivity().onBackPressed();
                }
                else{
                    Constants.showAlert(getActivity(),change_pwd_obj.getResult().getMessage());
                }
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

    private void submitRequest()
    {
        if(getActivity()!=null) {
            final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
        if(pDialog!=null)
            pDialog.show();

        rest.makeHttpRequest(rest.retrofit.create(RestHandler.RestInterface.class).changePassword(Constants.getUserId(getActivity()),
                edt_old_pwd.getText().toString().trim(),edt_new_pwd.getText().toString().trim()),"change_pwd");
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


    @Override
    public void onResume() {
        super.onResume();

        getActivity().findViewById(R.id.img_btm_home).setSelected(true);
        getActivity().findViewById(R.id.img_btm_charters).setSelected(false);
        getActivity().findViewById(R.id.img_btm_sites).setSelected(false);
        getActivity().findViewById(R.id.img_btm_boats).setSelected(false);
//        getActivity().findViewById(R.id.btm_meetups).setSelected(false);

        getActivity().findViewById(R.id.tv_btm_home).setSelected(true);
        getActivity().findViewById(R.id.tv_btm_charters).setSelected(false);
        getActivity().findViewById(R.id.tv_btm_sites).setSelected(false);
        getActivity().findViewById(R.id.tv_btm_boats).setSelected(false);
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

    private boolean validate()
    {

        if(edt_old_pwd.getText().toString().trim().length()==0)
        {
            Constants.showAlert(getActivity(),"Please enter old password.");
            return false;
        }
        else if(edt_new_pwd.getText().toString().trim().length()==0)
        {
            Constants.showAlert(getActivity(),"Please enter new password");
            return false;
        }
        else if(edt_cnf_new_pwd.getText().toString().trim().length()==0)
        {
            Constants.showAlert(getActivity(),"Please enter confirm password");
            return false;
        }
        else if(edt_new_pwd.getText().toString().length()<5)
        {
            Constants.showAlert(getActivity(),"Password must be of atleast 5 characters.");
            return false;
        }
        else if(!(edt_new_pwd.getText().toString().trim().equals(edt_cnf_new_pwd.getText().toString().trim())))
        {
            Constants.showAlert(getActivity(),"Passwords not match.");
            return false;
        }

        return true;
    }

}
