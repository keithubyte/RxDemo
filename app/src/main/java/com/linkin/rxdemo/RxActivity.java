package com.linkin.rxdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RxActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RxAndroid";

    private Button tButton, rButton, dButton;
    private GridView mGridView;
    private ImageAdapter mAdapter;
    private List<Bitmap> mBitmaps = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx);

        tButton = (Button) findViewById(R.id.traditional_way_to_load_bitmaps);
        rButton = (Button) findViewById(R.id.rx_way_to_load_bitmaps);
        dButton = (Button) findViewById(R.id.do_something);
        mGridView = (GridView) findViewById(R.id.images);

        tButton.setOnClickListener(this);
        rButton.setOnClickListener(this);
        dButton.setOnClickListener(this);

        mAdapter = new ImageAdapter(mBitmaps);
        mGridView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.traditional_way_to_load_bitmaps:
                traditionalWayToLoadBitmaps();
                break;
            case R.id.rx_way_to_load_bitmaps:
                rxWayToLoadBitmaps();
                break;
            case R.id.do_something:
                doSomething();
                break;
            default:
                break;
        }
    }

    private void traditionalWayToLoadBitmaps() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File directory = new File("/sdcard/Pictures");
                if (directory.exists() && directory.canRead()) {
                    mBitmaps.clear();
                    for (File file : directory.listFiles()) {
                        if (file.getAbsolutePath().endsWith(".png")) {
                            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                            mBitmaps.add(bitmap);
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }).start();
    }

    private void rxWayToLoadBitmaps() {
        File directory = new File("/sdcard/Pictures");
        if (directory.exists() && directory.canRead()) {
            mBitmaps.clear();
            Observable.from(directory.listFiles())
                    .filter(new Func1<File, Boolean>() {
                        @Override
                        public Boolean call(File file) {
                            return file.getAbsolutePath().endsWith(".png");
                        }
                    })
                    .map(new Func1<File, Bitmap>() {
                        @Override
                        public Bitmap call(File file) {
                            return BitmapFactory.decodeFile(file.getAbsolutePath());
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Bitmap>() {
                        @Override
                        public void call(Bitmap bitmap) {
                            mBitmaps.add(bitmap);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
        }
    }

    private void doSomething() {
        a();
    }

    private void rxWayToLoadBitmaps2() {
        File directory = new File("/sdcard/Pictures");
        if (directory.exists() && directory.canRead()) {
            Observable<File> observable = Observable.from(directory.listFiles());

            Action1<Bitmap> action = new Action1<Bitmap>() {
                @Override
                public void call(Bitmap bitmap) {
                    mBitmaps.add(bitmap);
                    mAdapter.notifyDataSetChanged();
                }
            };
        }
    }

    private void a() {
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onCompleted() {
                Log.e("RxAndroid", "observer -- onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e("RxAndroid", "observer -- onError");
            }

            @Override
            public void onNext(String s) {
                Log.e("RxAndroid", "observer -- onNext | " + s);
            }
        };

        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.e("RxAndroid", "subscriber -- onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e("RxAndroid", "subscriber -- onError");
            }

            @Override
            public void onNext(String s) {
                Log.e("RxAndroid", "subscriber -- onNext | " + s);
            }
        };

        Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("Hello");
                subscriber.onNext("Hi");
                subscriber.onNext("Aloha");
                subscriber.onCompleted();
            }
        });

        observable.subscribe(subscriber);
    }
}
