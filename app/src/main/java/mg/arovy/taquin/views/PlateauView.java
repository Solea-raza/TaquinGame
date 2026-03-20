package mg.arovy.taquin.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import mg.arovy.taquin.model.Plateau;

public class PlateauView extends View {

    private Plateau plateau;
    private Paint linePaint;
    private Paint textPaint;

    private float squareSize;
    private float cellSize;
    private float offsetX;
    private float offsetY;

    public PlateauView(Context context) {
        super(context);
        initComponents();
    }

    public PlateauView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initComponents();
    }

    private void initComponents() {
        linePaint = new Paint();
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(6);
        linePaint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint();
        textPaint.setColor(Color.BLUE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
        textPaint.setFakeBoldText(true);
    }

    public void setPlateau(Plateau plateau) {
        this.plateau = plateau;
        calculateDimensions();
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calculateDimensions();
    }

    private void calculateDimensions() {
        if (plateau == null || getWidth() == 0) return;

        int dimension = (int) Math.sqrt(plateau.getSize());

        squareSize = Math.min(getWidth(), getHeight()) * 0.9f;
        cellSize = squareSize / dimension;


        offsetX = (getWidth() - squareSize) / 2f;
        offsetY = (getHeight() - squareSize) / 2f;

        textPaint.setTextSize(cellSize / 2.5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (plateau == null) return;

        int dimension = (int) Math.sqrt(plateau.getSize());


        canvas.drawRect(offsetX, offsetY, offsetX + squareSize, offsetY + squareSize, linePaint);

        for (int i = 1; i < dimension; i++) {

            float x = offsetX + i * cellSize;
            canvas.drawLine(x, offsetY, x, offsetY + squareSize, linePaint);

            float y = offsetY + i * cellSize;
            canvas.drawLine(offsetX, y, offsetX + squareSize, y, linePaint);
        }

        for (int i = 0; i < plateau.getSize(); i++) {
            int number = plateau.getCell(i);

            if (number != 0) {
                int row = i / dimension;
                int col = i % dimension;

                float centerX = offsetX + col * cellSize + cellSize / 2f;

                float centerY = offsetY + row * cellSize + cellSize / 2f
                        - ((textPaint.descent() + textPaint.ascent()) / 2f);

                canvas.drawText(String.valueOf(number), centerX, centerY, textPaint);
            }
        }
    }
}