package com.example.tama.studentcardsample;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Created by tama on 2017/12/06.
 */

public class ReadNfcfFragment extends NfcFeliCaTagFragment {

    public static final String TAG = "ReadNfcfFragment";
    private NfcReader nfcReader = new NfcReader();

    private TextView status_tv;
    private TextView student_number_tv;
    private TextView date_of_issue_tv;
    private TextView expiration_date_tv;
    private TextView free_space_text_tv;

    private byte[] system_code = new byte[]{(byte) 0xfe,(byte) 0x00};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_read, container, false);
        status_tv          = (TextView) view.findViewById(R.id.status_text);
        student_number_tv  = (TextView) view.findViewById(R.id.student_number_text);
        date_of_issue_tv   = (TextView) view.findViewById(R.id.date_of_issue_text);
        expiration_date_tv = (TextView) view.findViewById(R.id.expiration_date_text);
        free_space_text_tv = (TextView) view.findViewById(R.id.free_space_text);



        return view;
    }
    // intentをもらってタグが使えるようになったら呼び出される
    @Override
    public void usingTag(Tag tag){
        // タグ、読み出すシステムコード、サービスコード
        byte[][] bytes = nfcReader.readTag(tag, system_code, new byte[]{(byte) 0x1a, (byte) 0x8b}, 4);

        status_tv.setText("status : successful");
        String sn = getStudentNumber(bytes);
        String di = getDateOfIssue(bytes);
        String ed = getExpirationDate(bytes);
        student_number_tv.setText( "学籍番号 : " + sn);
        date_of_issue_tv.setText(  "発行日　 : " + di);
        expiration_date_tv.setText("有効期限 : " + ed);


        // 学生証のサービス0x7A49のブロック数は12個
        // だけど、書き込めるのは10個までなので、10までしか読み込まない
        bytes = nfcReader.readTag(tag, system_code, new byte[]{(byte) 0x7a, (byte) 0x49}, 10);
        String fs = getFreeSpace(bytes);
        free_space_text_tv.setText(fs);
    }
    // system code  : 0x04B8
    // Service code : 0x104B
    // ↑の方で最初読み取ろうと思ったけど、上手くいかなかった
    // 学籍番号を取得する
    private String getStudentNumber(byte[][] bytes){
        String sn = "";
        for(byte by : bytes[0]){
            sn += (char)by;
        }
        return sn.substring(2,10);
    }
    private String getDateOfIssue(byte[][] bytes){
        String di = "";
        for(byte by : bytes[2]){
            di += (char)by;
        }
        return di.substring(8,16);
    }
    private String getExpirationDate(byte[][] bytes){
        String ed = "";
        for(byte by : bytes[3]){
            ed += (char)by;
        }
        return ed.substring(0,8);
    }
    private String getFreeSpace(byte[][] bytes){
        String fs = "";
        for(int i=0; i<10; i++){
            fs += new String(bytes[i])+"\n";
        }
        return fs;
    }

}