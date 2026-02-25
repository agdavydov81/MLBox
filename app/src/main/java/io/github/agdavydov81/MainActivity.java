package io.github.agdavydov81;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialCardView classificationCard = findViewById(R.id.classification_card);
        MaterialCardView regressionCard = findViewById(R.id.regression_card);
        MaterialCardView clusteringCard = findViewById(R.id.clustering_card);

        classificationCard.setOnClickListener(v -> OnClassificationSelected());
        regressionCard.setOnClickListener(v -> OnRegressionSelected());
        clusteringCard.setOnClickListener(v -> OnClusterizationSelected());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    if (isEnabled()) {
                        setEnabled(false);
                        onBackPressed();
                    }
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_classification) {
            OnClassificationSelected();
        } else if (itemId == R.id.nav_regression) {
            OnRegressionSelected();
        } else if (itemId == R.id.nav_clusterization) {
            OnClusterizationSelected();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void OnClassificationSelected() {
        Intent intent = new Intent(this, ClassificationActivity.class);
        startActivity(intent);
    }

    public void OnRegressionSelected() {
        Toast.makeText(this, "Regression selected", Toast.LENGTH_SHORT).show();
    }

    public void OnClusterizationSelected() {
        Toast.makeText(this, "Clusterization selected", Toast.LENGTH_SHORT).show();
    }
}