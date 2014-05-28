
package com.facebook.scrumptious;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ConfirmCurrentLocation extends Activity {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.confirmcurrentlocation, container, false);
        return view;
    }

  /*  public void setSkipLoginCallback(SkipLoginCallback callback) {
        skipLoginCallback = callback;
    }*/
}
