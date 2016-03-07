package shekar.com.popularmovies.ui.movielist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import shekar.com.popularmovies.BaseApplication;
import shekar.com.popularmovies.R;
import shekar.com.popularmovies.model.ResultsPage;
import shekar.com.popularmovies.services.ApiService;
import shekar.com.popularmovies.utils.NetworkConnectionUtils;
import shekar.com.popularmovies.utils.RecyclerViewItemClickListener;

public class MoviesListActivity extends AppCompatActivity  implements RecyclerViewItemClickListener {

    @Bind(R.id.all_genre_recyclerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.loadingView)
    ProgressBar mProgressBar;
    @Bind(R.id.errorView)
    TextView mErrorText;
    MoviesListAdapter mAdapter;

    @Inject
    ApiService mApiService;
    @Inject
    NetworkConnectionUtils connectionUtils;
    private String mSortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        (((BaseApplication) getApplication()).getComponent()).inject(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setUI();
        if (connectionUtils.isConnected()) {
            showProgress();
            getMovies();
        } else {
            showErrorMsg(getString(R.string.no_network));
        }
    }
    private void setUI() {
        mSortOrder=getResources().getString(R.string.sort_order_popular);
        mAdapter=new MoviesListAdapter(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void getMovies() {
        Call<ResultsPage> call=mApiService.getMovies(mSortOrder, getResources().getString(R.string.api_key));
        call.enqueue(new Callback<ResultsPage>() {
            @Override
            public void onResponse(Response<ResultsPage> response, Retrofit retrofit) {
                hideProgress();
                mAdapter.setData(response.body().getResults());
            }

            @Override
            public void onFailure(Throwable t) {
                showErrorMsg(getResources().getString(R.string.data_error));
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {

    }
    private void showErrorMsg(String errorMsg) {
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        mErrorText.setVisibility(View.VISIBLE);
        mErrorText.setText(errorMsg);
    }

    private void hideProgress() {
        if (mProgressBar.getVisibility() == View.VISIBLE) {
            mProgressBar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mErrorText.setVisibility(View.GONE);
        }
    }

    private void showProgress() {
        if (mProgressBar.getVisibility() != View.VISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            mErrorText.setVisibility(View.GONE);
        }
    }
}
