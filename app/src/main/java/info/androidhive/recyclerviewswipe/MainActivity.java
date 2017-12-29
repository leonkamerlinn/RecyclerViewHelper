package info.androidhive.recyclerviewswipe;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.View;

import com.leon.library.helper.RecyclerItemTouchHelper;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.recyclerviewswipe.adapter.CartListAdapter;

import info.androidhive.recyclerviewswipe.model.Item;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class MainActivity extends AppCompatActivity {
    public static final String BASE_URL = "https://api.androidhive.info/";

    private RecyclerView mRecyclerView;
    private List<Item> mItems = new ArrayList<>();
    private CartListAdapter mAdapter;
    private CoordinatorLayout mCoordinatorLayout;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setupActionBar();
        setupRecyclerView();
        fetchData();

    }

    private void setupActionBar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.my_cart));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds mItems to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void initViews() {
        mCoordinatorLayout = findViewById(R.id.coordinator_layout);
        mRecyclerView = findViewById(R.id.recycler_view);
        mToolbar = findViewById(R.id.toolbar);
    }

    private void setupRecyclerView() {

        mAdapter = new CartListAdapter(this, mItems);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);




        ItemTouchHelper.Callback itemTouchHelperCallback = new RecyclerItemTouchHelper(mAdapter) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                if (viewHolder instanceof CartListAdapter.CartViewHolder) {
                    // get the removed item name to display it in snack bar
                    String name = mItems.get(viewHolder.getAdapterPosition()).getName();

                    // backup of removed item for undo purpose
                    final Item deletedItem = mItems.get(viewHolder.getAdapterPosition());
                    final int deletedIndex = viewHolder.getAdapterPosition();



                    // showing snack bar with Undo option
                    Snackbar snackbar = Snackbar.make(mCoordinatorLayout, name + " removed from cart!", Snackbar.LENGTH_LONG);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            // undo is selected, restore the deleted item
                            mAdapter.restoreItem(deletedItem, deletedIndex);
                        }
                    });
                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();
                }

                super.onSwiped(viewHolder, direction);

            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

    }

    private void fetchData() {
        DataSource dataSource = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(DataSource.class);

        dataSource.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<Item>>() {
                    @Override
                    public void onNext(List<Item> items) {
                        // adding items to cart list
                        mItems.clear();
                        mItems.addAll(items);

                        // refreshing recycler view
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    interface DataSource {
        @GET("json/menu.json")
        Observable<List<Item>> getAll();
    }
}
