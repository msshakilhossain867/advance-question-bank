 package com.example.questionbank_practiceexam.helpers;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.view.ViewGroup;

import org.json.JSONObject;

public class LatexRenderer {

    private static final String MATHJAX_CDN =
            "https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.7/MathJax.js?config=TeX-MML-AM_CHTML";


    // ============================================================
    // DEFAULT RENDER
    // ============================================================

    public static void render(
            WebView webView,
            String rawText
    ) {

        render(
                webView,
                rawText,
                "#222222",
                16,
                false,
                false
        );
    }


    // ============================================================
    // CUSTOM RENDER
    // ============================================================

    @SuppressLint("SetJavaScriptEnabled")
    public static void render(
            WebView webView,
            String rawText,
            String textColor,
            int textSizeSp,
            boolean bold,
            boolean optionStyle
    ) {

        if (webView == null) {
            return;
        }


        // ========================================================
        // CLEAN JSON TEXT
        // ========================================================

        String cleanText = extractText(rawText);


        // ========================================================
        // WEB SETTINGS
        // ========================================================

        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);

        settings.setDomStorageEnabled(true);

        settings.setCacheMode(
                WebSettings.LOAD_CACHE_ELSE_NETWORK
        );

        settings.setAllowFileAccess(true);

        settings.setAllowContentAccess(true);


        // ========================================================
        // WEBVIEW STYLE
        // ========================================================

        webView.setBackgroundColor(
                Color.TRANSPARENT
        );

        webView.setVerticalScrollBarEnabled(false);

        webView.setHorizontalScrollBarEnabled(false);

        webView.setOverScrollMode(
                WebView.OVER_SCROLL_NEVER
        );

        // Initially invisible to avoid showing incomplete MathJax.
        webView.setAlpha(0.01f);


        // ========================================================
        // TEXT STYLE
        // ========================================================

        String safeColor =
                textColor == null
                        ? "#222222"
                        : textColor;

        String fontWeight =
                bold ? "700" : "400";


        // ========================================================
        // CONTENT STYLE
        // ========================================================

        String contentStyle;

        if (optionStyle) {

            contentStyle =
                    "display:block;" +
                            "width:100%;" +
                            "min-height:48px;" +
                            "box-sizing:border-box;" +
                            "padding:4px 12px;" +
                            "font-family:Arial,sans-serif;" +
                            "line-height:1.45;" +
                            "overflow:visible;" +
                            "word-wrap:break-word;" +
                            "overflow-wrap:anywhere;" +
                            "white-space:normal;";

        } else {

            contentStyle =
                    "display:block;" +
                            "width:100%;" +
                            "box-sizing:border-box;" +
                            "padding:0;" +
                            "overflow:visible;" +
                            "word-wrap:break-word;" +
                            "overflow-wrap:anywhere;" +
                            "white-space:normal;";
        }


        // ========================================================
        // SAFELY CONVERT TEXT TO JAVASCRIPT STRING
        // ========================================================

        String jsText;

        try {

            jsText =
                    JSONObject.quote(
                            cleanText
                    );

        } catch (Exception e) {

            jsText =
                    "\"\"";
        }


        // ========================================================
        // HTML
        // ========================================================

        String htmlData =

                "<!DOCTYPE html>" +

                        "<html>" +

                        "<head>" +

                        "<meta name=\"viewport\" " +
                        "content=\"width=device-width, " +
                        "initial-scale=1.0, " +
                        "maximum-scale=1.0, " +
                        "user-scalable=no\">" +


                        // =================================================
                        // MATHJAX CONFIG
                        // =================================================

                        "<script type=\"text/x-mathjax-config\">" +

                        "MathJax.Hub.Config({" +

                        "messageStyle:'none'," +

                        "showProcessingMessages:false," +

                        "skipStartupTypeset:false," +

                        "tex2jax:{" +

                        "inlineMath:[" +

                        "['$','$']," +

                        "['\\\\(','\\\\)']" +

                        "]," +

                        "displayMath:[" +

                        "['$$','$$']," +

                        "['\\\\[','\\\\]']" +

                        "]" +

                        "}," +

                        "\"HTML-CSS\":{" +

                        "linebreaks:{" +

                        "automatic:true" +

                        "}," +

                        "EqnChunk:100," +

                        "EqnChunkFactor:1" +

                        "}" +

                        "});" +

                        "</script>" +


                        // =================================================
                        // MATHJAX
                        // =================================================

                        "<script type=\"text/javascript\" " +
                        "src=\"" +
                        MATHJAX_CDN +
                        "\"></script>" +


                        // =================================================
                        // CSS
                        // =================================================

                        "<style>" +

                        "*{" +
                        "box-sizing:border-box;" +
                        "}" +


                        "html{" +
                        "margin:0;" +
                        "padding:0;" +
                        "width:100%;" +
                        "background:transparent;" +
                        "overflow:visible;" +
                        "}" +


