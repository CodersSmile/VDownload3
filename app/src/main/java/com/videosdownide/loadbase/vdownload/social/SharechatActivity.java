package com.videosdownide.loadbase.vdownload.social;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;
import static android.content.ContentValues.TAG;

import static com.videosdownide.loadbase.vdownload.utils.Utils.RootDirectoryShareChat;
import static com.videosdownide.loadbase.vdownload.utils.Utils.createFileFolder;
import static com.videosdownide.loadbase.vdownload.utils.Utils.startDownload;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.View;

import com.bumptech.glide.Glide;
import com.videosdownide.loadbase.vdownload.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import com.videosdownide.loadbase.vdownload.R;
import com.videosdownide.loadbase.vdownload.api.CommonClassForAPI;
import com.videosdownide.loadbase.vdownload.databinding.ActivityChingariBinding;
import com.videosdownide.loadbase.vdownload.model.TiktokModel;
import com.videosdownide.loadbase.vdownload.utils.AppLangSessionManager;
import com.videosdownide.loadbase.vdownload.utils.SharePrefs;
import com.videosdownide.loadbase.vdownload.utils.Utils;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;

public class SharechatActivity extends AppCompatActivity {
    private ActivityChingariBinding binding;
    SharechatActivity activity;
    CommonClassForAPI commonClassForAPI;
    private String VideoUrl;
    private ClipboardManager clipBoard;
    AppLangSessionManager appLangSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chingari);
        activity = this;
        commonClassForAPI = CommonClassForAPI.getInstance(activity);
        createFileFolder();
        initViews();

        binding.imAppIcon.setImageDrawable(getResources().getDrawable(R.drawable.sharechat_icon));
        binding.tvAppName.setText(getResources().getString(R.string.sharechat_app_name));
        binding.appName.setText(getResources().getString(R.string.sharechat_app_name));


        appLangSessionManager = new AppLangSessionManager(activity);
        setLocale(appLangSessionManager.getLanguage());

    }

    @Override
    protected void onResume() {
        super.onResume();
        activity = this;
        assert activity != null;
        clipBoard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
        PasteText();
    }

    private void initViews() {
        clipBoard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);

        binding.imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.imInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.layoutHowTo.LLHowToLayout.setVisibility(View.VISIBLE);
            }
        });

        Glide.with(activity)
                .load(R.drawable.sc1)
                .into(binding.layoutHowTo.imHowto1);

        Glide.with(activity)
                .load(R.drawable.sc2)
                .into(binding.layoutHowTo.imHowto2);

        Glide.with(activity)
                .load(R.drawable.sc1)
                .into(binding.layoutHowTo.imHowto3);

        Glide.with(activity)
                .load(R.drawable.sc2)
                .into(binding.layoutHowTo.imHowto4);

        binding.layoutHowTo.tvHowToHeadOne.setVisibility(View.GONE);
        binding.layoutHowTo.LLHowToOne.setVisibility(View.GONE);
        binding.layoutHowTo.tvHowToHeadTwo.setText(getResources().getString(R.string.how_to_download));

        binding.layoutHowTo.tvHowTo1.setText(getResources().getString(R.string.open_sharechat));
        binding.layoutHowTo.tvHowTo3.setText(getResources().getString(R.string.cop_link_from_sharechat));
        if (!SharePrefs.getInstance(activity).getBoolean(SharePrefs.ISSHOWHOWTOSHARECHAT)) {
            SharePrefs.getInstance(activity).putBoolean(SharePrefs.ISSHOWHOWTOSHARECHAT, true);
            binding.layoutHowTo.LLHowToLayout.setVisibility(View.VISIBLE);
        } else {
            binding.layoutHowTo.LLHowToLayout.setVisibility(View.GONE);
        }


        binding.loginBtn1.setOnClickListener(v -> {
            String LL = binding.etText.getText().toString();
            if (LL.equals("")) {
                Utils.setToast(activity, getResources().getString(R.string.enter_url));
            } else if (!Patterns.WEB_URL.matcher(LL).matches()) {
                Utils.setToast(activity, getResources().getString(R.string.enter_valid_url));
            } else {
                Utils.showProgressDialog(activity);
                GetSharechatData();
            }
        });

        binding.tvPaste.setOnClickListener(v -> {
            PasteText();
        });

        binding.imAppIcon.setOnClickListener(v -> {
            Utils.OpenApp(activity, "in.mohalla.sharechat");
        });
    }

    private void GetSharechatData() {
        try {
            createFileFolder();
            URL url = new URL(binding.etText.getText().toString());
            String host = url.getHost();
            if (host.contains("sharechat")) {
                Utils.showProgressDialog(activity);
                new callGetShareChatData().execute(binding.etText.getText().toString());
            } else {
                Utils.setToast(activity, getResources().getString(R.string.enter_url));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void PasteText() {
        try {
            binding.etText.setText("");
            String CopyIntent = getIntent().getStringExtra("CopyIntent");
            if (CopyIntent.equals("")) {

                if (!(clipBoard.hasPrimaryClip())) {

                } else if (!(clipBoard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))) {
                    if (clipBoard.getPrimaryClip().getItemAt(0).getText().toString().contains("sharechat")) {
                        binding.etText.setText(clipBoard.getPrimaryClip().getItemAt(0).getText().toString());
                    }

                } else {
                    ClipData.Item item = clipBoard.getPrimaryClip().getItemAt(0);
                    if (item.getText().toString().contains("sharechat")) {
                        binding.etText.setText(item.getText().toString());
                    }

                }
            } else {
                if (CopyIntent.contains("sharechat")) {
                    binding.etText.setText(CopyIntent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class callGetShareChatData extends AsyncTask<String, Void, Document> {
        Document ShareChatDoc;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Document doInBackground(String... urls) {
            try {
                ShareChatDoc = Jsoup.connect(urls[0]).get();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "doInBackground: Error");
            }
            return ShareChatDoc;
        }

        protected void onPostExecute(Document result) {
            Utils.hideProgressDialog(activity);
            try {

                VideoUrl = result.select("meta[property=\"og:video:secure_url\"]").last().attr("content");
                Log.e("onPostExecute: ", VideoUrl);
                if (!VideoUrl.equals("")) {
                    try {
                        startDownload(VideoUrl, RootDirectoryShareChat, activity, "sharechat_"+System.currentTimeMillis() + ".mp4");
                        VideoUrl = "";
                        binding.etText.setText("");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);


    }



}