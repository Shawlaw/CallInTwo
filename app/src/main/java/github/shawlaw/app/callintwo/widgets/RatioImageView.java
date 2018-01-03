package github.shawlaw.app.callintwo.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;

import github.shawlaw.app.callintwo.R;

/**
 * 支持固定比例的ImageView
 * @author Shawlaw
 * @date 2017/12/8
 */

public class RatioImageView extends android.support.v7.widget.AppCompatImageView {
    private static final float DEFAULT_RATIO = 0f;

    private float mWidthHeightRatio;

    public RatioImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initConfigs(context, attrs);
    }

    public RatioImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initConfigs(context, attrs);
    }

    private void initConfigs(Context context, @Nullable AttributeSet attrs) {
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RatioImageView);
        mWidthHeightRatio = a.getFloat(R.styleable.RatioImageView_widthHeightRatio, DEFAULT_RATIO);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isRatioEnable()) {
            checkLayoutParams();
            int mode = MeasureSpec.EXACTLY;
            int measuredSize;
            int newSize;
            int newMeasureSpec;
            ViewGroup.LayoutParams lp = getLayoutParams();
            if (lp.height == ViewGroup.LayoutParams.MATCH_PARENT) {
                measuredSize = MeasureSpec.getSize(heightMeasureSpec);
                newSize = ((int)(measuredSize * mWidthHeightRatio + 0.5f));
                newMeasureSpec = MeasureSpec.makeMeasureSpec(newSize, mode);
                super.onMeasure(newMeasureSpec, heightMeasureSpec);
            } else {
                measuredSize = MeasureSpec.getSize(widthMeasureSpec);
                newSize = ((int)(measuredSize / mWidthHeightRatio + 0.5f));
                newMeasureSpec = MeasureSpec.makeMeasureSpec(newSize, mode);
                super.onMeasure(widthMeasureSpec, newMeasureSpec);
            }
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private boolean isRatioEnable() {
        return Float.compare(mWidthHeightRatio, DEFAULT_RATIO) > 0;
    }

    private void checkLayoutParams() {
        ViewGroup.LayoutParams lp = getLayoutParams();
        if (lp != null) {
            boolean bothMatchParent
                    = (lp.width == ViewGroup.LayoutParams.MATCH_PARENT
                    && lp.height == ViewGroup.LayoutParams.MATCH_PARENT);
            if (bothMatchParent) {
                throw new RuntimeException("You must only set width/height to match_parent!");
            }
            boolean neitherMatchParent
                    = (lp.width != ViewGroup.LayoutParams.MATCH_PARENT
                    && lp.height != ViewGroup.LayoutParams.MATCH_PARENT);
            if (neitherMatchParent) {
                throw new RuntimeException("You must set width or height to match_parent!");
            }
        }
    }

}
