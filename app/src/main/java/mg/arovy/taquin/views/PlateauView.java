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
        linePaint.setStrokeWidth(4);
        linePaint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
    }

    public void setPlateau(Plateau plateau) {
        this.plateau = plateau;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int weight, int height, int oldweight, int oldheight) {
        super.onSizeChanged(weight, height, oldweight, oldheight);
        calculateDimensions();
    }
    private void calculateDimensions() {

        int rows = plateau.getRows();

        squareSize = Math.min(getWidth(), getHeight());
        cellSize = squareSize / rows;
        offsetX = (getWidth() - squareSize) / 2f;
        offsetY = (getHeight() - squareSize) / 2f;

        textPaint.setTextSize(cellSize / 2f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int rows = plateau.getRows();
        int cols = plateau.getCols();

        // Dessiner lignes
        for (int i = 1; i < cols; i++) {
            float x = offsetX + i * cellSize;
            canvas.drawLine(x, offsetY, x, offsetY + squareSize, linePaint);
        }
        for (int i = 1; i < rows; i++) {
            float y = offsetY + i * cellSize;
            canvas.drawLine(offsetX, y, offsetX + squareSize, y, linePaint);
        }
        canvas.drawRect(offsetX, offsetY, offsetX + squareSize, offsetY + squareSize, linePaint);

        //TODO : mettre à jour le dessin des chiffres en vue de l'ajout des fonctionnalités ultérieures

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int number = plateau.getCell(row, col);
                if (number != 0) {
                    float centerX = offsetX + col * cellSize + cellSize / 2f;
                    float centerY = offsetY + row * cellSize + cellSize / 2f - ((textPaint.descent() + textPaint.ascent()) / 2f);
                    canvas.drawText(String.valueOf(number), centerX, centerY, textPaint);
                }
            }
        }
    }
}

