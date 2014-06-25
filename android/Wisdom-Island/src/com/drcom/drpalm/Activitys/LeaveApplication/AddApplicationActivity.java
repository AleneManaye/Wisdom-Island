package com.drcom.drpalm.Activitys.LeaveApplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.LinearLayout.LayoutParams;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Activitys.events.NewEventAttc2Activity;
import com.drcom.drpalm.Activitys.events.NewEventAttcCFAdapter;
import com.drcom.drpalm.Activitys.mOrganization.StateActivity;
import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Tool.FileManagement;
import com.drcom.drpalm.View.LeaveApplication.AddLeaveApplicationManager;
import com.drcom.drpalm.View.controls.MyDatePicker;
import com.drcom.drpalm.View.controls.MyMothod;
import com.drcom.drpalm.objs.EventDraftItem;
import com.drcom.drpalm.objs.LeaveApplicationMainItem;
import com.wisdom.island.R;
import com.drcom.ui.View.controls.MulitImageActivity.ImagesScanActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class AddApplicationActivity extends ModuleActivity implements View.OnClickListener, View.OnTouchListener {
    private Context mContext;
    private Button btnSend;
    private TextView vacationType;
    private TextView tv_beginTime;
    private TextView tv_endTime;
    private TextView tv_receivers;
    private TextView tv_attr_num;
    private EditText etName;
    private EditText etDetail;
    private LinearLayout etReceiver;
    private RelativeLayout attachmentLayout;
    private RelativeLayout rl_beginTime;
    private RelativeLayout rl_endTime;

    private SlidingDrawer slidingDrawer;

    private AddLeaveApplicationManager addLeaveApplicationManager;
    private LeaveApplicationMainItem leaveApplicationMainItem = new LeaveApplicationMainItem();

    //告假类型
    public String[] type;
    //临时存放开始时间
    private Calendar startCalendar = Calendar.getInstance();
    //临时存放结束时间
    private Calendar endCalendar;

    public String TAG = "addApplicationActivity";
    public final static int START_TIME = 0;
    public final static int END_TIME = 1;

    public final static int REQUEST_RECEIVER = 2;
    public final static int REQUEST_OPEN_ATTR = 3;

    public final static String KEY_RECEIVER_BUNDLE = "receivers_bundle";
    public final static String KEY_RECEIVER_ID = "receivers_id";
    public final static String KEY_RECEIVER_LIST = "receivers";

    public final static String KEY_TYPE_SICK = "sick";
    public final static String KEY_TYPE_PRIVATE = "private";

    private String mReceivers_id = "";

    private NewEventAttcCFAdapter mCFAdapter;

    private EventDraftItem.Attachment attachmentdata;

    @Override
    public boolean onKeyDown(int keyCoder, KeyEvent event) {
        switch (keyCoder) {
            case KeyEvent.KEYCODE_BACK:
                //当抽屉打开的时候点击返回键 关闭抽屉
                if (slidingDrawer.isOpened()) {
                    slidingDrawer.animateClose();
                    return true;
                }

                break;
        }
        return super.onKeyDown(keyCoder, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.leave_application_add, mLayout_body);
        addLeaveApplicationManager = new AddLeaveApplicationManager(this, handler);
        mContext = this;
        type = getResources().getStringArray(R.array.leave_types);

        findByIds();
        initUI();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (GlobalVariables.mListAttachmentData.size() != 0) {
            GlobalVariables.mListAttachmentData.clear();
        }
    }

    public void findByIds() {
        vacationType = (TextView) findViewById(R.id.vacation_type);
        tv_receivers = (TextView) findViewById(R.id.tv_receiver);
        vacationType.requestFocus();
        etName = (EditText) findViewById(R.id.et_name);
        etDetail = (EditText) findViewById(R.id.et_detail);
        etReceiver = (LinearLayout) findViewById(R.id.vacation_receiver);
        attachmentLayout = (RelativeLayout) findViewById(R.id.vacation_attachment);
        rl_beginTime = (RelativeLayout) findViewById(R.id.begin_time);
        rl_endTime = (RelativeLayout) findViewById(R.id.end_time);
        tv_beginTime = (TextView) rl_beginTime.findViewById(R.id.tv_begin_time);
        tv_endTime = (TextView) rl_endTime.findViewById(R.id.tv_end_time);
        slidingDrawer = (SlidingDrawer) findViewById(R.id.module_drawer);
        tv_attr_num = (TextView) attachmentLayout.findViewById(R.id.attr_num);

    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(AddApplicationActivity.this, R.string.send_fail, Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    Toast.makeText(AddApplicationActivity.this, R.string.send_success, Toast.LENGTH_LONG).show();
                    AddApplicationActivity.this.finish();
                    break;
            }
        }
    };

    /**
     * 初始化抽屉
     */
    public void initSlideDrawer() {

        LinearLayout linearLayout = (LinearLayout) slidingDrawer.findViewById(R.id.module_content);

        for (int i = 0; i < type.length + 1; i++) {

            Log.i(TAG, "i-->" + i);
            if (i == type.length) {
                linearLayout.addView(createDrawerButton(getResources().getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        slidingDrawer.animateClose();
                    }
                }));
                break;
            } else {
                final int finalI = i;
                Log.i(TAG, "finalI-->" + finalI);
                linearLayout.addView(createDrawerButton(type[i], new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        slidingDrawer.animateClose();
                        vacationType.setText(type[finalI]);
                    }
                }));
            }
        }
    }

    /**
     * 关闭软键盘
     */
    private void closeSoftInputMethod() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etName.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(etDetail.getWindowToken(), 0);
    }

    /**
     * 创建抽屉的按钮
     *
     * @param btnText  按钮文字
     * @param listener 按钮监听器
     * @return
     */
    private Button createDrawerButton(String btnText, View.OnClickListener listener) {
        Button button = new Button(this);
        button.setText(btnText);
        button.setTextColor(Color.WHITE);
        button.setTextSize(17);
        button.setBackgroundResource(R.drawable.msgbox_btn_red_selector);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, MyMothod.Dp2Px(this, 45));
        params.setMargins(MyMothod.Dp2Px(this, 20), MyMothod.Dp2Px(this, 20), MyMothod.Dp2Px(this, 20), 0);
        button.setLayoutParams(params);
        button.setOnClickListener(listener);
        return button;
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        hideToolbar();
        initSlideDrawer();
        LayoutParams p = new LayoutParams(MyMothod.Dp2Px(this, GlobalVariables.btnWidth_Titlebar), MyMothod.Dp2Px(this, GlobalVariables.btnHeight_Titlebar));
        // 发送
        btnSend = new Button(this);
        btnSend.setBackgroundResource(R.drawable.btn_title_green_selector);
        btnSend.setText(getString(R.string.send));
        btnSend.setTextAppearance(mContext, R.style.TitleBtnText);
        btnSend.setLayoutParams(p);
        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                send();

            }


        });
        setTitleRightButton(btnSend);

        setTitleText(getString(R.string.leave_application));

        tv_beginTime.setText(addLeaveApplicationManager.getTimeStrByCalendar(startCalendar));

        vacationType.setText(type[0]);

        mCFAdapter = new NewEventAttcCFAdapter(this,
                GlobalVariables.mListAttachmentData);

        vacationType.setOnClickListener(this);
        etReceiver.setOnClickListener(this);
        rl_beginTime.setOnClickListener(this);
        rl_endTime.setOnClickListener(this);
        attachmentLayout.setOnClickListener(this);

        etName.setOnTouchListener(this);
        etReceiver.setOnTouchListener(this);
        rl_beginTime.setOnTouchListener(this);
        rl_endTime.setOnTouchListener(this);
        attachmentLayout.setOnTouchListener(this);
        etDetail.setOnTouchListener(this);
    }

    /**
     * 时间Handler
     */
    private Handler mChooseTimeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Calendar selectCalendar = (Calendar) msg.obj;
            Log.i(TAG, selectCalendar.getTime().toString());
            switch (msg.arg1) {
                case START_TIME:
                    //如果设置的是开始时间并且结束时间已经设置
                    if (!tv_endTime.getText().equals("")) {
                        //如果开始时间大于结束时间,则清除结束时间,并结束时间enable=true
                        if (startCalendar.getTime().compareTo(endCalendar.getTime()) > 0) {
                            tv_endTime.setText("");
                        }
                    } else {
                        tv_endTime.setEnabled(true);
                    }
                    tv_beginTime.setText(addLeaveApplicationManager.getTimeStrByCalendar(selectCalendar));
                    startCalendar = selectCalendar;

                    break;
                case END_TIME:
                    tv_endTime.setText(addLeaveApplicationManager.getTimeStrByCalendar(selectCalendar));
                    endCalendar = selectCalendar;
                    break;
            }
        }
    };

    /**
     * 创建并在日期控件中加上标题
     *
     * @return
     */
    public View createDatePickerView() {

        View view = getLayoutInflater().inflate(R.layout.prizechoosedatequery_view, null);

        return view;
    }


    /**
     * 日期选择控件
     *
     * @param type
     */
    private void showDateMessage(final int type) {
        final AlertDialog dlg = new AlertDialog.Builder(AddApplicationActivity.this).create();
        dlg.show();
        Window window = dlg.getWindow();

        window.setContentView(createDatePickerView());

        final MyDatePicker mDatePicker = (MyDatePicker) window.findViewById(R.id.datetime_picker);

        Calendar defaultCalendar = null;

        /**
         * 打开控件的时候显示默认时间
         */
        switch (type) {
            case START_TIME:
                defaultCalendar = startCalendar;
                break;
            case END_TIME:
                if (endCalendar != null) {
                    defaultCalendar = endCalendar;
                }
                break;
        }
        if (defaultCalendar != null) {
            Time defaultTime = new Time();
            defaultTime.set(defaultCalendar.getTimeInMillis());
            mDatePicker.setDate(defaultTime);
        }

        mDatePicker.setTimeVisibility(View.VISIBLE);
        Button ok = (Button) window.findViewById(R.id.datetime_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int year = Integer.parseInt(mDatePicker.getYear());
                int month = Integer.parseInt(mDatePicker.getMonth()) - 1;
                int day = Integer.parseInt(mDatePicker.getDay());
                int hour = Integer.parseInt(mDatePicker.getHours());
                int min = Integer.parseInt(mDatePicker.getMins());

                Log.i("select_calendar--", month + "," + day + "," + hour);

                Calendar calendar = addLeaveApplicationManager.createCalendar(year, month, day, hour, min);

                Log.i("select_calendar", calendar.getTime().toString());

                String StrMsg = getResources().getString(R.string.leave_set_time_error_msg);

                addLeaveApplicationManager.selectDate(type, calendar, AddApplicationActivity.this, mChooseTimeHandler, StrMsg);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //选择告假类型
            case R.id.vacation_type:
                closeSoftInputMethod();
                slidingDrawer.animateOpen();
                break;

            //选择接收人
            case R.id.vacation_receiver:
                Intent receiverIntent = new Intent(this, SelectReceiverActivity.class);
                receiverIntent.putExtra(KEY_RECEIVER_ID, mReceivers_id);
                startActivityForResult(receiverIntent, REQUEST_RECEIVER);

                break;

            //选择开始时间
            case R.id.begin_time:
                showDateMessage(START_TIME);
                break;

            //选择结束时间
            case R.id.end_time:
                showDateMessage(END_TIME);
                break;

            //附件
            case R.id.vacation_attachment:
                Intent attrIntent = new Intent(this, NewEventAttc2Activity.class);
                startActivityForResult(attrIntent, 0);
                break;

        }
    }

    /**
     * 处理选择接收人和添加附件的返回
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == StateActivity.RESULT_CODE) {
            switch (requestCode) {
                case REQUEST_RECEIVER:
                    Bundle bundle = data.getBundleExtra(KEY_RECEIVER_BUNDLE);
                    if (bundle != null) {
                        String receivers = bundle.getString(KEY_RECEIVER_LIST);
                        mReceivers_id = bundle.getString(KEY_RECEIVER_ID);
                        tv_receivers.setText("");
                        tv_receivers.setText(receivers);
                        //set owner 和 id
                        leaveApplicationMainItem.setOwner(receivers);
                        leaveApplicationMainItem.setOwnerid(mReceivers_id);

                    }
                    break;

            }
        } else {
            tv_attr_num.setText(GlobalVariables.mListAttachmentData.size() + "");
        }
    }

    /**
     * 发送告假
     * 验证
     */
    private void send() {
        // TODO Auto-generated method stub
        String title_str = etName.getText().toString();
        if (title_str.length() < 1) {
            Toast.makeText(AddApplicationActivity.this, getResources().getString(R.string.title_cant_be_null), Toast.LENGTH_LONG).show();
            return;
        }
        String receiver_str = tv_receivers.getText().toString();
        if (receiver_str.length() < 1) {
            Toast.makeText(AddApplicationActivity.this, getResources().getString(R.string.receiver_cant_be_null), Toast.LENGTH_LONG).show();
            return;
        }
        if (endCalendar == null) {
            Toast.makeText(AddApplicationActivity.this, getResources().getString(R.string.date_cant_be_null), Toast.LENGTH_LONG).show();
            return;
        }
        String detail_str = etDetail.getText().toString();
        if (detail_str.length() < 1) {
            Toast.makeText(AddApplicationActivity.this, getResources().getString(R.string.content_cant_be_null), Toast.LENGTH_LONG).show();
            return;
        }
        //点击发送后
        if (vacationType.getText().toString().equals(getResources().getString(R.string.leave_type_sick))) {
            leaveApplicationMainItem.setType(KEY_TYPE_SICK);
        } else {
            leaveApplicationMainItem.setType(KEY_TYPE_PRIVATE);
        }
        leaveApplicationMainItem.setStart(startCalendar.getTime());
        leaveApplicationMainItem.setEnd(endCalendar.getTime());
        leaveApplicationMainItem.setTitle(etName.getText().toString().trim());
        leaveApplicationMainItem.setContent(etDetail.getText().toString().trim());

        leaveApplicationMainItem.setAttachments(GlobalVariables.mListAttachmentData);

        addLeaveApplicationManager.submitLeave(leaveApplicationMainItem);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        /**
         * 当触摸到别的控件的时候,抽屉关闭
         */
        if (v.getId() != R.id.vacation_type) {
            if (slidingDrawer.isOpened()) {
                slidingDrawer.animateClose();
            }
        }
        return false;
    }

    public Calendar getStartCalendar() {
        return startCalendar;
    }

    public Calendar getEndCalendar() {
        return endCalendar;
    }

    public void setStartCalendar(Calendar startCalendar) {
        this.startCalendar = startCalendar;
    }

    public void setEndCalendar(Calendar endCalendar) {
        this.endCalendar = endCalendar;
    }
}

