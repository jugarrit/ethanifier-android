package io.supportkit.ethanifier;

import android.app.Application;

import io.supportkit.core.SupportKit;

public class EthanifierApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SupportKit.init(this, "3rsb75gjwwgojmc5x3mdal6au");
    }
}
