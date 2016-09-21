package tiamat.sample;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import tiamat.sample.databinding.ActivityMainBinding;

public final class MainActivity extends BaseActivity {

    StatusSharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.mainButton.setOnClickListener(view -> startActivity(NextActivity.createIntent(getApplicationContext())));
        getComponent().inject(this);
        bindPreference(binding.mainCheckbox, sharedPreferences.getChecked());
    }
}
