package com.anson.acode.fragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;

import com.anson.acode.ALog;

import java.lang.ref.WeakReference;
import java.util.Hashtable;

/**
 * Created by anson on 17-3-20.
 * base activity for control fragments
 */

public class BaseFragActivity extends Activity {
    private final String TAG = "BaseFragActivity";
    protected FragmentManager fmgr;
    protected BaseFragment curFragment;
    protected Hashtable<String, BaseFragment> fragments = new Hashtable<String, BaseFragment>();
    private int animatorChildIn, animatorChildOut;
    private int animatorParentIn, animatorParentOut;
    private int contentId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initResourceDefault();
        fmgr = getFragmentManager();
    }

    /**
     * init ui resources
     * @param rootId View for show fragment
     * @param animChildIn animator for child fragment in
     * @param animChildOut animator for child fragment out
     * @param animParentIn animator for parent fragment in
     * @param animParentOut animator for parent fragment out
     */
    public void initResource(int rootId, int animChildIn, int animChildOut, int animParentIn, int animParentOut){
        contentId = rootId;
        animatorChildIn = animChildIn;
        animatorChildOut = animChildOut;
        animatorParentIn = animParentIn;
        animatorParentOut = animParentOut;
    }

    public void initResourceDefault(){
        contentId = android.R.id.content;
        animatorParentIn = animatorChildIn = android.R.animator.fade_in;
        animatorParentOut = animatorChildOut = android.R.animator.fade_out;
    }


    public void addRootFragment(BaseFragment fragment){
        fragment.setParent(this, null);
        fragments.put(BaseFragment.TAG(fragment.getClass()), fragment);
    }

    public void addFragment(Class parent, BaseFragment fragment){
        BaseFragment parentFrag = fragments.get(BaseFragment.TAG(parent));
        fragment.setParent(this, parentFrag);
        fragments.put(BaseFragment.TAG(fragment.getClass()), fragment);
    }
    /**
     * switch to show fragment.
     * @param tag fragment tag.
     */
    public void switchFragment(String tag){
        switchFragment(tag, false);
    }
    /**
     * switch to show fragment.
     * @param tag fragment tag.
     */
    public void switchFragment(String tag, Object data){
        this.data = data;
        switchFragment(tag, false);
    }

    Object data = null;
    public Object getData(){
        return data;
    }

    /**
     * switch to show fragment
     * @param tag fragment tag
     * @param toParent back to parent
     */
    public void switchFragment(String tag, boolean toParent){
        ALog.d(TAG, "switchFragment(" + tag + ")");
        //ALog.d(TAG, hashCode() + ":" + (fragments == null ? "fragments NULL": "fragments NOT null"));
        BaseFragment lastFragment = curFragment;
        FragmentTransaction ft = fmgr.beginTransaction();
        if(toParent) {
            ft.setCustomAnimations(animatorParentIn, animatorChildOut);
        }else {
            ft.setCustomAnimations(animatorChildIn, animatorParentOut);
        }
        if(lastFragment != null) {
            if(lastFragment == fragments.get(tag))return;
            //ft.addToBackStack(null);
            ft.hide(lastFragment);
        }

        curFragment = fragments.get(tag);
        ///ALog.d(TAG, "curFragment(" + tag + ")");
        //set last fragment, when fragment was finish
        //this will make show parent or last fragment.
        if(lastFragment != null){
            curFragment.setLastFragment(BaseFragment.TAG(lastFragment.getClass()));
        }
        /*if(!curFragment.isAdded()){
            ft.add(R.id.content, curFragment, tag);
        }*/
        ft.replace(contentId, curFragment);
        //ft.show(curFragment);
        ft.commit();
    }

    //this method will called by last activity,
    //we can get the file selected from ImagePlayer or MusicPlayer.
    //and then, post to fragment, to notice fragment to show state correct.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ALog.d(TAG, "onActivityResult(" + requestCode + ", " + resultCode + " ," + data + ")");
        super.onActivityResult(requestCode, resultCode, data);
        if(curFragment != null){
            curFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    //intercept back key;
    //we do NOT finish till all fragment was closed.
    //so, post back key to fragment first.
    @Override
    public void onBackPressed() {
        if(curFragment != null){
            ALog.d(TAG, "onBackPressed " + curFragment.getClass().getSimpleName());
            if(curFragment.onBackPressed()){
                return;
            }
        }

        super.onBackPressed();
    }

    //makes fragment can get KeyEvent when key down.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return curFragment.onKeyDown(keyCode) || super.onKeyDown(keyCode, event);
    }

    public void postDelayed(Runnable r, int delayed){
        h.postDelayed(r, delayed);
    }

    public void removeCallback(Runnable r){
        h.removeCallbacks(r);
    }

    /**
     * if all message was no one to handle, i do it.
     * @param msg Message in.
     */
    protected void handleMessage(Message msg){}
    public Handler getHandler(){
        return h;
    }
    H h = new H(this);
    private static class H extends Handler{
        WeakReference<BaseFragActivity> wa;
        H(BaseFragActivity a){
            wa = new WeakReference<BaseFragActivity>(a);
        }

        @Override
        public void handleMessage(Message msg) {
            if(wa.get() != null){
                if(wa.get().curFragment.handleMessageLocal(msg)){
                    return;
                }

                wa.get().handleMessage(msg);
            }
        }
    }
}
