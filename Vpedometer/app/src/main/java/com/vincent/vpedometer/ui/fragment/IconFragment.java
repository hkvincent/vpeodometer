package com.vincent.vpedometer.ui.fragment;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vincent.vpedometer.R;
import com.vincent.vpedometer.ui.activity.MainActivity;
import com.vincent.vpedometer.ui.view.MyLinearLayout;
import com.vincent.vpedometer.ui.view.SlideMenu;

import static com.vincent.vpedometer.R.id.imageView;

/**
 * Created by Administrator on 2018/2/11 22:50
 */
public class IconFragment extends BaseFrament {

    MainActivity main;
    public ImageView mImageView;
    public TextView mTextView;

    public IconFragment(Context context, SlideMenu slideMenu, MainActivity main) {
        this.mContext = context;
        this.mSlideMenu = slideMenu;
        this.main = main;
    }



    @Override
    public void init() {
        ininView();

    }

    private void ininView() {
        View mainView = View.inflate(mContext, R.layout.layout_photo, this.mSlideMenu);
        mMyView = (MyLinearLayout) mainView.findViewById(R.id.photo_layout);
        Button btnPhone = (Button) mainView.findViewById(R.id.btnPhone);
        mImageView = (ImageView) mainView.findViewById(imageView);
        mTextView = (TextView) mainView.findViewById(R.id.img_path);
        btnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.setImage();
            }
        });
        this.mSlideMenu.setMainView(mMyView);
    }

    public IconFragment getMe() {
        return this;
    }
}
