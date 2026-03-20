package mg.arovy.taquin;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import mg.arovy.taquin.model.Plateau;
import mg.arovy.taquin.views.PlateauView; // correspond au XML maintenant


public class MainActivity extends AppCompatActivity {
    private PlateauView plateauView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        plateauView = findViewById(R.id.taquinView);

        Plateau plateau = new Plateau(5);

        plateauView.setPlateau(plateau);
        Button btnNewGame = findViewById(R.id.btnNewGame);

        btnNewGame.setOnClickListener(v -> {
            plateau.startNewGame();
            plateauView.invalidate();
        });
    }
}