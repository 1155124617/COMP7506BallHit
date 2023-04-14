package com.example.ballhit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;


public class Store extends AppCompatActivity {
    private List<Goods> goodsList=new ArrayList<>();
    int timePlus, lifePlus;
    int points, turn, goalPoints;
    TextView tvPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store);

        points=getIntent().getExtras().getInt("points");
        turn=getIntent() .getExtras().getInt("turn");
        goalPoints=getIntent().getExtras().getInt("goalPoints");
        timePlus=getIntent().getExtras().getInt("timePlus");
        lifePlus=getIntent().getExtras().getInt("lifePlus");

        tvPoints=findViewById(R.id.points);
        tvPoints.setText("points: "+points);

        initGoods();
        GoodAdapter goodAdapter=new GoodAdapter(this,R.layout.good_item,goodsList);
        ListView listView=findViewById(R.id.goods);
        listView.setAdapter(goodAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Goods good=goodsList.get(position);
                int price = Integer.parseInt(good.getPrice().replace("$", ""));
                if (price<=points){
                    points-=price;
                    Toast.makeText(Store.this, "points -"+price, Toast.LENGTH_SHORT).show();
                    switch (good.getImg_id()){
                        case R.drawable.life_plus:
                            lifePlus+=1;
                            break;
                        case R.drawable.time_plus:
                            timePlus+=1;
                            break;
                    }
                    tvPoints.setText("points: "+points);
                }else{
                    Toast.makeText(Store.this, "your points are not enough", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void initGoods(){
        Goods lifePlus=new Goods("life +1","$50",R.drawable.life_plus);
        Goods timePlus=new Goods("time +10s","$30",R.drawable.time_plus);
        goodsList.add(lifePlus);
        goodsList.add(timePlus);
    }

    public void nextRound(View view){
        Intent intent = new Intent(this, GamePlay.class);
        intent.putExtra("points", points);
        intent.putExtra("goalPoints", goalPoints);
        intent.putExtra("turn", turn);
        intent.putExtra("lifePlus", lifePlus);
        intent.putExtra("timePlus",timePlus);
        startActivity(intent);
        finish();
    }
}

