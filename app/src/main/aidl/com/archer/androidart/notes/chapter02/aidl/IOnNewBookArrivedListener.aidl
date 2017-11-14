// IOnNewBookArrivedListener.aidl
package com.archer.androidart.notes.chapter02.aidl;

import com.archer.androidart.notes.chapter02.aidl.Book;

interface IOnNewBookArrivedListener {
    void onNewBookArrived(in Book newBook);
}
