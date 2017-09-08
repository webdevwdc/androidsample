package com.divetime.charters;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.divetime.R;
import com.divetime.charters.adapters.FindChartersAdapters;
import com.divetime.charters.model.CharterListingDatum;
import com.divetime.utils.TrackApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentCharters.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentCharters#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentFindChartersByDate extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Toolbar toolbar;
    TextView toolbar_header,tv_site_name;
    LinearLayoutManager mLayoutManager;

    RecyclerView mRecycler;

    FindChartersAdapters adapterList;
    List<CharterListingDatum> al_charters_data =new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    public FragmentFindChartersByDate() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentHome.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentFindChartersByDate newInstance(String param1, String param2) {
        FragmentFindChartersByDate fragment = new FragmentFindChartersByDate();
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
        // Inflate the layout for this fragment

        View view= inflater.inflate(R.layout.fragment_find_charters_date, container, false);

        initView(view);

        return view;
    }

    private void initView(View view)
    {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar_header=(TextView)view.findViewById(R.id.toolbar_header);
        tv_site_name=(TextView)view.findViewById(R.id.tv_site_name);
        mRecycler=(RecyclerView)view.findViewById(R.id.mRecycler);

        if(((AppCompatActivity)getActivity()).getSupportActionBar()!=null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        toolbar_header.setText("Charters");



        al_charters_data.clear();
        al_charters_data.addAll(TrackApplication.getInstance().getCharter_listing().getResult().getData());

        adapterList = new FindChartersAdapters(al_charters_data, getActivity());
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.setNestedScrollingEnabled(false);
        mRecycler.setAdapter(adapterList);

        tv_site_name.setText("Dives for "+mParam1);

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

        getActivity().findViewById(R.id.img_btm_home).setSelected(false);
        getActivity().findViewById(R.id.img_btm_charters).setSelected(true);
        getActivity().findViewById(R.id.img_btm_sites).setSelected(false);
        getActivity().findViewById(R.id.img_btm_boats).setSelected(false);
//        getActivity().findViewById(R.id.btm_meetups).setSelected(false);

        getActivity().findViewById(R.id.tv_btm_home).setSelected(false);
        getActivity().findViewById(R.id.tv_btm_charters).setSelected(true);
        getActivity().findViewById(R.id.tv_btm_sites).setSelected(false);
        getActivity().findViewById(R.id.tv_btm_boats).setSelected(false);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TrackApplication.getInstance().setCharter_listing(null);
    }

    /*  @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.home_menu, menu);

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
    }*/
}
