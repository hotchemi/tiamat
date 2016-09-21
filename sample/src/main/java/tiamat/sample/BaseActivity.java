package tiamat.sample;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;

import com.jakewharton.rxbinding.widget.RxCompoundButton;

import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import tiamat.Preference;

abstract class BaseActivity extends AppCompatActivity {

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @NonNull
    public AppComponent getComponent() {
        App application = (App) getApplication();
        return application.getComponent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscriptions.unsubscribe();
    }

    void bindPreference(CheckBox checkBox, Preference<Boolean> preference) {
        subscriptions.add(preference.asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(RxCompoundButton.checked(checkBox)));
        subscriptions.add(RxCompoundButton.checkedChanges(checkBox)
                .skip(1)
                .subscribe(preference.asAction()));
    }
}
