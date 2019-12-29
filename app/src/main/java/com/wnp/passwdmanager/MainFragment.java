package com.wnp.passwdmanager;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wnp.passwdmanager.Database.EncryptionWorker;
import com.wnp.passwdmanager.Database.PasswordEntity;
import com.wnp.passwdmanager.Network.SyncWorker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class MainFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String WORK_NAME = "singleSyncWorker";
    private PasswordListDataAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView recyclerView;
    private final static String TAG = "mainFrag";
    private PasswordsViewModel passwordsViewModel;
    private FloatingActionButton addButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.exit_option:
                Objects.requireNonNull(getActivity()).finishAndRemoveTask();
                return true;
            case R.id.settings_option:
                ((MainActivity)getActivity()).navigateToFragment(new settings_fragment(), true);
                return false;
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
        mAdapter = new PasswordListDataAdapter();
        recyclerView.setAdapter(mAdapter);
        passwordsViewModel = new ViewModelProvider(Objects.requireNonNull(getActivity())).get(PasswordsViewModel.class);

        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        addButton = view.findViewById(R.id.addItemButton);
        MainActivity activity = ((MainActivity) getActivity());
        addButton.setOnClickListener(v ->
                activity.navigateToFragment(new EditFragment(), true));
        WorkManager.getInstance()
                .getWorkInfoByIdLiveData(((MainActivity) getActivity()).getDecryptRequest().getId())
                .observe(getViewLifecycleOwner(), workInfo -> {
                    if(workInfo.getState().isFinished()) {
                        refreshView();
                    }
        });
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
            return new PasswordListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PasswordListViewHolder holder, int position) {
            PasswordEntity item = mData.get(position);
            holder.urlView.setText(item.getURL());
            holder.domainView.setText(item.getDomain_name());
            holder.itemView.setOnClickListener(v -> {
                MainActivity activity = (MainActivity) getActivity();
                if (activity != null) {
                    activity.navigateToFragment(PasswordViewFragment.newInstance(item), true);
                }
            });
            holder.itemView.setOnLongClickListener(v -> {
                Log.d(TAG, "onLongClick");
                return true;
            });
            holder.itemView.findViewById(R.id.copy_but).setOnClickListener(v -> {
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
            });
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

}
