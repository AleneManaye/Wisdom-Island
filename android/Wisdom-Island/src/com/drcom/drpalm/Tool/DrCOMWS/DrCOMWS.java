/*
 * File         : DrCOMWS.java
 * Date         : 2011-06-17
 * Author       : Keqin Su
 * Copyright    : City Hotspot Co., Ltd.
 * Description  : Android DrCOMWS
 */

package com.drcom.drpalm.Tool.DrCOMWS;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm4tianzhujiao.R;


public class DrCOMWS extends ModuleActivity {

	// add by Kingsley
//	private LinearLayout m_paret_ntLayout;
//	private LinearLayout m_user_layout;
//	private LinearLayout m_useredit_layout;
//	private LinearLayout m_pass_layout;
//	private LinearLayout m_passedit_layout;
	private LinearLayout m_self_Layout;
	private ListView m_lvLoginDetail;
	private TextView m_txUsername;
	private TextView m_txPassword;
	private EditText m_edUsername;
	private EditText m_edPassword;
	private TextView m_txTime;
	private TextView m_txMin;
	private TextView m_txFlux;
	private TextView m_txMByte;
	private CheckBox m_cbKeepPassword;
	private CheckBox m_cbSign;
	private Button m_btOK;
	private AlertDialog m_msgBox;
	private AlertDialog m_msgBoxWarm;
	private ProgressDialog m_pDialog;

	private String m_strUsername = new String("");
	private String m_strPassword = new String("");
	private String m_strMsg = new String("");
	private String m_strGWAddress = new String("");

	private DrCOMServiceInterface m_Service = null;
	private Intent intentDrCOMService = new Intent();
	private Intent intentProcess = new Intent();
	private SharedPreferences m_spfConf = null;

