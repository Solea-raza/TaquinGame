package mg.arovy.taquin;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import mg.arovy.taquin.model.GameState;
import mg.arovy.taquin.model.Plateau;
import mg.arovy.taquin.views.PlateauView;
import mg.arovy.taquin.util.SaveManager;

public class MainActivity extends AppCompatActivity {

    private PlateauView plateauView;
    private Plateau plateau;
    private ImageView imgStart;
    private Button btnNewGame, btnNext;
    private TextView tvTitle;
    private SaveManager saveManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        plateauView = findViewById(R.id.taquinView);
        imgStart    = findViewById(R.id.imgStart);
        btnNewGame  = findViewById(R.id.btnNewGame);
        btnNext     = findViewById(R.id.btnNext);
        tvTitle     = findViewById(R.id.tvTitle);

        // Initialisation du gestionnaire de sauvegarde
        saveManager = new SaveManager(this);

        plateauView.setVisibility(View.GONE);
        tvTitle.setVisibility(View.GONE);
        btnNext.setVisibility(View.GONE);

        btnNewGame.setOnClickListener(v -> {
            imgStart.setVisibility(View.GONE);
            btnNewGame.setVisibility(View.GONE);

            plateau = new Plateau(3);
            plateau.prepareStart(); // MODIFIÉ : remplace shuffle() + setState()

            plateauView.setPlateau(plateau);
            plateauView.setVisibility(View.VISIBLE);
            tvTitle.setVisibility(View.VISIBLE);

            //Proposition de reprendre la sauvegarde si elle existe
            if (saveManager.hasSave()) {
                new AlertDialog.Builder(this)
                        .setTitle("Partie non finie trouvee")
                        .setMessage("Voulez-vous reprendre la partie ?")
                        .setPositiveButton("Oui", (d, w) -> loadGameSave())
                        .setNegativeButton("Non", (d, w) -> showGoalConfigDialog())
                        .setCancelable(false)
                        .show();
            } else {
                showStartConfigDialog();
            }

            // MODIFIÉ : dialog choix Manuel/Aléatoire pour le départ
                new AlertDialog.Builder(this)
                        .setTitle("Configuration départ")
                        .setMessage("Comment configurer le départ ?")
                        .setPositiveButton("Manuel", (d, w) -> {
                            tvTitle.setText("Configuration départ");
                            btnNext.setText("Confirmer départ");
                            btnNext.setVisibility(View.VISIBLE);
                        })
                        .setNegativeButton("Aléatoire", (d, w) -> {
                            plateau.randomizeStart();
                            plateau.confirmStart();
                            plateauView.invalidate();
                            showGoalConfigDialog();
                        })
                        .setCancelable(false)
                        .show();
                    });

        // MODIFIÉ : btnNext gère confirmStart() et confirmGoal()
        btnNext.setOnClickListener(v -> {
            if (plateau.getState() == GameState.CONFIG_START) {
                plateau.confirmStart();
                plateauView.invalidate();
                showGoalConfigDialog();
            } else if (plateau.getState() == GameState.CONFIG_END) {
                startGame();
            }
        });
    }
        private void showStartConfigDialog() {
            new AlertDialog.Builder(this)
                    .setTitle("Configuration départ")
                    .setMessage("Comment configurer le départ ?")
                    .setPositiveButton("Manuel", (d, w) -> {
                        tvTitle.setText("Configuration départ");
                        btnNext.setText("Confirmer départ");
                        btnNext.setVisibility(View.VISIBLE);
                    })
                    .setNegativeButton("Aléatoire", (d, w) -> {
                        plateau.randomizeStart();
                        plateau.confirmStart();
                        plateauView.invalidate();
                        showGoalConfigDialog();
                    })
                    .setCancelable(false)
                    .show();
        }

    // MODIFIÉ : ajout
    private void showGoalConfigDialog() {
        tvTitle.setText("Configuration arrivée");
        new AlertDialog.Builder(this)
                .setTitle("Configuration arrivée")
                .setMessage("Comment configurer l'arrivée ?")
                .setPositiveButton("Manuel", (d, w) -> {
                    plateau.setState(GameState.CONFIG_END);
                    btnNext.setText("Confirmer arrivée");
                    btnNext.setVisibility(View.VISIBLE);
                    plateauView.invalidate();
                })
                .setNegativeButton("Aléatoire", (d, w) -> {
                    plateau.randomizeGoal();
                    startGame();
                })
                .setCancelable(false)
                .show();
    }

    // MODIFIÉ : ajout
    private void startGame() {
        plateau.confirmGoal(); // sauvegarde arrivée + restore départ
        //sauvegarde automatique des grille depart et arrivee
        saveManager.save( (int) Math.sqrt(plateau.getSize()),plateau.getStartGrid(), plateau.getGoalGrid());
        tvTitle.setText("En jeu");
        btnNext.setVisibility(View.GONE);
        plateauView.invalidate();
    }

    //charge la sauvegarde et demarre directement
    private  void loadGameSave(){
        int[] startGrid = saveManager.loadStartGrid();
        int[] goalGrid = saveManager.loadGoalGrid();
                plateau.setStartGrid(startGrid);
                plateau.setGoalGrid(goalGrid);
                plateau.confirmGoal();

                tvTitle.setText("En jeu");
                btnNext.setVisibility(View.GONE);
                plateauView.invalidate();
    }
}