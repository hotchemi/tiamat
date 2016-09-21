package tiamat.sample;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import javax.inject.Inject;

import tiamat.sample.databinding.ActivityMainBinding;

public final class MainActivity extends BaseActivity {

    @Inject
    StatusSharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent().inject(this);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.mainButton.setOnClickListener(view -> startActivity(NextActivity.createIntent(getApplicationContext())));
        binding.mainToggleButton.setOnClickListener(view -> {
            if (sharedPreferences.getChecked().asValue()) {
                sharedPreferences.setChecked(false);
            } else {
                sharedPreferences.setChecked(true);
            }
        });
        bindPreference(binding.mainCheckbox, sharedPreferences.getChecked());
    }
}
