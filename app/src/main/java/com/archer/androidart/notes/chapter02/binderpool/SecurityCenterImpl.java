package com.archer.androidart.notes.chapter02.binderpool;

import android.os.RemoteException;

/**
 * Created by Archer on 2017/11/15.
 */

public class SecurityCenterImpl extends ISecurityCenter.Stub {
    private static final char SECRET_CODE = '^';

    @Override
    public String encrypt(String content) throws RemoteException {
        char[] charArray = content.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            charArray[i] ^= SECRET_CODE;
        }
        return new String(charArray);
    }

    @Override
    public String decrypt(String password) throws RemoteException {
        return encrypt(password);
    }
}
