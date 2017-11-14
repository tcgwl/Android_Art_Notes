package com.archer.androidart.notes.chapter02.aidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.archer.androidart.notes.R;

import java.lang.ref.WeakReference;
import java.util.List;

public class BookManagerActivity extends AppCompatActivity {
    private static final String TAG = "BookManagerActivity";
    private static final int MESSAGE_NEW_BOOK_ARRIVED = 1;
    private IBookManager mRemoteBookManager;
    private MyHandler mHandler;

    private class MyHandler extends Handler {
        private WeakReference<BookManagerActivity> mRefActivity;

        public MyHandler(BookManagerActivity activity) {
            mRefActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            BookManagerActivity activity = mRefActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case MESSAGE_NEW_BOOK_ARRIVED:
                        Log.d(TAG, "receive new book: " + msg.obj);
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        }
    }

    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.d(TAG, "binder died. threadName: " + Thread.currentThread().getName());
            if (mRemoteBookManager == null)
                return;
            mRemoteBookManager.asBinder().unlinkToDeath(mDeathRecipient, 0);
            mRemoteBookManager = null;
            //重新绑定远程Service
            Log.d(TAG, "重新绑定远程Service.");
            Intent intent = new Intent(BookManagerActivity.this, BookManagerService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            IBookManager bookManager = IBookManager.Stub.asInterface(iBinder);
            mRemoteBookManager = bookManager;
            try {
                mRemoteBookManager.asBinder().linkToDeath(mDeathRecipient, 0);
                //需要注意的是，服务器的方法有可能需要很久才能执行完毕，这个时候下面的代码可能会出现ANR，可放在子线程中执行
                List<Book> bookList = bookManager.getBookList();
                Log.i(TAG, "query book list, list type: " + bookList.getClass().getCanonicalName());
                Log.i(TAG, "query book list: " + bookList.toString());
                Book newBook = new Book(3, "Android进阶");
                bookManager.addBook(newBook);
                Log.i(TAG, "add book: " + newBook);
                List<Book> newList = bookManager.getBookList();
                Log.i(TAG, "query book list: " + newList.toString());
                bookManager.registerListener(mOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mRemoteBookManager = null;
            Log.d(TAG, "onServiceDisconnected. tname:" + Thread.currentThread().getName());
        }
    };

    private IOnNewBookArrivedListener mOnNewBookArrivedListener = new IOnNewBookArrivedListener.Stub() {
        @Override
        public void onNewBookArrived(Book newBook) throws RemoteException {
            mHandler.obtainMessage(MESSAGE_NEW_BOOK_ARRIVED, newBook).sendToTarget();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_manager);
        mHandler = new MyHandler(this);
        Intent intent = new Intent(this, BookManagerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        if (mRemoteBookManager != null && mRemoteBookManager.asBinder().isBinderAlive()) {
            try {
                Log.i(TAG, "unregister listener:" + mOnNewBookArrivedListener);
                mRemoteBookManager.unregisterListener(mOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(mConnection);
        super.onDestroy();
    }
}
