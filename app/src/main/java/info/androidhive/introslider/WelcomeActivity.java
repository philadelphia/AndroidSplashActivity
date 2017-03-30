package info.androidhive.introslider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private ArrayList<View> views = new ArrayList();
    private Button btnSkip, btnNext;
    private PrefManager prefManager;
    private LayoutInflater layoutInflater;
    private static final String TAG = "WelcomeActivity";
    private NGGuidePageTransformer ngGuidePageTransformer;


    private int screenWith ;//屏幕宽度


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Checking for first time launch - before calling setContentView()
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        layoutInflater = this.getLayoutInflater();
        setContentView(R.layout.activity_welcome);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);
        screenWith = getWindowManager().getDefaultDisplay().getWidth();

        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.welcome_slide1,
                R.layout.welcome_slide2,
                R.layout.welcome_slide3,
                R.layout.welcome_slide4};

        // adding bottom dots
        addBottomDots(0);
        initView();

        // making notification bar transparent
        changeStatusBarColor();


        ngGuidePageTransformer = new NGGuidePageTransformer();
        ngGuidePageTransformer.setCurrentItem(this, 0, views);
        viewPager.setPageTransformer(true, ngGuidePageTransformer);

        myViewPagerAdapter = new MyViewPagerAdapter(views);
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnSkip.setOnClickListener(this);

        btnNext.setOnClickListener(this);
    }

    private void initView() {
        for (int i = 0; i < layouts.length; i++) {
            View view = layoutInflater.inflate(layouts[i], null);
            views.add(view);
        }

    }

    public interface TranslationInterface {
        void translation(float x);
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
    }

    //	viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                btnNext.setText(getString(R.string.start));
                btnSkip.setVisibility(View.GONE);
            } else {
                // still pages are left
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }
        private View currentView;
        TranslationInterface tempfrag;
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            Log.i(TAG, "onPageScrolled: " + position);
            //如果向右
            switch (position) {
                case 0:
                    currentView = views.get(0);
                    currentView.setTranslationX(positionOffsetPixels);
                    break;
                case 1:
                    currentView = views.get(1);
                    currentView.setTranslationX(positionOffsetPixels);
                    break;
                case 2:
                    currentView = views.get(2);
                    currentView.setTranslationX(positionOffsetPixels);
                    break;
                case 3:
                    currentView = views.get(3);
                    currentView.setTranslationX(positionOffsetPixels);
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            Log.i(TAG, "onPageScrollStateChanged: " + arg0);
        }
    };

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_next:
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }
                break;
            case R.id.btn_skip:
                launchHomeScreen();
                break;
            default:
                break;

        }

    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;
        private ArrayList<View> views;

        public MyViewPagerAdapter(ArrayList<View> views) {
            this.views = views;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
//            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(views.get(position));

            return views.get(position);
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    class NGGuidePageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_ALPHA = 0.0f;    //最小透明度

        public void transformPage(View view, float position) {
            Log.i(TAG, "transformPage: position " + position);
            int pageWidth = view.getWidth();    //得到view宽

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left. 出了左边屏幕
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                if (position < 0) {
                    //消失的页面
                    view.setTranslationX(-pageWidth * position);  //阻止消失页面的滑动
                } else {
                    //出现的页面
                    view.setTranslationX(pageWidth);        //直接设置出现的页面到底
                    view.setTranslationX(-pageWidth * position);  //阻止出现页面的滑动
                }
                // Fade the page relative to its size.
                float alphaFactor = Math.max(MIN_ALPHA, 1 - Math.abs(position));
                //透明度改变Log
                view.setAlpha(alphaFactor);
            } else { // (1,+Infinity]
                // This page is way off-screen to the right.    出了右边屏幕
                view.setAlpha(0);
            }
        }

        int nowPostion = 0; //当前页面
        Context context;
        ArrayList<View> views ;

        public void setCurrentItem(Context context, int nowPostion, ArrayList<View> views) {
            this.nowPostion = nowPostion;
            this.context = context;
            this.views = views;
        }

        public void setCurrentItem(int nowPostion) {
            this.nowPostion = nowPostion;
        }

    }
}
