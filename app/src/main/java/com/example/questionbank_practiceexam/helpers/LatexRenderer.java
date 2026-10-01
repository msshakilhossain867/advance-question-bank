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
    // DEFAULT RENDER (Overloaded methods for text only)
    // ============================================================
    public static void render(WebView webView, String rawText) {
        render(webView, rawText, null, "#222222", 16, false, false);
    }

    public static void render(WebView webView, String rawText, String imagePath) {
        render(webView, rawText, imagePath, "#222222", 16, false, false);
    }

    // ============================================================
    // MAIN RENDER
    // ============================================================
    @SuppressLint("SetJavaScriptEnabled")
    public static void render(
            WebView webView,
            String rawText,
            String imagePath,
            String textColor,
            int textSizeSp,
            boolean bold,
            boolean optionStyle
    ) {
        if (webView == null) return;

        // ========================================================
        // WEBVIEW RESET (পুরোনো ডেটা বা ব্যাখ্যা পরের প্রশ্নে যাওয়া রোধ করতে)
        // ========================================================
        webView.loadUrl("about:blank");
        webView.clearHistory();

        // নতুন কন্টেন্ট লোড হওয়ার শুরুতে WebView-র আলফা শূন্য করা এবং হাইট র‍্যাপ কন্টেন্টে সেট করা
        webView.setAlpha(0f);

        ViewGroup.LayoutParams initialParams = webView.getLayoutParams();
        if (initialParams != null) {
            initialParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            webView.setLayoutParams(initialParams);
        } else {
            webView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
        }

        // JSON বা normal text এবং external image path থেকে সঠিক ডেটা ও ইমেজ এক্সট্রাক্ট করা
        ParsedContent parsed = parseContent(rawText, imagePath);
        String cleanText = parsed.text;
        String finalImagePath = parsed.imagePath;

        boolean hasImage = (finalImagePath != null && !finalImagePath.trim().isEmpty());

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

        String safeColor = textColor == null ? "#222222" : textColor;
        String fontWeight = bold ? "700" : "400";

        // যদি অপশন স্টাইল হয় এবং ইমেজ না থাকে, তবে টেক্সট ভার্টিক্যালি সেন্টারে থাকবে
        String contentStyle;
        if (optionStyle && !hasImage) {
            contentStyle = "display:flex; align-items:center; width:100%; min-height:48px; box-sizing:border-box; padding:4px 8px;" +
                    "font-family:Arial,sans-serif; line-height:1.5; overflow:visible; word-wrap:break-word; overflow-wrap:anywhere; white-space:normal;";
        } else {
            contentStyle = "display:block; width:100%; box-sizing:border-box; padding:2px 0;" +
                    "overflow:visible; word-wrap:break-word; overflow-wrap:anywhere; white-space:normal;";
        }

        StringBuilder combinedHtml = new StringBuilder();

        // ১. লেখা (Text) থাকলে সেটি যোগ করা
        if (cleanText != null && !cleanText.trim().isEmpty()) {
            combinedHtml.append("<div id=\"text-container\" style=\"width:100%;\">")
                    .append(cleanText)
                    .append("</div>");
        }

        // ২. ইমেজ (Image) থাকলে সেটি লেখার নিচে যোগ করা
        if (hasImage) {
            String cleanPath = finalImagePath.startsWith("/") ? finalImagePath.substring(1) : finalImagePath;
            String fullImgUrl = cleanPath.startsWith("file:///android_asset/") ? cleanPath : "file:///android_asset/" + cleanPath;

            combinedHtml.append("<div style=\"margin-top: 6px; text-align: center; width:100%;\">")
                    .append("<img id=\"dynamic-img\" src=\"").append(fullImgUrl).append("\" />")
                    .append("</div>");
        }

        String jsText;
        try {
            jsText = JSONObject.quote(combinedHtml.toString());
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
                "html, body { height: auto; min-height: 100%; margin: 0; padding: 0; }" +
                "body { width:100%; background:transparent; font-family:Arial,sans-serif; " +
                "font-size:" + textSizeSp + "px; line-height:1.5; color:" + safeColor + "; font-weight:" + fontWeight + "; " +
                "word-wrap:break-word; overflow-wrap:anywhere; white-space:normal; overflow:visible; " +
                (optionStyle && !hasImage ? "display: flex; align-items: center; min-height: 48px;" : "") + " }" +
                "#content { " + contentStyle + " }" +
                ".katex-display { margin: 4px 0 !important; overflow-x: auto; overflow-y: hidden; }" +
                "img { max-width: 100%; max-height: 160px; height: auto; width: auto; display: block; margin: 0 auto; object-fit: contain; }" +
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

                // আগের বড় হাইট আটকে থাকা রোধ করতে প্রথমে হাইট রিসেট করা
                "   if(window.localInterface) {" +
                "       window.localInterface.onRenderComplete(48);" +
                "   }" +

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
                "   var img = document.getElementById('dynamic-img');" +
                "   if (img) {" +
                "       img.onload = function() { sendHeight(); };" +
                "       img.onerror = function() { sendHeight(); };" +
                "       if (img.complete) { sendHeight(); }" +
                "   } else {" +
                "       setTimeout(sendHeight, 30);" +
                "   }" +
                "}" +
                "function sendHeight() {" +
                "   var body = document.body, html = document.documentElement;" +
                "   var height = Math.max( body.scrollHeight, body.offsetHeight, html.clientHeight, html.scrollHeight, html.offsetHeight );" +
                "   if(height < 48) height = 48;" +
                "   if(window.localInterface) {" +
                "       window.localInterface.onRenderComplete(height);" +
                "   }" +
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
                    int newHeight = (int) Math.ceil(height * density);

                    if (params.height != newHeight) {
                        params.height = newHeight;
                        webView.setLayoutParams(params);
                        webView.requestLayout();
                    }

                    // স্মুথ এবং প্রিমিয়াম ফেড-ইন অ্যানিমেশন (আসার সময় জাদুকরী লুক দেবে)
                    if (webView.getAlpha() < 1f) {
                        webView.animate()
                                .alpha(1f)
                                .setDuration(180) // অ্যানিমেশনের সময়কাল (মিলিসেকেন্ড)
                                .start();
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
                view.evaluateJavascript("renderKaTeX();", null);
            }
        });

        webView.loadDataWithBaseURL("file:///android_asset/", htmlData, "text/html", "UTF-8", null);
    }

    // ============================================================
    // HELPER CLASSES & METHODS FOR PARSING
    // ============================================================

    public static boolean containsLaTeXOrHtml(String text) {
        if (text == null) return false;
        return text.contains("$") || text.contains("\\\\(") || text.contains("\\\\[") ||
                text.contains("<") || text.contains("&") || text.contains("\\");
    }

    private static class ParsedContent {
        String text;
        String imagePath;

        ParsedContent(String text, String imagePath) {
            this.text = text;
            this.imagePath = imagePath;
        }
    }

    private static ParsedContent parseContent(String rawText, String externalImagePath) {
        String text = "";
        String imagePath = (externalImagePath != null && !externalImagePath.trim().isEmpty()) ? externalImagePath : "";

        if (rawText != null) {
            String trimmed = rawText.trim();
            try {
                if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
                    JSONObject object = new JSONObject(trimmed);

                    if (object.has("text")) {
                        text = object.optString("text", "");
                    } else {
                        text = rawText;
                    }

                    if (imagePath.isEmpty()) {
                        if (object.has("image")) {
                            String img = object.optString("image", "");
                            if (img != null && !img.trim().isEmpty()) {
                                imagePath = img;
                            }
                        } else if (object.has("imagePath")) {
                            String img = object.optString("imagePath", "");
                            if (img != null && !img.trim().isEmpty()) {
                                imagePath = img;
                            }
                        }
                    }
                } else if (trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
                    text = new JSONObject("{\"value\": " + trimmed + "}").optString("value", trimmed);
                } else {
                    text = rawText;
                }
            } catch (Exception e) {
                text = rawText;
            }
        }

        // ========================================================
        // ESCAPE SEQUENCE CLEANING (Fixes \n, \n\n displaying as text)
        // ========================================================
        if (text != null) {
            // যদি টেক্সটের ভেতর literal বা টেক্সট আকারে \n বা \n\n থাকে সেগুলোকে রূপান্তর করা
            text = text.replace("\\n\\n", "<br><br>");
            text = text.replace("\\n", "<br>");
            // সাধারণ নিউলাইন ক্যারেক্টার থাকলেও যেন এইচটিএমএলে ভাঙে
            text = text.replace("\n", "<br>");
        }

        return new ParsedContent(text, imagePath);
    }

    public static void renderQuestion(WebView webView, String text, String imagePath) {
        render(webView, text, imagePath, "#151515", 15, true, false);
    }
    public static void renderQuestion(WebView webView, String text) {
        renderQuestion(webView, text, null);
    }

    public static void renderOption(WebView webView, String text, String imagePath) {
        render(webView, text, imagePath, "#222222", 14, false, true);
    }
    public static void renderOption(WebView webView, String text) {
        renderOption(webView, text, null);
    }

    public static void renderCorrectOption(WebView webView, String text, String imagePath) {
        render(webView, text, imagePath, "#238347", 14, true, true);
    }
    public static void renderCorrectOption(WebView webView, String text) {
        renderCorrectOption(webView, text, null);
    }

    public static void renderExplanation(WebView webView, String text, String imagePath) {
        render(webView, text, imagePath, "#444444", 13, false, false);
    }
    public static void renderExplanation(WebView webView, String text) {
        renderExplanation(webView, text, null);



    }

    // বাইরের ক্লাস থেকে JSON বা Raw টেক্সট ক্লিন করার জন্য
    public static String getCleanText(String rawText) {
        ParsedContent parsed = parseContent(rawText, null);
        return parsed.text != null ? parsed.text : "";
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