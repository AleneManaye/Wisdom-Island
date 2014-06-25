/*
 * File         : DrCOMService.java
 * Date         : 2011-06-17
 * Author       : Keqin Su
 * Copyright    : City Hotspot Co., Ltd.
 * Description  : Android DrCOM Service handler
 */

package com.drcom.drpalm.Tool.DrCOMWS;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.wisdom.island.R;

public class DrCOMService extends Service {
	private DrCOMServiceListener m_DrCOMServicelistener = null;
	private boolean m_bConnected = false;

	private String m_strGWAddress = new String("");
	private String m_strUsername = new String("");
	private String m_strPassword = new String("");

	private Jni auth = new Jni();

	public class ThreadAuth extends Thread {
		private boolean m_bLogin = false;

		public void setType(boolean bLogin) {
			m_bLogin = bLogin;
		}

    	public void run() {
    		try {
	    		if (m_bLogin) {
	    			HttpLogin(m_strGWAddress, m_strUsername, m_strPassword);
	    		} else {
	    			HttpLogout();
	    		}
    		} catch (Exception e) {
    			Log.e("DrCOMService:ThreadAuth:run", e.toString());
    		}
        }
	}

	public class ThreadStatus extends Thread {
		private int iCount = 0;
		private int iTimer = 0;
		private boolean m_bRun = false;

		public void setRun(boolean bRun) {
			m_bRun = bRun;
		}

