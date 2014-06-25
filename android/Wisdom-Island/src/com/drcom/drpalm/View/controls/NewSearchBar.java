package com.drcom.drpalm.View.controls;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.wisdom.island.R;

public class NewSearchBar extends LinearLayout{
	
	private EditText searchEditText;
	private Button search_clear;
	private Button search_btn;
	private OnSearchButtonClickListener onButtonClickListener;
	private OnEnterKeyClickListener onEnterKeyClickListener;
	
	public NewSearchBar(Context context){
		this(context, null);
		initialContent(context);
	}
	
	public NewSearchBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialContent(context);
    }
    
    public void setOnSearchButtonClickListener(OnSearchButtonClickListener listener){
    	onButtonClickListener = listener;
    }
    
    public void setOnEnterKeyClickListener(OnEnterKeyClickListener listener){
    	onEnterKeyClickListener = listener;
    }
    
    private void initialContent(Context context){
    	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	inflater.inflate(R.layout.search_bar, this);
    	searchEditText = (EditText)findViewById(R.id.searchEditText);
    	search_clear = (Button)findViewById(R.id.button_clear);
    	search_clear.setVisibility(View.GONE);
    	search_btn = (Button)findViewById(R.id.search_btn);
    	searchEditText.addTextChangedListener(mTextWatcher);
    	
    	//添加软键盘回车键实现搜索响应接口
    	searchEditText.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if(keyCode == KeyEvent.KEYCODE_ENTER){
					if(event.getAction() == KeyEvent.ACTION_UP){
						String searchKey = searchEditText.getText().toString().trim();
						if(onEnterKeyClickListener != null){
							onEnterKeyClickListener.onClick(searchKey);
						}
					}
				}
				return false;
			}
		});
    	
    	search_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String searchKey = searchEditText.getText().toString();
				if(onButtonClickListener!=null){
					onButtonClickListener.onClick(v,searchKey);
				}
			}
		});
    	
    	search_clear.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			searchEditText.setText("");
    		}
    	});
    }
    
    TextWatcher mTextWatcher  = new TextWatcher() {
    	
    	@Override
    	public void onTextChanged(CharSequence s, int start, int before, int count) {
    	// TODO Auto-generated method stub
    	}
    	
    	@Override
    	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    	// TODO Auto-generated method stub
    	}
    	
    	@Override
    	public void afterTextChanged(Editable s) {
    		if(searchEditText.getText().toString()!=null&&!searchEditText.getText().toString().equals("")){
    			search_clear.setVisibility(View.VISIBLE);
    		}else{
    			search_clear.setVisibility(View.INVISIBLE);
    		}
    	}
    };
    
    public void setSearchText(String text){
    	searchEditText.setText(text);
    }
    
    public interface OnSearchButtonClickListener{
    	public void onClick(View v,String searchKey);
    }
    
    public interface OnEnterKeyClickListener{
    	public void onClick(String searchKey);
    }

}
