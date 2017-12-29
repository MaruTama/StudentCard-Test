package com.example.tama.studentcardsample;

import android.arch.lifecycle.LifecycleOwner;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by tama on 2017/12/06.
 */

public class WriteNfcfFragment extends NfcFeliCaTagFragment implements TextWatcher {

    public static final String TAG = "WriteNfcfFragment";
    private NfcWriter nfcWriter = new NfcWriter();

    private EditText edittext;
    private TextView status;
    private TextView preview;

    private byte[] system_code = new byte[]{(byte) 0xfe,(byte) 0x00};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_write, container, false);
        status   = (TextView) view.findViewById(R.id.status_text);
        preview  = (TextView) view.findViewById(R.id.preview_text);
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

    }

    /** 最後にこのメソッドが呼び出される
     *  @param Editable s 最終的にできた修正可能な、変更された文字列
     */
    @Override
    public void afterTextChanged(Editable s) {
//        edittext.setText(shapText(String.valueOf(s)));
        if(!isAcsii(String.valueOf(s))) status.setText("文字列はASCIIコード文字のみで構成してください。");
        else status.setText("");

        String str    = String.valueOf(s);
        String shaped = shapText(str);
        preview.setText(shaped);
        // 改行文字は文字数に加算しない
        status.setText("length : " + String.valueOf(shaped.replace("\n", "").length()));
    }

    // 渡された文字列がASCII文字のみから構成されるか判定
    // http://typea.info/tips/wiki.cgi?page=Java+ASCII%CA%B8%BB%FA%CE%F3%A4%AB%A4%CE%C8%BD%C4%EA
    private boolean isAcsii(String str){
        if (str.matches("\\p{ASCII}*")) return true;
        else                                  return false;
    }
// 現状、パディングしてもedittextに反映されず、160文字以上になっても制限が効かないので、あとで修正する。
// ↑このバグも一因として一旦textviewに表示している
    // 渡された文字列を16文字n行に成形して返す。
    // 16文字以内で改行されている場合は16文字まで空白でパディングする。
    private String shapText(String str){
        String shaped = "";
        // 改行で分解する
        String[] sArray = str.split("\n");
        for(String s : sArray){
            for(int i=0; i<s.length(); i=i+16){
                if(i+16 < s.length()){
                    shaped += s.substring(i,i+16) + "\n";
                }
                else{
                    shaped += padText(s.substring(i,s.length())) + "\n";
                }
            }
        }
        Log.d(TAG,shaped);
        return shaped;
    }
    // 16文字未満のときパディングする
    // 16文字以上のときはそのまま返す
    private String padText(String str){
        String s = "";
        if(s.length() < 16) return str+getSpace(16-str.length());
        else return str;
    }
    // 指定された長さの空白を返す
    private String getSpace(int n){
        String str = "";
        for(int i=0; i<n; i++){
            str += " ";
        }
        return str;
    }
}