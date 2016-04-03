package com.browser.Speed.layout;

import android.app.Activity;
import android.content.res.Resources;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

import com.browser.Speed.R;
import com.browser.Speed.utils.Utils;
import com.browser.Speed.view.XWebView;


public class RefreshAnimator  {
    // Maps to ProgressBar.Large style
    public static final int LARGE = MaterialProgressDrawable.LARGE;
    // Maps to ProgressBar default style
    public static final int DEFAULT = MaterialProgressDrawable.DEFAULT;

    private static final String LOG_TAG = "refresh animator";

    private static final int MAX_ALPHA = 255;
    private static final int STARTING_PROGRESS_ALPHA = (int) (.3f * MAX_ALPHA);

    private static final int CIRCLE_DIAMETER = 40;
    private static final int CIRCLE_DIAMETER_LARGE = 56;

    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
    private static final int INVALID_POINTER = -1;
    private static final float DRAG_RATE = .5f;

    // Max amount of circle that can be filled by progress during swipe gesture,
    // where 1.0 is a full circle
    private static final float MAX_PROGRESS_ANGLE = .8f;
    private static final int SCALE_DOWN_DURATION = 150;
    private static final int ALPHA_ANIMATION_DURATION = 300;
    private static final int ANIMATE_TO_TRIGGER_DURATION = 200;
    private static final int ANIMATE_TO_START_DURATION = 200;

    // Default background for the progress spinner
    private static final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;
    // Default offset in dips from the top of the view to where the progress spinner should stop
    private static final int DEFAULT_CIRCLE_TARGET = 64;


    private Activity mActivity;
    private View mTarget; // the target of the gesture
    private OnRefreshListener mListener;
    private boolean mRefreshing = false;
    private int mTouchSlop;
    private float mTotalDragDistance = -1;

    private int mMediumAnimationDuration;
    private int mCurrentTargetOffsetTop;
    // Whether or not the starting offset has been determined.
    private boolean mOriginalOffsetCalculated = false;

    private float mInitialMotionY;
    private float mInitialDownY;
    private boolean mIsBeingDragged;
    private int mActivePointerId = INVALID_POINTER;
    // Whether this item is scaled up rather than clipped
    private boolean mScale;

    // Target is returning to its start offset because it was cancelled or a
    // refresh was triggered.
    private boolean mReturningToStart;
    private final DecelerateInterpolator mDecelerateInterpolator;
    private static final int[] LAYOUT_ATTRS = new int[] {
            android.R.attr.enabled
    };

    private CircleImageView mCircleView;
    private int mCircleViewIndex = -1;

    protected int mFrom;
    protected int mOriginalOffsetTop;
    private float mStartingScale;

    private MaterialProgressDrawable mProgress;

    private Animation mScaleAnimation;
    private Animation mScaleDownAnimation;
    private Animation mAlphaStartAnimation;
    private Animation mAlphaMaxAnimation;
    private Animation mScaleDownToStartAnimation;

    private float mSpinnerFinalOffset;
    private boolean mNotify;

    private int mCircleWidth;
    private int mCircleHeight;

    // Whether the client has set a custom starting position;
    private boolean mUsingCustomStart;

