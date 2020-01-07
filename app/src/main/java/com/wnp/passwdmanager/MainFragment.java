package com.wnp.passwdmanager;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wnp.passwdmanager.AuthPart.UnlockFragment;
import com.wnp.passwdmanager.Database.EncryptionWorker;
import com.wnp.passwdmanager.Database.PasswordEntity;
import com.wnp.passwdmanager.Network.SyncWorker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class MainFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private PasswordListDataAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView recyclerView;
    private final static String TAG = "mainFrag";
    private PasswordsViewModel passwordsViewModel;
    private FloatingActionButton addButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        MainActivity activity = (MainActivity) getActivity();
        if (activity == null)
            return false;
        switch (item.getItemId()) {
            case R.id.exit_option:
                activity.finishAndRemoveTask();
                return true;
            case R.id.settings_option:
                activity.navigateToFragment(new settings_fragment(), "SETTINGS");
                return true;
            case R.id.lock_option:
                activity.navigateToFragment(new UnlockFragment(), null);
                recyclerView.setAdapter(null);
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.passwordList);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        addButton = view.findViewById(R.id.addItemButton);

        if (mAdapter == null) {
            mAdapter = new PasswordListDataAdapter();
        }
        recyclerView.setAdapter(mAdapter);
        passwordsViewModel = new ViewModelProvider(Objects.requireNonNull(getActivity())).get(PasswordsViewModel.class);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        MainActivity activity = ((MainActivity) getActivity());
        addButton.setOnClickListener(v ->
                activity.navigateToFragment(new EditFragment(), "EDIT"));
    }

    @Override
    public void onRefresh() {
        refreshView();
    }

    private void refreshView() {
        mSwipeRefreshLayout.setRefreshing(true);
        addButton.setEnabled(false);
        passwordsViewModel.getAllPasswords()
                .observe(getViewLifecycleOwner(), t -> mAdapter.setData(t));

        WorkManager workManager = WorkManager.getInstance();
        Constraints constraints = new Constraints.Builder()
//                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build();
        OneTimeWorkRequest syncRequest = new OneTimeWorkRequest
                .Builder(SyncWorker.class)
                .setConstraints(constraints)
                .build();
        workManager.enqueue(syncRequest);
        workManager.getWorkInfoByIdLiveData(syncRequest.getId()).observe(getViewLifecycleOwner(), workInfo -> {
            switch (workInfo.getState()) {
                case SUCCEEDED:
                    passwordsViewModel.getAllPasswords()
                            .observe(getViewLifecycleOwner(), t -> mAdapter.setData(t));
                    mSwipeRefreshLayout.setRefreshing(false);
                    addButton.setEnabled(true);
                    break;
                case FAILED:
                    Toast.makeText(getContext(), "Failed to sync with server", Toast.LENGTH_SHORT).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                    addButton.setEnabled(true);
                    break;
                case RUNNING:
                    break;
            }
        });
    }

    class PasswordListDataAdapter extends RecyclerView.Adapter<PasswordListViewHolder> {

        @Override
        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            Data data = new Data.Builder()
                    .putString(EncryptionWorker.TYPE, EncryptionWorker.DECRYPT).build();
            OneTimeWorkRequest decryptRequest = new OneTimeWorkRequest.
                    Builder(EncryptionWorker.class)
                    .setInputData(data).build();
            WorkManager.getInstance().enqueue(decryptRequest);
            WorkManager.getInstance()
                    .getWorkInfoByIdLiveData(decryptRequest.getId())
                    .observe(getViewLifecycleOwner(), workInfo -> {
                        if (workInfo.getState().isFinished()) {
                            refreshView();
                        }
                    });
            Log.d(TAG, "ATTACHED TO RECYCLER VIEW");
        }

        private List<PasswordEntity> mData;

        PasswordListDataAdapter() {
            mData = new ArrayList<>();
        }

        @NonNull
        @Override
        public PasswordListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.pass_list_item, parent, false);
            PasswordListViewHolder holder = new PasswordListViewHolder(view);
            view.setOnClickListener(v -> {
                int pos = holder.getAdapterPosition();
                Log.d(TAG, "POSITION" + pos);
                MainActivity activity = (MainActivity) getActivity();
                if (activity != null && pos != RecyclerView.NO_POSITION) {
                    PasswordEntity item = mData.get(pos);
                    activity.navigateToFragment(PasswordViewFragment.newInstance(item), "VIEW");
                }
            });
            view.findViewById(R.id.copy_but).setOnClickListener(v -> {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    PasswordEntity item = mData.get(pos);
                    Animator scale = ObjectAnimator.ofPropertyValuesHolder(v,
                            PropertyValuesHolder.ofFloat(View.SCALE_X, 1, 1.5f, 1),
                            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, 1.5f, 1));
                    scale.setDuration(1000);
                    scale.start();
                    ClipData clipData = ClipData.newPlainText(null, item.getPassword());
                    if (getActivity() != null) {
                        ClipboardManager clipboardManager = (ClipboardManager) getActivity()
                                .getSystemService(Context.CLIPBOARD_SERVICE);
                        if (clipboardManager != null) {
                            clipboardManager.setPrimaryClip(clipData);
                            Toast.makeText(getContext(), "Copied", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull PasswordListViewHolder holder, int position) {
            PasswordEntity item = mData.get(position);
            holder.urlView.setText(item.getURL());
            holder.domainView.setText(item.getDomain_name());
        }

        void setData(List<PasswordEntity> data) {
            mData = data;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    class PasswordListViewHolder extends RecyclerView.ViewHolder {
        private final TextView urlView;
        private final TextView domainView;

        PasswordListViewHolder(@NonNull View itemView) {
            super(itemView);
            urlView = itemView.findViewById(R.id.url_list_item);
            domainView = itemView.findViewById(R.id.domain_list_item);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }
}
