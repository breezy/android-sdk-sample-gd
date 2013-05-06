/**
 * File: FileTransferStatusListener.java
 * Created: 3/28/13
 * Author: Viacheslav Panasenko
 */
package com.breezy.good.android.sample;

import com.good.gd.icc.GDServiceError;

/**
 * FileTransferStatusListener
 * Sample file transfer status listener provides callbacks to the implementing activity.
 */
public interface FileTransferStatusListener
{
    void notifyError(GDServiceError error);

    void notifySuccess();
}
