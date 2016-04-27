package com.vitorog.nubankreport;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Vitor on 26/04/2016.
 */
public class NubankPurchase {

    private Double value;
    private String formattedValueStr;
    private String place;
    private String date;
    private String notificationPackage;
    private String notificationText;
    private String notificationTitle;

    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final String TAG = "NubankPurchase";

    public NubankPurchase(Intent intent){
        if(intent.getAction() == Constants.NUBANK_PURCHASE_LISTENER_INTENT) {
            Bundle extras = intent.getExtras();
            notificationTitle = extras.getString(Constants.TITLE_KEY);
            notificationText = extras.getString(Constants.TEXT_KEY);
            notificationPackage = extras.getString(Constants.PACKAGE_KEY);
            date = getFormattedDate(extras.getLong(Constants.POST_TIME_KEY));
            parseNotificationText();
        }else{
            Log.w(TAG, "Invalid intent.");
            value = -1.0;
        }
    }

    public NubankPurchase(String formattedValueStr, String place, String date){
        this.formattedValueStr = formattedValueStr;
        this.place = place;
        this.date = date;
        String valueStr = formattedValueStr.replaceFirst(Constants.NUBANK_CURRENCY_COMMA_CHAR, Constants.NUBANK_CURRENCY_DOT_CHAR);
        value = Double.valueOf(valueStr);
    }

    private void parseNotificationText() {
        if(notificationText == null){
            value = -1.0;
            Log.w(TAG, "Invalid notification.");
            return;
        }
        int separatorPos = notificationText.indexOf(Constants.NUBANK_VALUE_PLACE_SEPARATOR);
        int currencySymbolPos = notificationText.indexOf(Constants.NUBANK_BRAZILIAN_CURRENCY_SYMBOL);
        if(separatorPos != -1 && currencySymbolPos != -1){
            String valueStr = notificationText.substring(currencySymbolPos + Constants.NUBANK_BRAZILIAN_CURRENCY_SYMBOL.length(),
                    separatorPos - 1);
            if(valueStr.contains(Constants.NUBANK_CURRENCY_COMMA_CHAR)) {
                formattedValueStr = valueStr.trim();
                valueStr = formattedValueStr.replaceFirst(Constants.NUBANK_CURRENCY_COMMA_CHAR, Constants.NUBANK_CURRENCY_DOT_CHAR);
                value = Double.valueOf(valueStr);
            }else{
                Log.w(TAG, "Invalid purchase value format.");
                value = -1.0;
            }
            place = notificationText.substring(separatorPos + Constants.NUBANK_VALUE_PLACE_SEPARATOR.length());
        }else{
            Log.w(TAG, "Invalid notification format!");
            Log.w(TAG, "Purchase register will be invalid.");
            value = -1.0;
        }
    }

    private String getFormattedDate(Long postTime){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(postTime);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        String dateStr = simpleDateFormat.format(new Date(postTime));
        return dateStr;
    }

    // Returns the string in the format of my spreadsheet
    public String getFormattedString() {
        return getPlace() +
                Constants.SPREASHEET_COLUMN_DELIMITER + formattedValueStr +
                Constants.SPREASHEET_COLUMN_DELIMITER + Constants.NUBANK_NOTIFICATION_TAG +
                Constants.SPREASHEET_COLUMN_DELIMITER + date +
                Constants.SPREASHEET_COLUMN_DELIMITER + "1";
    }

    public String getDisplayString() {
        return getPlace() + " - " + Constants.NUBANK_BRAZILIAN_CURRENCY_SYMBOL + Double.toString(getValue()) + " - " + getDate();
    }

    public Double getValue() {
        return value;
    }

    public String getPlace() {
        return place;
    }

    public String getDate() {
        return date;
    }

    public String getNotificationPackage() {
        return notificationPackage;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public String getFormattedValueStr() {  return formattedValueStr;   }

    public Boolean isValid() { return value != -1.0; }
}
