package com.anson.acode.fragment;

import android.app.ActivityOptions;
import android.app.Fragment;
import android.content.Intent;
import android.os.Message;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 * Created by anson on 17-3-20.
 * form my fragment
 */

public abstract class BaseFragment extends Fragment {
    WeakReference<BaseFragment> parent;
    protected WeakReference<BaseFragActivity> activity;
    public BaseFragment(){
    }

    public static String TAG(Class f){
        return f.getSimpleName();
    }

    //set activity who attached. save it.
    public void setParent(BaseFragActivity activity, BaseFragment parent){
        this.parent = new WeakReference<BaseFragment>(parent);
        //ALog.d("BaseFragment", hashCode() + ", activity == null " + (activity == null));
        if(activity != null) {
            this.activity = new WeakReference<BaseFragActivity>(activity);
        }
    }

    public void postDelayed(Runnable r, int delayed){
        if(activity != null && activity.get() != null){
            activity.get().postDelayed(r, delayed);
        }
    }

    public void removeCallbacks(Runnable r){
        if(activity != null && activity.get() != null){
            activity.get().removeCallback(r);
        }
    }

    //handle back key pressed.
    //do nothing if in top fragment.
    public boolean onBackPressed() {
        return backToParent();
    }

    //to show parent fragment with animation by default.
    public boolean backToParent(){
        if(parent.get() != null && activity.get() != null){
            activity.get().switchFragment(TAG(parent.get().getClass()), true);
            return true;
        }
        return false;
    }

    String lastFragmentTAG = null;
    public void setLastFragment(String tag){
        lastFragmentTAG = tag;
    }

    public String getLastFragment(){
        String tag = lastFragmentTAG;
        lastFragmentTAG = null;
        return tag;
    }

    //start activity safe.
    protected void startActivitySafe(Intent intent, int anim_enter, int anim_exit){
        try{
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                ActivityOptions options = ActivityOptions.makeCustomAnimation(activity.get(), anim_enter, anim_exit);
                startActivity(intent, options.toBundle());
            }else {
                startActivity(intent);
            }
        }catch(Exception e){
            e.printStackTrace();
            String str = intent == null ? "NULL" : intent.toString();
            Toast.makeText(activity.get(), "could NOT open[" + str + "]" , Toast.LENGTH_SHORT).show();
        }
    }

    //called by H, fragment can handle message by override this method.
    public boolean handleMessageLocal(Message msg){return false;}

    //called by activity, makes me support handle key down event.
    public boolean onKeyDown(int keyCode){
        return false;
    }
}
