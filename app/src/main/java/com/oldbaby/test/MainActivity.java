package com.oldbaby.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.oldbaby.R;
import com.oldbaby.article.view.FragArticleDetail;
import com.oldbaby.common.bean.PageItem;
import com.oldbaby.common.view.zoompage.PinchZoomPage;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.btn);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        FragArticleDetail.invoke(this, "CyIKQcNLx2gEevf1");
    }
}