    private Animation.AnimationListener mRefreshListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationEnd(Animation animation) {
            if (mRefreshing) { // Make sure the progress view is fully visible
                mProgress.setAlpha(MAX_ALPHA);
                mProgress.start();
                if (mNotify && mListener != null) {
                    mListener.onRefresh();
                }
            }
            else {
                mProgress.stop();
                mCircleView.setVisibility(View.GONE);
                setColorViewAlpha(MAX_ALPHA);
                // Return the circle to its start position
                if (mScale) setAnimationProgress(0 /* animation complete and view is hidden */);
                else setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCurrentTargetOffsetTop);
            }
            mCurrentTargetOffsetTop = mCircleView.getTop();
        }
        @Override public void onAnimationRepeat(Animation animation) {}
        @Override public void onAnimationStart(Animation animation) {}
    };

    private void setColorViewAlpha(int targetAlpha) {
        mCircleView.getBackground().setAlpha(targetAlpha);
        mProgress.setAlpha(targetAlpha);
    }

    /**
     * The refresh indicator starting and resting position is always positioned
     * near the top of the refreshing content. This position is a consistent
     * location, but can be adjusted in either direction based on whether or not
     * there is a toolbar or actionbar present.
     *
     * @param scale Set to true if there is no view at a higher z-order than
     *            where the progress spinner is set to appear.
     * @param start The offset in pixels from the top of this view at which the
     *            progress spinner should appear.
     * @param end The offset in pixels from the top of this view at which the
     *            progress spinner should come to rest after a successful swipe
     *            gesture.
     */
    public void setProgressViewOffset(boolean scale, int start, int end) {
        mScale = scale;
        mCircleView.setVisibility(View.GONE);
        mOriginalOffsetTop = mCurrentTargetOffsetTop = start;
        mSpinnerFinalOffset = end;
        mUsingCustomStart = true;
        mCircleView.invalidate();
    }

    /**
     * The refresh indicator resting position is always positioned near the top
     * of the refreshing content. This position is a consistent location, but
     * can be adjusted in either direction based on whether or not there is a
     * toolbar or actionbar present.
     *
     * @param scale Set to true if there is no view at a higher z-order than
     *            where the progress spinner is set to appear.
     * @param end The offset in pixels from the top of this view at which the
     *            progress spinner should come to rest after a successful swipe
     *            gesture.
     */
    public void setProgressViewEndTarget(boolean scale, int end) {
        mSpinnerFinalOffset = end;
        mScale = scale;
        mCircleView.invalidate();
    }

    /**
     * One of DEFAULT, or LARGE.
     */
    public void setSize(int size) {
        if (size != MaterialProgressDrawable.LARGE && size != MaterialProgressDrawable.DEFAULT) {
            return;
        }
        final DisplayMetrics metrics = mActivity.getResources().getDisplayMetrics();
        if (size == MaterialProgressDrawable.LARGE) {
            mCircleHeight = mCircleWidth = (int) (CIRCLE_DIAMETER_LARGE * metrics.density);
        }
        else mCircleHeight = mCircleWidth = (int) (CIRCLE_DIAMETER * metrics.density);
        // force the bounds of the progress circle inside the circle view to
        // update by setting it to null before updating its size and then
        // re-setting it
        mCircleView.setImageDrawable(null);
        mProgress.updateSizes(size);
        mCircleView.setImageDrawable(mProgress);
    }

    public RefreshAnimator(Activity activity, FrameLayout root) {

        mActivity = activity;
        mTouchSlop = ViewConfiguration.get(activity).getScaledTouchSlop();

        mMediumAnimationDuration = mActivity.getResources().getInteger(android.R.integer.config_mediumAnimTime);

        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);

        final DisplayMetrics metrics = mActivity.getResources().getDisplayMetrics();
        mCircleWidth = (int) (CIRCLE_DIAMETER * metrics.density);
        mCircleHeight = (int) (CIRCLE_DIAMETER * metrics.density);

        mCircleView = new CircleImageView(mActivity, CIRCLE_BG_LIGHT, CIRCLE_DIAMETER/2);
        mProgress = new MaterialProgressDrawable(mActivity, mCircleView);
        mProgress.setBackgroundColor(CIRCLE_BG_LIGHT);
        mCircleView.setImageDrawable(mProgress);
        mCircleView.setVisibility(View.VISIBLE);
        ((FrameLayout)root.findViewById(R.id.webview_frame)).addView(mCircleView, 0, new FrameLayout.LayoutParams(-2, -2, Gravity.CENTER_HORIZONTAL));
        mSpinnerFinalOffset = DEFAULT_CIRCLE_TARGET * metrics.density;
        mTotalDragDistance = mSpinnerFinalOffset;
        mCircleView.setTranslationY(-Utils.convertDpToPixels(56));
    }

    /**
     * Set the listener to be notified when a refresh is triggered via the swipe
     * gesture.
     */
    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }
    /**
     * Notify the widget that refresh state has changed. Do not call this when
     * refresh is triggered by a swipe gesture.
     *
     * @param refreshing Whether or not the view should show refresh progress.
     */
    public void setRefreshing(boolean refreshing) {
        if (refreshing && !mRefreshing) {
            // scale and show
            mRefreshing = refreshing;
            int endTarget = 0;
            if (!mUsingCustomStart) endTarget = (int) (mSpinnerFinalOffset + mOriginalOffsetTop);
            else endTarget = (int) mSpinnerFinalOffset;
            setTargetOffsetTopAndBottom(endTarget - mCurrentTargetOffsetTop);
            mNotify = false;
            startScaleUpAnimation(mRefreshListener);
        }
        else setRefreshing(refreshing, false /* notify */);
    }

    private void startScaleUpAnimation(Animation.AnimationListener listener) {
        mCircleView.setVisibility(View.VISIBLE);
        mProgress.setAlpha(MAX_ALPHA);
        mScaleAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setAnimationProgress(interpolatedTime);
            }
        };
        mScaleAnimation.setDuration(mMediumAnimationDuration);
        if (listener != null) mCircleView.setAnimationListener(listener);
        mCircleView.clearAnimation();
        mCircleView.startAnimation(mScaleAnimation);
    }

    /**
     * Pre API 11, this does an alpha animation.
     * @param progress
     */
    private void setAnimationProgress(float progress) {
        ViewCompat.setScaleX(mCircleView, progress);
        ViewCompat.setScaleY(mCircleView, progress);
    }

    private void setRefreshing(boolean refreshing, final boolean notify) {
        if (mRefreshing != refreshing) {
            mNotify = notify;
            mRefreshing = refreshing;
            if (mRefreshing) animateOffsetToCorrectPosition(mCurrentTargetOffsetTop, mRefreshListener);
            else startScaleDownAnimation(mRefreshListener);
        }
    }

    private void startScaleDownAnimation(Animation.AnimationListener listener) {
        mScaleDownAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setAnimationProgress(1 - interpolatedTime);
            }
        };
        mScaleDownAnimation.setDuration(SCALE_DOWN_DURATION);
        mCircleView.setAnimationListener(listener);
        mCircleView.clearAnimation();
        mCircleView.startAnimation(mScaleDownAnimation);
    }

    private void startProgressAlphaStartAnimation() {
        mAlphaStartAnimation = startAlphaAnimation(mProgress.getAlpha(), STARTING_PROGRESS_ALPHA);
    }

    private void startProgressAlphaMaxAnimation() {
        mAlphaMaxAnimation = startAlphaAnimation(mProgress.getAlpha(), MAX_ALPHA);
    }

    private Animation startAlphaAnimation(final int startingAlpha, final int endingAlpha) {
        Animation alpha = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                mProgress.setAlpha((int) (startingAlpha+ ((endingAlpha - startingAlpha) * interpolatedTime)));
            }
        };
        alpha.setDuration(ALPHA_ANIMATION_DURATION);
        // Clear out the previous animation listeners.
        mCircleView.setAnimationListener(null);
        mCircleView.clearAnimation();
        mCircleView.startAnimation(alpha);
        return alpha;
    }

    /**
     * @deprecated Use {@link #setProgressBackgroundColorSchemeResource(int)}
     */
    @Deprecated
    public void setProgressBackgroundColor(int colorRes) {
        setProgressBackgroundColorSchemeResource(colorRes);
    }

    /**
     * Set the background color of the progress spinner disc.
     *
     * @param colorRes Resource id of the color.
     */
    public void setProgressBackgroundColorSchemeResource(@ColorRes int colorRes) {
        setProgressBackgroundColorSchemeColor(mActivity.getResources().getColor(colorRes));
    }

    /**
     * Set the background color of the progress spinner disc.
     *
     * @param color
     */
    public void setProgressBackgroundColorSchemeColor(@ColorInt int color) {
        mCircleView.setBackgroundColor(color);
        mProgress.setBackgroundColor(color);
    }

