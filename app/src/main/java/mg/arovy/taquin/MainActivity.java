package mg.arovy.taquin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import mg.arovy.taquin.IA.A_star;
import mg.arovy.taquin.model.GameState;
import mg.arovy.taquin.model.Plateau;
import mg.arovy.taquin.util.SaveManager;
import mg.arovy.taquin.views.MiniPlateauView;
import mg.arovy.taquin.views.PlateauView;

public class MainActivity extends AppCompatActivity {

    private PlateauView plateauView;
    private MiniPlateauView miniGoalView;
    private TextView tvGoalLabel;
    private Plateau plateau;
    private Button btnNext, btnAutoPlay, btnNewGame, btnResume;
    private ImageButton btnRestart, btnQuit;
    private TextView tvTitle;
    private SaveManager saveManager;
    private LinearLayout layoutAccueil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        plateauView   = findViewById(R.id.taquinView);
        btnNext       = findViewById(R.id.btnNext);
        btnAutoPlay   = findViewById(R.id.btnAutoPlay);
        tvTitle       = findViewById(R.id.tvTitle);
        miniGoalView  = findViewById(R.id.miniGoalView);
        tvGoalLabel   = findViewById(R.id.tvGoalLabel);
        btnRestart    = findViewById(R.id.btnRestart);
        btnQuit       = findViewById(R.id.btnQuit);
        btnNewGame    = findViewById(R.id.btnNewGame);
        btnResume     = findViewById(R.id.btnResume);
        layoutAccueil = findViewById(R.id.layoutAccueil);

        saveManager = new SaveManager(this);

        // État initial : tout caché
        plateauView.setVisibility(View.GONE);
        tvTitle.setVisibility(View.GONE);
        btnNext.setVisibility(View.GONE);
        miniGoalView.setVisibility(View.GONE);
        tvGoalLabel.setVisibility(View.GONE);
        btnAutoPlay.setVisibility(View.GONE);
        btnRestart.setVisibility(View.GONE);
        btnQuit.setVisibility(View.GONE);
        layoutAccueil.setVisibility(View.GONE);

        btnNext.setOnClickListener(v -> handleNext());
        btnAutoPlay.setOnClickListener(v -> startAutoPlay());
        btnRestart.setOnClickListener(v -> { plateau.reset(); plateauView.invalidate(); });
        btnQuit.setOnClickListener(v -> finish());

        // Vérifie si on vient de FormatActivity avec une taille choisie
        int dim = getIntent().getIntExtra("TAILLE_CHOISIE", -1);
        if (dim != -1) {
            startNewGameFlow(dim);
        } else {
            showAccueil();
        }
    }

    /*** -------------------- ACCUEIL -------------------- ***/

    private void showAccueil() {
        layoutAccueil.setVisibility(View.VISIBLE);
        btnResume.setVisibility(saveManager.hasSave() ? View.VISIBLE : View.GONE);

        btnNewGame.setOnClickListener(v -> {
            Intent intent = new Intent(this, FormatActivity.class);
            startActivity(intent);
        });

        btnResume.setOnClickListener(v -> {
            int[] startGrid = saveManager.loadStartGrid();
            if (startGrid == null) {
                saveManager.clear();
                return;
            }
            // On retrouve la dimension depuis la grille sauvegardée
            int savedDim = (int) Math.sqrt(startGrid.length);

            layoutAccueil.setVisibility(View.GONE);
            plateau = new Plateau(savedDim);
            plateauView.setPlateau(plateau);
            plateauView.setVisibility(View.VISIBLE);
            tvTitle.setVisibility(View.VISIBLE);
            loadGameSave();
        });
    }

    /*** -------------------- FLUX NOUVELLE PARTIE -------------------- ***/

    private void startNewGameFlow(int dim) {
        plateau = new Plateau(dim);
        plateau.prepareStart();
        plateauView.setPlateau(plateau);
        plateauView.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.VISIBLE);
        showStartConfigDialog();
    }

    private void handleNext() {
        if (plateau.getState() == GameState.CONFIG_START) {
            plateau.confirmStart();
            plateauView.invalidate();
            showGoalConfigDialog();
        } else if (plateau.getState() == GameState.CONFIG_END) {
            startGame();
        }
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
        saveManager.saveCurrentGrid(plateau.getCurrentGrid());

        tvTitle.setText("En jeu");
        btnNext.setVisibility(View.GONE);
        btnAutoPlay.setVisibility(View.VISIBLE);
        btnRestart.setVisibility(View.VISIBLE);
        btnQuit.setVisibility(View.VISIBLE);
        plateauView.invalidate();

        int dim = (int) Math.sqrt(plateau.getSize());
        miniGoalView.setGoalGrid(plateau.getGoalGrid(), dim);
        miniGoalView.setVisibility(View.VISIBLE);
        tvGoalLabel.setVisibility(View.VISIBLE);
    }

    /*** -------------------- AUTOPLAY -------------------- ***/

    private void startAutoPlay() {
        int dimension = (int) Math.sqrt(plateau.getSize());
        int total = plateau.getSize();

        int[] currentGrid = new int[total];
        for (int i = 0; i < total; i++) currentGrid[i] = plateau.getCell(i);

        int[] goalGrid = plateau.getGoalGrid();

        new Thread(() -> {
            A_star solver = new A_star(dimension, goalGrid);
            List<Integer> moves = solver.solve(currentGrid);

            if (moves == null) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Aucune solution possible", Toast.LENGTH_SHORT).show());
                return;
            }

            for (int index : moves) {
                try { Thread.sleep(500); } catch (InterruptedException e) { return; }
                runOnUiThread(() -> {
                    plateau.move(index);
                    plateauView.invalidate();
                    if (plateau.getState() == GameState.FINISHED) onGameWon();
                });
            }
        }).start();
    }

    /*** -------------------- SAUVEGARDE -------------------- ***/

    private void loadGameSave() {
        int[] startGrid   = saveManager.loadStartGrid();
        int[] goalGrid    = saveManager.loadGoalGrid();
        int[] currentGrid = saveManager.loadCurrentGrid();

        if (startGrid == null || goalGrid == null || currentGrid == null) {
            saveManager.clear();
            showAccueil();
            return;
        }

        plateau.setStartGrid(startGrid);
        plateau.setGoalGrid(goalGrid);
        plateau.setCurrentGrid(currentGrid);
        plateau.setState(GameState.PLAYING);

        tvTitle.setText("En jeu");
        btnNext.setVisibility(View.GONE);
        btnAutoPlay.setVisibility(View.VISIBLE);
        btnRestart.setVisibility(View.VISIBLE);
        btnQuit.setVisibility(View.VISIBLE);
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

    /*** -------------------- FIN DE PARTIE -------------------- ***/

    public void onGameWon() {
        new AlertDialog.Builder(this)
                .setTitle("Victoire")
                .setMessage("Bravo ! Tu as résolu le taquin !")
                .setPositiveButton("Nouvelle partie", (d, w) -> {
                    saveManager.clear();
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Quitter", (d, w) -> {
                    saveManager.clear();
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    public SaveManager getSaveManager() {
        return saveManager;
    }
}