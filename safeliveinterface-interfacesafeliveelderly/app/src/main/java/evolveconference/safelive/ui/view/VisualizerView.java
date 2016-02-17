package evolveconference.safelive.ui.view;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import evolveconference.safelive.R;

public class VisualizerView extends View {

    private byte[] values;

    private float[] points;

    private Rect rect = new Rect();
    private Paint paintLines = new Paint();
    private Paint paintPoints = new Paint();
    private Paint paintAxes = new Paint();
    private int m_iScaler = 40;

    public VisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        values = new byte[4000];
        paintLines.setStrokeWidth(1);
        paintLines.setAntiAlias(true);
        paintLines.setColor(getResources().getColor(R.color.wave_color));
        paintLines.setMaskFilter(new BlurMaskFilter(1, Blur.INNER));

        paintPoints.setStrokeWidth(2);
        paintPoints.setAntiAlias(true);
        paintPoints.setColor(getResources().getColor(R.color.banner_orange));
        paintPoints.setMaskFilter(new BlurMaskFilter(1, Blur.INNER));

        paintAxes.setStrokeWidth(2);
        paintAxes.setAntiAlias(true);
        paintAxes.setColor(getResources().getColor(R.color.x_axes));
        paintAxes.setMaskFilter(new BlurMaskFilter(1, Blur.INNER));
    }

    public void updateVisualizer(byte[] bytes) {
        values = bytes;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (values == null) {
            return;
        }
        /**
         * Set some base values as a starting point
         * This could be consider as a part of the calculation process
         */
        int height = canvas.getHeight();
        int buffIndex = (values.length / 2 - canvas.getWidth()) / 2;
        int width = canvas.getWidth();
        int scale = height / m_iScaler;
        int startX = 0;
        if (startX >= width) {
            canvas.save();
            return;
        }
        /**
         * Here is where the real calculations is taken in to action
         * In this while loop, we calculate the start and stop points
         * for both X and Y
         *
         * The line is then drawer to the canvas with drawLine method
         */
        while (startX < width - 1) {

            int startBaseY = values[(buffIndex - 1)] / scale;

            int stopBaseY = values[buffIndex] / scale;

            if (startBaseY > height / 2) {
                startBaseY = 2 + height / 2;
                int checkSize = height / 2;
                if (stopBaseY <= checkSize)
                    return;
                stopBaseY = 2 + height / 2;
            }

            int startY = startBaseY + height / 2;
            int stopY = stopBaseY + height / 2;
            canvas.drawLine(startX, startY, startX + 1, stopY, paintLines);
            buffIndex++;
            startX++;
        }
    }
}