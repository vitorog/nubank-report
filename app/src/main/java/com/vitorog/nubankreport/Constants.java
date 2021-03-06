package com.vitorog.nubankreport;

/**
 * Created by Vitor on 26/04/2016.
 */
public class Constants {
    public static final String NUBANK_NOTIFICATION_LISTENER_INTENT = "com.vitorog.nubankreport.NUBANK_NOTIFICATION_LISTENER";
    public static final String NUBANK_REPORT_MAIN_ACTIVITY_INTENT = "com.vitorog.nubankreport.NUBANK_REPORT_MAIN_ACTIVITY_INTENT";
    public static final String ANDROID_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";

    public static final String PACKAGE_KEY = "package";
    public static final String TICKER_KEY = "ticker";
    public static final String POST_TIME_KEY = "posttime";
    public static final String ID_KEY = "id";
    public static final String TITLE_KEY = "title";
    public static final String TEXT_KEY = "text";

    public static final String ANDROID_NOTIFICATION_TITLE = "android.title";
    public static final String ANDROID_NOTIFICATION_TEXT = "android.text";

    public static final String NUBANK_NOTIFICATION_TAG = "Nubank";
    public static final String NUBANK_NOTIFICATION_TITLE = "Compra no cartão Nubank";
    public static final String NUBANK_NOTIFICATION_TEXT_EXAMPLE = " em Loja Exemplo";
    public static final String NUBANK_VALUE_PLACE_SEPARATOR = "em";
    public static final String NUBANK_BRAZILIAN_CURRENCY_SYMBOL = "R\u2060$";
    public static final String NUBANK_CURRENCY_COMMA_CHAR = ",";
    public static final String NUBANK_CURRENCY_DOT_CHAR = ".";

    // Google Spreadsheet allows splitting by semicolon when pasting text
    public static final String SPREASHEET_COLUMN_DELIMITER = ";";

    public static final int ACCOUNT_PICKER_INTENT = 1;
    public static final int EMAIL_EXPORT_INTENT = 2;

    // Max timestamp difference used when checking for duplicate notifications
    public static final long MAX_TIMESTAMP_DIFF = 10000;
}
