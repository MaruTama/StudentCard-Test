package com.example.tama.studentcardsample;

import java.util.ArrayList;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public abstract class NfcFeliCaTagFragment extends Fragment {
    public static final String TAG = "NfcFeliCaTagFragment";
    private String[][] techListsArray;
    private IntentFilter[] intentFiltersArray;
    private Tag tag;

    private ArrayList<INfcTagListener<NfcFeliCaTagFragment>> _listnerList =
            new ArrayList<INfcTagListener<NfcFeliCaTagFragment>>();

    public static interface INfcTagListener<F extends Fragment> {
        void onTagDiscovered(Intent intent, Tag tag, F fragment);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // FelicaはNFC-TypeFなのでNfcFのみ指定でOK
        techListsArray = new String[][]{
                new String[]{NfcF.class.getName()}
        };

        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("text/plain");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        intentFiltersArray = new IntentFilter[] {ndef};

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText("aa");
        return view;
    }
    /**
     * インテントを捕捉する
     * @param intent Activityで捕捉したインテントがセットされます
     */
    public void onNewIntent(Intent intent) {
        String action = intent.getAction();
//        Log.d(TAG,"a");
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            // IntentにTagの基本データが入ってくるので取得。
            tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tag == null) {
                return;
            }else{
                Log.d(TAG, "** nfcTag = " + tag.toString() );
                usingTag(tag);
                for ( INfcTagListener<NfcFeliCaTagFragment> listener : _listnerList ) {
                    //リスナに通知
                    listener.onTagDiscovered(intent, tag, this);
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // NFCの読込みを無効化
        //foregrandDispathch無効
        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this.getActivity());
        adapter.disableForegroundDispatch(this.getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();

        // NFCの読み込みを有効化
        //foregrandDispathch有効
        Activity a = this.getActivity();
        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(a);
        PendingIntent pendingIntent = PendingIntent.getActivity(a, 0,
                new Intent(a, a.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);


        adapter.enableForegroundDispatch(this.getActivity()
                , pendingIntent, intentFiltersArray, techListsArray);

    }
    // クソみたいなメソッド名
    // オーバライドして使う
    public void usingTag(Tag tag){}

    public void addNfcTagListener(INfcTagListener<NfcFeliCaTagFragment> listener) {
        _listnerList.add(listener);
    }
    public void removeNfcTagListener(INfcTagListener<NfcFeliCaTagFragment> listener) {
        _listnerList.remove(listener);
    }
    public Tag getNfcTag() {
        return tag;
    }
    public void set_nfcTag(Tag tag) {
        this.tag = tag;
    }
}