package mg.arovy.taquin.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class MiniPlateauView extends View {

    private int[] goalGrid;
    private int dimension;
    private Paint linePaint, textPaint, bgPaint;

    public MiniPlateauView(Context context) {
        super(context);
        init();
    }

    public MiniPlateauView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        linePaint = new Paint();
        linePaint.setColor(Color.DKGRAY);
        linePaint.setStrokeWidth(2f);
        linePaint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);

        bgPaint = new Paint();
        bgPaint.setColor(0xFFE8E0F0); // violet très pâle
        bgPaint.setStyle(Paint.Style.FILL);
    }

    /** Appeler après confirmGoal() */
    public void setGoalGrid(int[] goalGrid, int dimension) {
        this.goalGrid = goalGrid;
        this.dimension = dimension;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (goalGrid == null) return;

        float size = Math.min(getWidth(), getHeight());
        float cellSize = size / dimension;
        textPaint.setTextSize(cellSize * 0.45f);

        // Fond
        canvas.drawRect(0, 0, size, size, bgPaint);

        // Grille + chiffres
        for (int i = 0; i < goalGrid.length; i++) {
            int row = i / dimension;
            int col = i % dimension;
            float left  = col * cellSize;
            float top   = row * cellSize;
            float right = left + cellSize;
            float bottom = top + cellSize;

            canvas.drawRect(left, top, right, bottom, linePaint);

            if (goalGrid[i] != 0) {
                float cx = left + cellSize / 2f;
                float cy = top  + cellSize / 2f
                        - (textPaint.descent() + textPaint.ascent()) / 2f;
                canvas.drawText(String.valueOf(goalGrid[i]), cx, cy, textPaint);
            }
        }
    }
}
