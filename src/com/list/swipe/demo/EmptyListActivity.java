package com.list.swipe.demo;

import java.util.ArrayList;

import com.list.swipe.demo.EmptyListActivity.CheeseAdapter.ViewHolder;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cyrilmottier.android.listviewtipsandtricks.R;

public class EmptyListActivity extends Activity {

    private static ArrayList<Model> empty = new ArrayList<Model>();
    private ArrayList<Model> items = new ArrayList<Model>();
    private CheeseAdapter mAdapter;
    private ListView mListView;
    private ViewStub inflate_stub;
    private MyTouchListener mOnTouchListener;
    private int action_down_x = 0;
    private int action_up_x = 0;
    private int difference = 0;
    Context mContext;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_list);
        mContext = this;
        	
        mOnTouchListener = new MyTouchListener();
        
        Model model;
        for (int i = 0; i < 10; i++) {
        	model = new Model();
        	model.setList_item("Item "+i);
        	model.setVisible(false);
        	items.add(model); 			
		}
       
        inflate_stub = (ViewStub) findViewById(R.id.inflate_stub);
        inflate_stub.inflate();
    	inflate_stub.setVisibility(View.GONE);
        
        mListView = (ListView) findViewById(R.id.listView);
        mAdapter = new CheeseAdapter(items);
        mListView.setAdapter(mAdapter);
    }
    
    class MyTouchListener implements OnTouchListener
    {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			ViewHolder holder = (ViewHolder) v.getTag(R.layout.list_row);
			int action = event.getAction();
			int position = (Integer) v.getTag();

			switch (action) {
			case MotionEvent.ACTION_DOWN:
				action_down_x = (int) event.getX();
				Log.d("action", "ACTION_DOWN - ");
				break;
			case MotionEvent.ACTION_MOVE:
				Log.d("action", "ACTION_MOVE - ");
				action_up_x = (int) event.getX();
				difference = action_down_x - action_up_x;
				break;
			case MotionEvent.ACTION_UP:
				Log.d("action", "ACTION_UP - ");
				calcuateDifference(holder, position);
				action_down_x = 0;
				action_up_x = 0;
				difference = 0;
				break;
			}
			return true;
		}
    }

	private void calcuateDifference(final ViewHolder holder, final int position) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (difference == 0) {
					Toast.makeText(mContext, items.get(position).getList_item(), Toast.LENGTH_LONG).show();
				}
				if (difference > 75) {
					holder.btn_remove.setVisibility(View.VISIBLE);
					items.get(position).setVisible(true);
					mAdapter.changeData(items);
					Toast.makeText(mContext, "Right to Left - "+position, Toast.LENGTH_LONG).show();
				}
				if (difference < -75) {
					holder.btn_remove.setVisibility(View.GONE);
					items.get(position).setVisible(false);
					mAdapter.changeData(items);
					Toast.makeText(mContext, "Left to Right - "+position, Toast.LENGTH_LONG).show();
				}
			}
		});
	}
    
    public void onSetEmpty(View v) {
        mAdapter.changeData(empty);
    }

    public void onSetData(View v) {
        mAdapter.changeData(items);
    }

    public class CheeseAdapter extends BaseAdapter {

        private ArrayList<Model> mData;
        public CheeseAdapter(ArrayList<Model> data) {
            mData = data;
        }

        public void changeData(ArrayList<Model> data) {
            mData = data;
            if(mData.size() > 0){
            	inflate_stub.setVisibility(View.GONE);
            }
            else{
            	inflate_stub.setVisibility(View.VISIBLE);
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
        
        class ViewHolder{
        	TextView list_item;
        	Button btn_remove;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

        	ViewHolder holder = new ViewHolder();
            if (convertView == null) {
            	convertView = getLayoutInflater().inflate(R.layout.list_row, null);
            	holder.list_item = (TextView) convertView.findViewById(R.id.list_item);
            	holder.btn_remove = (Button) convertView.findViewById(R.id.btn_remove);
            	
            	holder.btn_remove.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(final View v) {
						final int pos = (Integer) v.getTag();
						Builder builder = new Builder(mContext);
						builder.setTitle("Alert!!!!");
						builder.setMessage("Are you sure you want to delete "+items.get(pos).getList_item());
						builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								items.get(pos).setVisible(false);
								items.remove(pos);
								v.setVisibility(View.GONE);
								changeData(items);
							}
						});
						
						builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						});
						
						builder.create().show();
					}
				});
            	
            	convertView.setTag(R.layout.list_row, holder);
            }
            else{
            	holder = (ViewHolder) convertView.getTag(R.layout.list_row);
            }
            convertView.setTag(position);
            convertView.setOnTouchListener(mOnTouchListener);
            
            holder.btn_remove.setTag(position);
            if(items.get(position).isVisible())
            	holder.btn_remove.setVisibility(View.VISIBLE);
            else
            	holder.btn_remove.setVisibility(View.GONE);
            
            holder.list_item.setText(items.get(position).getList_item());

            return convertView;
        }
    }
}
