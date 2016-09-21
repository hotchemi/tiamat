package tiamat.sample;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
class AppModule {

    private Context context;

    public AppModule(Application app) {
        context = app;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return context;
    }

    @Provides
    @Singleton
    public StatusSharedPreferences provideStatusPreferences(Context context) {
        return new StatusSharedPreferences(context);
    }
}
