package com.example.tama.studentcardsample;

import android.content.IntentFilter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by tama on 2017/12/06.
 */

public class WriteNfcfFragment extends NfcFeliCaTagFragment {

    public static final String TAG = "WriteNfcfFragment";
    private NfcWriter nfcWriter = new NfcWriter();

    private EditText edittext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_write, container, false);
        edittext = (EditText) view.findViewById(R.id.editText);
        return view;
    }
    // intentをもらってタグが使えるようになったら呼び出される
    @Override
    public void usingTag(Tag tag){
        int size = 10;
        byte[] data = new byte[16*size];

        for (int i = 0; i < 16*size; i++) {
            data[i] = (byte) 0x31;
        }
        // なぜか0x0-0x9までしか書き込めない
        // 仕様書には13ブロックまで書き込めるみたいだが、カードによって異なるらしいので、学生証は10ブロックまで？
        boolean b = nfcWriter.writeTag(tag, new byte[]{(byte) 0xfe,(byte) 0x00}, new byte[]{(byte) 0x7a, (byte) 0x49}, data);
        Log.d(TAG, String.valueOf(b));
    }
}