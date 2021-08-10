package org.kimaita.vaccinationscheduler;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.jetbrains.annotations.NotNull;
import org.kimaita.vaccinationscheduler.adapters.ChatListAdapter;
import org.kimaita.vaccinationscheduler.databinding.FragmentMessagesBinding;

import java.io.File;

import static org.kimaita.vaccinationscheduler.Constants.usrDetails;
import static org.kimaita.vaccinationscheduler.Utils.readUserFile;

public class MessagesFragment extends Fragment {

    FragmentMessagesBinding binding;
    ExtendedFloatingActionButton btnNewChat;
    RecyclerView recyclerView;
    ChatListAdapter mAdapter;
    DBViewModel viewModel;
    File file;

    public MessagesFragment() {
        // Required empty public constructor
    }

    public static MessagesFragment newInstance() {
        return new MessagesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        file = new File(getContext().getFilesDir(), usrDetails);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMessagesBinding.inflate(inflater, container, false);
        btnNewChat = binding.btnNewChat;
        recyclerView = binding.recyclerMessageList;
        mAdapter = new ChatListAdapter(new ChatListAdapter.MessageListDiff(), message -> {
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra("hospital", message.getHospital());
            intent.putExtra("name", message.getHospitalName());
            startActivity(intent);
        });
        viewModel = new ViewModelProvider(this).get(DBViewModel.class);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), mLayoutManager.getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(mLayoutManager);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getChatList(readUserFile(file).getDbID()).observe(getViewLifecycleOwner(), messages -> mAdapter.submitList(messages));
        btnNewChat.setOnClickListener(v -> Navigation.findNavController(getView()).navigate(MessagesFragmentDirections.actionNewchat()));
    }
}