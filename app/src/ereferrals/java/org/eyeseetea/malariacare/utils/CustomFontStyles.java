package org.eyeseetea.malariacare.utils;

public enum CustomFontStyles {
    Medium(org.eyeseetea.sdk.R.style.FontStyle_Medium, "font_medium"),
    Large(org.eyeseetea.sdk.R.style.FontStyle_Large, "font_large");

    private int resId;
    private String title;

    public int getResId() {
        return resId;
    }

    public String getTitle() {
        return title;
    }

    CustomFontStyles(int resId, String title) {
        this.resId = resId;
        this.title = title;
    }
}
