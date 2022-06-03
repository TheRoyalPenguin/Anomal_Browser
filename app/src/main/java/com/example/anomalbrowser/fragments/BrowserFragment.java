package com.example.anomalbrowser.fragments;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.anomalbrowser.R;
import com.example.anomalbrowser.dbSQL.DB;
import com.example.anomalbrowser.dbSQL.History;
import com.example.anomalbrowser.firebaseController.BDControl;
import com.example.anomalbrowser.tabLogic.Tab;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BrowserFragment extends Fragment {
    private WebView webView;
    private View view;
    private WebSettings webSettings;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference mDataBase;
    private DatabaseReference mDataBaseTabs;
    private String DATA_KEY = "USERS";
    private int edEmailToHash, urlToHash;

    private ImageButton imgBtnTabs;
    private EditText edURL;
    private Bundle webViewBundle;
    private NavController navController;

    private BDControl bdControl;

    private String searchQuery = "https://yandex.ru/search/?text=";
    public static String loadPage = "";
    private List<Tab> arrTabs = new ArrayList<>();

    public static Tab tabDel = new Tab("test", "test", "test");
    public static Context context;
//    private String bacUrl;

    private void init()
    {
        webView = view.findViewById(R.id.webView);
        webSettings = webView.getSettings();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        edEmailToHash = user.getEmail().toString().hashCode();
        mDataBase = FirebaseDatabase.getInstance().getReference(DATA_KEY);
        mDataBaseTabs = FirebaseDatabase.getInstance().getReference(DATA_KEY + "/" + edEmailToHash + "/" + "tabs");
        getDataFromDB();

        bdControl = new BDControl();
        edURL = view.findViewById(R.id.editText);
        imgBtnTabs = view.findViewById(R.id.imgBtnTabs);
        navController = Navigation.findNavController(this.requireActivity(), R.id.navHostFragment);
        context = getContext();
    }


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_browser, container, false);
        init();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webView.setLongClickable(true);
        webView.setWebViewClient(new WebViewClient()
        {});

