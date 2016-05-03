package com.vitorog.nubankreport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Vitor on 02/05/2016.
 */
public class PurchasesAdapter extends ArrayAdapter<NubankPurchase> {

    List<NubankPurchase> purchasesList;

     public PurchasesAdapter(Context context, int resource, List<NubankPurchase> purchasesList) {
        super(context, resource, purchasesList);
        this.purchasesList = purchasesList;
    }

    public PurchasesAdapter(Context context, int resource) {
        super(context, resource);
        this.purchasesList = new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        if(v == null){
            v = LayoutInflater.from(getContext()).inflate(R.layout.purchase_item_row, null);//
        }

        NubankPurchase p = purchasesList.get(position);

        TextView place = (TextView) v.findViewById(R.id.place);
        place.setText(p.getPlace());

        TextView date = (TextView) v.findViewById(R.id.date);
        date.setText(p.getDate());

        TextView value = (TextView) v.findViewById(R.id.value);
        value.setText(p.getFormattedValueStr());
        return v;
    }

    @Override
    public int getCount() {
        return purchasesList.size();
    }

    public List<NubankPurchase> getPurchasesList(){
        return this.purchasesList;
    }

    public void addPurchase(NubankPurchase p) {
        purchasesList.add(p);
        sortPurchasesList();
        this.notifyDataSetChanged();
    }

    public NubankPurchase removePurchase(int position) {
        NubankPurchase p = purchasesList.get(position);
        purchasesList.remove(p);
        this.notifyDataSetChanged();
        return p;
    }

    public void clear() {
        purchasesList.clear();
        this.notifyDataSetChanged();
    }

    public boolean isDuplicated(NubankPurchase otherPurchase) {
        for(NubankPurchase p : purchasesList){
            if(p.getDisplayString().equals(otherPurchase.getDisplayString())){
                return true;
            }
        }
        return false;
    }

    private void sortPurchasesList() {
        //TOOD: Integrate all this with a custom adapter
        Collections.sort(purchasesList, new PurchasesComparator());
//        purchasesList.clear();
//        for(NubankPurchase p : purchasesList){
//            purchasesList.add(p.getDisplayString());
//        }
        this.notifyDataSetChanged();
    }

    class PurchasesComparator implements Comparator<NubankPurchase> {

        SimpleDateFormat dateFormat = new SimpleDateFormat(NubankPurchase.DATE_FORMAT);

        @Override
        public int compare(NubankPurchase p1, NubankPurchase p2) {
            try {
                return dateFormat.parse(p1.getDate()).compareTo(dateFormat.parse(p2.getDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return -1;
        }
    }
}
