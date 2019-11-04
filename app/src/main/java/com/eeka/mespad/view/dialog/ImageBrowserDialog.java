package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.Toast;

import com.bm.library.PhotoView;
import com.eeka.mespad.R;
import com.squareup.picasso.Picasso;

public class ImageBrowserDialog extends BaseDialog {

    private String mUrl;
    private int mResId;

    public ImageBrowserDialog(@NonNull Context context, String url) {
        super(context);
        mUrl = url;
        init();
    }

    public ImageBrowserDialog(@NonNull Context context, int resId) {
        super(context);
        mResId = resId;
        init();
    }

    @Override
    protected void init() {
        super.init();
        mView = LayoutInflater.from(mContext).inflate(R.layout.dlg_imagebrowser, null);
        setContentView(mView);
        setCanceledOnTouchOutside(true);

        PhotoView imageView = mView.findViewById(R.id.imageView);
        imageView.enable();
        if (!isEmpty(mUrl)) {
            Picasso.with(mContext).load(mUrl).into(imageView);
        } else if (mResId != 0) {
            Picasso.with(mContext).load(mResId).into(imageView);
        } else {
            Toast.makeText(mContext, "图片地址为空", Toast.LENGTH_SHORT).show();
            dismiss();
        }
    }

}