//        webView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                unregisterForContextMenu(webView);
//                WebView.HitTestResult result = webView.getHitTestResult();
//                if (result.getType() == WebView.HitTestResult.IMAGE_TYPE) {
//                    Toast.makeText(getContext(), result.getExtra(), Toast.LENGTH_LONG).show();
//                    registerForContextMenu(webView);
//                } else {
//                    return true;
//                }
//
//                return false;
//            }
//        });



        if (!loadPage.equals(""))
        {
            webView.loadUrl(loadPage);
            loadPage="";
        }
        else if (webViewBundle == null) {
            webView.loadUrl("https://yandex.ru/");
        } else {
            webView.restoreState(webViewBundle);
        }
        //...


        //Progress bar
        final ProgressBar pbar = (ProgressBar) view.findViewById(R.id.pB1);


        webView.setWebChromeClient(new MyChrome() {

            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100 && pbar.getVisibility() == ProgressBar.GONE) {
                    pbar.setVisibility(ProgressBar.VISIBLE);
                }
                pbar.setProgress(progress);
                if (progress == 100) {
                    pbar.setVisibility(ProgressBar.GONE);

//                    Toast.makeText(getContext(), arrayList.get(arrayList.size() - 1).getName(), Toast.LENGTH_SHORT).show();
//                    byte[] b = url.getBytes(StandardCharsets.UTF_8);
//                    String bS = b.toString();
//                    String str = new String(b, StandardCharsets.UTF_8);

//                    String sUrl = url.split("//", 2)[1].split("/", 2)[0];



//                    deleteCache(getContext());
                    //cache
//                    getContext().deleteDatabase("webview.db");
//                    getContext().deleteDatabase("webviewCache.db");
                    //cache
//                    WebStorage.getInstance().deleteAllData();
                }



            }



            //отслеживание заголовка страниц
            @Override
            public void onReceivedTitle(WebView view, String sTitle) {
                super.onReceivedTitle(view, sTitle);
                edURL.setText(webView.getUrl());
                String url = view.getUrl();
                String nameUrl = view.getTitle();
                if (webView.getWidth() > 0 &&  webView.getHeight() > 0) {
                    Bitmap bitmap = Bitmap.createBitmap(webView.getWidth(), webView.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    webView.draw(canvas);
//                    arrTabs = bdControl.createTabs(nameUrl, url, "", arrTabs);
                    arrTabs = bdControl.saveTabLogoUrl(nameUrl, url, bitmap, arrTabs);
                }
                DB db = new DB(BrowserFragment.this.getContext());
                db.addInHistory(view.getTitle(), view.getUrl(), "Сегодня", "2134124");

//                deleteCache(getContext());
//                WebStorage.getInstance().deleteAllData();
            }

        });
        //...

        // Go back on WebView
        webView.canGoBack();
        webView.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == MotionEvent.ACTION_UP
                        && webView.canGoBack()) {
//                    ImageView tvBac = (ImageView) BrowserFragment.this.getView().findViewById(R.id.tvBac);
//                    ImageView tvBac = (ImageView) new ImageView(BrowserFragment.this.getContext());
//                    Bitmap bitmap = Bitmap.createBitmap(webView.getWidth(), webView.getHeight(), Bitmap.Config.ARGB_8888);
//                    Canvas canvas = new Canvas(bitmap);
//                    webView.draw(canvas);
//                    if (bitmap != null) tvBac.setImageBitmap(bitmap);

//                    BDControl bdControl = new BDControl();
//                    bdControl.saveLogoUrl(webView.getUrl(), bitmap);
                    webView.goBack();
                    return true;
                }
                return false;
            }
        });
        //...



        edURL.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                //If the keyevent is a key-down event on the "enter" button
                if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String text = edURL.getText().toString();
                    if(text.contains("http://") || text.contains("https://")) webView.loadUrl(text);
                    else if(contains(text, ".")) webView.loadUrl("http://" + text);
                    else webView.loadUrl(searchQuery + text);
                    closeKeyboard();

                    return true;
                }
                return false;
            }
        });

        imgBtnTabs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.tabsFragment);
            }
        });

        return view;
    }



    // Проверка строки на наличие подстроки
    public static boolean contains(String str, String substr){
        return str.contains(substr);
    }
    //...

    // Скрытие клавиатуры
    private void closeKeyboard() {
        View view = this.getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    //...


    // WebView saving state
    @Override public void onPause() {
        super.onPause();

        webViewBundle = new Bundle();
        webView.saveState(webViewBundle);
    }
    //...




    // WebView FullScreen
    private class MyChrome extends WebChromeClient {
        private View mCustomView;
        private CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;


        MyChrome() {
        }


        public Bitmap getDefaultVideoPoster() {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView() {
            ((FrameLayout) getActivity().getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getActivity().getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            getActivity().setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, CustomViewCallback paramCustomViewCallback) {
            if (this.mCustomView != null) {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getActivity().getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getActivity().getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout) getActivity().getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getActivity().getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }

    }




    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }






//    private void createTabs(String name, String url, String url_image, List<Tab> arrTabs)
//    {
//
////        ArrayList<String> list;
////        list = new ArrayList<>();
////        list.add(name);
////        list.add(url);
//        Tab tab = new Tab(name, url, url_image);
//        arrTabs = bdControl.clearArrTabs(tab, arrTabs);
//        arrTabs.add(tab);
//
//        mDataBaseTabs.setValue(arrTabs);
//    }

    private void getDataFromDB()
    {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {

                if (arrTabs.size() > 0) arrTabs.clear();
                for (DataSnapshot ds : snapshot.getChildren())
                {
//                    ArrayList<String> list;
//                    list = new ArrayList<>();
//                    list.add(ds.child("0").getValue().toString());
//                    list.add(ds.child("1").getValue().toString());
                    Tab tab = new Tab(ds.child("name").getValue().toString(), ds.child("URL").getValue().toString(), ds.child("url_image").getValue().toString());
//                    arrTabs = bdControl.clearArrTabs(tab, arrTabs);
                    arrTabs.add(tab);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mDataBaseTabs.addValueEventListener(vListener);
    }


//    private void clearArrTabs(Tab tab)
//    {
//        for (Tab tab1 : arrTabs)
//        {
//            if (tab1.URL.equals(tab.URL))
//            {
//                arrTabs.remove(tab1);
//
//                break;
//            }
//        }
//    }


}


//...




