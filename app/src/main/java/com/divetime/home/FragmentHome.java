package com.divetime.home;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.divetime.LandingActivity;
import com.divetime.R;
import com.divetime.databinding.FragmentHomeBinding;
import com.divetime.home.adapter.LandingAdapter;
import com.divetime.home.adapter.ViewPagerAdapter;
import com.divetime.home.model.DashboardDatum;
import com.divetime.home.model.DashboardRequest;
import com.divetime.retrofit.RestHandler;
import com.divetime.retrofit.RetrofitListener;
import com.divetime.settings.FragmentSettings;
import com.divetime.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentHome.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHome extends Fragment implements RetrofitListener,View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ProgressBar progressBar2;

    // TODO: Rename and change types of parameters

    Toolbar toolbar;
    RecyclerView mRecycler;
    ImageView image_article;
    ViewPager  pager;
    View myView;
    TabLayout tabLayout;
    List<DashboardDatum> _data=new ArrayList<>();
    RestHandler rest;
    ProgressDialog pDialog;
    LandingAdapter mAdapter;
    PagerAdapter adapter;
    DashboardRequest req_obj;
    RelativeLayout constraintLwayout;
    private OnFragmentInteractionListener mListener;

    FragmentHomeBinding mBinding;

    public FragmentHome() {
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
    public static FragmentHome newInstance(String param1, String param2) {
        FragmentHome fragment = new FragmentHome();
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       /* View view= inflater.inflate(R.layout.fragment_home, container, false);
        init(view);
        return view;*/

        mBinding= DataBindingUtil.inflate(inflater,R.layout.fragment_home,container,false);

        View view=mBinding.getRoot();
        init(view);

        return view;
    }

    private void init(View view)
    {
        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);

        pager = (ViewPager) view.findViewById(R.id.photos_viewpager);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        image_article=(ImageView)view.findViewById(R.id.image_article);
        progressBar2=(ProgressBar)view.findViewById(R.id.progressBar2);

        constraintLwayout=(RelativeLayout)view.findViewById(R.id.constraintLwayout);

        myView=view.findViewById(R.id.view);
        mRecycler=(RecyclerView)view.findViewById(R.id.recyclerView);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        pDialog= Constants.getDialog(getActivity());

        mAdapter = new LandingAdapter(_data, getActivity(),false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.setNestedScrollingEnabled(false);
        mRecycler.setAdapter(mAdapter);

        image_article.setOnClickListener(this);

        rest=new RestHandler(getActivity(),this);
        getDashboardData();
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

    private void getDashboardData()
    {
        if (pDialog!=null)
            pDialog.show();

        rest.makeHttpRequest(rest.retrofit.create(RestHandler.RestInterface.class)
                .getDashboard(Constants.getUserId(getActivity())),"get_dashboard");
    }

    @Override
    public void onSuccess(Call call, Response response, String method) {

        if(pDialog!=null && pDialog.isShowing())
            pDialog.dismiss();

        if(response!=null && response.code()==200) {

            if (method.equalsIgnoreCase("get_dashboard")) {

                req_obj=(DashboardRequest)response.body();
                if(!req_obj.getResult().getError())
                {
                    setData(req_obj);
                }
                else{
                    Constants.showAlert(getActivity(),"Something went wrong..");
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

    private void setData(DashboardRequest req)
    {
        if(req.getResult().getData()!=null)
        {
            _data.clear();
            _data.addAll(req.getResult().getData());
            mAdapter.notifyDataSetChanged();
        }

//        if(req.getResult().getArticle()!=null)
//        Constants.setImage(getActivity(),image_article,false,Constants.articles_thumb_image_url+req.getResult().getArticle().getImage(),progressBar2);


        mBinding.setArticles(req.getResult().getArticle());

        adapter = new ViewPagerAdapter(getChildFragmentManager(),req);
        pager.setOffscreenPageLimit(0);
        pager.setAdapter(adapter);

        if(req.getResult().getContent()==null || req.getResult().getContent().size()==0)
            constraintLwayout.setVisibility(View.GONE);
        else if(req.getResult().getContent()!=null && req.getResult().getContent().size()<2)
            tabLayout.setVisibility(View.GONE);

        tabLayout.setupWithViewPager(pager, true);

        myView.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.image_article:

                if(req_obj!=null && req_obj.getResult().getArticle()!=null) {
//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(req_obj.getResult().getArticle().getUrl()));
//                    startActivity(browserIntent);

                    ((LandingActivity)getActivity()).replaceFragment(FragmentArticlesCms.newInstance(req_obj.getResult().getArticle().getUrl(),""));

                }

                break;
        }

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
        inflater.inflate(R.menu.home_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // TODO put your code here to respond to the button tap
//                Toast.makeText(getActivity(), "Settings!", Toast.LENGTH_SHORT).show();

                FragmentTransaction ft = getFragmentManager().beginTransaction();

                ft.replace(R.id.contentContainer, new FragmentSettings());
                ft.addToBackStack(new FragmentSettings().getClass().getName());
                ft.commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
