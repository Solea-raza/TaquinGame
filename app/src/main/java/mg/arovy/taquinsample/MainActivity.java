package mg.arovy.taquinsample;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import mg.arovy.taquinsample.model.Plateau;
import mg.arovy.taquinsample.views.PlateauView;

public class MainActivity extends AppCompatActivity {
    private PlateauView plateauView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        plateauView = findViewById(R.id.taquinView);

        Plateau plateau = new Plateau(5);


        plateauView.setPlateau(plateau);
    }
}