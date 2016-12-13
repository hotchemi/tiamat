package tiamat.sample;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import tiamat.sample.databinding.ActivityNextBinding;

public class NextActivity extends BaseActivity {

    public static Intent createIntent(Context context) {
        return new Intent(context, NextActivity.class);
    }

    @Inject
    StatusSharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityNextBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_next);
        binding.nextButton.setOnClickListener(view -> finish());
        getComponent().inject(this);
        bindPreference(binding.nextCheckbox, sharedPreferences.getChecked());
        Consumer<? super Boolean> value = sharedPreferences.getBooleanValue().asConsumer();
    }
}
