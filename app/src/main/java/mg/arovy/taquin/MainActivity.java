package mg.arovy.taquin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import mg.arovy.taquin.IA.A_star;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import mg.arovy.taquin.model.GameState;
import mg.arovy.taquin.model.Plateau;
import mg.arovy.taquin.views.MiniPlateauView;
import mg.arovy.taquin.views.PlateauView;
import mg.arovy.taquin.util.SaveManager;

public class MainActivity extends AppCompatActivity {

    private PlateauView plateauView;
    private MiniPlateauView miniGoalView;
    private TextView tvGoalLabel;
    private Plateau plateau;
    private ImageView imgStart;
    private Button btnNewGame, btnNext, btnResume, btnAutoPlay; // ✅ ajout btnResume
    private ImageButton btnRestart,btnQuit;
    private TextView tvTitle;
    private SaveManager saveManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        plateauView  = findViewById(R.id.taquinView);
        imgStart     = findViewById(R.id.imgStart);
        btnNewGame   = findViewById(R.id.btnNewGame);
        btnNext      = findViewById(R.id.btnNext);
        btnAutoPlay  = findViewById(R.id.btnAutoPlay);
        btnResume    = findViewById(R.id.btnResume);
        tvTitle      = findViewById(R.id.tvTitle);
        miniGoalView = findViewById(R.id.miniGoalView);
        tvGoalLabel  = findViewById(R.id.tvGoalLabel);
        btnRestart = findViewById(R.id.btnRestart);
        btnQuit    = findViewById(R.id.btnQuit);


        saveManager = new SaveManager(this);

        plateauView.setVisibility(View.GONE);
        tvTitle.setVisibility(View.GONE);
        btnNext.setVisibility(View.GONE);
        miniGoalView.setVisibility(View.GONE);
        tvGoalLabel.setVisibility(View.GONE);

        // affiche un bouton reprendre sur l'écran d'accueil si sauvegarde existe
        if (saveManager.hasSave()) {
            btnResume.setVisibility(View.VISIBLE);
        } else {
            btnResume.setVisibility(View.GONE);
        }

        // Reprendre la partie sauvegardée
        btnResume.setOnClickListener(v -> {
            imgStart.setVisibility(View.GONE);
            btnNewGame.setVisibility(View.GONE);
            btnResume.setVisibility(View.GONE);

            plateau = new Plateau(3);
            plateauView.setPlateau(plateau);
            plateauView.setVisibility(View.VISIBLE);
            tvTitle.setVisibility(View.VISIBLE);

            loadGameSave();
        });

        // Nouvelle partie
        btnNewGame.setOnClickListener(v -> {
            imgStart.setVisibility(View.GONE);
            btnNewGame.setVisibility(View.GONE);
            btnResume.setVisibility(View.GONE);

            plateau = new Plateau(3);
            plateau.prepareStart();

            plateauView.setPlateau(plateau);
            plateauView.setVisibility(View.VISIBLE);
            tvTitle.setVisibility(View.VISIBLE);

            showStartConfigDialog();
        });

        btnNext.setOnClickListener(v -> {
            if (plateau.getState() == GameState.CONFIG_START) {
                plateau.confirmStart();
                plateauView.invalidate();
                showGoalConfigDialog();
            } else if (plateau.getState() == GameState.CONFIG_END) {
                startGame();
            }
        });

