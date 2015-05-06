package com.apigee.appservices.android_template;

import android.app.Application;

import com.apigee.sdk.ApigeeClient;

/**
 * Created by Eric on 5/6/2015.
 */
public class YourApplication extends Application
{
    private ApigeeClient apigeeClient;

    public YourApplication()
    {
        this.apigeeClient = null;
    }

    public ApigeeClient getApigeeClient()
    {
        return this.apigeeClient;
    }

    public void setApigeeClient(ApigeeClient apigeeClient)
    {
        this.apigeeClient = apigeeClient;
    }
}