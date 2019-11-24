package com.wnp.passwdmanager;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

public class DatabaseViewModel extends AndroidViewModel {
    private final MediatorLiveData<DatabaseState> mDatabaseState = new MediatorLiveData<>();
    public LiveData<DatabaseState> getProcess() { return mDatabaseState; }

    public DatabaseViewModel(@NonNull Application application) {
        super(application);
        mDatabaseState.postValue(DatabaseState.NONE);
    }

    void requestDatabase() {
        mDatabaseState.postValue(DatabaseState.IN_PROGRESS);
        final LiveData<DatabaseRepo.DatabaseStatus> progressLiveData =
                DatabaseRepo.getInstance(getApplication()).getDatabase();
        mDatabaseState.addSource(progressLiveData, databaseStatus -> {
            switch (databaseStatus) {
                case IN_PROGRESS:
                    mDatabaseState.postValue(DatabaseState.IN_PROGRESS);
                    break;
                case SUCCESS:
                    mDatabaseState.postValue(DatabaseState.SUCCESS);
                    mDatabaseState.removeSource(progressLiveData);
                    break;
                case FAILED:
                    mDatabaseState.postValue(DatabaseState.FAILED);
                    mDatabaseState.removeSource(progressLiveData);
                    break;

                    default:
                        mDatabaseState.postValue(DatabaseState.NONE);
                        mDatabaseState.removeSource(progressLiveData);
                        break;
            }
        });
    }

    enum DatabaseState {
        NONE,
        SUCCESS,
        IN_PROGRESS,
        FAILED
    }
}
