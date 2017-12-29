package com.example.tama.studentcardsample;

import android.nfc.Tag;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by tama on 2017/12/06.
 */

public class WriteNfcfFragment extends NfcFeliCaTagFragment implements TextWatcher {

    public static final String TAG = "WriteNfcfFragment";
    private NfcWriter nfcWriter = new NfcWriter();

    private EditText edittext;

    private byte[] system_code = new byte[]{(byte) 0xfe,(byte) 0x00};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_write, container, false);
        edittext = (EditText) view.findViewById(R.id.editText);

        //リスナーを登録
        edittext.addTextChangedListener(this);

        return view;
    }
    // intentをもらってタグが使えるようになったら呼び出される
    @Override
    public void usingTag(Tag tag){
        String s = edittext.getText().toString();
        Log.d(TAG, String.valueOf(s.length()));
        if (s.length() > 0){
            int size = 10;
            byte[] data = new byte[16*size];

            for (int i = 0; i < 16*size; i++) {
                data[i] = (byte) 0x31;
            }
            // なぜか0x0-0x9までしか書き込めない
            // 仕様書には13ブロックまで書き込めるみたいだが、カードによって異なるらしいので、学生証は10ブロックまで？
            // システムコード 共通項目 FE00
            // サービスエリア 7A49
            boolean b = nfcWriter.writeTag(tag, system_code, new byte[]{(byte) 0x7a, (byte) 0x49}, data);
            Log.d(TAG, String.valueOf(b));
        }
    }

    // [Android] 入力を監視するTextWatcher
    // https://akira-watson.com/android/textwatcher.html
    /**
     * 文字列が修正される直前に呼び出されるメソッド
     *  @param CharSequence  s 現在EditTextに入力されている文字列
     *  @param int start     sの文字列で新たに追加される文字列のスタート位置
     *  @param int count     sの文字列の中で変更された文字列の総数
     *  @param int after     新規に追加された文字列の数
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    /**
     * 文字１つを入力した時に呼び出される
     *  @param CharSequence s  現在EditTextに入力されている文字列
     *  @param int start       sの文字列で新たに追加される文字列のスタート位置
     *  @param int before      削除される既存文字列の数
     *  @param int count       新たに追加された文字列の数
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String str = String.valueOf(s);
        if (str.matches("\\p{ASCII}*")) {
            System.out.println("Ascii charcter only.");
        }
    }

    /** 最後にこのメソッドが呼び出される
     *  @param Editable s 最終的にできた修正可能な、変更された文字列
     */
    @Override
    public void afterTextChanged(Editable s) {
    }
}