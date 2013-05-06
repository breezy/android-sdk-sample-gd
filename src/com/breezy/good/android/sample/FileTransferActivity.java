/**
 * File: FileTransferActivity.java
 * Created: 3/28/13
 * Author: Viacheslav Panasenko
 */
package com.breezy.good.android.sample;

import com.good.gd.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.good.gd.GDAndroid;
import com.good.gd.GDAppEvent;
import com.good.gd.GDAppEventListener;
import com.good.gd.GDAppEventType;
import com.good.gd.file.FileOutputStream;
import com.good.gd.icc.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * FileTransferActivity
 * UI for file transfer client sample wich allows to send a file to ICC service and receive
 * response.
 */
public class FileTransferActivity extends Activity implements FileTransferStatusListener
{

    private static final String TAG = FileTransferActivity.class.getSimpleName();

    private static final String APP_ID = "com.breezy.good.android.sample.filetransferclient";
    private static final String APP_VERSION = "1.0.0";

    private static final String TRANSFER_FILE_APPLICATION = "com.breezy.good.android.FileTransferActivity";
    private static final String TRANSFER_FILE_SERVER_ID = "com.good.gdservice.transfer-file";
    private static final String TRANSFER_FILE_SERVER_VERSION = "1.0.0.0";
    private static final String TRANSFER_FILE_METHOD = "transferFile";
    private static final String TEST_FILE_NAME = "testpage.pdf";

    private GDAndroid mGood;
    private volatile boolean mIsAuthorized;
    private Button mBtnFileTransfer;
    private TextView mFileTransferStatus;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_transfer_screen);
        Log.d(TAG, "onCreate(): enter");

        mBtnFileTransfer = (Button) findViewById(R.id.btn_transfer_file);
        mFileTransferStatus = (TextView) findViewById(R.id.status);

        // Authorize app
        if (!mIsAuthorized)
        {
            Log.d(TAG, "onCreate(): initiating authorization");
            updateUI();
            mGood = GDAndroid.getInstance();
            mGood.authorize(APP_ID, APP_VERSION, new GDEventListener());
        }

        // Set the Service Listener to get requests from clients
        try
        {
            Log.d(TAG, "onCreate(): trying to connect to the service");
            GDServiceClient.setServiceClientListener(FileTransferClientListener.getInstance());
        } catch (GDServiceException e)
        {
            Log.d(TAG, "onCreate(): error registering for service updates", e);
            mFileTransferStatus.setText(R.string.error_registering_for_service_updates);
        }

        FileTransferClientListener.getInstance().setStatusNotificationListener(this);
    }

    /**
     * Called when transfer file is clicked.
     * @param v Caller view (button).
     */
    public void onTransferFileClicked(View v)
    {
        Log.d(TAG, "onTransferFileClicked(): entered");

        try
        {
            String attachments[] = new String[1];
            attachments[0] = TEST_FILE_NAME;
            String requestID = GDServiceClient.sendTo(TRANSFER_FILE_APPLICATION,
                    TRANSFER_FILE_SERVER_ID, TRANSFER_FILE_SERVER_VERSION, TRANSFER_FILE_METHOD,
                null, attachments, GDICCForegroundOptions.PreferPeerInForeground);
            Log.d(TAG, "requestID=" + requestID);
        } catch (GDServiceException e)
        {
            Log.d(TAG, "onTransferFileClicked(): exception requesting file transfer:", e);
            mFileTransferStatus.setText(R.string.error_send_to_service);
        }
    }

    /**
     * Saves test file from the assets to Good Secure Storage.
     */
    private void saveTestFile()
    {
        Log.d(TAG, "saveTestFile(): entered");
        try
        {
            com.good.gd.file.File secureStoredFile = new com.good.gd.file.File(TEST_FILE_NAME);
            if (secureStoredFile.exists())
            {
                secureStoredFile.delete();
            }

            if (!secureStoredFile.createNewFile())
            {
                throw new IOException("Can't create a file");
            }

            FileOutputStream fos = null;

            try
            {
                fos = new FileOutputStream(secureStoredFile);
                InputStream testFileStream = getAssets().open(TEST_FILE_NAME);
                fos.write(readStreamDataFully(testFileStream));
                fos.flush();
                Log.d(TAG, "onCreate(): saved file successfully");
            } finally
            {
                if (fos != null)
                {
                    fos.close();
                }
            }
        } catch (IOException e)
        {
            Log.d(TAG, "saveTestFile(): error saving test file to secure storage", e);
            mFileTransferStatus.setText(R.string.error_saving_file);
        }

    }

    /**
     * Reads data from the input stream and returns it as a byte array.
     * @param inputStream Input stream to read.
     * @return byte array read from the given stream.
     * @throws IOException
     */
    private byte[] readStreamDataFully(InputStream inputStream) throws IOException
    {
        // this dynamically extends to take the bytes you read
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1)
        {
            byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }

    /**
     * Sets button enabled status according to the authorization status.
     */
    private void updateUI()
    {
        mBtnFileTransfer.setEnabled(mIsAuthorized);
    }

    @Override
    public void notifyError(GDServiceError error)
    {
        GDServiceErrorCode serviceErrorCode = error.getErrorCode();
        String errorMessage = "";
        switch (serviceErrorCode)
        {
            case GDServicesErrorGeneral:
            {
                errorMessage += "GDServiceError: General Error";
            }
            break;

            case GDServicesErrorApplicationNotFound:
            {
                errorMessage += "GDServiceError: Application Not Found";
            }
            break;

            case GDServicesErrorServiceNotFound:
            {
                errorMessage += "GDServiceError: Service Not Found";
            }
            break;

            case GDServicesErrorServiceVersionNotFound:
            {
                errorMessage += "GDServiceError: Service Version Not Found";
            }
            break;

            case GDServicesErrorMethodNotFound:
            {
                errorMessage += "GDServiceError: Method Not Found";
            }
            break;

            case GDServicesErrorNotAllowed:
            {
                errorMessage += "GDServiceError: Not Allowed";
            }
            break;

            case GDServicesErrorInvalidParams:
            {
                errorMessage += "GDServiceError: Invalid Parameters";
            }
            break;

            case GDServicesErrorInUse:
            {
                errorMessage += "GDServiceError: In Use";
            }
            break;

            default:
            {
                errorMessage += "GDServiceError: Unhandled Error";
            }
            break;
        }

        mFileTransferStatus.setText(errorMessage);
    }

    @Override
    public void notifySuccess()
    {
        mFileTransferStatus.setText(R.string.success);
    }

    /**
     * GDEventListener
     * Listens to good events (used for authorization callback).
     */
    public class GDEventListener implements GDAppEventListener
    {

        /* (non-Javadoc)
         * @see com.good.gd.GDAppEventListener#onGDEvent(com.good.gd.GDAppEvent)
         */
        @Override
        public void onGDEvent(GDAppEvent anEvent)
        {
            Log.d(TAG, "onGDEvent: " + anEvent.getMessage());
            if (anEvent.getEventType() == GDAppEventType.GDAppEventAuthorized)
            {
                mIsAuthorized = true;

                // save test file to secure storage
                saveTestFile();
                updateUI();
            }
            else if (anEvent.getEventType() == GDAppEventType.GDAppEventNotAuthorized)
            {
                mIsAuthorized = false;
                updateUI();
            }
        }
    }

}
