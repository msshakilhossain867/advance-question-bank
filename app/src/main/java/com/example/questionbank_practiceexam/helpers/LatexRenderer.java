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

        webView.setAlpha(0.01f);


        // ========================================================
        // TEXT STYLE
        // ========================================================

        String safeColor = textColor;

        String fontWeight =
                bold ? "700" : "400";


        // ========================================================
        // OPTION STYLE
        // ========================================================

        String contentStyle;

        if (optionStyle) {

            contentStyle =
                    "display:flex;" +
                            "align-items:center;" +
                            "min-height:48px;" +
                            "width:100%;" +
                            "box-sizing:border-box;" +
                            "padding:0 16px;" +
                            "font-family:Arial,sans-serif;" +
                            "line-height:1.4;";

        } else {

            contentStyle =
                    "width:100%;" +
                            "box-sizing:border-box;";
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

                        "html,body{" +

                        "margin:0;" +

                        "padding:0;" +

                        "width:100%;" +

                        "background:transparent;" +

                        "overflow:hidden;" +

                        "}" +


                        "body{" +

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

                        "overflow-wrap:break-word;" +

                        "}" +


                        "#content{" +

                        contentStyle +

                        "}" +


                        ".MathJax_Display{" +

                        "margin:4px 0 !important;" +

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

                        "function loadContent(){" +

                        "var element=" +
                        "document.getElementById('content');" +

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

                        "}" +

                        "]);" +

                        "}else{" +

                        "sendHeight();" +

                        "}" +

                        "}" +


                        "function sendHeight(){" +

                        "setTimeout(function(){" +

                        "var height=" +

                        "document.body.scrollHeight;" +

                        "if(height<20){" +

                        "height=20;" +

                        "}" +

                        "window.localInterface." +
                        "onRenderComplete(height);" +

                        "},100);" +

                        "}" +


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

                                int density =
                                        (int) webView.getResources()
                                                .getDisplayMetrics()
                                                .density;

                                int maxHeight =
                                        (int) (
                                                1000 * density
                                        );

                                int newHeight =
                                        (int) (
                                                height * density
                                        );

                                if (optionStyle) {

                                    int minimumHeight =
                                            (int) (
                                                    48 * density
                                            );

                                    newHeight =
                                            Math.max(
                                                    newHeight,
                                                    minimumHeight
                                            );
                                }

                                newHeight =
                                        Math.min(
                                                newHeight,
                                                maxHeight
                                        );

                                params.height =
                                        newHeight;

                                webView.setLayoutParams(
                                        params
                                );
                            }


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


        String weight =
                bold ? "700" : "400";


        String script =
                "javascript:(function(){" +

                        "var e=document.getElementById('content');" +

                        "if(e){" +

                        "e.style.color='" +
                        color +
                        "';" +

                        "e.style.fontWeight='" +
                        weight +
                        "';" +

                        "}" +

                        "})()";


        webView.evaluateJavascript(
                script,
                null
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

        webView.loadUrl("about:blank");

        webView.clearHistory();

        webView.removeAllViews();

        webView.destroy();
    }
}