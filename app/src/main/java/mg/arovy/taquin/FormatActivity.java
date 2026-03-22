package mg.arovy.taquin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText; // Ajouté
import android.widget.Toast;    // Ajouté pour les messages d'erreur

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FormatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_format);

        // Gestion des marges système
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btn3x3 = findViewById(R.id.btn3x3);
        Button btn4x4 = findViewById(R.id.btn4x4);
        Button btn5x5 = findViewById(R.id.btn5x5);

        btn3x3.setOnClickListener(v -> lancerLeJeu(3));
        btn4x4.setOnClickListener(v -> lancerLeJeu(4));
        btn5x5.setOnClickListener(v -> lancerLeJeu(5));


        EditText editTaillePerso = findViewById(R.id.editTaillePerso);
        Button btnValiderPerso = findViewById(R.id.btnValiderPerso);

        btnValiderPerso.setOnClickListener(v -> {
            String texteSaisi = editTaillePerso.getText().toString();

            if (!texteSaisi.isEmpty()) {
                try {
                    int taille = Integer.parseInt(texteSaisi);

                    if (taille >= 2 && taille <= 10) {
                        lancerLeJeu(taille);
                    } else {
                        Toast.makeText(this, "Choisis une taille entre 2 et 10", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Entre un nombre valide", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Précise une taille !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void lancerLeJeu(int dimension) {
        Intent intent = new Intent(FormatActivity.this, MainActivity.class);
        intent.putExtra("TAILLE_CHOISIE", dimension);
        startActivity(intent);
    }
}
