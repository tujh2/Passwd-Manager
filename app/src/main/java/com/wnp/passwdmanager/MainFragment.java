package com.wnp.passwdmanager;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainFragment extends Fragment {
    private PasswordListDataAdapter mAdapter;
    private PasswordsRepository listRepo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.passwordList);
        listRepo = new PasswordsRepository(getContext().getFilesDir().getAbsolutePath() + "/sampleDb.db", getContext());
        mAdapter = new PasswordListDataAdapter(listRepo.getData());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(mAdapter);
    }

    class PasswordListDataAdapter extends RecyclerView.Adapter<PasswordListViewHolder> {
        private final List<PasswordsRepository.Item> mData;

        PasswordListDataAdapter(List<PasswordsRepository.Item> data) {
            mData = data;
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
            PasswordsRepository.Item item = mData.get(position);
            holder.emailView.setText(item.domain);
            holder.itemView.setOnClickListener(v -> {
                Toast.makeText(getContext(), "username " + item.username + " pass " + item.password, Toast.LENGTH_SHORT).show();
            });
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
