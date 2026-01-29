package mg.arovy.taquin;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import mg.arovy.taquin.model.Plateau;
import mg.arovy.taquin.views.PlateauView;

public class MainActivity extends AppCompatActivity {
    private PlateauView plateauView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        plateauView = findViewById(R.id.taquinView);

        Plateau plateau = new Plateau(3, 3);

        plateauView.setPlateau(plateau);
    }
}