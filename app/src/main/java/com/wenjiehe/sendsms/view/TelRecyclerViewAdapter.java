package com.wenjiehe.sendsms.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wenjiehe.sendsms.R;
import com.wenjiehe.sendsms.Utils;
import com.wenjiehe.sendsms.entity.PhoneNumber;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/23.
 */

public class TelRecyclerViewAdapter extends RecyclerView.Adapter<TelRecyclerViewAdapter.MyViewHolder> implements SlidingButtonView.IonSlidingButtonListener {
    private Context mContext;

    private IonSlidingViewClickListener mIDeleteBtnClickListener;

    private List<PhoneNumber> mDatas = new ArrayList<PhoneNumber>();

    private SlidingButtonView mMenu = null;

    public TelRecyclerViewAdapter(Context context,List<PhoneNumber> mDatas) {

        mContext = context;
        mIDeleteBtnClickListener = (IonSlidingViewClickListener) context;

        /*for (int i = 0; i < 10; i++) {
            mDatas.add(i+"");
        }*/
        this.mDatas = mDatas;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.textView.setText(mDatas.get(position).getNumber());
        //设置内容布局的宽为屏幕宽度
        holder.layout_content.getLayoutParams().width = Utils.getScreenWidth(mContext);

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断是否有删除菜单打开
                if (menuIsOpen()) {
                    closeMenu();//关闭菜单
                } else {
                    int n = holder.getLayoutPosition();
                    mIDeleteBtnClickListener.onItemClick(v, n);
                }

            }
        });
        holder.btn_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int n = holder.getLayoutPosition();
                mIDeleteBtnClickListener.onDeleteBtnCilck(v, n);
            }
        });
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_item_activity_tel, arg0,false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }



    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView btn_Delete;
        public TextView textView;
        public ViewGroup layout_content;
        public MyViewHolder(View itemView) {
            super(itemView);
            btn_Delete = (TextView) itemView.findViewById(com.wenjiehe.sendsms.R.id.tv_delete);
            textView = (TextView) itemView.findViewById(com.wenjiehe.sendsms.R.id.text);
            layout_content = (ViewGroup) itemView.findViewById(com.wenjiehe.sendsms.R.id.layout_content);

            ((SlidingButtonView) itemView).setSlidingButtonListener(TelRecyclerViewAdapter.this);
        }
    }

    public void addData(int position) {
        //mDatas.add(position, "添加项");
        notifyItemInserted(position);
    }

    public void removeData(int position){
        DataSupport.delete(PhoneNumber.class,mDatas.get(position).getId());
        mDatas.remove(position);
        notifyItemRemoved(position);
    }

    public void removeAllData(int phone_book_num){
        notifyItemRangeRemoved(0,mDatas.size());
        DataSupport.deleteAll(PhoneNumber.class,"owntable = ?",phone_book_num+"");
        mDatas.clear();
    }
    public void changeAllData(List<PhoneNumber> list){
        mDatas = list;
        notifyDataSetChanged();
    }

    /**
     * 删除菜单打开信息接收
     */
    @Override
    public void onMenuIsOpen(View view) {
        mMenu = (SlidingButtonView) view;
    }

    /**
     * 滑动或者点击了Item监听
     * @param slidingButtonView
     */
    @Override
    public void onDownOrMove(SlidingButtonView slidingButtonView) {
        if(menuIsOpen()){
            if(mMenu != slidingButtonView){
                closeMenu();
            }
        }
    }

    /**
     * 关闭菜单
     */
    public void closeMenu() {
        mMenu.closeMenu();
        mMenu = null;

    }
    /**
     * 判断是否有菜单打开
     */
    public Boolean menuIsOpen() {
        if(mMenu != null){
            return true;
        }
        Log.i("asd","mMenu为null");
        return false;
    }



    public interface IonSlidingViewClickListener {
        void onItemClick(View view,int position);
        void onDeleteBtnCilck(View view,int position);
    }
}
