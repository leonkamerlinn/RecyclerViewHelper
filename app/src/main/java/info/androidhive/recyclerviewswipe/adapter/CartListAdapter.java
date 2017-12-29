package info.androidhive.recyclerviewswipe.adapter;

/**
 * Created by ravi on 26/09/17.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.leon.library.helper.RecyclerItemTouchHelper;

import java.util.Collections;
import java.util.List;

import info.androidhive.recyclerviewswipe.R;
import info.androidhive.recyclerviewswipe.model.Item;

import static android.support.v7.widget.helper.ItemTouchHelper.Callback.getDefaultUIUtil;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.CartViewHolder> implements RecyclerItemTouchHelper.Adapter {
    private Context context;
    private List<Item> cartList;
    public CartListAdapter(Context context, List<Item> cartList) {
        this.context = context;
        this.cartList = cartList;

    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_list_item, parent, false);


        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CartViewHolder holder, final int position) {
        final Item item = cartList.get(position);
        holder.name.setText(item.getName());
        holder.description.setText(item.getDescription());
        holder.price.setText("₹" + item.getPrice());

        Glide.with(context)
                .load(item.getThumbnail())
                .into(holder.thumbnail);


    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public void removeItem(int position) {
        cartList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(Item item, int position) {
        cartList.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }


    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(cartList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        removeItem(position);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags() {
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        final int swipeFlags = ItemTouchHelper.START  | ItemTouchHelper.END;
        return RecyclerItemTouchHelper.makeMovementFlags(dragFlags, swipeFlags);
    }


    public class CartViewHolder extends RecyclerView.ViewHolder implements RecyclerItemTouchHelper.ViewHolder {
        public TextView name, description, price;
        public ImageView thumbnail;
        public RelativeLayout viewBackground, viewBackground2, viewForeground;

        public CartViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            description = view.findViewById(R.id.description);
            price = view.findViewById(R.id.price);
            thumbnail = view.findViewById(R.id.thumbnail);
            viewBackground = view.findViewById(R.id.view_background);
            viewBackground2 = view.findViewById(R.id.view_background2);
            viewForeground = view.findViewById(R.id.view_foreground);

        }

        @Override
        public void onItemSelected() {
            thumbnail.setElevation(itemView.getContext().getResources().getDimension(R.dimen.default_spacing_small));
        }

        @Override
        public void onItemClear() {
            getDefaultUIUtil().clearView(viewForeground);
        }

        @Override
        public boolean onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                childDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                return true;
            }

            return false;
        }

        @Override
        public boolean onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                childDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                return true;
            }

            return false;
        }

        private void childDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            CartListAdapter.CartViewHolder holder = (CartListAdapter.CartViewHolder) viewHolder;
            if (dX < 0) {
                //left
                if (holder.viewBackground2.getVisibility() == View.VISIBLE) {
                    holder.viewBackground2.setVisibility(View.INVISIBLE);
                }


                if (holder.viewBackground.getVisibility() == View.INVISIBLE) {
                    holder.viewBackground.setVisibility(View.VISIBLE);
                }


            } else {
                //right

                if (holder.viewBackground.getVisibility() == View.VISIBLE) {
                    holder.viewBackground.setVisibility(View.INVISIBLE);
                }

                if (holder.viewBackground2.getVisibility() == View.INVISIBLE) {
                    holder.viewBackground2.setVisibility(View.VISIBLE);
                }
            }

            getDefaultUIUtil().onDraw(c, recyclerView, holder.viewForeground, dX, dY, actionState, isCurrentlyActive);
        }
    }
}
