package com.example.data;



import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRv_todo;
    private FloatingActionButton mBtn_write;
    private ArrayList<MyRItem> mRItems;
    private DBHelper mDBHelper;//m을 붙이면 전역변수임으로 지역변수와 구분하려고 붙이기도 한다
    private CustomAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setInit();
    }

    private void setInit() {
        mDBHelper = new DBHelper(this);
        mRv_todo = findViewById(R.id.rv_todo);
        mBtn_write = findViewById(R.id.btn_write);
        mRItems = new ArrayList<>();

        //load recent DB
        loadRecentDB();//이전에 데이터가 있다면 불러오고 그렇지 않으면 새로 생성

        mBtn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //팝업 창 띄우기
                Dialog dialog = new Dialog(MainActivity.this, android.R.style.Theme_Material_Light_Dialog);
                dialog.setContentView(R.layout.dialog_insert);//뷰가 연결되었으므로 이 레이아웃에서 find view by id사용 가능
                EditText et_type = dialog.findViewById(R.id.et_type);//그냥 find가 아니라 dialog.~해야 한다
                EditText et_name = dialog.findViewById(R.id.et_name);
                EditText et_cnt = dialog.findViewById(R.id.et_cnt);
                EditText et_unit = dialog.findViewById(R.id.et_unit);
                Button btn_ok = dialog.findViewById(R.id.btn_ok);
                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //작성시간을 넣기위한 string 변수 // Insert Database
                        String currentTime = new SimpleDateFormat("yyyy_MM_dd HH:mm:ss").format(new Date());//현재 시간 연월일시분초 받아오기
                        //앱이나 프로그램에서 시간을 가져올 수 있는 함수
                        mDBHelper.InsertTodo(et_type.getText().toString(), et_name.getText().toString(),et_cnt.getText().toString(),et_unit.getText().toString(), currentTime);//입력필드에 적은 값 가져온다
                        //Insert UI(UI에서도 보여줘야 한다)
                        MyRItem item = new MyRItem();
                        item.setType(et_type.getText().toString());
                        item.setName(et_name.getText().toString());
                        item.setCnt(et_cnt.getText().toString());
                        item.setUnit(et_unit.getText().toString());
                        item.setWritedate(currentTime);

                        mAdapter.addItem(item);

                        mRv_todo.smoothScrollToPosition(0);//데이터 올라갈때마다 0의 위치로 간다 //이쁘게 올라간다
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "할일 목록에 추가 되었습니다 !", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();//필수

            }
        });
    }

    private void loadRecentDB() {
        // 저장되어있던 DB를 가져온다
        mRItems = mDBHelper.getRefrigerator();
        if(mAdapter == null){
            mAdapter = new CustomAdapter(mRItems,this);//context는 자기자신
            //첫번째 리스트는 ArrayList가 되어야 한다 생성자에서 그렇게 만들었으므로 //ctrl + CustomAdapter누르면 그 생성자로 볼수있다
            mRv_todo.setHasFixedSize(true);//recycler성능 강화라고 한다
            mRv_todo.setAdapter(mAdapter);
        }
    }
}