package com.example.anomalbrowser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.anomalbrowser.databinding.ActivityMainBinding;
import com.example.anomalbrowser.firebaseController.BDControl;
import com.example.anomalbrowser.fragments.BrowserFragment;
import com.example.anomalbrowser.fragments.HomeFragment;
import com.example.anomalbrowser.fragments.ProfileFragment;
import com.example.anomalbrowser.fragments.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    private void init()
    {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        fragmentManager = getSupportFragmentManager();

        homeFragment = new HomeFragment();
        settingsFragment = new SettingsFragment();
        profileFragment = new ProfileFragment();
        browserFragment = new BrowserFragment();
        bdControl = new BDControl();
        ProfileFragment.setUserName(bdControl.getUserNameFromBd());
    }

    HomeFragment homeFragment;
    SettingsFragment settingsFragment;
    ProfileFragment profileFragment;
    BrowserFragment browserFragment;

    BDControl bdControl;


    FragmentManager fragmentManager;
    ActivityMainBinding binding;

    @SuppressLint({"NonConstantResourceId", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setContentView(binding.getRoot());

//        setContentView(binding.getRoot());
//        replaceFragment(homeFragment);
        binding.bottomNavigationView.findViewById(R.id.browserFragment).performClick();
        binding.bottomNavigationView.setOnItemSelectedListener(item ->
        {
            switch (item.getItemId())
            {
                case R.id.homeFragment:
                    Navigation.findNavController(this, R.id.navHostFragment).navigate(R.id.homeFragment);
//                    replaceFragment(homeFragment);
                    break;
                case R.id.profileFragment:
                    Navigation.findNavController(this, R.id.navHostFragment).navigate(R.id.profileFragment);
//                    replaceFragment(profileFragment);
                    break;
                case R.id.browserFragment:
                    Navigation.findNavController(this, R.id.navHostFragment).navigate(R.id.browserFragment);
//                    replaceFragment(browserFragment);
                    break;
            }

            return true;
        });

    }

//    public void replaceFragment(Fragment fragment)
//    {
//        fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.frame_layout_activity, fragment);
//        fragmentTransaction.commit();
//    }


}