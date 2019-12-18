package com.wnp.passwdmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.wnp.passwdmanager.Database.PasswordsRepository;
import com.wnp.passwdmanager.Database.PasswordEntity;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private PasswordListDataAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    private PasswordsViewModel passwordsViewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.passwordList);
        progressBar = view.findViewById(R.id.progressBar);
        mAdapter = new PasswordListDataAdapter();
        recyclerView.setAdapter(mAdapter);
        passwordsViewModel = new ViewModelProvider(getActivity()).get(PasswordsViewModel.class);
        passwordsViewModel.getAllPasswords().observe(getViewLifecycleOwner(), t -> {
            mAdapter.setData(t);
            mSwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        });
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        view.findViewById(R.id.addItemButton).setOnClickListener(v -> {
            ((MainActivity)getActivity()).navigateToFragment(new EditFragment(), true);
        });
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
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
            holder.emailView.setText(item.getDomain_name());
            holder.itemView.setOnClickListener(v -> {
                MainActivity activity = (MainActivity)getActivity();
                if( activity != null) {
                    activity.navigateToFragment(PasswordViewFragment.newInstance(item), true);
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
        private final TextView emailView;

        PasswordListViewHolder(@NonNull View itemView) {
            super(itemView);
            emailView = itemView.findViewById(R.id.email_list_item);
            itemView.findViewById(R.id.copy_but).setOnClickListener(v -> {
            });
        }
    }

}
