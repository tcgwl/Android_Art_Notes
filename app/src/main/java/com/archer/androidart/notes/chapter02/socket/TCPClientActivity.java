package com.archer.androidart.notes.chapter02.socket;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.archer.androidart.notes.R;
import com.archer.androidart.notes.chapter02.utils.MyUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TCPClientActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int MESSAGE_RECEIVE_NEW_MSG = 1;
    private static final int MESSAGE_SOCKET_CONNECTED = 2;
    private static final int MESSAGE_SEND_NEW_MSG = 3;

    private Button mSendButton;
    private TextView mMessageTextView;
    private EditText mMessageEditText;

    private PrintWriter mPrintWriter;
    private Socket mClientSocket;
    private MyHandler mHandler;

    private static class MyHandler extends Handler {
        private WeakReference<TCPClientActivity> mRefActivity;

        public MyHandler(TCPClientActivity activity) {
            mRefActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            TCPClientActivity activity = null;
            if (mRefActivity != null) {
                activity = mRefActivity.get();
            }
            switch (msg.what) {
                case MESSAGE_RECEIVE_NEW_MSG:
                    activity.handleReceiveNewMsg((String) msg.obj);
                    break;
                case MESSAGE_SOCKET_CONNECTED:
                    activity.handleSocketConnected();
                    break;
                case MESSAGE_SEND_NEW_MSG:
                    activity.handleSendNewMsg((String) msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }
    private void handleReceiveNewMsg(String content) {
        mMessageTextView.setText(mMessageTextView.getText() + content);
    }
    private void handleSocketConnected() {
        mSendButton.setEnabled(true);
    }
    private void handleSendNewMsg(String content) {
        mMessageEditText.setText("");
        mMessageTextView.setText(mMessageTextView.getText() + content);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcpclient);
        mHandler = new MyHandler(this);
        mMessageTextView = (TextView) findViewById(R.id.msg_container);
        mSendButton = (Button) findViewById(R.id.send);
        mSendButton.setOnClickListener(this);
        mMessageEditText = (EditText) findViewById(R.id.msg);

        Intent service = new Intent(this, TCPServerService.class);
        startService(service);

        new Thread() {
            @Override
            public void run() {
                connectTCPServer();
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        if (mClientSocket != null) {
            try {
                mClientSocket.shutdownInput();
                mClientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if (view == mSendButton) {
            final String msg = mMessageEditText.getText().toString();
            if (!TextUtils.isEmpty(msg) && mPrintWriter != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mPrintWriter.println(msg);
                        String time = formatDateTime(System.currentTimeMillis());
                        final String showedMsg = "self " + time + ":" + msg + "\n";
                        mHandler.obtainMessage(MESSAGE_SEND_NEW_MSG, showedMsg).sendToTarget();
                    }
                }).start();
            }
        }
    }

    private String formatDateTime(long time) {
        return new SimpleDateFormat("(HH:mm:ss)").format(new Date(time));
    }

    private void connectTCPServer() {
        Socket socket = null;
        while (socket == null) {
            try {
                socket = new Socket("localhost", 8688);
                mClientSocket = socket;
                mPrintWriter = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())), true);
                mHandler.sendEmptyMessage(MESSAGE_SOCKET_CONNECTED);
                System.out.println("connect server success");
            } catch (IOException e) {
                SystemClock.sleep(1000);
                System.out.println("connect tcp server failed, retry...");
            }
        }

        try {
            // 接收服务器端的消息
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            while (!TCPClientActivity.this.isFinishing()) {
                String msg = br.readLine();
                System.out.println("receive :" + msg);
                if (msg != null) {
                    String time = formatDateTime(System.currentTimeMillis());
                    final String showedMsg = "server " + time + ":" + msg + "\n";
                    mHandler.obtainMessage(MESSAGE_RECEIVE_NEW_MSG, showedMsg).sendToTarget();
                }
            }
            System.out.println("quit...");
            MyUtils.close(mPrintWriter);
            MyUtils.close(br);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
