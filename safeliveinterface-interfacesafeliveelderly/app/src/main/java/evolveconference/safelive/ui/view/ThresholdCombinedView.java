package evolveconference.safelive.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.CombinedChart;

public class ThresholdCombinedView extends CombinedChart {


    public ThresholdCombinedView(Context context) {
        super(context);
    }

    public ThresholdCombinedView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ThresholdCombinedView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint p = new Paint();
        p.setStrokeWidth(7);
        p.setAntiAlias(true);
        p.setStrokeCap(Cap.SQUARE);
        p.setStyle(Style.STROKE);
        p.setColor(Color.RED);

        canvas.drawLine(mViewPortHandler.contentLeft(),100, mViewPortHandler.contentRight(), 100, p);
    }
}
