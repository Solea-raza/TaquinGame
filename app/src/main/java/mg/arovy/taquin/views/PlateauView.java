package mg.arovy.taquin.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import mg.arovy.taquin.model.GameState;
import mg.arovy.taquin.model.Plateau;


public class PlateauView extends View {

    private Plateau plateau;
    private Paint linePaint;
    private Paint textPaint;

    private float squareSize;
    private float cellSize;
    private float offsetX;
    private float offsetY;
    private int selectedIndex = -1;
    private Paint selectedPaint;
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

        selectedPaint = new Paint();
        selectedPaint.setColor(0xFFB19CD9); // violet pastel (ARGB)
        selectedPaint.setStyle(Paint.Style.FILL);
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

        if (plateau == null) return;

        int dimension = (int) Math.sqrt(plateau.getSize());

        squareSize = Math.min(getWidth(), getHeight());
        cellSize = squareSize / dimension;
        offsetX = (getWidth() - squareSize) / 2f;
        offsetY = (getHeight() - squareSize) / 2f;

        textPaint.setTextSize(cellSize / 2f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (plateau == null) return;

        int dimension = (int) Math.sqrt(plateau.getSize());

        // Dessiner lignes
        for (int i = 1; i < dimension; i++) {
            float x = offsetX + i * cellSize;
            canvas.drawLine(x, offsetY, x, offsetY + squareSize, linePaint);
        }
        for (int i = 1; i < dimension; i++) {
            float y = offsetY + i * cellSize;
            canvas.drawLine(offsetX, y, offsetX + squareSize, y, linePaint);
        }
        canvas.drawRect(offsetX, offsetY, offsetX + squareSize, offsetY + squareSize, linePaint);

        // Dessiner chiffres
        for (int i = 0; i < plateau.getSize(); i++) {
            int number = plateau.getCell(i);
            int row = i / dimension;
            int col = i % dimension;

            float left = offsetX + col * cellSize;
            float top = offsetY + row * cellSize;
            float right = left + cellSize;
            float bottom = top + cellSize;

            // case sélectionnée en mode config
            if ((plateau.getState() == GameState.CONFIG_START || plateau.getState() == GameState.CONFIG_END)
                    && i == selectedIndex) {
                canvas.drawRect(left, top, right, bottom, selectedPaint);
            }

            if (number != 0) {
                float centerX = left + cellSize / 2f;
                float centerY = top + cellSize / 2f - ((textPaint.descent() + textPaint.ascent()) / 2f);
                canvas.drawText(String.valueOf(number), centerX, centerY, textPaint);
            }
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            int dimension = (int) Math.sqrt(plateau.getSize());

            float x = event.getX() - offsetX;
            float y = event.getY() - offsetY;

            int col = (int) (x / cellSize);
            int row = (int) (y / cellSize);

            int index = row * dimension + col;

            if (plateau.getState() == GameState.CONFIG_START || plateau.getState() == GameState.CONFIG_END) {
                if (selectedIndex == -1) {
                    selectedIndex = index; // première case sélectionnée
                } else {
                    if (selectedIndex != index) {
                        plateau.swap(selectedIndex, index); // swap
                    }
                    selectedIndex = -1; // reset sélection
                }
                invalidate(); // redessiner pour mettre à jour la couleur
            } else {
                if (plateau.move(index)) {
                    invalidate();
                }
            }
        }
        return true;
    }
}