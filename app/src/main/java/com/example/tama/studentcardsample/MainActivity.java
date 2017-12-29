package com.example.tama.studentcardsample;
;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


/*
 *NFCをFragment化するため
 * [FragmentはActivityを置き換えない] http://d.hatena.ne.jp/Kazzz/20110722/p1
 * [NfcFeliCaTagFragmentを作る] http://d.hatena.ne.jp/Kazzz/20110704/p1
 *タブ画面の作成
 * [ViewPagerとTabLayoutを使用してタブのある画面を作る。] http://android.sakuraweb.com/wordpress/2016/05/07/viewpager%E3%81%A8tablayout%E3%82%92%E4%BD%BF%E7%94%A8%E3%81%97%E3%81%A6%E3%82%BF%E3%83%96%E3%81%AE%E3%81%82%E3%82%8B%E7%94%BB%E9%9D%A2%E3%82%92%E4%BD%9C%E3%82%8B%E3%80%82/
 * [Support design libraryのTabLayoutをタブレットで使用するときの注意点] https://qiita.com/Rompei/items/34f039aca8262c7897b2
 * https://qiita.com/hisakioomae/items/ce63d5fefdc619f2f1c3
 */
public class MainActivity extends AppCompatActivity implements ReadNfcfFragment.INfcTagListener {

    public static final int READ_FRAGMENT  = 0;
    public static final int WRITE_FRAGMENT = 1;

    // スライド用の部品
    private ViewPager mPager;
    public static ReadNfcfFragment  mReadNfcffragment;
    public static WriteNfcfFragment mWriteNfcffragment;

    public static int nowFragment = READ_FRAGMENT;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPager = (ViewPager) findViewById(R.id.viewpager);
        mPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager(),
                MainActivity.this));
        // 上部にタブをセットする
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mPager);

        //FeliCa, FeliCaLite用フラグメント
        mReadNfcffragment  = new ReadNfcfFragment();
        mWriteNfcffragment = new WriteNfcfFragment();
        // カード発見時のリスナーの追加
        // writeはいらないや。readでさえつかってないし
        mReadNfcffragment.addNfcTagListener(this);

        //インテントから起動された際の処理
        Intent intent = this.getIntent();
        this.onNewIntent(intent);
    }

    // Fragmentはintentを受けることができないので、
    // Activityで受けたintentをFragmentに流す
    @Override
    protected void onNewIntent(Intent intent) {
        // 今表示中のFragmentのpositionを取得する
        Log.d(TAG,String.valueOf(mPager.getCurrentItem()));

        switch (mPager.getCurrentItem()){
            case READ_FRAGMENT:
                if ( mReadNfcffragment != null ) mReadNfcffragment.onNewIntent(intent);
                break;
            case WRITE_FRAGMENT:
                if ( mWriteNfcffragment != null ) mWriteNfcffragment.onNewIntent(intent);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // カード発見時のリスナーを削除
        mReadNfcffragment.removeNfcTagListener(this);
    }

    // カード発見時リスナー(ReadFragment)
    @Override
    public void onTagDiscovered(Intent intent, Tag tag, Fragment fragment) {
        Log.d(TAG,"Card Discover!!");
    }
}