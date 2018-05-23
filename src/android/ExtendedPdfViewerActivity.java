package net.dusal.androidpdfviewer;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Build.VERSION;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import java.io.File;
import xyz.guutong.androidpdfviewer.R.drawable;
import xyz.guutong.androidpdfviewer.R.id;
// import xyz.guutong.androidpdfviewer.R.layout;
import xyz.guutong.androidpdfviewer.R.string;
import xyz.guutong.androidpdfviewer.Utils.DownloadFile;
import xyz.guutong.androidpdfviewer.Utils.DownloadFileUrlConnectionImpl;
import xyz.guutong.androidpdfviewer.Utils.FileUtil;
import xyz.guutong.androidpdfviewer.Utils.DownloadFile.Listener;

public class ExtendedPdfViewerActivity extends AppCompatActivity implements Listener, OnPageChangeListener, OnLoadCompleteListener {
    public static final String EXTRA_PDF_URL = "EXTRA_PDF_URL";
    public static final String EXTRA_PDF_TITLE = "EXTRA_PDF_TITLE";
    public static final String EXTRA_SHOW_SCROLL = "EXTRA_SHOW_SCROLL";
    public static final String EXTRA_SWIPE_HORIZONTAL = "EXTRA_SWIPE_HORIZONTAL";
    public static final String EXTRA_SHOW_SHARE_BUTTON = "EXTRA_SHOW_SHARE_BUTTON";
    public static final String EXTRA_SHOW_CLOSE_BUTTON = "EXTRA_SHOW_CLOSE_BUTTON";
    public static final String EXTRA_TOOLBAR_COLOR = "EXTRA_TOOLBAR_COLOR";
    private static final int MENU_CLOSE = 1;
    private static final int MENU_SHARE = 2;
    private Toolbar toolbar;
    private PDFView pdfView;
    private Intent intentUrl;
    private ProgressBar progressBar;
    private String pdfUrl;
    private Boolean showScroll;
    private Boolean swipeHorizontal;
    private String toolbarColor = "#1191d5";
    private String toolbarTitle;
    private Boolean showShareButton;
    private Boolean showCloseButton;
    private DefaultScrollHandle scrollHandle;

