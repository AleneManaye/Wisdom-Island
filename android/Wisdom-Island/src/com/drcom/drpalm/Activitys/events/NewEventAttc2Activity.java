package com.drcom.drpalm.Activitys.events;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout.LayoutParams;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Tool.FileManagement;
import com.drcom.drpalm.View.controls.CoverFlow.CoverFlow;
import com.drcom.drpalm.View.controls.MyMothod;
import com.drcom.drpalm.objs.EventDraftItem;
import com.drcom.drpalm.objs.EventDraftItem.Attachment;
import com.drcom.drpalm.objs.MainMenuItem;
import com.wisdom.island.R;
import com.drcom.ui.View.controls.MulitImageActivity.ImagesScanActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewEventAttc2Activity extends ModuleActivity {
    public static int RESULT_CODE = 2104;
    public static String RESULT_CODE_KEY = "RESULT_CODE_KEY";
    public static String ATTC_KEY = "ATTC_KEY";
    public static final int RESULT_CAPTURE_IMAGE = 1;
    public static final int RESULT_CHOOSE_IMAGE = 2;
    public static final int RESULT_CHOOSE_MORE_IMAGE = 3;
    public static String IMAGE_ALL_SELECTED = "IMAGE_ALL_SELECTED";
    public static final String CAN_CHOOSE = "can_choose";
    private static final int CompressQuality = 20; // 相片压缩质量 (0-100)100为质量最高
    private static final String IMGDIR = "/DrpalmPic/";
    // 控件
    private TextView mTextViewSum;
    private CoverFlow cf;
    private EditText mEditTextDes;
    // 变量
    private NewEventAttcCFAdapter mCFAdapter;
    private EventDraftItem mEventDraftItem = new EventDraftItem();
    private List<MainMenuItem> mMyGVList1 = new ArrayList<MainMenuItem>(); // 保存附件图标
    private String[] mChoose;
    private int mgridviewItemNum = 1;
    private String strImgPath = "";
    private Attachment attachmentdata;
    // private ArrayList<Attachment> mListAttachmentData = new
    // ArrayList<EventDraftItem.Attachment>();
    // private ArrayList<Attachment> mListAttachmentDataTemp = new
    // ArrayList<EventDraftItem.Attachment>();
    private Uri mCapturedImageUri;
    private int SUM = 15;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflator = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflator.inflate(R.layout.newevent_attc2_view, mLayout_body);

        mChoose = getResources().getStringArray(R.array.choosephoto);
        // Bundle extras = getIntent().getExtras();
        // if (extras != null) {
        // if (extras.containsKey(ATTC_KEY)) {
        // mListAttachmentData = extras.getParcelableArrayList(ATTC_KEY);
        // }
        // }

        // initData();
        mTextViewSum = (TextView) findViewById(R.id.Textview_sum);

        mEditTextDes = (EditText) findViewById(R.id.newevent_attc_des_Edittxt);

        mCFAdapter = new NewEventAttcCFAdapter(this,
                GlobalVariables.mListAttachmentData);
        cf = (CoverFlow) findViewById(R.id.Gallery01);
        cf.setAdapter(mCFAdapter);
        cf.setAnimationDuration(500);
        cf.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                mEditTextDes.setText(GlobalVariables.mListAttachmentData
                        .get(position).description);
                // parent.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
                // view.setFocusable(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
        //
        // cf.setOnHierarchyChangeListener(new OnHierarchyChangeListener() {
        //
        // @Override
        // public void onChildViewRemoved(View parent, View child) {
        // // TODO Auto-generated method stub
        // if(mListAttachmentData.size() == 0){
        // SetBackBtnBackgroundResource(R.drawable.title_back_selector);
        // }
        // }
        //
        // @Override
        // public void onChildViewAdded(View parent, View child) {
        // // TODO Auto-generated method stub
        // SetBackBtnBackgroundResource(R.drawable.save_btn_selector);
        // }
        // });

        // cf.setOnItemClickListener(new OnItemClickListener() {
        //
        // @Override
        // public void onItemClick(AdapterView<?> parent, View view,
        // int position, long id) {
        // // TODO Auto-generated method stub
        // final int index = position;
        //
        // Log.i("zjj", "点击第:" + position);
        //
        // // MainItemViewHolder perView =
        // (MainItemViewHolder)parent.getChildAt(position).getTag();
        // // perView.imgDel.setOnClickListener(new View.OnClickListener() {
        // //
        // // @Override
        // // public void onClick(View v) {
        // // // TODO Auto-generated method stub
        // // Log.i("zjj", "删掉第:" + index);
        // //// mModulelist.remove(index);
        // //// notifyDataSetChanged();
        // // }
        // // });
        // }
        // });

        mEditTextDes.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                if (GlobalVariables.mListAttachmentData.size() > 0)
                    GlobalVariables.mListAttachmentData.get(cf
                            .getSelectedItemPosition()).description = mEditTextDes
                            .getText().toString();
            }
        });

        // mGridViewAdapter = new MainAdapter(this,mMyGVList1);
        // mGVgridview = (GridView) findViewById(R.id.newevent_attc_gridView);
        // mGVgridview.setAdapter(mGridViewAdapter);
        // mGVgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        // mGVgridview.setOnItemClickListener(new OnItemClickListener() {
        //
        // @Override
        // public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long
        // arg3) {
        // if (arg2 == mMyGVList1.size()-1) {
        // showListDialog(); //选择相片对话框
        // // additem();
        // }
        // }
        // });
        //
        // initData();
        initTitlebar();
        hideToolbar();
        ReflashUI();

        SetBackBtnOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Back();
            }
        });

    }

    private void initTitlebar() {

        LayoutParams p = new LayoutParams(MyMothod.Dp2Px(this,
                GlobalVariables.btnWidth_Titlebar), MyMothod.Dp2Px(this,
                GlobalVariables.btnHeight_Titlebar));

        // 添加照片按钮
        Button btnAdd = new Button(this);
        btnAdd.setBackgroundResource(R.drawable.btn_title_blue_selector);
        btnAdd.setText(getString(R.string.btn_add));
        btnAdd.setTextAppearance(NewEventAttc2Activity.this,
                R.style.TitleBtnText);
        btnAdd.setLayoutParams(p);
        btnAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showListDialog(); // 选择相片对话框
            }
        });

        // ToolbarAddRightButton(btnAdd);
        setTitleRightButton(btnAdd);

        // showTitleRightButton();
        // setTitleRightButtonOnClickListener(new View.OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // // TODO Auto-generated method stub
        // showListDialog(); //选择相片对话框
        // }
        // });

        // 定义返回按钮
        SetBackBtnBackgroundResource(R.drawable.btn_title_right_blue_selector);
        SetBackBtnBackgroundText(getString(R.string.ok));

        setTitleText(getString(R.string.select_attachment));
    }

    /**
     *
     */
    public void ReflashUI() {
        if (GlobalVariables.mListAttachmentData.size() == 0) {
            mEditTextDes.setVisibility(View.GONE);
        } else {
            mEditTextDes.setVisibility(View.VISIBLE);
        }

        mTextViewSum.setText("" + GlobalVariables.mListAttachmentData.size()
                + "/" + SUM);
    }

    // /**
    // * 初始化 按钮数据
    // */
    // private void initData(){
    // // MainMenuItem mi0 = new MainMenuItem();
    // // mi0.setId(0);
    // // mi0.setCount(0);
    // // mi0.setName(getResources().getString(R.string.news));
    // // mi0.setPicResId(R.drawable.add_middle);
    // //
    // // mMyGVList1.add(mi0);
    // //
    // //
    // Attachment a = new Attachment();
    // a.data
    // =MyMothod.Bitmap2Bytes(MyMothod.Drawable2Bitmap(getResources().getDrawable(R.drawable.alert)));
    // a.description = "111111";
    //
    // mListAttachmentData.add(a);
    // }

    // /**
    // * 添加附件到GRIDVIEW中
    // */
    // private void additem(){
    // mgridviewItemNum ++;
    //
    // final MainMenuItem mi1 = new MainMenuItem();
    // mi1.setId(mgridviewItemNum);
    // mi1.setCount(0);
    // mi1.setCanDel(true);
    // mi1.setName(getResources().getString(R.string.news) + mgridviewItemNum);
    // mi1.setPicResId(R.drawable.white_kk);
    // mi1.setmBitmap(MyMothod.Byte2Bitmap(attachmentdata.data));
    // mi1.setonClickDelListener(new View.OnClickListener() {
    //
    // @Override
    // public void onClick(View v) {
    // // TODO Auto-generated method stub
    // for(int i = 0;i < mMyGVList1.size()-1;i++){
    // if(mMyGVList1.get(i) == mi1){
    // mMyGVList1.remove(i);
    // mGridViewAdapter.notifyDataSetChanged();
    // ResetGridviewHeight();
    //
    // mListAttachmentData.remove(i);
    // }
    // }
    //
    // }
    // });
    //
    // mMyGVList1.add(mMyGVList1.size() - 1, mi1);
    // mGridViewAdapter.notifyDataSetChanged();
    // ResetGridviewHeight();
    // }

    // /**
    // * 重新计算Gridview高度
    // */
    // private void ResetGridviewHeight(){
    // mGVgridview.getChildAt(0).measure(0, 0);
    // int mgridviewItemHeight = mGVgridview.getChildAt(0).getMeasuredHeight();
    //
    // int listviewHeight = (mGVgridview.getChildCount()/3 + 1) *
    // mgridviewItemHeight;
    // ViewGroup.LayoutParams params = mGVgridview.getLayoutParams();
    // params.height = listviewHeight;
    // mGVgridview.setLayoutParams(params);
    // mGVgridview.invalidate();
    // }

    /**
     * 弹出对话框
     */
    private void showListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // builder.setTitle("请选择省份");
        builder.setItems(mChoose, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // 拍照
                        takephoto();
                        break;
                    case 1: // 选本地图片
                        Intent intent = new Intent(NewEventAttc2Activity.this,
                                ImagesScanActivity.class);
                        intent.putExtra(CAN_CHOOSE, 15 - GlobalVariables.mListAttachmentData.size());
                        startActivityForResult(intent, RESULT_CHOOSE_MORE_IMAGE);
                        // Intent intent = new Intent();
                        // /* 开启Pictures画面Type设定为image */
                        // intent.setType("image/*");
                        // /* 使用Intent.ACTION_GET_CONTENT这个Action */
                        // intent.setAction(Intent.ACTION_GET_CONTENT);
                        // /* 取得相片后返回本画面 */
                        // startActivityForResult(intent, RESULT_CHOOSE_IMAGE);
                        break;
                    default:
                        break;
                }
            }
        });
        builder.show();
    }

    /**
     * 临时预览图片集合
     */
    private ArrayList<String> tmpPicPath = new ArrayList<String>();

    /**
     * 将图片增加到预览空间当中
     *
     * @param path
     */
    public void addAttrToView() {
        for (int i = 0; i < tmpPicPath.size(); i++) {
            ImageView image = new ImageView(NewEventAttc2Activity.this);
            image.setLayoutParams(new LayoutParams(150, 150));
            String path = tmpPicPath.get(i);
            attachmentdata = new Attachment();
            // 后缀名
            attachmentdata.fileType = FileManagement.getExtName(path);
            // 压缩数据
            byte[] filedata = MyMothod.compressFile(path);
            if (filedata != null) {
                attachmentdata.data = filedata;
            }
            String strCrc = "";
            strCrc = GlobalVariables.calcCrc(attachmentdata.data);
            attachmentdata.fileId = strCrc;
            attachmentdata.item = strCrc;
            attachmentdata.type = "file";
            GlobalVariables.mListAttachmentData.add(attachmentdata);
            mCFAdapter.notifyDataSetChanged();
            attachmentdata = null;
            filedata = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        boolean bCamera = false;

        // System.out.println("requestCode:" + requestCode + ",resultCode:" +
        // resultCode);

        // 不能多过 SUM(15) 张图片
        if (GlobalVariables.mListAttachmentData.size() >= SUM) {
            return;
        }
        if (resultCode == RESULT_OK) {
//			if (requestCode == RESULT_CHOOSE_IMAGE) { // 选图
//				Uri uri = data.getData();
//				Log.e("uri", uri.toString());
//
//				if (uri.toString().indexOf("file:///") > -1) {
//					strImgPath = uri.toString().replace("file:///", "");
//				} else {
//					String[] proj = { MediaStore.Images.Media.DATA };
//					Cursor actualimagecursor = managedQuery(uri, proj, null,
//							null, null);
//					if (actualimagecursor != null) {
//						int actual_image_column_index = actualimagecursor
//								.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//						actualimagecursor.moveToFirst();
//						strImgPath = actualimagecursor
//								.getString(actual_image_column_index);
//						try {
//							// 4.0以上的版本会自动关闭 (3.0--11; 4.0--14;; 4.0.3--15)
//							if (Integer.parseInt(Build.VERSION.SDK) < 11) {
//								actualimagecursor.close();
//							}
//						} catch (Exception e) {
//						}
//					}
//				}
            if (requestCode == RESULT_CHOOSE_MORE_IMAGE) {
                ShowLoadingDialog();
                //多选图片
                ArrayList<String> pathList = data.getExtras().getStringArrayList(
                        IMAGE_ALL_SELECTED);
                tmpPicPath.clear();
                if (tmpPicPath.size() == 0) {
                    Log.i("PATH_LIST", "一开始什么都没有");
                    tmpPicPath.addAll(pathList);
                    addAttrToView();
                } else {
                    for (int i = 0; i < pathList.size(); i++) {
                        boolean isSame = false;
                        for (int j = 0; j < tmpPicPath.size(); j++) {
                            if (tmpPicPath.get(j).equals(pathList.get(i))) {
                                isSame = true;
                            }
                            if (j == tmpPicPath.size() - 1 && !isSame) {
                                Log.i("PATH_LIST", "图片不相同");
                                tmpPicPath.add(pathList.get(i));
                                addAttrToView();
                            }
                        }
                    }
                }
                ReflashUI();

                HideLoadingDialog();
                return;
            } else if (requestCode == RESULT_CAPTURE_IMAGE) // 拍照
            {
                bCamera = true;

                attachmentdata = new Attachment();
                // 后缀名
                attachmentdata.fileType = FileManagement.getExtName(strImgPath);
                if (strImgPath.length() <= 0)
                    strImgPath = GlobalVariables.gPicPath;

                // 压缩数据
                byte[] filedata = MyMothod.compressFile(strImgPath);
                if (filedata != null) {
                    // attachmentdata.mstrFildData = filedata.toString();
                    attachmentdata.data = filedata;
                }
                Log.i("zjj", "拍照成功:" + attachmentdata.data.length + ",地址:" + strImgPath);

                // Calc CRC 2
                String strCrc = "";
                strCrc = GlobalVariables.calcCrc(attachmentdata.data);

                attachmentdata.fileId = strCrc;
                attachmentdata.item = strCrc;
                attachmentdata.type = "file";
                boolean attcexists = false;
                // 附件是否已被添加过
                for (int i = 0; i < GlobalVariables.mListAttachmentData.size(); i++) {
                    // 重新生成fileId
                    if (GlobalVariables.mListAttachmentData.get(i).fileId == null) {
                        GlobalVariables.mListAttachmentData.get(i).fileId = GlobalVariables
                                .calcCrc(GlobalVariables.mListAttachmentData.get(i).data);
                    }

                    if (GlobalVariables.mListAttachmentData.get(i).fileId
                            .equals(attachmentdata.fileId)) {
                        Toast.makeText(
                                NewEventAttc2Activity.this,
                                getResources()
                                        .getString(R.string.attachment_exists),
                                Toast.LENGTH_SHORT).show();
                        attcexists = true;
                        break;
                    }
                }

                if (attcexists == false) {
                    GlobalVariables.mListAttachmentData.add(attachmentdata);
                    mCFAdapter.notifyDataSetChanged();
                    attachmentdata = null;
                    filedata = null;
                    // additem();
                    if (bCamera) {
                        File file = new File(strImgPath);
                        file.delete();
                    }
                }
                ReflashUI();
            }

        }
    }

    /**
     * 拍照
     */
    private void takephoto() {
        try {
            Intent imageCaptureIntent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);
            strImgPath = Environment.getExternalStorageDirectory().toString()
                    + IMGDIR;// 存放照片的文件夹
            String fileName = new SimpleDateFormat("yyyyMMddHHmmss")
                    .format(new Date()) + ".jpg";// 照片命名
            File out = new File(strImgPath);
            if (!out.exists()) {
                out.mkdirs();
            }
            out = new File(strImgPath, fileName);
            strImgPath = strImgPath + fileName;// 该照片的绝对路径
            GlobalVariables.gPicPath = strImgPath;
            mCapturedImageUri = Uri.fromFile(out);
            imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    mCapturedImageUri);
            startActivityForResult(imageCaptureIntent, RESULT_CAPTURE_IMAGE);
        } catch (Exception e) {
            System.out.println("AttachmentActivity.takePhoto()");
        }
    }

    // ??????????????????????????????????????????????
    // @Override
    // protected void onSaveInstanceState(Bundle outState) {
    // // TODO Auto-generated method stub
    // super.onSaveInstanceState(outState);
    // /*解决部分机器返回无图片问题*/
    // if(mListAttachmentData.size() > 0)
    // outState.putParcelableArrayList("ListAttachmentData",
    // mListAttachmentData);
    // Log.i("zjj", "onSaveInstanceState" + mListAttachmentData.size() );
    //
    // }
    //
    // @Override
    // protected void onRestoreInstanceState(Bundle savedInstanceState) {
    // super.onRestoreInstanceState(savedInstanceState);
    // if(savedInstanceState.containsKey("ListAttachmentData")){
    // mListAttachmentDataTemp =
    // savedInstanceState.getParcelableArrayList("ListAttachmentData");
    //
    // Log.i("zjj", "onRestoreInstanceState:" + mListAttachmentDataTemp.size());
    //
    // if(mListAttachmentDataTemp!=null){
    // if(mListAttachmentDataTemp.size()>0){
    // //
    // if((mListAttachmentData.get(0).data!=null)&&(mListAttachmentData.get(0).data.length>0)){
    // // bitMapAttachmentshow =
    // Attachment.Bytes2Bimap(mListAttachmentData.get(0).data);
    // // mImgViewAttachment.setImageBitmap(bitMapAttachmentshow);
    // // mImgViewAttachment.setVisibility(View.VISIBLE);
    // // }
    // for(int i = 0 ; i < mListAttachmentDataTemp.size() - 1;i++){
    // attachmentdata = mListAttachmentDataTemp.get(i);
    // additem();
    // Log.i("zjj", "onRestoreInstanceState:" + attachmentdata.data.length);
    // }
    // }
    // }
    // }
    // }

    private void Back() {
        // 传递参数
        Intent newIntent = new Intent();
        // newIntent.putExtra(RESULT_CODE_KEY, mListAttachmentData);
        setResult(RESULT_CODE, newIntent);
        //
        // mListAttachmentData = null;
        finishDraw();
    }
}
