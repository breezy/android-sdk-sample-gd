/*
 *  Copyright (c) Visto Corporation dba Good Technology, 2012. All rights reserved.
 */
package com.breezy.good.android.sample;

import android.content.Intent;
import android.os.Bundle;
import com.good.gd.Activity;
import com.good.gd.GDAndroid;

/**
 * IccReceivingActivity - all applications which may act as an Authentication Delegate
 * or publish AppKinetics services must implement this activity. It used to facilitate
 * communication between applications. 
 */
public class IccReceivingActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

    	// GDAndroid call to check if on loading this activity it should launch
    	// the Launch Activity to authorize.
    	if (GDAndroid.getInstance().IccReceiverShouldAuthorize()) {
            Intent i = new Intent(this, FileTransferActivity.class);
            this.startActivity(i);
    	}

    	finish();
    }
}
