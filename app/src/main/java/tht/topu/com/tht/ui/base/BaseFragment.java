package tht.topu.com.tht.ui.base;

import android.support.v4.app.Fragment;
import android.util.Log;

public abstract class BaseFragment extends Fragment {
    /** Fragment当前状态是否可见 */
    protected boolean isVisible;

    //setUserVisibleHint  adapter中的每个fragment切换的时候都会被调用，如果是切换到当前页，那么isVisibleToUser==true，否则为false
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }


    /**
     * 可见
     */
    protected void onVisible() {
        lazyLoad();
        Log.d("lazyLoad", "LazyLoad");
    }


    /**
     * 不可见
     */
    protected void onInvisible() {
        invisibleFunc();

    }

    /**
     * 延迟加载
     * 子类必须重写此方法
     */
    protected abstract void lazyLoad();

    protected abstract void invisibleFunc();


}