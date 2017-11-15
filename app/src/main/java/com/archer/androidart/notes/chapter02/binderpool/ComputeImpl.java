package com.archer.androidart.notes.chapter02.binderpool;

import android.os.RemoteException;

/**
 * Created by Archer on 2017/11/15.
 */

public class ComputeImpl extends ICompute.Stub {
    @Override
    public int add(int a, int b) throws RemoteException {
        return a + b;
    }
}
