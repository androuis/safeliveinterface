package evolveconference.safelive.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundImageView extends ImageView {

    public RoundImageView(Context context) {
        super(context);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
        int w = Math.min(b.getHeight(), b.getWidth());
        Bitmap bitmap = Bitmap.createBitmap(b, 0, 0, w, w);
        Bitmap roundBitmap = getCroppedBitmap(bitmap);
        canvas.drawBitmap(roundBitmap, 0, 0, null);
    }


    private Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(),
                bitmap.getHeight());
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        int radius = bitmap.getWidth() / 2;
        canvas.drawCircle(radius, radius, radius-5, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        if (color != -1) {
            Paint p = new Paint();
            p.setStrokeWidth(7);
            p.setAntiAlias(true);
            p.setStrokeCap(Cap.SQUARE);
            p.setStyle(Style.STROKE);
            p.setColor(getResources().getColor(color));
            canvas.drawCircle(rect.centerX(), rect.centerY(), radius - 4, p);
        }
        /**
         * Warning width and height of current view (DO NOT USE parameter bitmap)
         */
        return Bitmap.createScaledBitmap(output, getWidth(), getHeight(), false);
    }

    private int color = -1;

    public void setCircleColor(int color){
        this.color = color;
        invalidate();
    }
}