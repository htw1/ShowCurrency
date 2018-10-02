package com.example.htw.currencyconverter.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.htw.currencyconverter.R;
import com.example.htw.currencyconverter.callback.ClickCallback;
import com.example.htw.currencyconverter.callback.CustomItemClickListener;
import com.example.htw.currencyconverter.databinding.CurrencyBindingDate;
import com.example.htw.currencyconverter.databinding.CurrencyBindingItem;
import com.example.htw.currencyconverter.model.Currency;
import com.example.htw.currencyconverter.model.CurrencyBinding;
import com.example.htw.currencyconverter.model.CurrencyDate;
import com.example.htw.currencyconverter.model.CurrencyItemsList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class CurrencyRecyclerAdapter extends RecyclerView.Adapter<CurrencyRecyclerAdapter.AbstractCurrencyViewHolder>  {

    final private int CURRENCY_VALUE_VIEW = 0;
    final private int CURRENCY_DATE_VIEW = 1;


    CustomItemClickListener mCustomOnItemClickListener;
    ClickCallback clickCallback;

    @Nullable
    private List<CurrencyItemsList> currencyList = new ArrayList<>();

    public CurrencyRecyclerAdapter(@Nullable ClickCallback clickCallback) {

        this.clickCallback = clickCallback;
    }

    public void setProjectList(final Currency data) {

        final List<CurrencyItemsList> newList = ratesFactory(data.getRates(), data.getDate());

        if(this.currencyList != newList){
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new CurrencyDiffUtil(newList));
            result.dispatchUpdatesTo(this);
            currencyList.clear();
            currencyList.addAll(newList);
        }
    }


    public void updateProjectList(final Currency newData) {
        final List<CurrencyItemsList> newList = new ArrayList<>(currencyList);
        newList.addAll(ratesFactory(newData.getRates(), newData.getDate()));
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new CurrencyDiffUtil(newList));
        result.dispatchUpdatesTo(this);
        currencyList.clear();
        currencyList.addAll(newList);
    }

    private List<CurrencyItemsList> ratesFactory(Map<String, Double> mapResult, String date){
        List<CurrencyBinding> result= new ArrayList<>();
        for (Map.Entry<String, Double> entry : mapResult.entrySet()) {
            result.add(new CurrencyBinding(entry.getKey(),entry.getValue(),date));
        }
        Collections.sort(result, new CustomComparator());
        ArrayList<CurrencyItemsList> returnList = new ArrayList<>();
        returnList.add(new CurrencyDate(date));
        returnList.addAll(result);
        return returnList;
    }



    //SORTING BY KEY
    public class CustomComparator implements Comparator<CurrencyBinding> {
        @Override
        public int compare(CurrencyBinding o1, CurrencyBinding o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

    @Override
    public AbstractCurrencyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == CURRENCY_DATE_VIEW){
            CurrencyBindingDate bindingDate  = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_header_recycler, parent, false);

            return new CurrencyDateViewHolder(bindingDate);
        }
        if(viewType == CURRENCY_VALUE_VIEW){
            CurrencyBindingItem bindingValue = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.inner_recycler_layout, parent, false);

            bindingValue.setCallback(clickCallback);

            return new CurrencyViewHolder(bindingValue);
        }
        return null;
    }



    @Override
    public void onBindViewHolder(@NonNull AbstractCurrencyViewHolder holder, int position) {

        if(holder instanceof CurrencyViewHolder ){
            ((CurrencyViewHolder)holder).bindingValue.setCurrencyItem((CurrencyBinding) currencyList.get(position));
            // for test
/*            ((CurrencyViewHolder) holder).bindingValue.setClickCustomlistener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.w("onClick", ((CurrencyBinding) currencyList.get(position)).toString());
                        mCustomOnItemClickListener.onItemClick(view, (CurrencyBinding) currencyList.get(position));
                }
            });*/


            ((CurrencyViewHolder)holder).bindingValue.executePendingBindings();
        }

        if(holder instanceof CurrencyDateViewHolder){
            ((CurrencyDateViewHolder)holder).bindingValue.setCurrencyDate((CurrencyDate) currencyList.get(position));

            ((CurrencyDateViewHolder)holder).bindingValue.executePendingBindings();
        }
    }

    @Override
    public int getItemCount() {

         return currencyList == null ? 0 : currencyList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(currencyList.get(position) instanceof CurrencyBinding ){
            return CURRENCY_VALUE_VIEW;
        }
        if(currencyList.get(position) instanceof CurrencyDate){
            return CURRENCY_DATE_VIEW;
        }
        return -1;
    }

    abstract class AbstractCurrencyViewHolder extends RecyclerView.ViewHolder{

        private View itemView;

        public AbstractCurrencyViewHolder(View itemView) {
            super(itemView);
        }



    }

    class CurrencyViewHolder extends AbstractCurrencyViewHolder {

                CurrencyRecyclerAdapter adapter;
         final CurrencyBindingItem bindingValue;
        CurrencyBinding currencyItem;

        public CurrencyViewHolder(CurrencyBindingItem bindingValue) {
            super(bindingValue.getRoot());
            this.bindingValue = bindingValue;
        }
        public CurrencyBindingItem getBinding() {
            return bindingValue;
        }



    }

    class CurrencyDateViewHolder extends AbstractCurrencyViewHolder {

        final CurrencyBindingDate bindingValue;


        public CurrencyDateViewHolder(CurrencyBindingDate bindingValue) {
            super(bindingValue.getRoot());
            this.bindingValue = bindingValue;
        }
    }

    class CurrencyDiffUtil extends DiffUtil.Callback{

        List<CurrencyItemsList> newList;

        public CurrencyDiffUtil(List<CurrencyItemsList> newList){
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return currencyList.size() ;
        }

        @Override
        public int getNewListSize() {
            return newList.size() ;
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {

            if(currencyList.get(oldItemPosition) instanceof CurrencyDate &&  newList.get(newItemPosition) instanceof CurrencyDate){
                return ((CurrencyDate)currencyList.get(oldItemPosition)).getDate().equals(((CurrencyDate)newList.get(newItemPosition)).getDate());
            }else if(currencyList.get(oldItemPosition) instanceof CurrencyBinding &&  newList.get(newItemPosition) instanceof CurrencyBinding){
                return ((CurrencyBinding)currencyList.get(oldItemPosition)).getName().equals(((CurrencyBinding)newList.get(newItemPosition)).getName());
            }else{
                return false;
            }


        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            if(currencyList.get(oldItemPosition) instanceof CurrencyDate &&  newList.get(newItemPosition) instanceof CurrencyDate){
                return ((CurrencyDate)currencyList.get(oldItemPosition)).getDate().equals(((CurrencyDate)newList.get(newItemPosition)).getDate());
            }else if(currencyList.get(oldItemPosition) instanceof CurrencyBinding &&  newList.get(newItemPosition) instanceof CurrencyBinding){
                return ((CurrencyBinding)currencyList.get(oldItemPosition)).getValue().equals(((CurrencyBinding)newList.get(newItemPosition)).getValue());
            }else{
                return false;
            }
        }
    }

}
