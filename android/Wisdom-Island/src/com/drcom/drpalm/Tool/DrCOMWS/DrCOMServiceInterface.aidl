/*
 * File         : DrCOMServiceInterface.aidl
 * Date         : 2011-06-20
 * Author       : Keqin Su
 * Copyright    : City Hotspot Co., Ltd.
 * Description  : Android DrCOM Service interface
 */

package com.drcom.drpalm.Tool.DrCOMWS;

import com.drcom.drpalm.Tool.DrCOMWS.DrCOMServiceListener;

interface DrCOMServiceInterface {
    void addListener(DrCOMServiceListener listener);
    void rmtListener(DrCOMServiceListener listener);
    boolean getStatus();
    void Login(String strGWAddress, String strUser, String strPass);
    void Logout();
    void getInfo(boolean bStart);
    String getGWAddress();
}