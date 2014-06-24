package com.drcom.drpalm.Activitys.events;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Activitys.mOrganization.StateActivity;
import com.drcom.drpalm.DB.EventsDB;
import com.drcom.drpalm.Definition.ModuleNameDefine;
import com.drcom.drpalm.Definition.RequestCategoryID;
import com.drcom.drpalm.Tool.LanguageManagement;
import com.drcom.drpalm.Tool.SendpermisManagement;
import com.drcom.drpalm.Tool.CommonTranslate.DateFormatter;
import com.drcom.drpalm.Tool.Imagetool.BitmapCache;
import com.drcom.drpalm.Tool.LanguageManagement.CurrentLan;
import com.drcom.drpalm.View.controls.MyDatePicker;
import com.drcom.drpalm.View.controls.MyMothod;
import com.drcom.drpalm.View.events.NewEventActivityManagement;
import com.drcom.drpalm.View.login.LoginManager;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.Block;
import com.drcom.drpalm.objs.EventDetailsItem;
import com.drcom.drpalm.objs.EventDraftItem;
import com.drcom.drpalm.objs.EventsTypeLanguage;
import com.drcom.drpalm.objs.EventsTypeLanguageResultItem;
import com.drcom.drpalm.objs.OrganizationItem;
import com.drcom.drpalm4tianzhujiao.R;

public class NewEventActivity extends ModuleActivity {
	private static int STARTTIME = 0;
	private static int ENDTIME = 1;
	public static final int RESULT_CAPTURE_IMAGE = 1;
	public static final int RESULT_CHOOSE_IMAGE = 2;
	private static final int CompressQuality = 20; // 相片压缩质量 (0-100)100为质量最高
	private static final String IMGDIR = "/DrpalmPic/";
	public static String KEY_DRAFTITEM_ID = "KEY_DRAFTITEM_ID";
	public static String KEY_DETAILITEM = "KEY_DETAILITEM";
	private int SEND_SUCCEED = 1;
	private int SEND_FAILD = 0;
	public static String KEY_EDITTYPE = "KEY_EDITTYPE";	//编辑类型
	public static final int EDITTYPE_RETRANS = 0;	//转发	//对应选择框的ITEM位置
	public static final int EDITTYPE_REPLACE = 1;	//替换
	
	// 控件
	private EditText mEditTextTitle;
	private Spinner mSpinnerCategory;
	private Spinner mSpinnerSendtype;
	private Spinner mSpinnerTitletype;
	private TextView mTextViewRecevier;
//	private EditText mEditTextLocation;
	private LinearLayout mLinearLayoutime;
	private TextView mTextViewStarttime;
	private TextView mTextViewEndtime;
	private EditText mEditTextContent;
	private LinearLayout mAttcLinearLayout;
	private TextView mTextViewAttcnum;
	private ImageView mImageViewIseshow;
	private Button buttonSend;
	private Button buttonSave;
	// 变量
	private int mCategoryID = RequestCategoryID.EVENTS_NEWS_ID;
	private String mOristatus = EventDraftItem.ORISTATUS_TYPE_N;
	private String[] mCategoryStrs = new String[0];// ={"新闻","评语","课件","活动"};
	private String[] mSendTypeStrs = new String[0];// {"转发","重发"}
	private String[] mTitletypeStrs = new String[]{};	//标题类型
	private List<EventsTypeLanguage> mTitleTypeList;	//标题类型对像列表
	private ArrayAdapter<String> mCategoryAdapter;
	private ArrayAdapter<String> mSendTypeAdapter;
	private ArrayAdapter<String> mTitletypeAdapter;
	// private MainAdapter mGridViewAdapter;
	private LoginManager mLogininstance = LoginManager.getInstance(this);
	private boolean isRequestRelogin = true; // 登录SECCION超时要重登录?
	private EventDraftItem mEventDraftItem = new EventDraftItem();
	// private ArrayList<Attachment> mListAttachmentData = new
	// ArrayList<EventDraftItem.Attachment>();
	private boolean iseshow = false; // 是否加急显示
	private EventsDB mEventsDB;
	private SettingManager setInstance;
	private String mUsername = "";
	private int mEventDraftPkid = -1;
	private ArrayList<OrganizationItem> orgList = new ArrayList<OrganizationItem>();
	private NewEventActivityManagement mNewEventActivityManagement;
	private int mEdittype = EDITTYPE_RETRANS;
	
	public final static int SUCCESS = 0x1;
	public final static int FAIL = 0x2;
	
	public final static int DRAFT_TITLE = 0x1;
	public final static int NORMAL_TITLE =  0x2;
	
	private String mTitleId = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflator.inflate(R.layout.newevent_view, mLayout_body);

