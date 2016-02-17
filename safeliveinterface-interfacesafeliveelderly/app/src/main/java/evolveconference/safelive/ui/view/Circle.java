package evolveconference.safelive.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import evolveconference.safelive.R;

public class Circle extends View {

    private List<Series> seriesList = new ArrayList<>();
    private boolean drawLabel = true;
    private int strokeWidth = 60;
    private float startingAngle = 90;
    private float maxValue = 100;
    private float gap = 15;
    private float radius = 60;
    private boolean drawBubble = false;

    public Circle(Context context, AttributeSet attrs) {
        super(context, attrs);
        paintText.setTextSize(50);
        paintText.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        paintBorder.setAntiAlias(true);
        paintBorder.setStrokeWidth(strokeWidth + 10);
        paintBorder.setStrokeCap(Cap.ROUND);
        paintBorder.setStyle(Paint.Style.STROKE);
        paintBorder.setColor(getResources().getColor(R.color.dark_gray));
    }

    private Paint paintBorder = new Paint();
    private Paint paintText = new Paint();
    private Paint paint = new Paint();
    private Paint paintSelected = new Paint();
    private Path path = new Path();

    private OnSeriesClickedListener listener;

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public void update() {
        postInvalidate();
    }

    public void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);

        paint.reset();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(strokeWidth);
        paint.setStrokeCap(Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);

        float r = radius;
        float midX = getWidth() / 2;
        float midY = getHeight() / 2;

        for (Series series : seriesList) {
            paint.setColor(series.getColor());
            paintText.setColor(series.getColor());

            float max = (360 - startingAngle);
            float endAngle = (series.getValue() < maxValue ? series.getValue() : maxValue) * max / 100;
            Path p = series.getPath();
            p.reset();

            RectF rectF = series.getRectF();
            rectF.set(midX - r, midY - r, midX + r, midY + r);

            p.addArc(rectF, startingAngle, endAngle);

            canvas.drawPath(p, paintBorder);
            canvas.drawPath(p, paint);

            if (drawLabel) {
                float percentage = endAngle * 100 / max;
                canvas.drawText(String.format("%.0f%% %s", percentage, series.getLabel()), midX + 60, midY + r + gap + 5, paintText);
            }

            if (series.isSelected() && listener != null) {
                paintSelected.reset();
                paintSelected.setColor(series.getColor());
                paintSelected.setAlpha(90);
                paintSelected.setAntiAlias(true);
                paintSelected.setStrokeCap(Cap.ROUND);
                paintSelected.setStyle(Paint.Style.STROKE);
                paintSelected.setStrokeWidth(strokeWidth + 40);
                path.reset();
                path.addArc(rectF, startingAngle, endAngle);
                canvas.drawPath(path, paintSelected);
            }

            if (drawBubble) {
                Bitmap icon = series.getBitmap();
                if (icon != null) {
                    double rad = 2 * Math.PI * (endAngle + startingAngle) / 360;
                    double left = Math.cos(rad) * r;
                    double top = Math.sin(rad) * r;
                    canvas.drawBitmap(icon, (float) left + midX - icon.getWidth() / 2, midY + (float) top - icon.getHeight(), null);
                }
            }
            r += strokeWidth + gap;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Point point = new Point();
        point.x = (int) event.getX();
        point.y = (int) event.getY();

        float midX = getWidth() / 2;
        float midY = getHeight() / 2;

        double dist = Math.pow(point.x - midX, 2) + Math.pow(point.y - midY, 2);
        dist = Math.sqrt(dist);

        for (Series series : seriesList) {
            RectF rectF = series.getRectF();
            float width = (rectF.width() - radius) / 2;

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                series.setSelected(dist > width && dist < width + strokeWidth && !series.isSelected());
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (series.isSelected() && listener != null) {
                    listener.onClick(series);
                }
            }
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP) {
            postInvalidate();
        }

        return true;
    }

    public void setOnSeriesClickedListener(OnSeriesClickedListener listener) {
        this.listener = listener;
    }

    public void removeSlices() {
        seriesList.clear();
        postInvalidate();
    }

    public interface OnSeriesClickedListener {
        void onClick(Series serie);
    }

    public void setGap(float gap) {
        this.gap = gap;
    }

    public void setMinRadius(float radius) {
        this.radius = radius;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public float getStartingAngle() {
        return startingAngle;
    }

    public void setStartingAngle(float startingAngle) {
        this.startingAngle = startingAngle;
    }

    public void addSeries(Series series) {
        seriesList.add(series);
        postInvalidate();
    }

    public void setSeries(List<Series> series) {
        this.seriesList = series;
        postInvalidate();
    }

    public boolean isDrawLabel() {
        return drawLabel;
    }

    public void setDrawLabel(boolean drawLabel) {
        this.drawLabel = drawLabel;
    }

    public void setDrawBubble(boolean drawBubble) {
        this.drawBubble = drawBubble;
    }

    public static class Series {
        private Path path = new Path();
        private int color = Color.argb(235, 74, 138, 255);
        private float value = 0;
        private String label;
        private Bitmap bitmap;
        private boolean selected = false;
        private RectF rectF = new RectF();

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public float getValue() {
            return value;
        }

        public void setValue(float value) {
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public Path getPath() {
            return path;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public boolean isSelected() {
            return selected;
        }

        public RectF getRectF() {
            return rectF;
        }

        public static class SeriesBuilder {
            private final Series series;

            public SeriesBuilder() {
                this.series = new Series();
            }

            public SeriesBuilder setColor(int color) {
                series.setColor(color);
                return this;
            }

            public SeriesBuilder setValue(float value) {
                series.setValue(value);
                return this;
            }

            public SeriesBuilder setBitmap(Bitmap bitmap) {
                series.setBitmap(bitmap);
                return this;
            }

            public SeriesBuilder setLabel(String label) {
                series.setLabel(label);
                return this;
            }

            public SeriesBuilder setSelected(boolean selected) {
                series.setSelected(selected);
                return this;
            }

            public Series build() {
                return series;
            }
        }
    }
}
