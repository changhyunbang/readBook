package com.rooms.android.readbook.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.rooms.android.readbook.R;

public class CreateBookDialog extends Dialog {

    private EditText mEtText;
    private Button mPositiveButton;
    private Button mNegativeButton;

    private View.OnClickListener mPositiveListener;
    private View.OnClickListener mNegativeListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //다이얼로그 밖의 화면은 흐리게 만들어줌
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);

        setContentView(R.layout.dialog_createbook);

        //셋팅
        mEtText = (EditText)findViewById(R.id.ET_TITLE);
        mPositiveButton=(Button)findViewById(R.id.BTN_OK);
        mNegativeButton=(Button)findViewById(R.id.BTN_CANCEL);

        //클릭 리스너 셋팅 (클릭버튼이 동작하도록 만들어줌.)
        mPositiveButton.setOnClickListener(mPositiveListener);
        mNegativeButton.setOnClickListener(mNegativeListener);
    }

    //생성자 생성
    public CreateBookDialog(@NonNull Context context, View.OnClickListener positiveListener, View.OnClickListener negativeListener) {
        super(context);
        this.mPositiveListener = positiveListener;
        this.mNegativeListener = negativeListener;
    }
}
