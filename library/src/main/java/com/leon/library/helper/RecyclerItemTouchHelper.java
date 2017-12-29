package com.leon.library.helper;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by ravi on 29/09/17.
 */

public class RecyclerItemTouchHelper extends ItemTouchHelper.Callback {
    private final RecyclerItemTouchHelper.Adapter mAdapterHelper;


    public RecyclerItemTouchHelper(RecyclerItemTouchHelper.Adapter adapter) {
        mAdapterHelper = adapter;
    }




    @Override
    public boolean isLongPressDragEnabled() {
        return mAdapterHelper.isLongPressDragEnabled();
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return mAdapterHelper.isItemViewSwipeEnabled();
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        if (source.getItemViewType() != target.getItemViewType()) {
            return false;
        }

        // Notify the adapter of the move
        mAdapterHelper.onItemMove(source.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        // Notify the adapter of the dismissal
        mAdapterHelper.onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {

        // We only want the active item to change
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof RecyclerItemTouchHelper.ViewHolder) {
                // Let the view holder know that this item is being moved or dragged
                RecyclerItemTouchHelper.ViewHolder itemViewHolder = (RecyclerItemTouchHelper.ViewHolder) viewHolder;
                itemViewHolder.onItemSelected();
            }
        }
        super.onSelectedChanged(viewHolder, actionState);

    }


    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        RecyclerItemTouchHelper.ViewHolder viewHolderHelper = (RecyclerItemTouchHelper.ViewHolder) viewHolder;
        // Tell the view holder it's time to restore the idle state
        viewHolderHelper.onItemClear();
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        RecyclerItemTouchHelper.ViewHolder viewHolderHelper = (RecyclerItemTouchHelper.ViewHolder) viewHolder;
        if (!viewHolderHelper.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)) {
            super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        RecyclerItemTouchHelper.ViewHolder viewHolderHelper = (RecyclerItemTouchHelper.ViewHolder) viewHolder;
        if (!viewHolderHelper.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }



    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return mAdapterHelper.getMovementFlags();
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }


    public interface ViewHolder {
        void onItemSelected();
        void onItemClear();
        boolean onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive);
        boolean onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive);
    }

    public interface Adapter {
        boolean onItemMove(int fromPosition, int toPosition);
        void onItemDismiss(int position);
        boolean isLongPressDragEnabled();
        boolean isItemViewSwipeEnabled();
        int getMovementFlags();
    }
}
