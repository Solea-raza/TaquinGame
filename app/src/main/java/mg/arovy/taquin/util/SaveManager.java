package mg.arovy.taquin.util;
import android.content.Context;
import android.content.SharedPreferences;

import mg.arovy.taquin.views.PlateauView;

public class SaveManager {
    private static String PREFS_NAME = "t_save";
    private static String KEY_DIM = "dim";
    private static String PREFIX_STRART = "start_";
    private static String PREFIX_GOAL = "goal_";

    private SharedPreferences prefs;

    public SaveManager(Context context){
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    //sauvegarde la dimension et les grilles case par case
    public void save(int dimension, int[] startGrid, int[] goalGrid) {
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(KEY_DIM, dimension);

        for (int i = 0; i < startGrid.length; i++) {
            editor.putInt(PREFIX_STRART + i, startGrid[i]);
            editor.putInt(PREFIX_GOAL + i, goalGrid[i]);
        }

        editor.apply();
    }

    //return la dimension sauvegarder
    public int loadDimension() {
        return prefs.getInt(KEY_DIM, -1);
    }

    //relit les cases de depart une par une et retourne l tableau reconstitue, ou null si rien n'est sauvegarder
    public int[] loadStartGrid() {
        return loadGrid(PREFIX_STRART);
    }

    //    //relit les cases d'arrivee une par une et retourne l tableau reconstitue, ou null si rien n'est sauvegarder
    public int[] loadGoalGrid() {
        return loadGrid(PREFIX_GOAL);
    }
        //retourne true si une sauvegarde complete existe
    public Boolean hasSave() {
        return prefs.contains(KEY_DIM)
                && prefs.contains(PREFIX_STRART + "0")
                && prefs.contains(PREFIX_GOAL + "0")
                && prefs.contains("current_0");
    }

    //supprime toutes les donnees sauvegardees
    public void clear() {
        prefs.edit().clear().apply();
    }

    //reconstruit un int [] a partir des cles
    //deduction de la taille via les dimension sauvegarder
    private int[] loadGrid(String prefix) {
        int dim = loadDimension();
        if (dim == -1) return null;

        int total = dim * dim;
        int[] grid = new int[total];

        for (int i = 0; i < total; i++) {
            // Utiliser -2 comme sentinelle au lieu de -1
            // car 0 est une valeur valide (case vide) et -1 aussi potentiellement
            int value = prefs.getInt(prefix + i, -2);
            if (value == -2) return null;
            grid[i] = value;
        }
        return grid;
    }
    // enregistrer l'état courant de la grille
    public void saveCurrentGrid(int[] currentGrid) {
        SharedPreferences.Editor editor = prefs.edit();
        for (int i = 0; i < currentGrid.length; i++) {
            editor.putInt("current_" + i, currentGrid[i]);
        }
        editor.apply();
    }

    // affiche l'état courant de la grille
    public int[] loadCurrentGrid() {
        return loadGrid("current_");
    }
}
