package com.example.devposapp2;

import android.view.View;
import android.widget.ProgressBar;

public class SpinnerVigi {
    public ProgressBar spinner;

    public void setVigi(View view, ProgressBar spinner2) {
        this.spinner = spinner2;
                spinner.setVisibility(view.VISIBLE);
    }
    public void reSetVigi(View view,ProgressBar spinner2) {
        this.spinner = spinner2;
        spinner.setVisibility(view.GONE);
    }
}
