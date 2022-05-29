package com.example.data;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//recycler view (재활용)

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder>{
    private ArrayList<MyRItem> mRItems;
    private Context mContext;
    private DBHelper mDBHelper;
    //생성자 Alt + insert control+a enter


    public CustomAdapter(ArrayList<MyRItem> rItems, Context mContext) {
        this.mRItems = rItems;
        this.mContext = mContext;
        mDBHelper = new DBHelper(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list,parent,false);
        //item list에 대한 view 하나하나를 연결
        return new ViewHolder(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_type.setText(mRItems.get(position).getType());
        holder.tv_name.setText(mRItems.get(position).getName());
        holder.tv_cnt.setText(mRItems.get(position).getCnt());
        holder.tv_unit.setText(mRItems.get(position).getUnit());
        holder.tv_writeDate.setText(mRItems.get(position).getWritedate());

    }

    @Override
    public int getItemCount() {
        return mRItems.size();
    }//recycler view

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_type;
        private TextView tv_name;
        private TextView tv_cnt;
        private TextView tv_unit;
        private TextView tv_writeDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_type = itemView.findViewById(R.id.tv_type);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_cnt = itemView.findViewById(R.id.tv_cnt);
            tv_unit = itemView.findViewById(R.id.tv_unit);
            tv_writeDate = itemView.findViewById(R.id.tv_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int curPos = getAdapterPosition();// 현재 리스트 클릭한 아이템위치
                    MyRItem rItem = mRItems.get(curPos);//아이템 정보 가져온다

                    String[] strChoiceItems = {"수정하기","삭제하기"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("원하는 작업을 선택해주세요");
                    builder.setItems(strChoiceItems, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int position) {//position을 의미
                            if(position == 0) {
                                // 수정하기
                                Dialog dialog = new Dialog(mContext, android.R.style.Theme_Material_Light_Dialog);
                                dialog.setContentView(R.layout.dialog_edit);//뷰가 연결되었으므로 이 레이아웃에서 find view by id사용 가능
                                //그냥 find가 아니라 dialog.~해야 한다
                                TextView tv_name = dialog.findViewById(R.id.tv_name);
                                EditText et_cnt = dialog.findViewById(R.id.et_cnt);
                                EditText et_unit = dialog.findViewById(R.id.et_unit);
                                Button btn_ok = dialog.findViewById(R.id.btn_ok);


                                tv_name.setText(rItem.getName());
                                et_cnt.setText(rItem.getCnt());
                                et_unit.setText(rItem.getUnit());



                                btn_ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //수정시나리오
                                        //update table
                                        String type = rItem.getType();
                                        String name = rItem.getName();
                                        String cnt = et_cnt.getText().toString();
                                        String unit = et_unit.getText().toString();
                                        String currentTime = new SimpleDateFormat("yyyy_MM_dd HH:mm:ss").format(new Date());//현재 시간 연월일시분초 받아오기
                                        String beforeTime = rItem.getWritedate();//이전에 저장된 시간

                                        mDBHelper.UpdateTodo(cnt,unit, currentTime,beforeTime);//입력필드에 적은 값 가져온다
                                        //UpdateTodo ctrl누르면서 클릭하면 그 함수로 이동할 수 있다

                                        //update UI
                                        rItem.setType(type);
                                        rItem.setName(name);
                                        rItem.setCnt(cnt);
                                        rItem.setUnit(unit);
                                        rItem.setWritedate(currentTime);
                                        notifyItemChanged(curPos, rItem);//클릭한 아이템에 갱신된 아이템을 갱신
                                        dialog.dismiss();//dialog 종료
                                        Toast.makeText(mContext, "목록 수정이 완료 되었습니다.", Toast.LENGTH_SHORT).show();

                                    }
                                });
                                dialog.show();//필수

                            }else if(position == 1){
                                // delete table
                                String beforeTime = rItem.getWritedate();
                                mDBHelper.deleteTodo(beforeTime);
                                // delete UI
                                mRItems.remove(curPos);
                                notifyItemRemoved(curPos);
                                Toast.makeText(mContext, "목록이 제거 되었습니다", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.show();
                }
            });
        }
    }
    //액티비티에서 호출되는 함수이며, 현재 어댑터에 새로운 게시글 아이템을 전달받아 추가하는 목적이다.
    public void addItem(MyRItem _item){
        mRItems.add(0,_item);//역순으로 add된다 최신순으로 위에 들어간다
        notifyItemInserted(0);//notify들어간건 모두 새로고침이라 보면 된다
    }
}
