package evolveconference.safelive.ui.view;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import evolveconference.safelive.R;

public class ThresholdVisualizerView extends View {

    private byte[] values;
    private Paint paintLines = new Paint();
    private Paint paintPoints = new Paint();
    private Paint paintAxes = new Paint();

    public ThresholdVisualizerView(Context context, AttributeSet attrs) {
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

        int height = canvas.getHeight();
        int buffIndex = 1;
        int width = canvas.getWidth();
        int startX = 0;
        if (startX >= width) {
            canvas.save();
            return;
        }

        while (startX < width - 1) {
            int startBaseY = values[(buffIndex - 1)];
            int stopBaseY = values[buffIndex];
            int startY = startBaseY + height / 2;
            int stopY = stopBaseY + height / 2;
            canvas.drawLine(startX, startY, startX + 1, stopY, paintLines);
            buffIndex++;
            startX++;
        }

        // Drawing X axis
//        canvas.drawLine(0, height / 2, width, height / 2, paintAxes);

        // Drawing max threshold
        float max = height * (1 - 0.65f);
        canvas.drawLine(0, max, width, max, paintAxes);

        // Drawing min threshold
        float min = height * (1 - 0.35f);
        canvas.drawLine(0, min, width, min, paintAxes);


        //Drawing time line
        int shift = 10;
        paintAxes.setTextSize(30);
        paintAxes.setColor(Color.BLACK);
        float intervalSize = 5;
        float intervals = width / intervalSize;
        for (int i = 0; i <= intervals; i++) {
            canvas.drawText(String.format("%.0f min", (i * intervalSize) - shift) , i * intervals, height, paintAxes);
        }

        // Drawing now line
        paintAxes.setColor(Color.GREEN);
        paintAxes.setStrokeWidth(5);
        float now = (shift / intervalSize) * intervals;

        canvas.drawLine(now, height - 60, now, 60, paintAxes);
        paintAxes.setTextSize(50);
        float w = paintAxes.measureText("Now");
        canvas.drawText("Now", now - (w / 2), height - 30, paintAxes);


    }
}