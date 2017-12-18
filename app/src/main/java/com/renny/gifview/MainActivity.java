package com.renny.gifview;

import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.renny.gifview.toast.ToastHelper;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    GifImageView gifImageView;
    Button pauseBtn;
    boolean hasPaused = true;
    TextView percentTv;
    SeekBar mSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pauseBtn = (Button) findViewById(R.id.pause);
        pauseBtn.setVisibility(View.GONE);
        mSeekBar = (SeekBar) findViewById(R.id.seek);
        mSeekBar.setOnSeekBarChangeListener(this);
        percentTv = (TextView) findViewById(R.id.percent);
        gifImageView = (GifImageView) findViewById(R.id.gif);
        gifImageView.setGifResource(R.drawable.meizhi, new GifImageView.OnPlayListener() {
            @Override
            public void onPlayStart() {
                ToastHelper.getInstance(MainActivity.this.getApplication()).makeToast("开始");
            }

            @Override
            public void onPlaying(@FloatRange(from = 0f, to = 1.0f) float percent) {
                int per = Math.round(percent * 100);
                mSeekBar.setProgress(per);
                percentTv.setText("播放进度: " + per + "%");
            }

            @Override
            public void onPlayPause(boolean pauseSuccess) {
                if (pauseSuccess)
                    ToastHelper.getInstance(MainActivity.this.getApplication()).makeToast("暂停成功");
                else {
                    ToastHelper.getInstance(MainActivity.this.getApplication()).makeToast("暂停失败");
                }
            }

            @Override
            public void onPlayRestart() {
                ToastHelper.getInstance(MainActivity.this.getApplication()).makeToast("继续");
            }

            @Override
            public void onPlayEnd() {
                ToastHelper.getInstance(MainActivity.this.getApplication()).makeToast("结束");
                pauseBtn.setVisibility(View.GONE);
            }
        });

    }

    public void palycycle(View view) {
        hasPaused = false;
        pauseBtn.setText("暂停");
        gifImageView.play(-1);
        pauseBtn.setVisibility(View.VISIBLE);
    }

    public void palyreverse(View view) {
        hasPaused = false;
        pauseBtn.setText("暂停");
        gifImageView.playReverse();
        pauseBtn.setVisibility(View.VISIBLE);
    }

    public void palyone(View view) {
        hasPaused = false;
        pauseBtn.setText("暂停");
        gifImageView.play(1);
        pauseBtn.setVisibility(View.VISIBLE);
    }

    public void pause(View view) {
        if (hasPaused) {
            pauseBtn.setText("继续");
            gifImageView.play();
        } else {
            pauseBtn.setText("暂停");
            gifImageView.pause();
        }
        hasPaused = !hasPaused;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.d("onProgressChanged", "  " + progress);
        float per = progress;
        gifImageView.setPercent(per / 100f);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


}
