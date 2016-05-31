package com.vitorog.nubankreport;

import android.provider.BaseColumns;

/**
 * Created by vitor.gomes on 27/04/2016.
 */
public class NubankPurchasesContract {

    public NubankPurchasesContract() {
        // Do nothing
    }

    public static abstract class PurchaseEntry implements BaseColumns{
        public static final String TABLE_NAME = "purchases";
        public static final String COLUMN_NAME_VALUE = "value";
        public static final String COLUMN_NAME_PLACE = "place";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    }
}