        btnAutoPlay.setOnClickListener( v -> {
            // recupere l'etat actuel et l'objectif depuis le plateau
            int dimension = (int) Math.sqrt(plateau.getSize());
            int total = plateau.getSize();

            int[] currentGrid =new int[total];
            for (int i = 0; i < total; i++) currentGrid[i] = plateau.getCell(i);

            int[] goalGrid = plateau.getGoalGrid();

            // Lancer A* dans un thread separe pour ne pas bloquer l interface
            new Thread(() -> {
                A_star solver = new A_star(dimension, goalGrid);
                List<Integer> moves = solver.solve(currentGrid);

                if (moves == null) {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Aucune solution possible", Toast.LENGTH_SHORT).show());
                    return;
                }

                // Rejouer chaque mouvement avec un delai de 500ms entre chaque noeud
                for (int index : moves) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        return;
                    }
                    runOnUiThread(() -> {
                        plateau.move(index); // utilise la methode existante sans la modifier
                        plateauView.invalidate(); // redessine la vue

                        if (plateau.getState() == GameState.FINISHED) {
                            onGameWon();
                        }
                    });
                }
            }).start();
        });
        btnRestart.setOnClickListener(v -> {
            plateau.reset();
            plateauView.invalidate();
        });
        btnQuit.setOnClickListener(v -> {
            finish();            // ferme l'activité actuelle
        });
    }

    @SuppressLint("SetTextI18n")
    private void showStartConfigDialog() {
        tvTitle.setText("Configuration départ");
        new AlertDialog.Builder(this)
                .setTitle("Configuration départ")
                .setMessage("Comment configurer le départ ?")
                .setPositiveButton("Manuel", (d, w) -> {
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

    @SuppressLint("SetTextI18n")
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

    @SuppressLint("SetTextI18n")
    private void startGame() {
        plateau.confirmGoal();
        saveManager.save(
                (int) Math.sqrt(plateau.getSize()),
                plateau.getStartGrid(),
                plateau.getGoalGrid()
        );
        // Sauvegarder l'état initial comme currentGrid aussi
        saveManager.saveCurrentGrid(plateau.getCurrentGrid());

        tvTitle.setText("En jeu");
        btnNext.setVisibility(View.GONE);
        btnAutoPlay.setVisibility(View.VISIBLE);
        plateauView.invalidate();

        int dim = (int) Math.sqrt(plateau.getSize());
        miniGoalView.setGoalGrid(plateau.getGoalGrid(), dim);
        miniGoalView.setVisibility(View.VISIBLE);
        tvGoalLabel.setVisibility(View.VISIBLE);
        btnRestart.setVisibility(View.VISIBLE);
        btnQuit.setVisibility(View.VISIBLE);
    }

    private void loadGameSave() {
        int[] startGrid   = saveManager.loadStartGrid();
        int[] goalGrid    = saveManager.loadGoalGrid();
        int[] currentGrid = saveManager.loadCurrentGrid(); // ✅ ajout

        if (startGrid == null || goalGrid == null || currentGrid == null) {
            saveManager.clear();
            showStartConfigDialog();
            return;
        }

        plateau.setStartGrid(startGrid);
        plateau.setGoalGrid(goalGrid);

// 👇 IMPORTANT : ne pas reset la grille
        plateau.setCurrentGrid(currentGrid);

// remettre juste l’état
        plateau.setState(GameState.PLAYING);

        tvTitle.setText("En jeu");
        btnNext.setVisibility(View.GONE);
        plateauView.invalidate();

        int dim = (int) Math.sqrt(plateau.getSize());
        miniGoalView.setGoalGrid(plateau.getGoalGrid(), dim);
        miniGoalView.setVisibility(View.VISIBLE);
        tvGoalLabel.setVisibility(View.VISIBLE);
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (plateau != null && plateau.getState() == GameState.PLAYING) {
            saveManager.saveCurrentGrid(plateau.getCurrentGrid());
        }
    }
    public SaveManager getSaveManager() {
        return saveManager;
    }
    public void onGameWon() {
        new AlertDialog.Builder(this)
                .setTitle("Victoire")
                .setMessage("Bravo ! Tu as résolu le taquin !")
                .setPositiveButton("Nouvelle partie", (d, w) -> {
                    restartGame();
                })
                .setNegativeButton("Quitter", (d, w) -> finish())
                .setCancelable(false)
                .show();

        saveManager.clear(); // optionnel : supprimer la sauvegarde
    }
    private void restartGame() {
        plateau = new Plateau(3); // ou dimension sauvegardée si tu veux être clean
        plateau.prepareStart();

        plateauView.setPlateau(plateau);
        plateauView.invalidate();

        tvTitle.setText("Configuration départ");

        miniGoalView.setVisibility(View.GONE);
        tvGoalLabel.setVisibility(View.GONE);
        btnNext.setVisibility(View.GONE);

        showStartConfigDialog();
    }
}