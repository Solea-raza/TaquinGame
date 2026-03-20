package mg.arovy.taquin;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import mg.arovy.taquin.model.GameState;
import mg.arovy.taquin.model.Plateau;
import mg.arovy.taquin.views.PlateauView; // correspond au XML maintenant


public class MainActivity extends AppCompatActivity {
    private PlateauView plateauView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        plateauView = findViewById(R.id.taquinView);

        Button btnNewGame = findViewById(R.id.btnNewGame);
        Plateau plateau = new Plateau(5);
        plateauView.setPlateau(plateau);

        btnNewGame.setOnClickListener(v -> {
            plateau.startNewGame(); // initialise + shuffle
            plateau.setState(GameState.CONFIG_START); // mode config
            plateauView.setPlateau(plateau); // passe le plateau à la vue
            plateauView.invalidate(); // redraw
        });
    }
}