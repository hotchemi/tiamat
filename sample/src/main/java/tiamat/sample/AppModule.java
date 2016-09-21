package tiamat.sample;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
class AppModule {

    private Context context;

    AppModule(Application app) {
        context = app;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return context;
    }

    @Provides
    @Singleton
    StatusSharedPreferences provideStatusPreferences(Context context) {
        return new StatusSharedPreferences(context);
    }
}
