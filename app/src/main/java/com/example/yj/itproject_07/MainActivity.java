package com.example.yj.itproject_07;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;




public class MainActivity extends AppCompatActivity {
    boolean check = false;
    private FloatingActionButton fab;
    public ImageView chatView;
    public RelativeLayout chat;

    public static String recommends;
    public static ArrayList<JSONObject> phone_details = new ArrayList<JSONObject>();
    public static ArrayList<JSONObject> plan_details = new ArrayList<JSONObject>();

    public JSONArray phone;
    public JSONArray plan;

    List<Integer> listImages = new ArrayList<>();
    List<String> listPlanNames = new ArrayList<>();
    List<String> listPlanCosts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        setContentView(R.layout.activity_main);

        chatView = findViewById(R.id.chatview);
        chatView.setVisibility(View.INVISIBLE);

        //Toast.makeText(getApplicationContext(), recommends, Toast.LENGTH_LONG).show();
        this.parseRecommends();

        //System.out.println("teste!!!!! : "+this.GetDetailPlan(0,"name"));

        final View mainview = findViewById(R.id.mainview);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!check)
                {
                    Toast.makeText(getApplicationContext(), "click", Toast.LENGTH_LONG).show();
                    chatView.setVisibility(View.VISIBLE);
                    check = true;
                }
                else if(check){

                    Toast.makeText(getApplicationContext(), "unclick", Toast.LENGTH_LONG).show();
                    chatView.setVisibility(View.GONE);
                    check = false;
                }
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                 //       .setAction("Action", null).show();

            }
        });

        initData();

        HorizontalInfiniteCycleViewPager pagerPhone = (HorizontalInfiniteCycleViewPager) findViewById(R.id.horizontal_cycle_phone);
        ImageAdapter adapter = new ImageAdapter(listImages, getBaseContext());
        pagerPhone.setAdapter(adapter);

        HorizontalInfiniteCycleViewPager pagerPlan = (HorizontalInfiniteCycleViewPager) findViewById(R.id.horizontal_cycle_plan);
        PlanAdapter adapterPlan = new PlanAdapter(listPlanNames, listPlanCosts, getBaseContext());
        pagerPlan.setAdapter(adapterPlan);

        pagerPhone.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                ImageView imageViewBackground = (ImageView) findViewById(R.id.imageViewBackground);
                switch (position%3) {
                    case 0:
                        imageViewBackground.setBackgroundResource(R.drawable.bg_iphone_10);
                        break;
                    case 1:
                        imageViewBackground.setBackgroundResource(R.drawable.bg_galaxy_s8);
                        break;
                    case 2:
                        imageViewBackground.setBackgroundResource(R.drawable.bg_lg_v30);
                        break;
                }

                Log.d("DT","onPageScrolled : "+position);
            }

            @Override
            public void onPageSelected(int position) {
                Log.d("DT","onPageSelected : "+position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//Toast.makeText(getApplicationContext(), state+"onPageScrollStateChanged", Toast.LENGTH_LONG).show();

                Log.d("DT","onPageScrollStateChanged : "+state);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String GetDetailPlan(int index, String name){
        String out = new String();
        try{
            out = plan_details.get(index).getString(name);
        }catch(Exception e){
            e.printStackTrace();
        }
        return out;
    }

    private void parseRecommends(){
        plan_details.clear();
        phone_details.clear();

        try
        {
            JSONObject obj = new JSONObject(recommends);
            JSONObject data = obj.getJSONObject("data");
            phone = data.getJSONArray("phone");
            plan = data.getJSONArray("plan");

            System.out.println(plan);
            System.out.println(phone);

            try{
                //new GetDetails().GetPhoneDetail(phone.getString(0));
                for(int i=0;i<3;i++){
                    new GetDetails().GetPlanDetail(plan.getString(i));
                }
                //System.out.println("size :: " + plan_details.size());

            }catch(Exception e){
                e.printStackTrace();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void initData(){
        // 핸드폰 사진 추가하기 위해서 Drawable의 사진 파일 불러옴.
        listImages.add(R.drawable.iphone_10);
        listImages.add(R.drawable.galaxy_s8);
        listImages.add(R.drawable.lg_v30);

        listPlanNames.add("band 데이터 퍼펙트");
        listPlanNames.add("뉴T끼리 맞춤형");
        listPlanNames.add("band 데이터 1.2G");

        listPlanCosts.add("88,000원");
        listPlanCosts.add("27,830원");
        listPlanCosts.add("39,600원");

    }
}
