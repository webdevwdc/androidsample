package com.divetime.home;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.divetime.LandingActivity;
import com.divetime.R;
import com.divetime.utils.Constants;

public class FragmentLandingContent extends Fragment implements View.OnClickListener{

    Handler handler;
	String image_name="",content="",mUrl="";
    public FragmentLandingContent(){
        handler = new Handler();
    }
	ConstraintLayout root_container;
	ScrollView tv_scroll;

	public static FragmentLandingContent newInstance(String image_name,String content,String url) {
		FragmentLandingContent fragment = new FragmentLandingContent();
		Bundle args = new Bundle();
		args.putString("image", image_name);
        args.putString("content",content);
		args.putString("url",url);
		fragment.setArguments(args);
		return fragment;
	}
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_content, container, false);

        ImageView image=(ImageView) rootView.findViewById(R.id.image_pic);
        TextView tv_content=(TextView)rootView.findViewById(R.id.tv_content);
		root_container=(ConstraintLayout)rootView.findViewById(R.id.root_container);
		tv_scroll=(ScrollView)rootView.findViewById(R.id.tv_scroll);

        Constants.setImage(getActivity(),image,true,Constants.contents_thumb_image_url+image_name,null);
        tv_content.setText(content);

		image.setOnClickListener(this);
		tv_content.setOnClickListener(this);
		tv_scroll.setOnClickListener(this);


        return rootView;
    }

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
		}else{
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);  
		if (getArguments() != null) {
			image_name = getArguments().getString("image");
            content=getArguments().getString("content");
			mUrl=getArguments().getString("url");
		}
	}

	private void openCms()
	{
		((LandingActivity)getActivity()).replaceFragment(FragmentArticlesCms.newInstance(mUrl,"content"));
	}

	@Override
	public void onClick(View v) {

		switch (v.getId())
		{
			case R.id.image_pic:

				openCms();
				break;

			case R.id.root_container:

				openCms();
				break;

			case R.id.tv_scroll:

				openCms();
				break;

            case R.id.tv_content:

                openCms();
                break;
		}
	}
}