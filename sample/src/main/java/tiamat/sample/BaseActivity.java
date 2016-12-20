package tiamat.sample;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;

import com.jakewharton.rxbinding.widget.RxCompoundButton;

import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import tiamat.Preference;

abstract class BaseActivity extends AppCompatActivity {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @NonNull
    protected AppComponent getComponent() {
        App application = (App) getApplication();
        return application.getComponent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();;
    }

    void bindPreference(CheckBox checkBox, Preference<Boolean> preference) {
        compositeDisposable.add(preference.asFlowable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(checkBox::setChecked));
        compositeDisposable.add(RxJavaInterop.toV2Observable(RxCompoundButton.checkedChanges(checkBox))
                .skip(1)
                .subscribe(preference.asConsumer()));
    }
}
