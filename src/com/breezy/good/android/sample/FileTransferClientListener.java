/**
 * File: FileTransferClientListener.java
 * Created: 3/28/13
 * Author: Viacheslav Panasenko
 */
package com.breezy.good.android.sample;

import android.util.Log;
import com.good.gd.icc.*;

/**
 * FileTransferClientListener
 * Responsible for handling response messages from the Good Service Provider (TransferFile).
 */
public class FileTransferClientListener implements GDServiceClientListener
{

    private static final String TAG = FileTransferClientListener.class.getSimpleName();
    private static FileTransferClientListener _instance;
    private FileTransferStatusListener mObserver;

    public synchronized static FileTransferClientListener getInstance()
    {
        if (_instance == null)
        {
            _instance = new FileTransferClientListener();
        }

        return _instance;
    }

    public void setStatusNotificationListener(FileTransferStatusListener activity)
    {
        mObserver = activity;
    }

    @Override
    public void onReceiveMessage(String s, Object o, String[] strings, String s2)
    {
        if (o == null)
        {
            // According to documentation 'null' is a correct response
            mObserver.notifySuccess();
        }
        else if (o instanceof GDServiceError)
        {
            mObserver.notifyError((GDServiceError) o);
        }

    }

    @Override
    public void onMessageSent(String s, String s2, String[] strings)
    {
        Log.d(TAG, "+ onMessageSent");
        // Print file message sent, co
    }

    private boolean consumeFrontRequestService(String serviceID, String application, String method,
            String version, String requestID)
    {
        if (serviceID.equals(GDService.GDFrontRequestService) && version.equals("1.0.0.0"))
        {
            if (method.equals(GDService.GDFrontRequestMethod))
            {
                // bring to front
                try
                {
                    GDService.bringToFront(application);
                } catch (GDServiceException e)
                {
                    Log.d(TAG, "- bringToFront - Error: " + e.getMessage());
                }

            }
            else
            {
                GDServiceError serviceError = new GDServiceError(
                        GDServiceErrorCode.GDServicesErrorMethodNotFound);
                //Send service reply
                try
                {
                    GDService.replyTo(application, serviceError,
                            GDICCForegroundOptions.NoForegroundPreference, null, requestID);
                } catch (GDServiceException e)
                {
                    Log.d(TAG, "- replyTo - Error: " + e.getMessage());
                }
            }
            return true;
        }

        return false;
    }
}
