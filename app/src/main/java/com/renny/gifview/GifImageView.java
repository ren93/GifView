package com.renny.gifview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.SystemClock;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by LuckyCrystal on 2017/3/9.
 */


public class GifImageView extends AppCompatImageView {

    private static final int DEFAULT_DURATION = 1000;

    private Movie movie;

    private long mMovieStart;
    private long mMoviePauseTime;
    private long dealyTime;
    float percent;
    private int counts = -1;
    private volatile boolean mPaused;
    private volatile boolean hasStart;
    private boolean mVisible = true;

    private OnPlayListener mOnPlayListener;
    private int movieDuration;

    public GifImageView(Context context) {
        this(context, null);
    }

    public GifImageView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public GifImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setViewAttributes(context, attrs, defStyle);
    }

    private void setViewAttributes(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.GIFVIEW, defStyle, 0);

        int srcID = a.getResourceId(R.styleable.GIFVIEW_gifSrc, 0);
        boolean authPlay = a.getBoolean(R.styleable.GIFVIEW_authPlay, true);
        counts = a.getInt(R.styleable.GIFVIEW_playCount, -1);
        if (srcID > 0) {
            setGifResource(srcID, null);
            if (authPlay) play(counts);
        }
        a.recycle();
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public void setGifResource(int movieResourceId, OnPlayListener onPlayListener) {
        mOnPlayListener = onPlayListener;
        movie = Movie.decodeStream(getResources().openRawResource(movieResourceId));
        if (movie == null) {
            //如果movie为空，那么就不是gif文件，尝试转换为bitmap显示
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), movieResourceId);
            if (bitmap != null) {
                setImageBitmap(bitmap);
                return;
            }
        }
        movieDuration = movie.duration() == 0 ? DEFAULT_DURATION : movie.duration();
        requestLayout();
    }

    public void setGifResource(final String path, OnPlayListener onPlayListener) {
        movie = Movie.decodeFile(path);
        mOnPlayListener = onPlayListener;
        if (movie == null) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            if (bitmap != null) {
                setImageBitmap(bitmap);
                return;
            }
        }
        movieDuration = movie.duration() == 0 ? DEFAULT_DURATION : movie.duration();
        requestLayout();
        if (mOnPlayListener != null) {
            mOnPlayListener.onPlayStart();
        }
    }


    public void playOver() {
        if (movie != null) {
            play(-1);
        }
    }

    public void playReserver() {
        if (movie != null) {
            reset();
            reverse = true;
            if (mOnPlayListener != null) {
                mOnPlayListener.onPlayStart();
            }
            invalidate();
        }
    }

    public void play(int counts) {
        this.counts = counts;
        reset();
        if (mOnPlayListener != null) {
            mOnPlayListener.onPlayStart();
        }
        invalidate();
    }

    private void reset() {
        reverse = false;
        mMovieStart = SystemClock.uptimeMillis();
        mPaused = false;
        hasStart = true;
        mMoviePauseTime = 0;
        dealyTime = 0;
    }

    public void play() {
        if (movie == null)
            return;
        if (hasStart) {
            if (mPaused && mMoviePauseTime > 0) {
                mPaused = false;
                dealyTime = dealyTime + SystemClock.uptimeMillis() - mMoviePauseTime;
                invalidate();
                if (mOnPlayListener != null) {
                    mOnPlayListener.onPlayRestart();
                }
            }
        } else {
            play(-1);
        }
    }

    public void pause() {
        if (movie != null && !mPaused && hasStart) {
            mPaused = true;
            invalidate();
            mMoviePauseTime = SystemClock.uptimeMillis();
            if (mOnPlayListener != null) {
                mOnPlayListener.onPlayPause(true);
            }
        } else {
            if (mOnPlayListener != null) {
                mOnPlayListener.onPlayPause(false);
            }
        }
    }

    private int getCurrentFrameTime() {
        if (movieDuration == 0)
            return 0;
        long now = SystemClock.uptimeMillis() - dealyTime;
        int nowCount = (int) ((now - mMovieStart) / movieDuration);
        if (counts != -1 && nowCount >= counts) {
            hasStart = false;
            if (mOnPlayListener != null) {
                mOnPlayListener.onPlayEnd();
            }
        }
        int currentTime = (int) ((now - mMovieStart) % movieDuration);
        percent = ((float) currentTime) / ((float) movieDuration);
        if (mOnPlayListener != null && hasStart) {
            mOnPlayListener.onPlaying(percent);
        }
        return currentTime;
    }

    public void setPercent(float percent) {
        if (movie != null && movieDuration > 0) {
            this.percent = percent;
            movie.setTime((int) (movieDuration * percent));
            invalidateView();
            if (mOnPlayListener != null) {
                mOnPlayListener.onPlaying(percent);
            }
        }

    }

    public boolean isPaused() {
        return this.mPaused;
    }

    public boolean isPlaying() {
        return !this.mPaused && hasStart;
    }

    boolean reverse = false;

    @Override
    protected void onDraw(Canvas canvas) {
        if (movie != null) {
            if (!mPaused && hasStart) {
                if (reverse) {
                    movie.setTime(movieDuration - getCurrentFrameTime());
                } else {
                    movie.setTime(getCurrentFrameTime());
                }
                drawMovieFrame(canvas);
                invalidateView();
            } else {
                drawMovieFrame(canvas);
            }
        } else {
            super.onDraw(canvas);
        }
    }

    /**
     * 画出gif帧
     */
    private void drawMovieFrame(Canvas canvas) {

        movie.draw(canvas, 0.0f, 0.0f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (movie != null) {
            int movieWidth = movie.width();
            int movieHeight = movie.height();
            setMeasuredDimension(movieWidth, movieHeight);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }


    private void invalidateView() {
        if (mVisible) {
            postInvalidateOnAnimation();
        }
    }

    public int getDuration() {
        if (movie != null) {
            return movie.duration();
        } else return 0;
    }

    @Override
    public void onScreenStateChanged(int screenState) {
        super.onScreenStateChanged(screenState);
        mVisible = screenState == SCREEN_STATE_ON;
        invalidateView();
    }


    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        mVisible = visibility == View.VISIBLE;
        invalidateView();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        mVisible = visibility == View.VISIBLE;
        invalidateView();
    }


    public interface OnPlayListener {
        void onPlayStart();

        void onPlaying(float percent);

        void onPlayPause(boolean pauseSuccess);

        void onPlayRestart();

        void onPlayEnd();
    }

}