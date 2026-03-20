package mg.arovy.taquin;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import mg.arovy.taquin.model.GameState;
import mg.arovy.taquin.model.Plateau;
import mg.arovy.taquin.views.PlateauView;

public class MainActivity extends AppCompatActivity {

    private PlateauView plateauView;
    private Plateau plateau;

    private ImageView imgStart;
    private Button btnNewGame, btnNext;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        plateauView = findViewById(R.id.taquinView);
        imgStart = findViewById(R.id.imgStart);
        btnNewGame = findViewById(R.id.btnNewGame);
        btnNext = findViewById(R.id.btnNext);
        tvTitle = findViewById(R.id.tvTitle);

        // Au départ, seul l'écran d'accueil est visible
        plateauView.setVisibility(View.GONE);
        tvTitle.setVisibility(View.GONE);
        btnNext.setVisibility(View.GONE);

        btnNewGame.setOnClickListener(v -> {
            // Masquer l'accueil
            imgStart.setVisibility(View.GONE);
            btnNewGame.setVisibility(View.GONE);

            // Initialiser le plateau
            plateau = new Plateau(3);
            plateau.shuffle();
            plateau.setState(GameState.CONFIG_START);

            // Passer le plateau à la vue et afficher
            plateauView.setPlateau(plateau);
            plateauView.setVisibility(View.VISIBLE);

            // Titre et bouton Next
            tvTitle.setText("Configuration départ");
            tvTitle.setVisibility(View.VISIBLE);

            btnNext.setText("Next");
            btnNext.setVisibility(View.VISIBLE);
        });
        btnNext.setOnClickListener(v -> {
            if (plateau.getState() == GameState.CONFIG_START) {
                plateau.setState(GameState.CONFIG_END);
                tvTitle.setText("Configuration arrivée");
                // le plateau reste affiché, juste le titre change
            } else if (plateau.getState() == GameState.CONFIG_END) {
                plateau.setState(GameState.PLAYING);
                tvTitle.setText("En jeu");
                btnNext.setVisibility(View.GONE);
            }
            plateauView.invalidate();
        });
    }
}