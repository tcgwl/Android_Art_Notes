// ISecurityCenter.aidl
package com.archer.androidart.notes.chapter02.binderpool;

interface ISecurityCenter {
    String encrypt(String content);
    String decrypt(String password);
}