		setInstance = SettingManager.getSettingManager(this);
		mEventsDB = EventsDB.getInstance(this, GlobalVariables.gSchoolKey);
		mUsername = setInstance.getCurrentUserInfo().strUsrName;
		mNewEventActivityManagement = new NewEventActivityManagement(NewEventActivity.this);

//		mCategoryStrs = getResources().getStringArray(R.array.eventscategory);
		mSendTypeStrs = getResources().getStringArray(R.array.eventssendtype);
		// mChoose = getResources().getStringArray(R.array.choosephoto);
		//
		mEditTextTitle = (EditText) findViewById(R.id.newevent_title_editText);
//		mEditTextLocation = (EditText) findViewById(R.id.newevent_location_editText);
		mEditTextContent = (EditText) findViewById(R.id.newevent_content_editText);
		mTextViewAttcnum = (TextView) findViewById(R.id.newevent_attc_txtview);
		mTextViewRecevier = (TextView) findViewById(R.id.newevent_recevier_txtview);
		mImageViewIseshow = (ImageView) findViewById(R.id.newevent_impo_imgView);
		mImageViewIseshow.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (iseshow)
					iseshow = false;
				else
					iseshow = true;
				changeIsimpoImgview();
			}
		});

		//编辑类型
		mSpinnerSendtype = (Spinner) findViewById(R.id.newevent_sendtype_spinner);
		mSendTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mSendTypeStrs);
		// 设置下拉列表的风格
		mSendTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// 将adapter 添加到spinner中
		mSpinnerSendtype.setAdapter(mSendTypeAdapter);
		mSpinnerSendtype.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				switch (position) {
				case EDITTYPE_RETRANS:
					mOristatus = EventDraftItem.ORISTATUS_TYPE_N;
					break;
				case EDITTYPE_REPLACE:
					mOristatus = EventDraftItem.ORISTATUS_TYPE_C;
					break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		SendpermisManagement  sm = new SendpermisManagement();
		
		//配置文件与接口返回的可发类型进行匹配
		final List<Block> showBlock = new ArrayList<Block>();
		for (Block iterable_element : GlobalVariables.blocks) {
			if ("class".equals(iterable_element.getType())) {
				if (RequestCategoryID.EVENTS_NEWS_ID == iterable_element.getId()) {
					if (iterable_element.isVisible() && sm.isCanSend(String.valueOf(iterable_element.getId()))) {
						showBlock.add(iterable_element);
						continue;
					}
				}
				if (RequestCategoryID.EVENTS_ACTIVITY_ID == iterable_element.getId()) {
					if (iterable_element.isVisible() && sm.isCanSend(String.valueOf(iterable_element.getId()))) {
						showBlock.add(iterable_element);
						continue;
					}
				}
				if (RequestCategoryID.EVENTS_COMMENT_ID == iterable_element.getId()) {
					if (iterable_element.isVisible() && sm.isCanSend(String.valueOf(iterable_element.getId()))) {
						showBlock.add(iterable_element);
						continue;
					}
				}
				if (RequestCategoryID.EVENTS_ALBUM_ID == iterable_element.getId()) {
					if (iterable_element.isVisible() && sm.isCanSend(String.valueOf(iterable_element.getId()))) {
						showBlock.add(iterable_element);
						continue;
					}
				}
				if (RequestCategoryID.EVENTS_COURSEWARE_ID == iterable_element.getId()) {
					if (iterable_element.isVisible() && sm.isCanSend(String.valueOf(iterable_element.getId()))) {
						showBlock.add(iterable_element);
						continue;
					}
				}
			}
		}
		
		// <item>班级新闻</item>0 1001
		// <item>班级评语</item>1
		// <item>班级计划</item>
		// <item>班级活动</item>
		// <item>班级相册</item>
		mCategoryStrs = new String[showBlock.size()];
		//取对应类型名字
		for (int i = 0; i < showBlock.size(); i++) {
			mCategoryStrs[i] = ModuleNameDefine.getEventsModuleNamebyId(showBlock.get(i).getId());
//			switch (showBlock.get(i).getId()) {
//			case RequestCategoryID.EVENTS_NEWS_ID:
//				mCategoryStrs[i] = getResources().getStringArray(R.array.eventscategory)[0];
//				break;
//			case RequestCategoryID.EVENTS_COMMENT_ID:
//				mCategoryStrs[i] = getResources().getStringArray(R.array.eventscategory)[1];
//				break;
//			case RequestCategoryID.EVENTS_COURSEWARE_ID:
//				mCategoryStrs[i] = getResources().getStringArray(R.array.eventscategory)[2];
//				break;
//			case RequestCategoryID.EVENTS_ACTIVITY_ID:
//				mCategoryStrs[i] = getResources().getStringArray(R.array.eventscategory)[3];
//				break;
//			case RequestCategoryID.EVENTS_ALBUM_ID:
//				mCategoryStrs[i] = getResources().getStringArray(R.array.eventscategory)[4];
//				break;
//			default:
//				mCategoryStrs[i] = "";
//				break;
//			}
		}

		mSpinnerCategory = (Spinner) findViewById(R.id.newevent_category_spinner);

		mCategoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mCategoryStrs);
		// 设置下拉列表的风格
		mCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// 将adapter 添加到spinner中
		mSpinnerCategory.setAdapter(mCategoryAdapter);
		// 添加事件Spinner事件监听
		mSpinnerCategory.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mCategoryID = showBlock.get(position).getId();
				showItem(mCategoryID);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		
		//标题类型
		mSpinnerTitletype = (Spinner) findViewById(R.id.newevent_titletype_spinner);
		mTitletypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,mTitletypeStrs);
		mTitletypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
		mSpinnerTitletype.setAdapter(mTitletypeAdapter);
		
		// 选择联系人
		mTextViewRecevier.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(NewEventActivity.this, StateActivity.class);
				i.putParcelableArrayListExtra(StateActivity.ORG_LIST_KEY, orgList);
				startActivityForResult(i, 0);
			}
		});

		mLinearLayoutime = (LinearLayout) findViewById(R.id.newevent_time_Layout);
		// 开始时间
		mTextViewStarttime = (TextView) findViewById(R.id.newevent_starttime_txtview);
		mTextViewStarttime.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDateMessage(STARTTIME);
			}
		});

		// 结束时间
		mTextViewEndtime = (TextView) findViewById(R.id.newevent_endtime_txtview);
		mTextViewEndtime.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDateMessage(ENDTIME);
			}
		});

		// 附件
		mAttcLinearLayout = (LinearLayout) findViewById(R.id.newevent_attc_LLayout);
		mAttcLinearLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(NewEventActivity.this, NewEventAttc2Activity.class);
				// if(mListAttachmentData.size() > 0)
				// i.putExtra(NewEventAttc2Activity.ATTC_KEY,
				// mListAttachmentData);
				startActivityForResult(i, 0);
			}
		});

		// 接收传递参数
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			if (extras.containsKey(KEY_DRAFTITEM_ID)) {	//打开草稿
				// mEventDraftItem = extras.getParcelable(KEY_DRAFTITEM);
				//

				
				mEventDraftPkid = extras.getInt(KEY_DRAFTITEM_ID);
				mEventDraftItem = mEventsDB.getEventDraftItemById(mEventDraftPkid, mUsername);
				// 给界面赋值
				if (mEventDraftItem != null) {
					if(mEventDraftItem.old_eventid != null){
						mSpinnerSendtype.setVisibility(View.VISIBLE);
					}else{
						mSpinnerSendtype.setVisibility(View.GONE);
					}
					mTitleId = mEventDraftItem.type_titleId;

					
					mEditTextTitle.setText(mEventDraftItem.title);
//					mEditTextLocation.setText(mEventDraftItem.shortloc);
					
					
					mEditTextContent.setText(mEventDraftItem.body);
					if (mEventDraftItem.start != null) {
						mTextViewStarttime.setText(getResources().getString(R.string.start_time) + DateFormatter.getStringYYYYMMDDHHmm(mEventDraftItem.start));
					}
					if (mEventDraftItem.end != null) {
						mTextViewEndtime.setText(getResources().getString(R.string.end_time) + DateFormatter.getStringYYYYMMDDHHmm(mEventDraftItem.end));
					}
					mTextViewRecevier.setText(mEventDraftItem.owner);
					GlobalVariables.mListAttachmentData = mEventDraftItem.eventDraftAttachment;
					iseshow = mEventDraftItem.bifeshow;
//					mEventDraftItem.old_eventid = null;

					Log.i("zjj", "mEventDraftItem.type:" + mEventDraftItem.type);

					ChangeCategorySpinner(mEventDraftItem.type);
					ReflashAttcNum();
					
					//获取通告类型标题数据
					showProgressBar();
					mNewEventActivityManagement.getEventTypeLanguage(handler ,DRAFT_TITLE);
					
				}
			} else if (extras.containsKey(KEY_DETAILITEM)) {	//编辑通告
				EventDetailsItem detailitem = (EventDetailsItem) extras.getSerializable(KEY_DETAILITEM);

				mSpinnerSendtype.setVisibility(View.VISIBLE);
				mEditTextTitle.setText(detailitem.title);
//				mEditTextLocation.setText(detailitem.location);
				// mEditTextContent.setText(detailitem.body);
				mEditTextContent.setText(detailitem.cleanbody); // 显示无HTML标记内容
				if (detailitem.start != null) {
					mTextViewStarttime.setText(getResources().getString(R.string.start_time) + DateFormatter.getStringYYYYMMDDHHmm(detailitem.start));
				}
				if (detailitem.end != null) {
					mTextViewEndtime.setText(getResources().getString(R.string.end_time) + DateFormatter.getStringYYYYMMDDHHmm(detailitem.end));
				}
				mTextViewRecevier.setText(detailitem.owner);
				iseshow = detailitem.ifeshow;

				mEventDraftItem = mNewEventActivityManagement.initEditDraf(detailitem);
				
//				mEventDraftItem.old_eventid = detailitem.eventid;
//				mEventDraftItem.owner = detailitem.owner;
//				mEventDraftItem.ownerid = detailitem.ownerid;
//
//				for (int i = 0; i < detailitem.imgs.size(); i++) {
//					Attachment a = new Attachment();
//					a.data = detailitem.imgs.get(i).imgData;
//					a.description = detailitem.imgs.get(i).imgDescription;
//					a.fileId = detailitem.imgs.get(i).fileId;
//					a.fileType = detailitem.imgs.get(i).fileType;
//					a.item = String.valueOf(detailitem.imgs.get(i).attid);
//					a.type = "id";
//
//					GlobalVariables.mListAttachmentData.add(a);
//				}
				
				ChangeCategorySpinner(detailitem.type);
				
				//编辑类型
				if(extras.containsKey(KEY_EDITTYPE)){
					mEdittype = extras.getInt(KEY_EDITTYPE);
					mSpinnerSendtype.setSelection(mEdittype);
					mSpinnerSendtype.setEnabled(false);
				}
				
				ReflashAttcNum();
				
				//获取通告类型标题数据
				showProgressBar();
				mNewEventActivityManagement.getEventTypeLanguage(handler,NORMAL_TITLE);
			}
		} else {	//新建通告
			// 有效期3个月
			// Date d =
			// DateFormatter.getDateFromMilliSeconds(System.currentTimeMillis())
			// ;
			// mEventDraftItem.start = d;
			// mEventDraftItem.end = new Date(d.getYear(), d.getMonth() + 3,
			// d.getDay(), d.getHours(), d.getMinutes(), d.getSeconds());

			Calendar calStart = Calendar.getInstance();
			Calendar calEnd = Calendar.getInstance();
			calEnd.add(Calendar.MONTH, 3);
			mEventDraftItem.start = calStart.getTime();
			mEventDraftItem.end = calEnd.getTime();

			if (mEventDraftItem.start != null) {
				mTextViewStarttime.setText(getResources().getString(R.string.start_time) + DateFormatter.getStringYYYYMMDDHHmm(mEventDraftItem.start));
			}
			if (mEventDraftItem.end != null) {
				mTextViewEndtime.setText(getResources().getString(R.string.end_time) + DateFormatter.getStringYYYYMMDDHHmm(mEventDraftItem.end));
			}
			
			mEventDraftItem.pk_id = null;
			mEventDraftItem.old_eventid = null;
			
			//获取通告类型标题数据
			showProgressBar();
			mNewEventActivityManagement.getEventTypeLanguage(handler,NORMAL_TITLE);
		}

		// Titlebar返回按钮事件
		SetBackBtnOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Back();
			}
		});

		changeIsimpoImgview();
		initTitlebar();
		hideToolbar();
		
		//清掉图片缓存,腾出内存
		BitmapCache.getInstance().clearCache();
		//初始化附件数
		ReflashAttcNum();
		
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			
			if(msg.arg1==SUCCESS){
				refreshmSpinnerTitletype(msg.arg2);
			}
				
		}
	};
	
	public void refreshmSpinnerTitletype(int arg2){
		int index = 0;
		String type_null = NewEventActivity.this.getResources().getString(R.string.type_null);
		CurrentLan l = LanguageManagement.getSysLanguage(NewEventActivity.this);
		mTitleTypeList = new ArrayList<EventsTypeLanguage>();
		mTitleTypeList.add(new EventsTypeLanguage());
		mTitleTypeList.addAll(mNewEventActivityManagement.getTypeData(l,mCategoryID));
		if (mTitleTypeList.size() > 1) {
			mTitletypeStrs = new String[mTitleTypeList.size()];
			mTitletypeStrs[0] = type_null;
			
			for (int i = 1; i < mTitleTypeList.size(); i++) {
				if(l == CurrentLan.SIMPLIFIED_CHINESE){
					mTitletypeStrs[i] = mTitleTypeList.get(i).type_chinese;
				}else if (l==CurrentLan.COMPLES_CHINESE) {
					mTitletypeStrs[i] = mTitleTypeList.get(i).type_chinese_character;
				}else if (l==CurrentLan.ENGLISH) {
					mTitletypeStrs[i] = mTitleTypeList.get(i).type_english;
				}
				if(mTitleId.equals(mTitleTypeList.get(i).type_index)){
					index = i;
				}
			}
		}else {
			mTitletypeStrs = new String[1];
			mTitletypeStrs[0] = type_null;
		} 
		ArrayAdapter<String> titletypeAdapter = new ArrayAdapter<String>(NewEventActivity.this, android.R.layout.simple_spinner_item,mTitletypeStrs);
		titletypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinnerTitletype.setAdapter(titletypeAdapter);
		if(arg2==DRAFT_TITLE){
			mSpinnerTitletype.setSelection(index);
		}
		hideProgressBar();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		GlobalVariables.mListAttachmentData.clear();
		super.onDestroy();
	}

	/**
	 * Titlebar
	 */
	private void initTitlebar() {

		LayoutParams p = new LayoutParams(MyMothod.Dp2Px(this, GlobalVariables.btnWidth_Titlebar), MyMothod.Dp2Px(this, GlobalVariables.btnHeight_Titlebar));
		p.setMargins(0, 0, 5, 0);
		
		// 保存草稿
		buttonSave = new Button(this);
		buttonSave.setBackgroundResource(R.drawable.btn_title_blue_selector);
		buttonSave.setText(getString(R.string.saveimagefile));
		buttonSave.setTextAppearance(NewEventActivity.this, R.style.TitleBtnText);
		buttonSave.setLayoutParams(p);
		buttonSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("zjj", "保存草稿(mCategoryID):" + mCategoryID);

				// TODO Auto-generated method stub
				Save();
			}
		});

		setTitleRightButton(buttonSave);

		p = new LayoutParams(MyMothod.Dp2Px(this, GlobalVariables.btnWidth_Titlebar), MyMothod.Dp2Px(this, GlobalVariables.btnHeight_Titlebar));
		
		// 发送
		buttonSend = new Button(this);
		buttonSend.setBackgroundResource(R.drawable.btn_title_green_selector);
		buttonSend.setText(getString(R.string.send));
		buttonSend.setTextAppearance(NewEventActivity.this, R.style.TitleBtnText);
		buttonSend.setLayoutParams(p);
		buttonSend.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Send();
			}
		});
		setTitleRightButton(buttonSend);

		setTitleText(getString(R.string.editevent));
	}

	/**
	 * 发送通告
	 */
	private void Send() {
		Log.i("zjj", "发送(mCategoryID):" + mCategoryID + "," + mEventDraftItem.old_eventid + "," + mOristatus);

		// TODO Auto-generated method stub
		//无标题时
		String titlestr = mEditTextTitle.getText().toString().trim();
		if (titlestr.length() < 1) {
			Toast.makeText(NewEventActivity.this, getResources().getString(R.string.title_cant_be_null), Toast.LENGTH_LONG).show();
			return;
		}
		
		//相册无添加照片时
		if(mCategoryID == RequestCategoryID.EVENTS_ALBUM_ID && GlobalVariables.mListAttachmentData.size() == 0){
			Toast.makeText(NewEventActivity.this, getResources().getString(R.string.pic_cant_be_null), Toast.LENGTH_LONG).show();
			return;
		}
		
		//标题类型不为空时添加[]+标题
		if(!mTitleTypeList.get(mSpinnerTitletype.getSelectedItemPosition()).type_index.equals("")){
			mEventDraftItem.title = "[" + mTitletypeStrs[ mSpinnerTitletype.getSelectedItemPosition()] + "]" + titlestr;
		}else{
			mEventDraftItem.title = titlestr;
		}
		
//		mEventDraftItem.shortloc = mEditTextLocation.getText().toString().length() > 0 ? mEditTextLocation.getText().toString() : "　 ";
		mEventDraftItem.body = mEditTextContent.getText().toString().length() > 0 ? mEditTextContent.getText().toString() : "　 ";
		mEventDraftItem.type = mCategoryID;
		mEventDraftItem.oristatus = mOristatus;
		mEventDraftItem.eventDraftAttachment = GlobalVariables.mListAttachmentData;
		if (GlobalVariables.mListAttachmentData.size() > 0) {
			// 附件是已发过的话,要将内容赋为空
			for (int i = 0; i < mEventDraftItem.eventDraftAttachment.size(); i++) {
				if (mEventDraftItem.eventDraftAttachment.get(i).type != null) {
					if (mEventDraftItem.eventDraftAttachment.get(i).type.equals("id")) {
						mEventDraftItem.eventDraftAttachment.get(i).data = null;
						GlobalVariables.mListAttachmentData.get(i).data = null;
						Log.i("zjj", "转发的附件ID:" + mEventDraftItem.eventDraftAttachment.get(i).item);
					}
				}
			}
		}
		mEventDraftItem.bifeshow = iseshow;
		if (mEventDraftItem.owner.length() <= 0) {
			Toast.makeText(NewEventActivity.this, getResources().getString(R.string.receiver_cant_be_null), Toast.LENGTH_SHORT).show();
			return;
		}
		
		SendEventRequest();
	}

	/**
	 * 返回 保存草稿
	 */
	private void Save() {
		if (mEditTextTitle.getText().toString().equals("") && mTextViewRecevier.getText().toString().equals("") //&& mEditTextLocation.getText().toString().equals("")
				&& mEditTextContent.getText().toString().equals("") && GlobalVariables.mListAttachmentData.size() == 0) {
			finishDraw();
		} else {
			mNewEventActivityManagement.SaveDraf(mEventDraftItem.pk_id, 
					mEditTextTitle.getText().toString(), 
//					mEditTextLocation.getText().toString(), 
					"_",
					mEditTextContent.getText().toString(), 
					mCategoryID, mOristatus, mEventDraftItem.old_eventid, iseshow, 
					mEventDraftItem.start, mEventDraftItem.end, mEventDraftItem.owner, 
					mEventDraftItem.ownerid,
					mTitleTypeList.get(mSpinnerTitletype.getSelectedItemPosition()).type_index);
			
			Toast.makeText(NewEventActivity.this, R.string.savesucceed, Toast.LENGTH_LONG).show();
			
//			EventDraftItem ditem = new EventDraftItem();
//			ditem.pk_id = mEventDraftItem.pk_id;
//			ditem.title = mEditTextTitle.getText().toString();
//			ditem.shortloc = mEditTextLocation.getText().toString();
//			ditem.body = mEditTextContent.getText().toString();
//			ditem.type = mCategoryID;
//			ditem.oristatus = mOristatus;
//			ditem.eventDraftAttachment = GlobalVariables.mListAttachmentData;
//			
//			if(mEventDraftItem.old_eventid != null){	//如果是转发的通告保存为附件，则把在服务器的附件ID保存,下次从草稿中发送时同时发送
//				ditem.old_eventid = mEventDraftItem.old_eventid;
//				
//				if (GlobalVariables.mListAttachmentData.size() > 0) {
//					ditem.isAttc = true;
//
//					// 附件是已发通告的话,要将内容赋为空
//					for (int i = 0; i < ditem.eventDraftAttachment.size(); i++) {
//						if (ditem.eventDraftAttachment.get(i).type.equals("id")) {
//							//如果附件是在服务器端，就不保存到草稿附件库
//							ditem.eventDraftAttachment.get(i).data = null;
//							GlobalVariables.mListAttachmentData.get(i).data = null;
//						}
//					}
//				}
//			}else{
//				if (GlobalVariables.mListAttachmentData.size() > 0) {
//					ditem.isAttc = false;
//
//					// 附件是已发过的话,要将内容赋为空
//					for (int i = 0; i < ditem.eventDraftAttachment.size(); i++) {
//						if (ditem.eventDraftAttachment.get(i).type.equals("id")) {
//							//如果附件是在服务器端，就不保存到草稿附件库
//							ditem.eventDraftAttachment.get(i).data = null;
//							GlobalVariables.mListAttachmentData.get(i).data = null;
//						}else{
//							//否则，保存到草稿附件库
//							ditem.isAttc = true;
//						}
//					}
//				}
//			}
//
//
//			ditem.bifeshow = iseshow;
//			ditem.user = mUsername;
//			ditem.save = DateFormatter.getDateFromMilliSeconds(System.currentTimeMillis());
//			ditem.start = mEventDraftItem.start;
//			ditem.end = mEventDraftItem.end;
//			ditem.owner = mEventDraftItem.owner;
//			ditem.ownerid = mEventDraftItem.ownerid;
//
//			mEventsDB.saveEventsDraftItem(ditem);

			finishDraw();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == StateActivity.RESULT_CODE) {
			if (data != null) {
				Log.i("zjj", "**********************************");
				orgList = data.getParcelableArrayListExtra(StateActivity.RESULT_CODE_KEY);

				mEventDraftItem.owner = "";
				mEventDraftItem.ownerid = "";
				if (orgList.size() > 0) {
					for (int i = 0; i < orgList.size(); i++) {
						Log.i("zjj", "**" + orgList.get(i).orgID + "," + orgList.get(i).orgName);
						mEventDraftItem.owner += orgList.get(i).orgName + ",";
						mEventDraftItem.ownerid += orgList.get(i).orgID + ",";
					}
					// 删掉最后个","
					if (mEventDraftItem.owner.indexOf(",") > -1) {
						mEventDraftItem.owner = mEventDraftItem.owner.substring(0, mEventDraftItem.owner.lastIndexOf(","));
					}
					if (mEventDraftItem.ownerid.indexOf(",") > -1) {
						mEventDraftItem.ownerid = mEventDraftItem.ownerid.substring(0, mEventDraftItem.ownerid.lastIndexOf(","));
					}

					mTextViewRecevier.setText(mEventDraftItem.owner);
				} else {
					mTextViewRecevier.setText(R.string.addressee);
				}
			}
		} else {
			// GlobalVariables.mListAttachmentData =
			// data.getParcelableArrayListExtra(NewEventAttc2Activity.RESULT_CODE_KEY);
			ReflashAttcNum();
		}

	}

	/**
	 * 根据分类ID改变选中项
	 * 
	 * @param categoryid
	 */
	private void ChangeCategorySpinner(int categoryid) {
		switch (categoryid) {
		case RequestCategoryID.EVENTS_NEWS_ID:
			mSpinnerCategory.setSelection(0);
			break;
		case RequestCategoryID.EVENTS_COMMENT_ID:
			mSpinnerCategory.setSelection(1);
			break;
		case RequestCategoryID.EVENTS_COURSEWARE_ID:
			mSpinnerCategory.setSelection(2);
			break;
		case RequestCategoryID.EVENTS_ACTIVITY_ID:
			mSpinnerCategory.setSelection(3);
			break;
		case RequestCategoryID.EVENTS_ALBUM_ID:
			mSpinnerCategory.setSelection(4);
			break;
		default:
			break;
		}
	}

	/**
	 * 刷新附件数
	 */
	private void ReflashAttcNum() {
		mTextViewAttcnum.setText(getResources().getString(R.string.attachment) + ":" + GlobalVariables.mListAttachmentData.size());
	}

	/**
	 * 发送通告(请求)
	 */
	private void SendEventRequest() {
		showProgressBar();
		mNewEventActivityManagement.SendEventRequest(mUIHandler,mEventDraftItem);
		
//		RequestOperation mRequestOperation = RequestOperation.getInstance();
//		RequestOperationReloginCallback callback = new RequestOperationReloginCallback() {
//			@Override
//			public void onSuccess() { // 请求数据成功
//				Message message = Message.obtain();
//				message.arg1 = SEND_SUCCEED;
//				mUIHandler.sendMessage(message);
//				Log.i("zjj", "发送通告成功");
//			}
//
//			@Override
//			public void onCallbackError(String str) {
//				Message message = Message.obtain();
//				message.arg1 = SEND_FAILD;
//				message.obj = str;
//				mUIHandler.sendMessage(message);
//				Log.i("zjj", "发送通告失败" + str);
//			}
//
//			@Override
//			public void onReloginError() {
//				// TODO Auto-generated method stub
//				Log.i("zjj", "发送通告:自动重登录失败");
//			}
//
//			@Override
//			public void onReloginSuccess() {
//				// TODO Auto-generated method stub
//				Log.i("zjj", "发送通告:自动重登录成功");
//				if (isRequestRelogin) {
//					SendEventRequest(); // 自动登录成功后，再次请求数据
//					isRequestRelogin = false;
//				}
//			}
//		};
//		mRequestOperation.sendGetNeededInfo("SubmitEvent", new Object[] { mEventDraftItem, callback }, callback.getClass().getName());
	}

	/**
	 * 选择日期对话框
	 * 
	 * @param pTitle
	 */
	private void showDateMessage(final int type) {
		final AlertDialog dlg = new AlertDialog.Builder(NewEventActivity.this).create();
		dlg.show();
		Window window = dlg.getWindow();
		window.setContentView(R.layout.prizechoosedatequery_view);
		final MyDatePicker mDatePicker = (MyDatePicker) window.findViewById(R.id.datetime_picker);
		mDatePicker.setTimeVisibility(View.VISIBLE);
		Button ok = (Button) window.findViewById(R.id.datetime_ok);
		ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Time t = mDatePicker.getTime();

				Message msg = new Message();
				msg.arg1 = type;
				msg.obj = t;
				mChooseTimeHandler.sendMessage(msg);

				dlg.cancel();
			}
		});

		// 关闭alert对话框
		Button cancel = (Button) window.findViewById(R.id.datetime_cancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dlg.cancel();
			}
		});
	}

	/**
	 * 发送成功句柄
	 */
	private Handler mUIHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			hideProgressBar();
			if (msg.arg1 == SEND_SUCCEED) {
				if (mEventDraftPkid != -1) {
					mEventsDB.deleteEventsDraftItem(mEventDraftPkid, mUsername);
				}
				Toast.makeText(NewEventActivity.this, NewEventActivity.this.getResources().getString(R.string.send_success), Toast.LENGTH_LONG).show();

				finishDraw();
			} else if (msg.arg1 == SEND_FAILD) {
				String err = (String) msg.obj;
				if (err != null) {
					if (err.equals(GlobalVariables.gAppContext.getResources().getString(R.string.InvalidSessionKey))) {
						GlobalVariables.showInvalidSessionKeyMessage(NewEventActivity.this);
					} else {
						Toast.makeText(NewEventActivity.this, err, Toast.LENGTH_LONG).show();
					}
				}
			}
		}
	};

	private Handler mChooseTimeHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Time t = (Time) msg.obj;
			Date tempdate = new Date(t.year - 1900, (t.month), t.monthDay, t.hour, t.minute); // 年份从1900开始;

			if (msg.arg1 == STARTTIME) {
				// 结束时间要在开始时间之后
				if (mEventDraftItem.end.getTime() - tempdate.getTime() < 6000 * 60 * 24) {
					Toast.makeText(NewEventActivity.this, NewEventActivity.this.getResources().getString(R.string.set_date_warning), Toast.LENGTH_LONG).show();
					return;
				}

				mEventDraftItem.start = tempdate;
				mTextViewStarttime.setText(getResources().getString(R.string.start_time) + t.year + "-" + (t.month + 1) + "-" + t.monthDay + " " + t.hour + ":" + t.minute);
			} else if (msg.arg1 == ENDTIME) {
				if (tempdate.getTime() - mEventDraftItem.start.getTime() < 6000 * 60 * 24) {
					Toast.makeText(NewEventActivity.this, NewEventActivity.this.getResources().getString(R.string.set_date_warning2), Toast.LENGTH_LONG).show();
					return;
				}

				mEventDraftItem.end = tempdate;
				mTextViewEndtime.setText(getResources().getString(R.string.end_time) + t.year + "-" + (t.month + 1) + "-" + t.monthDay + " " + t.hour + ":" + t.minute);
			}
		}
	};

	/**
	 * 更改加急状态图标
	 */
	private void changeIsimpoImgview() {
		if (iseshow)
			mImageViewIseshow.setBackgroundResource(R.drawable.important_middle);
		else
			mImageViewIseshow.setBackgroundResource(R.drawable.notimportant_middle);
	}

	/**
	 * 截取返回按钮事件
	 */
	public boolean onKeyDown(int keyCoder, KeyEvent event) {
		if (keyCoder == KeyEvent.KEYCODE_BACK) {
			Back();
			return false;
		}

		return false;
	}

	/**
	 * 返回事件
	 */
	private void Back() {
		showLogoutMessage(getResources().getString(R.string.saveasdraft));
	}

	/**
	 * 保存草稿提示框(系统)
	 * 
	 * @param pMsg
	 */
	private void showLogoutMessage(String pMsg) {
		AlertDialog.Builder builder = new Builder(NewEventActivity.this);
		builder.setMessage(pMsg);
		builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Save();
				finishDraw();
			}
		});

		builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finishDraw();
			}
		});

		builder.create().show();
	}

	/**
	 * 根据所选分类ID确定要显示项
	 * 
	 * @param id
	 */
	private void showItem(int id) {
		switch (id) {
		case RequestCategoryID.EVENTS_NEWS_ID:
			mLinearLayoutime.setVisibility(View.GONE);
//			mAttcLinearLayout.setVisibility(View.VISIBLE);
//			mEditTextLocation.setVisibility(View.VISIBLE);
			break;
		case RequestCategoryID.EVENTS_COMMENT_ID:
			mLinearLayoutime.setVisibility(View.GONE);
//			mAttcLinearLayout.setVisibility(View.GONE);
//			mEditTextLocation.setVisibility(View.VISIBLE);
			mEditTextContent.setVisibility(View.VISIBLE);
			break;
		case RequestCategoryID.EVENTS_COURSEWARE_ID:
			mLinearLayoutime.setVisibility(View.GONE);
//			mAttcLinearLayout.setVisibility(View.VISIBLE);
			mEditTextContent.setVisibility(View.VISIBLE);
//			mEditTextLocation.setVisibility(View.VISIBLE);
			break;
		case RequestCategoryID.EVENTS_ACTIVITY_ID:
			mLinearLayoutime.setVisibility(View.GONE);
//			mEditTextLocation.setVisibility(View.VISIBLE);
//			mAttcLinearLayout.setVisibility(View.VISIBLE);
			mEditTextContent.setVisibility(View.VISIBLE);
			break;
		case RequestCategoryID.EVENTS_ALBUM_ID:
			mLinearLayoutime.setVisibility(View.GONE);
			mEditTextContent.setVisibility(View.GONE);
//			mEditTextLocation.setVisibility(View.VISIBLE);
//			mAttcLinearLayout.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
		//更改通告类型时刷新通告标题列表数据
		refreshmSpinnerTitletype(NORMAL_TITLE);
	}
	

}