    public ExtendedPdfViewerActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // this.setContentView(layout.dusal_activity_pdf_view);
        setContentView(getApplication().getResources().getIdentifier("dusal_activity_pdf_view", "layout",
                getApplication().getPackageName()));
        this.intentUrl = this.getIntent();
        this.pdfUrl = this.intentUrl.getStringExtra("EXTRA_PDF_URL");
        this.toolbarTitle = this.intentUrl.getStringExtra("EXTRA_PDF_TITLE") == null ? "" : this.intentUrl.getStringExtra("EXTRA_PDF_TITLE");
        this.toolbarColor = this.intentUrl.getStringExtra("EXTRA_TOOLBAR_COLOR") == null ? this.toolbarColor : this.intentUrl.getStringExtra("EXTRA_TOOLBAR_COLOR");
        this.showScroll = this.intentUrl.getBooleanExtra("EXTRA_SHOW_SCROLL", false);
        this.swipeHorizontal = this.intentUrl.getBooleanExtra("EXTRA_SWIPE_HORIZONTAL", false);
        this.showShareButton = this.intentUrl.getBooleanExtra("EXTRA_SHOW_SHARE_BUTTON", true);
        this.showCloseButton = this.intentUrl.getBooleanExtra("EXTRA_SHOW_CLOSE_BUTTON", true);
        this.progressBar = (ProgressBar)this.findViewById(id.progressBar);
        this.pdfView = (PDFView)this.findViewById(id.pdfView);
        this.toolbar = (Toolbar)this.findViewById(id.toolbar);
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(this.toolbarColor), hsv);
        hsv[2] *= 0.8F;
        int colorPrimaryDark = Color.HSVToColor(hsv);
        if (VERSION.SDK_INT >= 21) {
            this.getWindow().setStatusBarColor(colorPrimaryDark);
        }

        this.toolbar.setBackgroundColor(Color.parseColor(this.toolbarColor));
        this.toolbar.setTitle(this.toolbarTitle);
        if (this.showScroll) {
            this.scrollHandle = new DefaultScrollHandle(this);
        }

        this.setSupportActionBar(this.toolbar);
        this.progressBar.setVisibility(0);
        this.openPdf(this.pdfUrl);
    }

    private void openPdf(String inPdfUrl) {
        try {
            String urlPre = inPdfUrl.substring(0, 4);

            if(urlPre.equalsIgnoreCase("http")) {
                DownloadFile downloadFile = new DownloadFileUrlConnectionImpl(this, new Handler(), this);
                downloadFile.download(inPdfUrl, (new File(this.getCacheDir(), FileUtil.extractFileNameFromURL(inPdfUrl))).getAbsolutePath());
            } else {
                if(urlPre.equalsIgnoreCase("file")) {
                    inPdfUrl = inPdfUrl.replace("file://", "");
                }

                File pdf = new File(inPdfUrl);

                if(pdf.exists()){
                    this.pdfView.fromFile(pdf).defaultPage(0).onPageChange(this).enableAnnotationRendering(true).onLoad(this).scrollHandle(this.scrollHandle).swipeHorizontal(this.swipeHorizontal).load();
                } else {
                    Toast.makeText(this, "Error! File not found.", 0).show();
                    this.finish();
                }
            }
        } catch (Exception var3) {
            Toast.makeText(this, "Error!", 0).show();
            this.finish();
        }

    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (this.showShareButton) {
            menu.add(0, 2, 0, string.share).setIcon(drawable.ic_share).setShowAsAction(1);
        }

        if (this.showCloseButton) {
            menu.add(0, 1, 1, string.close).setIcon(drawable.ic_close).setShowAsAction(1);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == 1) {
            this.finish();
        } else if (i == 2) {
            Builder builder = new Builder(this);
            CharSequence[] itemsAlert = new CharSequence[]{"Copy link", "Open browser"};
            builder.setItems(itemsAlert, new OnClickListener() {
                public void onClick(DialogInterface dialog, int itemIndex) {
                    // int COPY_LINK = 0;
                    String label = "URL";
                    if (itemIndex == 0) {
                        ClipboardManager clipboard = (ClipboardManager)ExtendedPdfViewerActivity.this.getSystemService("clipboard");
                        ClipData clip = ClipData.newPlainText("URL", ExtendedPdfViewerActivity.this.pdfUrl);
                        clipboard.setPrimaryClip(clip);
                    } else {
                        Intent intentBrowser = new Intent("android.intent.action.VIEW");
                        intentBrowser.setData(Uri.parse(ExtendedPdfViewerActivity.this.pdfUrl));
                        ExtendedPdfViewerActivity.this.startActivity(intentBrowser);
                    }
                }
            });
            builder.show();
        }

        return true;
    }

    public void onSuccess(String url, String destinationPath) {
        File pdf = new File(destinationPath);
        this.pdfView.fromFile(pdf).defaultPage(0).onPageChange(this).enableAnnotationRendering(true).onLoad(this).scrollHandle(this.scrollHandle).swipeHorizontal(this.swipeHorizontal).load();
    }

    public void onFailure(Exception e) {
        this.progressBar.setVisibility(8);
        Builder alert = (new Builder(this)).setMessage("Cannot open file!").setPositiveButton(17039379, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ExtendedPdfViewerActivity.this.finish();
            }
        }).setIcon(17301543);
        alert.show();
    }

    public void onProgressUpdate(int progress, int total) {
    }

    public void loadComplete(int nbPages) {
        this.progressBar.setVisibility(8);
    }

    public void onPageChanged(int page, int pageCount) {
    }
}
