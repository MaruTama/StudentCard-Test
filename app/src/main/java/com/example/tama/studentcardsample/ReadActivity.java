package com.example.tama.studentcardsample;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by tama on 2017/12/03.
 */

public class ReadActivity extends AppCompatActivity {

    private IntentFilter[] intentFiltersArray;
    private String[][] techListsArray;
    private NfcAdapter mAdapter;
    private PendingIntent pendingIntent;
    private NfcReader nfcReader = new NfcReader();
    private NfcWriter nfcWriter = new NfcWriter();

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        tv = (TextView) findViewById(R.id.textView);

        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);

        try {
            ndef.addDataType("text/plain");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        intentFiltersArray = new IntentFilter[]{ndef};

        // FelicaはNFC-TypeFなのでNfcFのみ指定でOK
        techListsArray = new String[][]{
                new String[]{NfcF.class.getName()}
        };

        // NfcAdapterを取得
        mAdapter = NfcAdapter.getDefaultAdapter(getApplicationContext());

    }


    @Override
    protected void onResume() {
        super.onResume();
        // NFCの読み込みを有効化
        mAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // IntentにTagの基本データが入ってくるので取得。
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag == null) {
            return;
        }
        // タグ、読み出すシステムコード、サービスコード
        byte[][] a = nfcReader.readTag(tag, new byte[]{(byte) 0xfe,(byte) 0x00}, new byte[]{(byte) 0x7a, (byte) 0x49});
        Log.d("aa",String.valueOf(a.length));

        // ここで取得したTagを使ってデータの読み書きを行う。
        tv.setText(bin2hex(a[0]));



        int size = 10;
        byte[] data = new byte[16*size];

        for (int i = 0; i < 16*size; i++) {
            data[i] = (byte) 0x31;
        }
        // なぜか0x0-0x9までしか書き込めない
        // 仕様書には13ブロックまで書き込めるみたいだが、カードによって異なるらしいので、学生証は10ブロックまで？
        boolean b = nfcWriter.writeTag(tag, new byte[]{(byte) 0xfe,(byte) 0x00}, new byte[]{(byte) 0x7a, (byte) 0x49}, data);
        Log.d("aa", String.valueOf(b));

//        mAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // NFCの読込みを無効化
        mAdapter.disableForegroundDispatch(this);
    }

    public static String bin2hex(byte[] data) {
        StringBuffer sb = new StringBuffer();
        for (byte b : data) {
            String s = Integer.toHexString(0xff & b);
            if (s.length() == 1) {
                sb.append("0");
            }
            sb.append(s);
        }
        return sb.toString();
    }
    public static byte[] hex2bin(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int index = 0; index < bytes.length; index++) {
            bytes[index] = (byte) Integer.parseInt(hex.substring(index * 2, (index + 1) * 2), 16);
        }
        return bytes;
    }
}