    	public void run() {
    		iTimer = 0;
    		iCount = 0;
    		try {
    			while (m_bRun) {
    				if (iTimer == 0) {
		    			int iStatus = 0;
		    			if (m_bRun) {
		    				iStatus = HttpLoginCheck();
		    			}
		    			if (iStatus == DrCOMDefine.iSUCCESS) {
		    				if (m_bRun) {
		    					HttpLogin();
		    				}
		    				iCount = 0;
		    			} else if (iStatus == DrCOMDefine.iCHECK) {
		    				iCount = (iCount % 4);
		    				if (iCount++ == 0) {
		    					if (m_bRun) {
		    						HttpGetStatus();
		    					}
		    				}
		    			}
    				}
    				iTimer++;
    				iTimer = (iTimer % 300);
	    			Thread.sleep(100);
    			}
    		} catch (Exception e) {
    			Log.e("DrCOMService:Thread:run", e.toString());
    		}
        }
	}
	private ThreadAuth threadAuth = null;
	private ThreadStatus threadStatus = null;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return m_mBinder;
	}

	private DrCOMServiceInterface.Stub m_mBinder = new DrCOMServiceInterface.Stub() {
		@Override
		public void addListener(DrCOMServiceListener listener)
				throws RemoteException {
			// TODO Auto-generated method stub
			if (listener != null) {
				m_DrCOMServicelistener = listener;
	        }
		}

		@Override
		public void rmtListener(DrCOMServiceListener listener)
				throws RemoteException {
			// TODO Auto-generated method stub
			if (m_DrCOMServicelistener != listener) {
				m_DrCOMServicelistener = listener;
	        }
		}

		@Override
		public boolean getStatus() throws RemoteException {
			// TODO Auto-generated method stub
			return m_bConnected;
		}

		@Override
		public void Login(String strGWAddress, String strUser, String strPass) throws RemoteException {
			// TODO Auto-generated method stub
			m_strGWAddress = strGWAddress;
			m_strUsername = strUser;
			m_strPassword = strPass;

			try {
				if (threadAuth != null) {
					threadAuth.join();
					threadAuth = null;
				}
				threadAuth = new ThreadAuth();
				threadAuth.setType(true);
				threadAuth.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e("DrCOMService:Login", e.toString());
			}
		}

		@Override
		public void Logout() throws RemoteException {
			// TODO Auto-generated method stub
			try {
				if (threadAuth != null) {
					threadAuth.join();
					threadAuth = null;
				}
				threadAuth = new ThreadAuth();
				threadAuth.setType(false);
				threadAuth.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e("DrCOMService:Logout", e.toString());
			}
		}

		public void getInfo(boolean bStart) throws RemoteException {
			try {
				if (threadStatus != null) {
					threadStatus.setRun(false);
					threadStatus.join();
					threadStatus = null;
				}
				if (m_bConnected && bStart) {
					threadStatus = new ThreadStatus();
					threadStatus.setRun(true);
					threadStatus.start();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e("DrCOMService:getInfo", e.toString());
			}
		}

		public String getGWAddress() {
			return m_strGWAddress;
		}
    };

	@Override
    public void onDestroy() {
        super.onDestroy();
    }

	public void HttpLogin(String strGWAddress, String strUser, String strPass) {
		int iRet = auth.httpLogin(m_strGWAddress, m_strUsername, m_strPassword);
		if (iRet == DrCOMDefine.iSUCCESS) {
			m_bConnected = auth.getLoginStatus();
			m_strGWAddress = auth.getGatewayAddress();
			CallbackResult("", DrCOMDefine.iLoginResult);
		} else {
			CallbackResult(getStringById(iRet), DrCOMDefine.iLoginResult);
		}
	}

	public void HttpLogout() {
		//stop get info first
		try {
			if (threadStatus != null) {
				threadStatus.setRun(false);
				threadStatus.join();
				threadStatus = null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("DrCOMService:HttpLogout", e.toString());
		}

		int iRet = auth.httpLogout();
		if (iRet == DrCOMDefine.iSUCCESS) {
			m_bConnected = auth.getLoginStatus();
			CallbackResult("", DrCOMDefine.iLogoutResult);
		} else {
			CallbackResult(getStringById(iRet), DrCOMDefine.iLogoutResult);
		}
	}

	public void HttpGetStatus() {
		boolean bRet = auth.httpStatus();
		if (bRet) {
			String strFlux = "", strTime = "";
			strFlux = auth.getFluxStatus();
			strTime = auth.getTimeStatus();
			if (strFlux.length() > 0) {
				CallbackResult(strFlux, DrCOMDefine.iFluxResult);
			}
			if (strTime.length() > 0) {
				CallbackResult(strTime, DrCOMDefine.iTimeResult);
			}
		}
	}

	private int HttpLoginCheck() {
		int iRet = auth.httpLoginCheck();
		if ((iRet != DrCOMDefine.iSUCCESS) && (iRet != DrCOMDefine.iCHECK)) {
			CallbackResult(getStringById(iRet), DrCOMDefine.iLoginResult);
		}
		return iRet;
	}

	private int HttpLogin() {
		int iRet = auth.httpLoginAuth();
		if (iRet != DrCOMDefine.iSUCCESS) {
			CallbackResult(getStringById(iRet), DrCOMDefine.iLoginResult);
		}
		return iRet;
	}

	/*
	 * iType should be in the below
	 * iLoginResult = 1;
	 * iLogoutResult = 2;
	 * iFluxResult = 3;
	 * iTimeResult = 4;
	 */
	private void CallbackResult(String strMsg, int iType) {
		if (m_DrCOMServicelistener != null) {
			try {
				switch (iType) {
					case DrCOMDefine.iLoginResult:
						m_DrCOMServicelistener.onLoginResult(strMsg);
						break;
					case DrCOMDefine.iLogoutResult:
						m_DrCOMServicelistener.onLogoutResult(strMsg);
						break;
					case DrCOMDefine.iFluxResult:
						m_DrCOMServicelistener.onRecvFlux(strMsg);
						break;
					case DrCOMDefine.iTimeResult:
						m_DrCOMServicelistener.onRecvTime(strMsg);
						break;
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				Log.e("DrCOMService:CallbackResult", e.toString());
			}
        }
	}

	private String getStringById(int iId) {
		String strRet = "";
		switch (iId) {
			case DrCOMDefine.Network_connection_interruption_check_the_network_configuration_please:
				strRet = this.getString(R.string.Network_connection_interruption_check_the_network_configuration_please);
				break;
			case DrCOMDefine.This_account_does_not_allow_use_NAT:
				strRet = this.getString(R.string.This_account_does_not_allow_use_NAT);
				break;
			case DrCOMDefine.Can_not_find_Dr_COM_web_protocol:
				strRet = this.getString(R.string.Can_not_find_Dr_COM_web_protocol);
				break;
			case DrCOMDefine.This_equipment_already_online_do_not_need_to_log_in:
				strRet = this.getString(R.string.This_equipment_already_online_do_not_need_to_log_in);
				break;
			case DrCOMDefine.The_IP_does_not_allow_login_base_Dr_COM_web_technology:
				strRet = this.getString(R.string.The_IP_does_not_allow_login_base_Dr_COM_web_technology);
				break;
			case DrCOMDefine.The_account_does_not_allow_login_base_Dr_COM_web_technology:
				strRet = this.getString(R.string.The_account_does_not_allow_login_base_Dr_COM_web_technology);
				break;
			case DrCOMDefine.The_account_does_not_allow_change_password:
				strRet = this.getString(R.string.The_account_does_not_allow_change_password);
				break;
			case DrCOMDefine.Invalid_account_or_password_please_login_again:
				strRet = this.getString(R.string.Invalid_account_or_password_please_login_again);
				break;
			case DrCOMDefine.This_account_is_tie_up_please_contact_network_administration_IP_MAC:
				strRet = String.format(this.getString(R.string.This_account_is_tie_up_please_contact_network_administration_IP_MAC), auth.getXip(), auth.getMac());
				break;
			case DrCOMDefine.This_account_use_on_appointed_address_only_IP:
				strRet = String.format(this.getString(R.string.This_account_use_on_appointed_address_only_IP), auth.getXip());
				break;
			case DrCOMDefine.This_account_charge_be_overspend_or_flux_over:
				strRet = this.getString(R.string.This_account_charge_be_overspend_or_flux_over);
				break;
			case DrCOMDefine.This_account_be_break_off:
				strRet = this.getString(R.string.This_account_be_break_off);
				break;
			case DrCOMDefine.System_buffer_full:
				strRet = this.getString(R.string.System_buffer_full);
				break;
			case DrCOMDefine.This_account_is_tie_up_can_not_amend:
				strRet = this.getString(R.string.This_account_is_tie_up_can_not_amend);
				break;
			case DrCOMDefine.The_new_and_the_confirm_password_are_differ_can_not_amend:
				strRet = this.getString(R.string.The_new_and_the_confirm_password_are_differ_can_not_amend);
				break;
			case DrCOMDefine.The_password_amend_successed:
				strRet = this.getString(R.string.The_password_amend_successed);
				break;
			case DrCOMDefine.This_account_use_on_appointed_address_only_MAC:
				strRet = String.format(this.getString(R.string.This_account_use_on_appointed_address_only_MAC), auth.getMac());
				break;
			case DrCOMDefine.Undefine_error:
				strRet = auth.getUnfineError();
				break;
		}
		return strRet;
	}
}
