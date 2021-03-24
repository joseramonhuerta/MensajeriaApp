package com.eletronica.mensajeriaapp;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class MyDiffUtilCallback extends DiffUtil.Callback {
    private List<Pedido> oldList;
    private List<Pedido> newList;

    public MyDiffUtilCallback(List<Pedido> oldList, List<Pedido> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }


    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldItemPosition == newItemPosition;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition) == newList.get(newItemPosition);
    }
}
