package com.main.maybe.miplayer.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;

import com.main.maybe.miplayer.R;
import com.main.maybe.miplayer.model.AlbumBean;
import com.main.maybe.miplayer.model.ArtistBean;
import com.main.maybe.miplayer.model.SingleBean;
import com.main.maybe.miplayer.presenter.LocalMusicPresenter;
import com.main.maybe.miplayer.ui.adapter.AlbumListAdapter;
import com.main.maybe.miplayer.ui.adapter.ArtistListAdapter;
import com.main.maybe.miplayer.ui.adapter.FolderListAdapter;
import com.main.maybe.miplayer.ui.adapter.SingleListAdapter;
import com.main.maybe.miplayer.ui.view.BaseView;

import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Observer;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Lucifer on 2015/3/25.
 */
public class HomeFragment extends Fragment implements BaseView{

    private static final String ARG_POSITION = "position";
    private int position;

    private LocalMusicPresenter mPresenter;
    private Subscription mSubscription;

    public static HomeFragment newInstance(int position){
        HomeFragment f = new HomeFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
        initPresenter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        FrameLayout fl = new FrameLayout(getActivity());
        fl.setLayoutParams(params);

        final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        params.setMargins(margin, margin, margin ,margin);
        // switch options
        switch (position) {
            case 0:
                // songs
                fl.addView(initSingle());
                break;
            case 1:
                // artist
                fl.addView(initArtists());
                break;
            case 2:
                // album
                fl.addView(initAlbum());
                break;
            case 3:
                // list
                fl.addView(initFolder());
                break;
        }
        return fl;
    }

    @Override
    public void onDestroy() {
        if (mSubscription != null && mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
        super.onDestroy();
    }

    @Override
    public void initPresenter() {
        mPresenter = new LocalMusicPresenter(getActivity(), this);
    }

    // init single list
    public LinearLayout initSingle(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        LinearLayout root = (LinearLayout)inflater.inflate(R.layout.base_list, null);
        RecyclerView rv = (RecyclerView) root.findViewById(R.id.rv_ls);
        final SingleListAdapter adapter = new SingleListAdapter();
        rv.setAdapter(adapter);
        // create an observer
        Observer<List<SingleBean>> observer = new Observer<List<SingleBean>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<SingleBean> singleBeen) {
                if (singleBeen != null && !singleBeen.isEmpty())
                    adapter.updateData(singleBeen);
            }
        };
        mSubscription = Observable.create(new Observable.OnSubscribe<List<SingleBean>>() {
            @Override
            public void call(Subscriber<? super List<SingleBean>> subscriber) {
                subscriber.onNext(mPresenter.getSingle());
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
        return root;
    }

    // init artist list
    public LinearLayout initArtists(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        LinearLayout root = (LinearLayout)inflater.inflate(R.layout.base_list, null);
        RecyclerView rv = (RecyclerView) root.findViewById(R.id.rv_ls);
        final ArtistListAdapter adapter = new ArtistListAdapter();
        rv.setAdapter(adapter);
        // create an observer
        Observer<List<ArtistBean>> observer = new Observer<List<ArtistBean>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<ArtistBean> singleBeen) {
                if (singleBeen != null && !singleBeen.isEmpty())
                    adapter.updateData(singleBeen);
            }
        };
        mSubscription = Observable.create(new Observable.OnSubscribe<List<ArtistBean>>() {
            @Override
            public void call(Subscriber<? super List<ArtistBean>> subscriber) {
                subscriber.onNext(mPresenter.getArtist());
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
        return root;
    }

    // init album list
    public LinearLayout initAlbum(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        LinearLayout root = (LinearLayout)inflater.inflate(R.layout.base_list, null);
        RecyclerView rv = (RecyclerView) root.findViewById(R.id.rv_ls);
        final AlbumListAdapter adapter = new AlbumListAdapter();
        rv.setAdapter(adapter);
        Single<List<AlbumBean>> single = Single.fromCallable(new Callable<List<AlbumBean>>() {
            @Override
            public List<AlbumBean> call() throws Exception {
                return mPresenter.getAlbum();
            }
        });
        mSubscription = single.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<List<AlbumBean>>() {
                    @Override
                    public void onSuccess(List<AlbumBean> value) {
                        if ( value != null && !value.isEmpty()) {
                            adapter.updateData(value);
                        }
                    }

                    @Override
                    public void onError(Throwable error) {

                    }
                });
        return root;
    }

    // init folder list
    public LinearLayout initFolder(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        LinearLayout root = (LinearLayout)inflater.inflate(R.layout.base_list, null);
        RecyclerView rv = (RecyclerView) root.findViewById(R.id.rv_ls);
        FolderListAdapter adapter = new FolderListAdapter();
        rv.setAdapter(adapter);
        return root;
    }
}
