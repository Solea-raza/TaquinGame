package mg.arovy.taquin.util;
import android.content.Context;
import android.content.SharedPreferences;

import mg.arovy.taquin.views.PlateauView;

public class SaveManager {
    private static String PREFS_NAME = "t_save";
    private static String KEY_DIM = "dim";
    private static String PREFIX_STRART = "start ";
    private static String PREFIX_GOAL = "goal ";

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
                && prefs.contains(PREFIX_GOAL + "0");
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
            //valeur par defaut -1 permet de detecter une cle manquante
            int value = prefs.getInt(prefix + i, -1);
            if (value == -1) return null;
            grid[i] = value;
        }
        return grid;
    }
}