//    /**
//     * @deprecated Use {@link #setColorSchemeResources(int...)}
//     */
//    @Deprecated
//    public void setColorScheme(@ColorInt int... colors) {
//        setColorSchemeResources(colors);
//    }

    /**
     * Set the color resources used in the progress animation from color resources.
     * The first color will also be the color of the bar that grows in response
     * to a user swipe gesture.
     *
     * @param colorResIds
     */
    public void setColorSchemeResources(@ColorRes int... colorResIds) {
        final Resources res = mActivity.getResources();
        int[] colorRes = new int[colorResIds.length];
        for (int i = 0; i < colorResIds.length; i++) {
            colorRes[i] = res.getColor(colorResIds[i]);
        }
        setColorSchemeColors(colorRes);
    }

    /**
     * Set the colors used in the progress animation. The first
     * color will also be the color of the bar that grows in response to a user
     * swipe gesture.
     *
     * @param colors
     */
    @ColorInt
    public void setColorSchemeColors(int... colors) {
        mProgress.setColorSchemeColors(colors);
    }

    /**
     * @return Whether the SwipeRefreshWidget is actively showing refresh
     *         progress.
     */
    public boolean isRefreshing() {
        return mRefreshing;
    }

    /**
     * Set the distance to trigger a sync in dips
     *
     * @param distance
     */
    public void setDistanceToTriggerSync(int distance) {
        mTotalDragDistance = distance;
    }

    /**
     * Get the diameter of the progress circle that is displayed as part of the
     * swipe to refresh layout. This is not valid until a measure pass has
     * completed.
     *
     * @return Diameter in pixels of the progress circle view.
     */
    public int getProgressCircleDiameter() {
        return mCircleView != null ?mCircleView.getMeasuredHeight() : 0;
    }

    private XWebView.Future mFuture;

    public View.OnTouchListener getOnTouchListener(){
        return mTouchListener;
    }

    public void setFinishListener(XWebView.Future future){
        mFuture = future;
    }

    private boolean canRefresh;

    public View getView(){
        return mCircleView;
    }

    public void initTouchEvent(MotionEvent ev){
        setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCircleView.getTop());
        mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
        mIsBeingDragged = true;
        mInitialDownY = getMotionEventY(ev, mActivePointerId);
        mInitialMotionY = mInitialDownY + mTouchSlop;
        mProgress.setAlpha(STARTING_PROGRESS_ALPHA);
        mRefreshing = false;
        canRefresh = true;
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent ev) {

            final int action = MotionEventCompat.getActionMasked(ev);
            int pointerIndex = -1;

            switch (action) {
//                case MotionEvent.ACTION_DOWN:
//                    setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCircleView.getTop(), true);
//                    mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
//                    mIsBeingDragged = true;
//                    float initialDownY = getMotionEventY(ev, mActivePointerId);
//                    if (initialDownY == -1) return false;
//                    mInitialDownY = initialDownY;
//                    mInitialMotionY = mInitialDownY + mTouchSlop;
//                    mProgress.setAlpha(STARTING_PROGRESS_ALPHA);
//                    canRefresh = true;
//                    break;
                case MotionEvent.ACTION_MOVE: {
                    if (!canRefresh) return false;
                    pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                    if (pointerIndex < 0) { //Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                        return true;
                    }
                    final float y = MotionEventCompat.getY(ev, pointerIndex);
                    final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                    if (mIsBeingDragged) {
                        if (overscrollTop > 0) moveSpinner(overscrollTop);
                        else return true;
                    }
                    return true;
                }
                case MotionEventCompat.ACTION_POINTER_DOWN: {
                    pointerIndex = MotionEventCompat.getActionIndex(ev);
                    if (pointerIndex < 0) { //Log.e(LOG_TAG, "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                        return false;
                    }
                    mActivePointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
                    break;
                }

                case MotionEventCompat.ACTION_POINTER_UP:
                    onSecondaryPointerUp(ev);
                    break;

                case MotionEvent.ACTION_UP: {
                    mFuture.onFinish();
                    canRefresh = false;
                    pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                    if (pointerIndex < 0) { //Log.e(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.");
                        return false;
                    }
                    final float y = MotionEventCompat.getY(ev, pointerIndex);
                    final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                    mIsBeingDragged = false;
                    finishSpinner(overscrollTop);
                    mActivePointerId = INVALID_POINTER;
                    return false;
                }
                case MotionEvent.ACTION_CANCEL: {
                    mFuture.onFinish();
                    return false;
                }
            }

            return false;
        }
    };

    private float getMotionEventY(MotionEvent ev, int activePointerId) {
        final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
        if (index < 0) {
            return -1;
        }
        return MotionEventCompat.getY(ev, index);
    }

    private boolean isAnimationRunning(Animation animation) {
        return animation != null && animation.hasStarted() && !animation.hasEnded();
    }

    private void moveSpinner(float overscrollTop) {
        mProgress.showArrow(true);
        float originalDragPercent = overscrollTop / mTotalDragDistance;

        float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
        float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3;
        float extraOS = Math.abs(overscrollTop) - mTotalDragDistance;
        float slingshotDist = mUsingCustomStart ? mSpinnerFinalOffset - mOriginalOffsetTop : mSpinnerFinalOffset;
        float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, slingshotDist * 2) / slingshotDist);
        float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow((tensionSlingshotPercent / 4), 2)) * 2f;
        float extraMove = (slingshotDist) * tensionPercent * 2;

        int targetY = mOriginalOffsetTop + (int) ((slingshotDist * dragPercent) + extraMove);
        // where 1.0f is a full circle
        if (mCircleView.getVisibility() != View.VISIBLE) {
            mCircleView.setVisibility(View.VISIBLE);
        }
        if (!mScale) {
            ViewCompat.setScaleX(mCircleView, 1f);
            ViewCompat.setScaleY(mCircleView, 1f);
        }
        if (overscrollTop < mTotalDragDistance) {
            if (mScale) setAnimationProgress(overscrollTop / mTotalDragDistance);
            if (mProgress.getAlpha() > STARTING_PROGRESS_ALPHA && !isAnimationRunning(mAlphaStartAnimation)) {// Animate the alpha
                startProgressAlphaStartAnimation();
            }
            float strokeStart = adjustedPercent * .8f;
            mProgress.setStartEndTrim(0f, Math.min(MAX_PROGRESS_ANGLE, strokeStart));
            mProgress.setArrowScale(Math.min(1f, adjustedPercent));
        }
        else {
            if (mProgress.getAlpha() < MAX_ALPHA && !isAnimationRunning(mAlphaMaxAnimation)) {
                // Animate the alpha
                startProgressAlphaMaxAnimation();
            }
        }
        float rotation = (-0.25f + .4f * adjustedPercent + tensionPercent * 2) * .5f;
        mProgress.setProgressRotation(rotation);
        setTargetOffsetTopAndBottom(targetY - mCurrentTargetOffsetTop);
    }

    private void finishSpinner(float overscrollTop) {
        if (overscrollTop > mTotalDragDistance) {
            setRefreshing(true, true /* notify */);
        }
        else { // cancel refresh
            mRefreshing = false;
            mProgress.setStartEndTrim(0f, 0f);
            Animation.AnimationListener listener = null;
            if (!mScale) {
                listener = new Animation.AnimationListener() {

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (!mScale) startScaleDownAnimation(null);
                    }

                    @Override public void onAnimationRepeat(Animation animation) {}
                    @Override public void onAnimationStart(Animation animation) {}
                };
            }
            animateOffsetToStartPosition(mCurrentTargetOffsetTop, listener);
            mProgress.showArrow(false);
        }
    }

    private void animateOffsetToCorrectPosition(int from, Animation.AnimationListener listener) {
        mFrom = from;
        mAnimateToCorrectPosition.reset();
        mAnimateToCorrectPosition.setDuration(ANIMATE_TO_TRIGGER_DURATION);
        mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
        if (listener != null) {
            mCircleView.setAnimationListener(listener);
        }
        mCircleView.clearAnimation();
        mCircleView.startAnimation(mAnimateToCorrectPosition);
    }

    private void animateOffsetToStartPosition(int from, Animation.AnimationListener listener) {
        if (mScale) startScaleDownReturnToStartAnimation(from, listener);// Scale the item back down
        else {
            mFrom = from;
            mAnimateToStartPosition.reset();
            mAnimateToStartPosition.setDuration(ANIMATE_TO_START_DURATION);
            mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
            if (listener != null) {
                mCircleView.setAnimationListener(listener);
            }
            mCircleView.clearAnimation();
            mCircleView.startAnimation(mAnimateToStartPosition);
        }
    }

    private final Animation mAnimateToCorrectPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int targetTop = 0;
            int endTarget = 0;
            if (!mUsingCustomStart) endTarget = (int) (mSpinnerFinalOffset - Math.abs(mOriginalOffsetTop));
            else endTarget = (int) mSpinnerFinalOffset;
            targetTop = (mFrom + (int) ((endTarget - mFrom) * interpolatedTime));
            int offset = targetTop - mCircleView.getTop();
            setTargetOffsetTopAndBottom(offset);
            mProgress.setArrowScale(1 - interpolatedTime);
        }
    };

    private void moveToStart(float interpolatedTime) {
        int targetTop = 0;
        targetTop = (mFrom + (int) ((mOriginalOffsetTop - mFrom) * interpolatedTime));
        int offset = targetTop - mCircleView.getTop();
        setTargetOffsetTopAndBottom(offset);
    }

    private final Animation mAnimateToStartPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            moveToStart(interpolatedTime);
        }
    };

    private void startScaleDownReturnToStartAnimation(int from, Animation.AnimationListener listener) {
        mFrom = from;
        mStartingScale = ViewCompat.getScaleX(mCircleView);

        mScaleDownToStartAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                float targetScale = (mStartingScale + (-mStartingScale  * interpolatedTime));
                setAnimationProgress(targetScale);
                moveToStart(interpolatedTime);
            }
        };
        mScaleDownToStartAnimation.setDuration(SCALE_DOWN_DURATION);
        if (listener != null) mCircleView.setAnimationListener(listener);
        mCircleView.clearAnimation();
        mCircleView.startAnimation(mScaleDownToStartAnimation);
    }

    private void setTargetOffsetTopAndBottom(int offset) {
        mCircleView.offsetTopAndBottom(offset);
        mCurrentTargetOffsetTop = mCircleView.getTop();
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
        }
    }

    /**
     * Classes that wish to be notified when the swipe gesture correctly
     * triggers a refresh should implement this interface.
     */
    public interface OnRefreshListener {
        public void onRefresh();
    }

    public interface ScrollCallback {
        boolean canChildScrollUp();
    }
}
