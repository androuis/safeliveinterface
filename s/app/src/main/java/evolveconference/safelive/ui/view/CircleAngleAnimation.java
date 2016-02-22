package evolveconference.safelive.ui.view;

import android.view.animation.Animation;
import android.view.animation.Transformation;

public class CircleAngleAnimation extends Animation {

    private Circle circle;

    public CircleAngleAnimation(Circle circle) {
        this.circle = circle;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        circle.setMaxValue(100  * interpolatedTime);
        circle.requestLayout();
    }
}