                        "body{" +
                        "margin:0;" +
                        "padding:0;" +
                        "width:100%;" +
                        "background:transparent;" +
                        "overflow:visible;" +

                        "font-family:sans-serif;" +

                        "font-size:" +
                        textSizeSp +
                        "px;" +

                        "line-height:1.5;" +

                        "color:" +
                        safeColor +
                        ";" +

                        "font-weight:" +
                        fontWeight +
                        ";" +

                        "word-wrap:break-word;" +

                        "overflow-wrap:anywhere;" +

                        "white-space:normal;" +
                        "}" +


                        "#content{" +

                        contentStyle +

                        "}" +


                        // =================================================
                        // MATHJAX DISPLAY
                        // =================================================

                        ".MathJax_Display{" +

                        "display:block !important;" +

                        "margin:0 !important;" +

                        "padding:0 !important;" +

                        "max-width:100% !important;" +

                        "overflow:visible !important;" +

                        "}" +


                        ".MathJax{" +

                        "max-width:100%;" +

                        "}" +


                        "img{" +

                        "max-width:100%;" +

                        "height:auto;" +

                        "}" +

                        "</style>" +

                        "</head>" +


                        "<body>" +

                        "<div id=\"content\"></div>" +


                        // =================================================
                        // JAVASCRIPT
                        // =================================================

                        "<script>" +


                        // =================================================
                        // LOAD CONTENT
                        // =================================================

                        "function loadContent(){" +

                        "var element=" +
                        "document.getElementById('content');" +

                        "if(!element) return;" +

                        "element.innerHTML=" +
                        jsText +
                        ";" +


                        "if(window.MathJax){" +

                        "MathJax.Hub.Queue([" +

                        "\"Typeset\"," +

                        "MathJax.Hub," +

                        "element," +

                        "function(){" +

                        "sendHeight();" +

                        "setTimeout(sendHeight,150);" +

                        "setTimeout(sendHeight,400);" +

                        "setTimeout(sendHeight,800);" +

                        "}" +

                        "]);" +

                        "}else{" +

                        "sendHeight();" +

                        "}" +

                        "}" +


                        // =================================================
                        // EXACT CONTENT HEIGHT
                        // =================================================

                        "function sendHeight(){" +

                        "setTimeout(function(){" +

                        "var element=" +
                        "document.getElementById('content');" +

                        "if(!element) return;" +


                        "var contentHeight=" +
                        "element.scrollHeight;" +

                        "var bodyHeight=" +
                        "document.body.scrollHeight;" +

                        "var htmlHeight=" +
                        "document.documentElement.scrollHeight;" +


                        "var height=Math.ceil(Math.max(" +

                        "contentHeight," +

                        "bodyHeight," +

                        "htmlHeight" +

                        "));" +


                        "if(height<1){" +

                        "height=1;" +

                        "}" +


                        "window.localInterface." +
                        "onRenderComplete(height);" +

                        "},50);" +

                        "}" +


                        // =================================================
                        // PAGE LOAD
                        // =================================================

                        "window.onload=function(){" +

                        "loadContent();" +

                        "};" +


                        "</script>" +

                        "</body>" +

                        "</html>";


        // ========================================================
        // JAVASCRIPT INTERFACE
        // ========================================================

        webView.addJavascriptInterface(

                new Object() {

                    @JavascriptInterface
                    public void onRenderComplete(
                            final int height
                    ) {

                        webView.post(() -> {

                            ViewGroup.LayoutParams params =
                                    webView.getLayoutParams();

                            if (params != null) {

                                float density =
                                        webView.getResources()
                                                .getDisplayMetrics()
                                                .density;


                                // =================================================
                                // CSS PX -> ANDROID PX
                                // =================================================

                                int newHeight =
                                        (int) Math.ceil(
                                                height * density
                                        );


                                // =================================================
                                // OPTION MINIMUM HEIGHT
                                // =================================================

                                if (optionStyle) {

                                    int minimumHeight =
                                            (int) Math.ceil(
                                                    48 * density
                                            );

                                    newHeight =
                                            Math.max(
                                                    newHeight,
                                                    minimumHeight
                                            );
                                }


                                // =================================================
                                // NO EXTRA SPACE
                                // =================================================

                                params.height =
                                        newHeight;

                                webView.setLayoutParams(
                                        params
                                );

                                webView.requestLayout();
                            }


                            // =================================================
                            // SHOW WEBVIEW
                            // =================================================

                            if (webView.getAlpha() < 1f) {

                                webView.animate()
                                        .alpha(1f)
                                        .setDuration(120)
                                        .start();
                            }

                        });
                    }

                },

                "localInterface"
        );


        // ========================================================
        // WEB CLIENT
        // ========================================================

