package com.oldbaby.oblib.mvp.view.pullrefresh;

import android.view.ViewGroup;

public abstract class PullRecyclerViewAdapter<VH extends RecyclerViewHolder> {

    private PullRecyclerAdapterShell adpaterShell;

    public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);

    public abstract void onBindViewHolder(VH holder, int position);

    public int getItemViewType(int position) {
        return 0;
    }

    public int getSpanSize(int position, int total) {
        return 1;
    }

    public void setAdpaterShell(PullRecyclerAdapterShell adpaterShell) {
        this.adpaterShell = adpaterShell;
    }

    public PullRecyclerAdapterShell getAdpaterShell() {
        return adpaterShell;
    }

}