	private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
        	String strMsg = msg.getData().getString(Integer.toString(msg.what));
            switch (msg.what) {
	            case DrCOMDefine.iLoginResult:
	            	onResultMsg(true, strMsg);
	                break;
	            case DrCOMDefine.iLogoutResult:
	            	onResultMsg(false, strMsg);
	            	break;
	            case DrCOMDefine.iTimeResult:
	            	onResultInfo(true, strMsg);
	            	break;
	            case DrCOMDefine.iFluxResult:
	            	onResultInfo(false, strMsg);
	            	break;
            }
        };
    };

	private ServiceConnection m_Connection = new ServiceConnection() {
        public void onServiceDisconnected(ComponentName name) {
            m_Service = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            m_Service = DrCOMServiceInterface.Stub.asInterface(service);
            Log.d("DrCOMWS:onServiceConnected","ok");
            try {
            	m_Service.addListener(m_Listener);
            	if (m_cbSign.isChecked()) {
                	onLogin();
                }
            } catch (RemoteException e) {
            	Log.e("DrCOMWS:ServiceConnection:onServiceConnected", e.toString());
            }
        }
    };

    private DrCOMServiceListener.Stub m_Listener = new DrCOMServiceListener.Stub() {
    	public void onLoginResult(String strError) {
    		sendMsg(DrCOMDefine.iLoginResult, strError);
    	}

    	public void onLogoutResult(String strError) {
    		sendMsg(DrCOMDefine.iLogoutResult, strError);
    	}

    	public void onRecvFlux(String strFlux) {
    		sendMsg(DrCOMDefine.iFluxResult, strFlux);
    	}

    	public void onRecvTime(String strTime) {
    		sendMsg(DrCOMDefine.iTimeResult, strTime);
    	}

    	private boolean sendMsg(int iMsgType, String Msg) {
    		Message msg = new Message();
    		msg.what = iMsgType;
    		msg.getData().putString(Integer.toString(iMsgType), Msg);
    		return mHandler.sendMessage(msg);
    	}
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	//for debug
    	//Jni auth = new Jni();
    	//String a = auth.getData();
    	//end debug
        super.onCreate(savedInstanceState);
        initalizeLoadingDialog();
        initAllViews();
        initViewListener();
        setConnectStatus(false);
        initUI();     
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	m_msgBoxWarm.setMessage(this.getString(R.string.Do_you_want_to_exist));
	    	m_msgBoxWarm.show();
	    	return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}

    public void onLogin() {
    	m_strMsg = "";
    	String strMessage = "";
    	m_strUsername = m_edUsername.getText().toString();
    	m_strPassword = m_edPassword.getText().toString();

    	if (m_strUsername.length() < 1) {
    		strMessage = this.getString(R.string.Please_enter_your_username);
    	} else if (m_strPassword.length() < 1) {
    		strMessage = this.getString(R.string.Please_enter_your_password);
    	}

    	if (strMessage.length() > 1) {
    		showMsg(strMessage);
    	} else {
    		//showAnimation(true);    		
    		try {
    			if (!m_Service.getStatus()) {
    				m_pDialog.setMessage(GlobalVariables.gAppResource.getString(R.string.logining));
    				m_pDialog.show();
    				m_Service.Login(m_strGWAddress, m_strUsername, m_strPassword);
    			} else {
    				m_pDialog.setMessage(GlobalVariables.gAppResource.getString(R.string.logouting));
    				m_pDialog.show();
    				m_Service.Logout();
    			}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				Log.e("DrCOMWS:onLogin", e.toString());
			}
    	}
    }

    public void showMsg(String str) {
    	m_msgBox.setMessage(str);
    	m_msgBox.show();
    }

    public void onDestroy() {
    	try {
        	m_Service.rmtListener(m_Listener);
        } catch (RemoteException e) {
        	Log.e("DrCOMWS:onDestroy", e.toString());
        }
        stopService(intentDrCOMService);
        GlobalVariables.gAppContext.unbindService(m_Connection);
    	super.onDestroy();
    }

    private void showAnimation(boolean bShow) {
    	try {
	    	if (bShow) {
	    		startActivityForResult(intentProcess, 0);
	    	} else {
	    		finishActivity(0);
	    	}
    	} catch (Exception e) {
    		Log.e("DrCOMWS:showAnimation", e.toString());
    	}
    }

    /*
	 * when bFlag is ture means Login resault, otherwise means Logout
	 */
    public void onResultMsg(boolean bFlag, String strMsg) {
		if (strMsg.length() > 0) {
			if (!m_strMsg.equals(strMsg)) {
				m_strMsg = strMsg;
				showMsg(strMsg);
			}
		} else {
			try {
				if (bFlag) {
					m_Service.getInfo(true);
					setConnectStatus(true);
					putPreferences(DrCOMDefine.DrCOMUsername, m_strUsername);
					putPreferences(DrCOMDefine.DrCOMPass, m_strPassword);
					if (m_Service.getStatus()) {
						m_strGWAddress = m_Service.getGWAddress();
						putPreferences(DrCOMDefine.DrCOMUrl, m_strGWAddress);
					}
				} else {
					m_Service.getInfo(false);
					setConnectStatus(false);
					if (getPreferences(DrCOMDefine.DrCOMRememberPass).equals(DrCOMDefine.DrCOMYNO)) {
						putPreferences(DrCOMDefine.DrCOMUrl, "");
					}
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				Log.e("DrCOMWS:onResultMsg", e.toString());
			}
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//showAnimation(false);
		m_pDialog.cancel();
	}

    /*
	 * when bFlag is ture means time info, otherwise means flux info
	 */
    public void onResultInfo(boolean bFlag, String strInfo) {
    	if (bFlag) {
    		m_txTime.setText(strInfo);
    	} else {
    		m_txFlux.setText(strInfo);
    	}
    	// add by Kingsley
    	updateStatusListView();
    }

    // add by Kingsley
    private void initalizeLoadingDialog(){
		if(null == m_pDialog)
		{
			m_pDialog = new ProgressDialog(this);
		}
		m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		m_pDialog.setMessage(GlobalVariables.gAppResource.getString(R.string.logining));
		m_pDialog.setIndeterminate(false);
		m_pDialog.setCancelable(true);
	}
    private void updateStatusListView(){
    	// status ListView
		ArrayList<HashMap<String, Object>> lvItem = new ArrayList<HashMap<String,Object>>();
		HashMap<String,Object> mapName = new HashMap<String, Object>();
		mapName.put("Title", getString(R.string.Username));
		mapName.put("Detail", m_strUsername);
		//mapName.put("Last", getString(R.string.MByte));
		lvItem.add(mapName);

		HashMap<String,Object> map = new HashMap<String, Object>();
		map.put("Title", getString(R.string.Used_flux));
		map.put("Detail", m_txFlux.getText());
		map.put("Last", getString(R.string.MByte));
		lvItem.add(map);

		HashMap<String,Object> map2 = new HashMap<String, Object>();
		map2.put("Title", getString(R.string.Used_time));
		map2.put("Detail", m_txTime.getText());
		map2.put("Last", getString(R.string.Min));
		lvItem.add(map2);

		SimpleAdapter lvItemAdapter = new SimpleAdapter(this,lvItem,R.layout.drcomws_list_item,
				new String[]{"Title","Detail","Last"},
				new int[]{R.id.tx_Title,R.id.tx_Detail,R.id.tx_Last});
		m_lvLoginDetail.setAdapter(lvItemAdapter);
    }
    private class StatusListAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 3;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}
    	
    }

    private void setConnectStatus(boolean bConnected) {
    	if (bConnected) {
//    		LinearLayout t_paret_ntLayout = (LinearLayout)this.findViewById(R.id.linearLayoutEditGoup);
//    		m_nameLayout = new LinearLayout(this);
//    		LinearLayout t_fLayout = new LinearLayout(this);
//    		LinearLayout t_sLayout = new LinearLayout(this);
//    		m_txTipsName = new TextView(this);
//    		m_txName = new TextView(this);
//
//    		t_fLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
//    				ViewGroup.LayoutParams.FILL_PARENT,1));
//    		t_fLayout.setPadding(5, 0, 5, 5);
//    		m_txTipsName.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
//    				ViewGroup.LayoutParams.FILL_PARENT));
//    		//m_txTipsName.setPadding(5, 0, 5, 0);
//    		m_txTipsName.setGravity(Gravity.LEFT);
//    		m_txTipsName.setText(R.string.Username);
//    		t_fLayout.addView(m_txTipsName);
//
//    		t_sLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
//    				ViewGroup.LayoutParams.FILL_PARENT,1));
//    		t_sLayout.setPadding(5, 0, 5, 5);
//    		m_txName.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
//    				ViewGroup.LayoutParams.FILL_PARENT));
//    		//m_txName.setPadding(5, 0, 5, 0);
//    		m_txName.setGravity(Gravity.RIGHT);
//    		m_txName.setText(m_strUsername);
//    		m_txName.setTextColor(Color.BLUE);
//    		t_sLayout.addView(m_txName);
//
//    		m_nameLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
//    				ViewGroup.LayoutParams.FILL_PARENT,1));
//    		m_nameLayout.setPadding(20, 0, 20, 0);
//    		m_nameLayout.setOrientation(LinearLayout.HORIZONTAL);
//    		//m_nameLayout.setBackgroundResource(R.drawable.itemback);
//    		m_nameLayout.addView(t_fLayout);
//    		m_nameLayout.addView(t_sLayout);
//    		t_paret_ntLayout.addView(m_nameLayout);
//    		t_paret_ntLayout.setBackgroundResource(R.drawable.itemback);

    		//m_userLayout.setBackgroundResource(R.drawable.itemback);
    		//m_passLayout.setBackgroundResource(R.drawable.itemback);

//    		m_paret_ntLayout.removeView(m_user_layout);
//    		m_paret_ntLayout.removeView(m_useredit_layout);
//    		m_paret_ntLayout.removeView(m_pass_layout);
//    		m_paret_ntLayout.removeView(m_passedit_layout);

    		//m_txUsername.setText(R.string.Used_time);
	        //m_txPassword.setText(R.string.Used_flux);
    		m_txUsername.setVisibility(View.GONE);
    		m_txPassword.setVisibility(View.GONE);
	        m_edUsername.setVisibility(View.GONE);
	        m_edPassword.setVisibility(View.GONE);

    		m_txTime.setVisibility(View.VISIBLE);
	        m_txMin.setVisibility(View.VISIBLE);
	        m_txFlux.setVisibility(View.VISIBLE);
	        m_txMByte.setVisibility(View.VISIBLE);

	        m_btOK.setText(R.string.logout);
	        //m_btOK.setTextColor(this.getResources().getColor(R.color.light_red));

	        m_cbKeepPassword.setEnabled(false);
	        m_cbKeepPassword.setVisibility(View.GONE);
	        m_cbSign.setEnabled(false);
	        m_cbSign.setVisibility(View.GONE);

	        // add by Kingsley
	        updateStatusListView();
	        m_lvLoginDetail.setVisibility(View.VISIBLE);    		
    	} else { 	

    		//m_txUsername.setText(R.string.Username);
	        //m_txPassword.setText(R.string.Password);
    		m_txUsername.setVisibility(View.VISIBLE);
    		m_txPassword.setVisibility(View.VISIBLE);
	        m_edUsername.setVisibility(View.VISIBLE);
	        m_edPassword.setVisibility(View.VISIBLE);

	    	m_txTime.setVisibility(View.GONE);
	        m_txMin.setVisibility(View.GONE);
	        m_txFlux.setVisibility(View.GONE);
	        m_txMByte.setVisibility(View.GONE);

	        m_btOK.setText(R.string.login);
	        //m_btOK.setTextColor(this.getResources().getColor(R.color.light_green));

	        m_cbKeepPassword.setEnabled(true);
	        m_cbSign.setEnabled(true);
	        m_cbKeepPassword.setVisibility(View.VISIBLE);
	        m_cbSign.setVisibility(View.VISIBLE);
	        
	        m_lvLoginDetail.setVisibility(View.GONE);
    	}
    }
    private void initAllViews(){
    	/**
    	mTitleBar.mBackgroundView.setBackgroundDrawable(GlobalVariables.gResourceManagement.getDrawableByFileName(
    			GlobalVariables.gAppContext.getString(R.string.drcomws_titlebar_image)));
    	mModuleLayout.setBackgroundDrawable(GlobalVariables.gResourceManagement.getDrawableByFileName(
    			GlobalVariables.gAppContext.getString(R.string.drcomws_background)));
    	mModuleLayout.setGravity(Gravity.CENTER_VERTICAL);
    	
    	m_self_Layout = new LinearLayout(this);
    	m_self_Layout.setLayoutParams(new LinearLayout.LayoutParams(
    			ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
    	m_self_Layout.setOrientation(LinearLayout.VERTICAL);
    	m_self_Layout.setGravity(Gravity.CENTER_HORIZONTAL);
    	m_self_Layout.setPadding(20, 0, 20, 0);  
    	mModuleLayout.addView(m_self_Layout);
    	
    	
        m_lvLoginDetail = new CornerListView(this);
        m_lvLoginDetail.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
        m_lvLoginDetail.setCacheColorHint(Color.TRANSPARENT);
        m_lvLoginDetail.setDivider(null);
        RelativeLayout.LayoutParams vCenter = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    	vCenter.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE); 
    	vCenter.setMargins(0, 10, 0, 10);
        m_self_Layout.addView(m_lvLoginDetail,vCenter);
        
        m_txUsername = new TextView(this);
        m_txUsername.setLayoutParams(new LinearLayout.LayoutParams(
        		LinearLayout.LayoutParams.FILL_PARENT,
        		LinearLayout.LayoutParams.WRAP_CONTENT)
        );  
        m_txUsername.setText(R.string.Username);       
        m_txUsername.setTextSize(TypedValue.COMPLEX_UNIT_PX,
        		getResources().getDimension(R.dimen.TextDefaultSize));
        m_txUsername.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        m_txUsername.setTextColor(Color.BLACK); 
        m_self_Layout.addView(m_txUsername);
        
        m_edUsername = new EditText(this);
        m_edUsername.setLayoutParams(new LinearLayout.LayoutParams(
        		LinearLayout.LayoutParams.FILL_PARENT,
        		LinearLayout.LayoutParams.WRAP_CONTENT)
        ); 
        //m_edUsername.setHint(R.string.username);
        m_edUsername.setTextSize(TypedValue.COMPLEX_UNIT_PX,
        		getResources().getDimension(R.dimen.TextDefaultSize));
        m_edUsername.setSingleLine();
        m_self_Layout.addView(m_edUsername);
        
        m_txPassword = new TextView(this);
        m_txPassword.setLayoutParams(new LinearLayout.LayoutParams(
        		LinearLayout.LayoutParams.FILL_PARENT,
        		LinearLayout.LayoutParams.WRAP_CONTENT)
        );    
        m_txPassword.setText(R.string.Password);
        m_txPassword.setTextSize(TypedValue.COMPLEX_UNIT_PX,
        		getResources().getDimension(R.dimen.TextDefaultSize));
        m_txPassword.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        m_txPassword.setTextColor(Color.BLACK); 
        m_self_Layout.addView(m_txPassword);
        
        m_edPassword = new EditText(this);
        m_edPassword.setLayoutParams(new LinearLayout.LayoutParams(
        		LinearLayout.LayoutParams.FILL_PARENT,
        		LinearLayout.LayoutParams.WRAP_CONTENT)
        ); 
        //m_edPassword.setHint(R.string.password);
        
        m_edPassword.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        m_edPassword.setTransformationMethod(PasswordTransformationMethod.getInstance()); 
        m_edPassword.setTextSize(TypedValue.COMPLEX_UNIT_PX,
        		getResources().getDimension(R.dimen.TextDefaultSize));       
        m_self_Layout.addView(m_edPassword);        
        
        RelativeLayout.LayoutParams lpLyCenter = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		lpLyCenter.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE); 
		lpLyCenter.setMargins(0,10,0,10);
        m_cbKeepPassword = new CheckBox(this);
        m_cbKeepPassword.setLayoutParams(new LinearLayout.LayoutParams(
        		LinearLayout.LayoutParams.FILL_PARENT,
        		LinearLayout.LayoutParams.WRAP_CONTENT)
        ); 
        m_cbKeepPassword.setText(R.string.Remember_my_password);
        float a = getResources().getDimension(R.dimen.TextDefaultSize);
        m_cbKeepPassword.setTextSize(TypedValue.COMPLEX_UNIT_PX,
        		getResources().getDimension(R.dimen.TextDefaultSize));
        m_cbKeepPassword.setTextColor(Color.BLACK);        
        m_self_Layout.addView(m_cbKeepPassword, lpLyCenter);
        
        m_cbSign = new CheckBox(this);
      
        m_btOK = new Button(this);
        m_btOK.setLayoutParams( new LinearLayout.LayoutParams(
        		LinearLayout.LayoutParams.FILL_PARENT,
        		LinearLayout.LayoutParams.WRAP_CONTENT)); 
       
        m_btOK.setText(R.string.btnlogin);
        m_btOK.setTextSize(TypedValue.COMPLEX_UNIT_PX,
        		getResources().getDimension(R.dimen.TextDefaultSize));
        m_btOK.setWidth((int)TypeValueTranslate.getRawSize(TypedValue.COMPLEX_UNIT_DIP,100));
        m_self_Layout.addView(m_btOK, lpLyCenter);      
        
        m_txTime = new TextView(this);
        m_txTime.setLayoutParams(new LinearLayout.LayoutParams(
        		LinearLayout.LayoutParams.FILL_PARENT,
        		LinearLayout.LayoutParams.WRAP_CONTENT)
        ); 
        m_txTime.setTextColor(Color.BLUE);
    	m_txMin = new TextView(this);
    	m_txMin.setLayoutParams(new LinearLayout.LayoutParams(
        		LinearLayout.LayoutParams.FILL_PARENT,
        		LinearLayout.LayoutParams.WRAP_CONTENT)
        ); 
    	m_txMin.setTextColor(Color.BLUE);
    	
    	m_txFlux = new TextView(this);
    	m_txFlux.setLayoutParams(new LinearLayout.LayoutParams(
        		LinearLayout.LayoutParams.FILL_PARENT,
        		LinearLayout.LayoutParams.WRAP_CONTENT)
        ); 
    	m_txFlux.setTextSize(TypedValue.COMPLEX_UNIT_PX,
    			getResources().getDimension(R.dimen.TextDefaultSize));
    	m_txFlux.setTextColor(Color.BLUE);
    	
    	m_txMByte = new TextView(this);
    	m_txMByte.setLayoutParams(new LinearLayout.LayoutParams(
        		LinearLayout.LayoutParams.FILL_PARENT,
        		LinearLayout.LayoutParams.WRAP_CONTENT)
        );   
    	m_txMByte.setTextSize(TypedValue.COMPLEX_UNIT_PX,
    			getResources().getDimension(R.dimen.TextDefaultSize));
    	m_txMByte.setTextColor(Color.BLUE);
    	
    	m_msgBox = new AlertDialog.Builder(this).create();
        m_msgBox.setTitle(this.getString(R.string.Tips));

        m_msgBoxWarm = new AlertDialog.Builder(this).create();
        m_msgBoxWarm.setTitle(this.getString(R.string.Tips));
        
        */
    }
    private void initViewListener(){
    	intentDrCOMService.setClass(this, DrCOMService.class);
        intentProcess.setClass(this, Process.class);

        m_btOK.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLogin();
			}
        });

        m_cbKeepPassword.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (m_cbKeepPassword.isChecked()) {
					putPreferences(DrCOMDefine.DrCOMRememberPass, DrCOMDefine.DrCOMYES);
				} else {
					m_cbSign.setChecked(false);
					putPreferences(DrCOMDefine.DrCOMSignIn, DrCOMDefine.DrCOMYNO);
					putPreferences(DrCOMDefine.DrCOMRememberPass, DrCOMDefine.DrCOMYNO);
				}
			}
        });

        m_cbSign.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (m_cbSign.isChecked()) {
					m_cbKeepPassword.setChecked(true);
					putPreferences(DrCOMDefine.DrCOMRememberPass, DrCOMDefine.DrCOMYES);
					putPreferences(DrCOMDefine.DrCOMSignIn, DrCOMDefine.DrCOMYES);
				} else {
					putPreferences(DrCOMDefine.DrCOMSignIn, DrCOMDefine.DrCOMYNO);
				}
			}
        });

        m_msgBox.setButton(this.getString(R.string.OK), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});

        m_msgBoxWarm.setButton(this.getString(R.string.OK), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if(m_Service != null){
		            try {
		    			if (m_Service.getStatus()) {
		    				m_Service.Logout();
		    			}
		            } catch (RemoteException e) {
		            	Log.e("DrCOMWS:onCreate:m_msgBoxWarm", e.toString());
		            }
		        }
				finishDraw();
			}
		});

        m_msgBoxWarm.setButton2(this.getString(R.string.Cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});

        try {
        	GlobalVariables.gAppContext.bindService(intentDrCOMService, m_Connection, Context.BIND_AUTO_CREATE);
        	Log.d("DrCOMWS:bindService","bind");
        }catch (SecurityException e) {
        	Log.e("DrCOMWS:onCreate", e.toString());
		}
    }

    private void initUI() {
    	m_spfConf = getSharedPreferences(DrCOMDefine.DrCOMClientWS, 0);
        m_edUsername.setText(getPreferences(DrCOMDefine.DrCOMUsername));
        m_strGWAddress = getPreferences(DrCOMDefine.DrCOMUrl);
        if (getPreferences(DrCOMDefine.DrCOMSignIn).equals(DrCOMDefine.DrCOMYES)) {
        	m_cbSign.setChecked(true);
        	m_cbKeepPassword.setChecked(true);
        } else {
        	m_cbSign.setChecked(false);
        }
        if (getPreferences(DrCOMDefine.DrCOMRememberPass).equals(DrCOMDefine.DrCOMYES)) {
        	m_cbKeepPassword.setChecked(true);
        	m_edPassword.setText(getPreferences(DrCOMDefine.DrCOMPass));
        } else {
        	m_cbKeepPassword.setChecked(false);
        }
    }

    private String getPreferences(String strKey) {
    	String strValue = m_spfConf.getString(strKey, "");
    	if (strValue.length() > 0) {
    		try {
				strValue = DrAES128.decrypt(DrCOMDefine.DrCOMClientWS, strValue);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	return strValue;
    }

    private boolean putPreferences(String strKey, String strValue) {
    	String strValueTmp = "";
    	try {
			strValueTmp = DrAES128.encrypt(DrCOMDefine.DrCOMClientWS, strValue);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	SharedPreferences.Editor editor = m_spfConf.edit();
    	editor.putString(strKey, strValueTmp);
    	return editor.commit();
    }

    static {
        System.loadLibrary("DrCOMWS");
    }
}