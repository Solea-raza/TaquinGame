package mg.arovy.taquin;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import mg.arovy.taquin.model.Plateau;
import mg.arovy.taquin.views.PlateauView;

public class MainActivity extends AppCompatActivity {
    private PlateauView plateauView;
    private Spinner spinnerDimensions;
    private EditText editManualDimension;
    private Plateau plateau;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        plateauView = findViewById(R.id.taquinView);
        spinnerDimensions = findViewById(R.id.spinnerDimensions);
        editManualDimension = findViewById(R.id.editManualDimension);
        Button btnNewGame = findViewById(R.id.btnNewGame);

        int tailleInitiale = getIntent().getIntExtra("TAILLE_CHOISIE", 3);


        String[] options = {"3 x 3", "4 x 4", "5 x 5", "Personnalisé..."};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDimensions.setAdapter(adapter);


        if (tailleInitiale >= 3 && tailleInitiale <= 5) {
            spinnerDimensions.setSelection(tailleInitiale - 3);
        } else {
            spinnerDimensions.setSelection(3);
            editManualDimension.setText(String.valueOf(tailleInitiale));
            editManualDimension.setVisibility(View.VISIBLE);
        }

        spinnerDimensions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 3) {
                    editManualDimension.setVisibility(View.VISIBLE);
                } else {
                    editManualDimension.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnNewGame.setOnClickListener(v -> setupGame());

        initialStart(tailleInitiale);
    }

    private void initialStart(int dimension) {
        plateau = new Plateau(dimension);
        plateau.startNewGame();
        plateauView.setPlateau(plateau);
    }

    private void setupGame() {
        int dimension;
        int selection = spinnerDimensions.getSelectedItemPosition();

        if (selection == 3) {
            String input = editManualDimension.getText().toString();
            if (input.isEmpty()) {
                Toast.makeText(this, "Entre une dimension", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                dimension = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                dimension = 3;
            }
            if (dimension < 2) {
                Toast.makeText(this, "Minimum 2x2", Toast.LENGTH_SHORT).show();
                dimension = 2;
            }
        } else {
            dimension = selection + 3;
        }

        plateau = new Plateau(dimension);
        plateau.startNewGame();
        plateauView.setPlateau(plateau);

        Toast.makeText(this, "Nouvelle partie : " + dimension + "x" + dimension, Toast.LENGTH_SHORT).show();
    }
}