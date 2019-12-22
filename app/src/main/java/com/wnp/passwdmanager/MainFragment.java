package com.wnp.passwdmanager;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.work.ListenableWorker;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Operation;
import androidx.work.WorkManager;

import com.wnp.passwdmanager.Database.PasswordEntity;
import com.wnp.passwdmanager.Network.SyncWorker;

import java.util.ArrayList;
import java.util.List;


public class MainFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private PasswordListDataAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private final static String TAG = "mainFrag";
    private PasswordsViewModel passwordsViewModel;


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
                getActivity().finishAndRemoveTask();
                return true;
            case R.id.settings_option:
                //TODO:
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
        progressBar = view.findViewById(R.id.progressBar);
        mAdapter = new PasswordListDataAdapter();
        recyclerView.setAdapter(mAdapter);
        passwordsViewModel = new ViewModelProvider(getActivity()).get(PasswordsViewModel.class);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        passwordsViewModel.getAllPasswords().observe(getViewLifecycleOwner(), t -> {
            mAdapter.setData(t);
            mSwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        });
        mSwipeRefreshLayout.setOnRefreshListener(this);

        OneTimeWorkRequest syncRequest = new OneTimeWorkRequest
                .Builder(SyncWorker.class).build();
        WorkManager.getInstance().enqueue(syncRequest);
        WorkManager.getInstance().getWorkInfoByIdLiveData(syncRequest.getId()).observe(getViewLifecycleOwner(), state -> {
            switch (state.getState()) {
                case SUCCEEDED:
                    mSwipeRefreshLayout.setRefreshing(false);
                    passwordsViewModel.updateDB();
                    passwordsViewModel.getAllPasswords().observe(getViewLifecycleOwner(), t -> {
                        mAdapter.setData(t);
                    });
                    Log.d(TAG, "SUCCESS");
                    break;
                case FAILED:
                    mSwipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(), "FAILED", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        view.findViewById(R.id.addItemButton).setOnClickListener(v -> {
            ((MainActivity)getActivity()).navigateToFragment(new EditFragment(), true);
        });
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        OneTimeWorkRequest syncRequest = new OneTimeWorkRequest
                .Builder(SyncWorker.class).build();
        WorkManager.getInstance().enqueue(syncRequest);
        WorkManager.getInstance().getWorkInfoByIdLiveData(syncRequest.getId()).observe(getViewLifecycleOwner(), state -> {
            switch (state.getState()) {
                case SUCCEEDED:
                    mSwipeRefreshLayout.setRefreshing(false);
                    passwordsViewModel.updateDB();
                    Log.d(TAG, "SUCCESS");
                    break;
                case FAILED:
                    mSwipeRefreshLayout.setRefreshing(false);
                    Log.d(TAG, "FAILURE");
                    break;
                default:
                    mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        //passwordsViewModel.updateDB();
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
                MainActivity activity = (MainActivity)getActivity();
                if( activity != null) {
                    activity.navigateToFragment(PasswordViewFragment.newInstance(item), true);
                }
            });
            holder.itemView.findViewById(R.id.copy_but).setOnClickListener(v -> {
                Animator scale = ObjectAnimator.ofPropertyValuesHolder(v,
                        PropertyValuesHolder.ofFloat(View.SCALE_X, 1, 1.5f, 1),
                        PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, 1.5f, 1));
                scale.setDuration(1000);
                scale.start();
                ClipData clipData = ClipData.newPlainText(null, item.getPassword());
                if(getActivity() != null) {
                    ClipboardManager clipboardManager = (ClipboardManager) getActivity()
                            .getSystemService(Context.CLIPBOARD_SERVICE);
                    if(clipboardManager != null)
                        clipboardManager.setPrimaryClip(clipData);
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