        webView.setWebViewClient(
                new WebViewClient() {

                    @Override
                    public void onPageFinished(
                            WebView view,
                            String url
                    ) {

                        super.onPageFinished(
                                view,
                                url
                        );


                        // =================================================
                        // FIRST HEIGHT CHECK
                        // =================================================

                        view.postDelayed(
                                () -> {

                                    view.evaluateJavascript(
                                            "sendHeight();",
                                            null
                                    );

                                },
                                150
                        );


                        // =================================================
                        // SECOND HEIGHT CHECK
                        // =================================================

                        view.postDelayed(
                                () -> {

                                    view.evaluateJavascript(
                                            "sendHeight();",
                                            null
                                    );

                                },
                                500
                        );


                        // =================================================
                        // FINAL HEIGHT CHECK
                        // =================================================

                        view.postDelayed(
                                () -> {

                                    view.evaluateJavascript(
                                            "sendHeight();",
                                            null
                                    );

                                },
                                1000
                        );


                        // =================================================
                        // SHOW WEBVIEW
                        // =================================================

                        view.postDelayed(
                                () -> {

                                    if (
                                            view.getAlpha()
                                                    < 1f
                                    ) {

                                        view.animate()
                                                .alpha(1f)
                                                .setDuration(120)
                                                .start();
                                    }

                                },
                                300
                        );
                    }
                }
        );


        // ========================================================
        // LOAD HTML
        // ========================================================

        webView.loadDataWithBaseURL(
                "https://localhost/",
                htmlData,
                "text/html",
                "UTF-8",
                null
        );
    }


    // ============================================================
    // QUESTION STYLE
    // ============================================================

    public static void renderQuestion(
            WebView webView,
            String text
    ) {

        render(
                webView,
                text,
                "#151515",
                15,
                true,
                false
        );
    }


    // ============================================================
    // OPTION
    // ============================================================

    public static void renderOption(
            WebView webView,
            String text
    ) {

        render(
                webView,
                text,
                "#222222",
                14,
                false,
                true
        );
    }


    // ============================================================
    // CORRECT OPTION
    // ============================================================

    public static void renderCorrectOption(
            WebView webView,
            String text
    ) {

        render(
                webView,
                text,
                "#238347",
                14,
                true,
                true
        );
    }


    // ============================================================
    // EXPLANATION
    // ============================================================

    public static void renderExplanation(
            WebView webView,
            String text
    ) {

        render(
                webView,
                text,
                "#444444",
                13,
                false,
                false
        );
    }


    // ============================================================
    // CHANGE TEXT STYLE
    // ============================================================

    @SuppressLint("SetJavaScriptEnabled")
    public static void setTextStyle(
            WebView webView,
            String color,
            boolean bold
    ) {

        if (webView == null) {
            return;
        }


        String safeColor =
                color == null
                        ? "#222222"
                        : color;


        String weight =
                bold ? "700" : "400";


        String script =

                "(function(){" +

                        "var e=" +
                        "document.getElementById('content');" +

                        "if(!e) return 1;" +

                        "e.style.color='" +
                        safeColor +
                        "';" +

                        "e.style.fontWeight='" +
                        weight +
                        "';" +


                        "var h=Math.max(" +

                        "e.scrollHeight," +

                        "document.body.scrollHeight," +

                        "document.documentElement.scrollHeight" +

                        ");" +

                        "return Math.ceil(h);" +

                        "})()";


        webView.evaluateJavascript(
                script,
                value -> {

                    try {

                        String cleanValue =
                                value.replace(
                                        "\"",
                                        ""
                                );


                        int cssHeight =
                                Integer.parseInt(
                                        cleanValue
                                );


                        float density =
                                webView.getResources()
                                        .getDisplayMetrics()
                                        .density;


                        int height =
                                (int) Math.ceil(
                                        cssHeight * density
                                );


                        ViewGroup.LayoutParams params =
                                webView.getLayoutParams();


                        if (params != null) {

                            params.height =
                                    height;

                            webView.setLayoutParams(
                                    params
                            );

                            webView.requestLayout();
                        }

                    } catch (Exception ignored) {

                    }
                }
        );
    }


    // ============================================================
    // JSON TEXT CLEANER
    // ============================================================

    private static String extractText(
            String value
    ) {

        if (
                value == null ||
                        value.trim().isEmpty()
        ) {

            return "";
        }


        String text =
                value.trim();


        try {

            if (
                    text.startsWith("{") &&
                            text.endsWith("}")
            ) {

                JSONObject object =
                        new JSONObject(text);


                if (
                        object.has("text")
                ) {

                    return object.optString(
                            "text",
                            ""
                    );
                }
            }

        } catch (Exception ignored) {

        }


        return value;
    }


    // ============================================================
    // DESTROY
    // ============================================================

    public static void destroy(
            WebView webView
    ) {

        if (webView == null) {
            return;
        }


        webView.stopLoading();

        webView.loadUrl(
                "about:blank"
        );

        webView.clearHistory();

        webView.removeAllViews();

        webView.destroy();
    }
}

