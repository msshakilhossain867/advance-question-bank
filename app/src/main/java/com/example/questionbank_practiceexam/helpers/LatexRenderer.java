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

    // ============================================================
    // KATEX ALL ASSETS PATHS (LOCAL ASSETS)
    // ============================================================
    private static final String KATEX_CSS = "file:///android_asset/katex/katex.min.css";
    private static final String KATEX_SWAP_CSS = "file:///android_asset/katex/katex-swap.min.css";
    private static final String KATEX_JS = "file:///android_asset/katex/katex.min.js";

    // Contrib Libraries
    private static final String KATEX_MHCHEM = "file:///android_asset/katex/contrib/mhchem.min.js";
    private static final String KATEX_COPY_TEX = "file:///android_asset/katex/contrib/copy-tex.min.js";
    private static final String KATEX_MATHTEX = "file:///android_asset/katex/contrib/mathtex-script-type.min.js";
    private static final String KATEX_A11Y = "file:///android_asset/katex/contrib/render-a11y-string.min.js";
    private static final String KATEX_AUTO_RENDER = "file:///android_asset/katex/contrib/auto-render.min.js";

    // ============================================================
    // DEFAULT RENDER
    // ============================================================
    public static void render(WebView webView, String rawText) {
        render(webView, rawText, "#222222", 16, false, false);
    }

    // ============================================================
    // MAIN RENDER
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
        if (webView == null) return;

        String cleanText = extractText(rawText);

        // ========================================================
        // WEB SETTINGS
        // ========================================================
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);

        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setOverScrollMode(WebView.OVER_SCROLL_NEVER);

        // Hide until render completes to avoid flicker
        webView.setAlpha(0.01f);

        String safeColor = textColor == null ? "#222222" : textColor;
        String fontWeight = bold ? "700" : "400";

        String contentStyle;
        if (optionStyle) {
            contentStyle = "display:block;width:100%;box-sizing:border-box;min-height:48px;padding:4px 12px;" +
                    "font-family:Arial,sans-serif;line-height:1.5;overflow:visible;word-wrap:break-word;" +
                    "overflow-wrap:anywhere;white-space:normal;";
        } else {
            contentStyle = "display:block;width:100%;box-sizing:border-box;padding:2px 0;" +
                    "overflow:visible;word-wrap:break-word;overflow-wrap:anywhere;white-space:normal;";
        }

        String jsText;
        try {
            jsText = JSONObject.quote(cleanText);
        } catch (Exception e) {
            jsText = "\"\"";
        }

        // ========================================================
        // HTML CONTENT (INCLUDES ALL KATEX SCRIPTS & STYLES)
        // ========================================================
        String htmlData = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\">" +
                "<link rel=\"stylesheet\" href=\"" + KATEX_CSS + "\">" +
                "<link rel=\"stylesheet\" href=\"" + KATEX_SWAP_CSS + "\">" +
                "<script src=\"" + KATEX_JS + "\"></script>" +
                "<script src=\"" + KATEX_MHCHEM + "\"></script>" +
                "<script src=\"" + KATEX_COPY_TEX + "\"></script>" +
                "<script src=\"" + KATEX_MATHTEX + "\"></script>" +
                "<script src=\"" + KATEX_A11Y + "\"></script>" +
                "<script src=\"" + KATEX_AUTO_RENDER + "\"></script>" +
                "<style>" +
                "* { box-sizing: border-box; }" +
                "body { margin:0; padding:0; width:100%; background:transparent; font-family:Arial,sans-serif; " +
                "font-size:" + textSizeSp + "px; line-height:1.5; color:" + safeColor + "; font-weight:" + fontWeight + "; " +
                "word-wrap:break-word; overflow-wrap:anywhere; white-space:normal; }" +
                "#content { " + contentStyle + " }" +
                ".katex-display { margin: 6px 0 !important; overflow-x: auto; overflow-y: hidden; }" +
                "img { max-width:100%; height:auto; }" +
                "table { max-width:100%; border-collapse:collapse; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div id=\"content\"></div>" +
                "<script>" +
                "function renderKaTeX() {" +
                "   var element = document.getElementById('content');" +
                "   if(!element) return;" +
                "   element.innerHTML = " + jsText + ";" +
                "   if (window.renderMathInElement) {" +
                "       renderMathInElement(element, {" +
                "           delimiters: [" +
                "               {left: '$$', right: '$$', display: true}," +
                "               {left: '$', right: '$', display: false}," +
                "               {left: '\\\\(', right: '\\\\)', display: false}," +
                "               {left: '\\\\[', right: '\\\\]', display: true}" +
                "           ]," +
                "           throwOnError : false" +
                "       });" +
                "   }" +
                "   sendHeight();" +
                "}" +
                "function sendHeight() {" +
                "   setTimeout(function() {" +
                "       var element = document.getElementById('content');" +
                "       if(!element) return;" +
                "       var height = Math.ceil(Math.max(" +
                "           element.scrollHeight, document.body.scrollHeight, document.documentElement.scrollHeight" +
                "       ));" +
                "       if(height < 1) height = 1;" +
                "       if(window.localInterface) {" +
                "           window.localInterface.onRenderComplete(height);" +
                "       }" +
                "   }, 50);" +
                "}" +
                "window.onload = function() {" +
                "   renderKaTeX();" +
                "};" +
                "</script>" +
                "</body>" +
                "</html>";

        // ========================================================
        // JAVASCRIPT INTERFACE
        // ========================================================
        webView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void onRenderComplete(final int height) {
                webView.post(() -> {
                    ViewGroup.LayoutParams params = webView.getLayoutParams();
                    if (params == null) return;

                    float density = webView.getResources().getDisplayMetrics().density;
                    int newHeight = (int) Math.ceil(height * density) + (int) Math.ceil(4 * density);

                    if (optionStyle) {
                        int minimumHeight = (int) Math.ceil(48 * density);
                        newHeight = Math.max(newHeight, minimumHeight);
                    }

                    params.height = newHeight;
                    webView.setLayoutParams(params);
                    webView.requestLayout();

                    if (webView.getAlpha() < 1f) {
                        webView.animate().alpha(1f).setDuration(80).start();
                    }
                });
            }
        }, "localInterface");

        // ========================================================
        // WEB CLIENT
        // ========================================================
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.postDelayed(() -> view.evaluateJavascript("sendHeight();", null), 100);
            }
        });

        webView.loadDataWithBaseURL("file:///android_asset/", htmlData, "text/html", "UTF-8", null);
    }

    // ============================================================
    // HELPER METHODS
    // ============================================================
    public static void renderQuestion(WebView webView, String text) {
        render(webView, text, "#151515", 15, true, false);
    }

    public static void renderOption(WebView webView, String text) {
        render(webView, text, "#222222", 14, false, true);
    }

    public static void renderCorrectOption(WebView webView, String text) {
        render(webView, text, "#238347", 14, true, true);
    }

    public static void renderExplanation(WebView webView, String text) {
        render(webView, text, "#444444", 13, false, false);
    }

    private static String extractText(String value) {
        if (value == null || value.trim().isEmpty()) return "";
        String text = value.trim();
        try {
            if (text.startsWith("{") && text.endsWith("}")) {
                JSONObject object = new JSONObject(text);
                if (object.has("text")) return object.optString("text", "");
            }
            if (text.startsWith("\"") && text.endsWith("\"")) {
                return new JSONObject("{\"value\":" + text + "}").optString("value", text);
            }
        } catch (Exception ignored) {}
        return value;
    }

    public static void destroy(WebView webView) {
        if (webView == null) return;
        try {
            webView.stopLoading();
            webView.loadUrl("about:blank");
            webView.clearHistory();
            webView.removeAllViews();
            webView.destroy();
        } catch (Exception ignored) {}
    }
}