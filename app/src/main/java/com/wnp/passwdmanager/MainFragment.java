package com.wnp.passwdmanager;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wnp.passwdmanager.Database.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {
    private PasswordListDataAdapter mAdapter;
    private PasswordsRepository listRepo;
    private DatabaseViewModel dbViewModel;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;


    private final DatabaseManager.ReadListener<DatabaseManager.Item> listener = items -> getActivity().runOnUiThread(() -> {
       mAdapter.setData(items);
       recyclerView.setVisibility(View.VISIBLE);
       progressBar.setVisibility(View.GONE);
    });

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
        listRepo = new PasswordsRepository(getContext());
        DatabaseManager.getInstance(getContext()).readAll(listener);

        if(savedInstanceState == null) {
            dbViewModel = new ViewModelProvider(getActivity()).get(DatabaseViewModel.class);
            dbViewModel.requestDatabase();
            dbViewModel.getProcess().observe(getViewLifecycleOwner(), databaseState -> {
                switch (databaseState) {
                    case SUCCESS:
                        Log.d("MainFragment", "SUCCESS");
                        break;
                    case IN_PROGRESS:
                        recyclerView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                        break;
                }
            });
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        view.findViewById(R.id.addItemButton).setOnClickListener(v -> {
            ((MainActivity)getActivity()).navigateToFragment(new EditFragment(), true);
        });
    }

    class PasswordListDataAdapter extends RecyclerView.Adapter<PasswordListViewHolder> {
        private List<DatabaseManager.Item> mData;

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
            DatabaseManager.Item item = mData.get(position);
            holder.emailView.setText(item.domain);
            holder.itemView.setOnClickListener(v -> {
                ((MainActivity)getActivity()).navigateToFragment(PasswordViewFragment.newInstance(item), true);
                //Toast.makeText(getContext(), "username " + item.username + " pass " + item.password, Toast.LENGTH_SHORT).show();
            });
        }

        void setData(List<DatabaseManager.Item> data) {
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
