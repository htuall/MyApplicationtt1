package com.example.myapplicationtt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnWordListener, PutDataToRVListener {
    private ImageButton home, search,plus,poll,acc;

    public ArrayList<String> cards;

    MyDatabaseHelper mydbh;

    List<VideoItem> videoItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        home=findViewById(R.id.house);

        search=findViewById(R.id.search);

        plus=findViewById(R.id.plus);

        poll=findViewById(R.id.poll);

        acc=findViewById(R.id.acc);

        home.setOnClickListener(this);

        search.setOnClickListener(this);

        plus.setOnClickListener(this);

        poll.setOnClickListener(this);

        acc.setOnClickListener(this);

        cards = new ArrayList<>();

        mydbh= new MyDatabaseHelper(this);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final ViewPager2 videosViewPager = findViewById(R.id.viewPager2);

        videoItems = new ArrayList<>();

        VideoItem videoItemCambr= new VideoItem(Uri.parse("http://195.19.44.146:90/cambridge.mp4"),
                "http://195.19.44.146:90/cambridge.srt");

        videoItems.add(videoItemCambr);

        VideoItem videoItemCartoon= new VideoItem(Uri.parse("http://195.19.44.146:90/Friends.S07E01.mkv"),
                "http://195.19.44.146:90/Friends.S07E01.srt");

        videoItems.add(videoItemCartoon);


        putDataToArray();


        videosViewPager.setAdapter(new VideosAdapter(videoItems, this::onWord));

    }



    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.acc){
            loadFragment(new ProfileFragment());
        } else if (v.getId()==R.id.house){
            removeFragment();
        } else if (v.getId()==R.id.poll){
            loadFragment(new CardsFragment());
        } else if (v.getId()==R.id.plus){
            loadFragment(new PlusFragment(this::putDataToArray));
        }
    }

    private void removeFragment() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm != null) {
            FragmentTransaction ft = fm.beginTransaction();
            Fragment currentFragment = fm.findFragmentById(R.id.frLayout);
            if (currentFragment != null) {
                ft.remove(currentFragment);
                ft.commit();
            }
        }
    }

    public void loadFragment(Fragment f) {
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        Bundle args = new Bundle();
        args.putStringArrayList("cards", cards);
        f.setArguments(args);
        ft.replace(R.id.frLayout, f);
        ft.commit();
    }

    @Override
    public void onWord(String s) {
        cards.add(s);
        Log.e("RRR",cards.toString());
    }

    @Override
    public void putDataToArray() {
        Cursor cursor = mydbh.readData();
        if (cursor.getCount()==0){
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()){
                videoItems.add(new VideoItem(Uri.parse(cursor.getString(2)), cursor.getString(3)));
            }
        }
    }
}