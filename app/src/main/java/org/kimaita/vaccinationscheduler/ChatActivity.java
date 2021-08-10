package org.kimaita.vaccinationscheduler;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.kimaita.vaccinationscheduler.adapters.ChatMessageAdapter;
import org.kimaita.vaccinationscheduler.databinding.ActivityChatBinding;
import org.kimaita.vaccinationscheduler.models.Hospital;
import org.kimaita.vaccinationscheduler.models.Message;
import org.kimaita.vaccinationscheduler.models.User;

import java.io.File;

import static org.kimaita.vaccinationscheduler.Constants.usrDetails;
import static org.kimaita.vaccinationscheduler.Utils.readUserFile;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    RecyclerView mRecyclerView;
    TextInputEditText inputEditText;
    TextInputLayout inputLayout;
    MaterialToolbar materialToolbar;
    DBViewModel viewModel;
    ChatMessageAdapter chatAdapter;
    File file;
    User user = new User();
    Hospital hospital = new Hospital();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        file = new File(this.getFilesDir(), usrDetails);
        user = readUserFile(file);
        int hosID = getIntent().getIntExtra("hospital", 0);
        hospital.setHospital_id(hosID);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(DBViewModel.class);
        materialToolbar = findViewById(R.id.topAppBar);
        inputEditText = findViewById(R.id.textinput_message);
        inputLayout = findViewById(R.id.textinput_layout);
        mRecyclerView = findViewById(R.id.recycler_chat_messages);
        chatAdapter = new ChatMessageAdapter(new ChatMessageAdapter.MessageDiff(), user);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setAdapter(chatAdapter);
        layoutManager.setReverseLayout(true);
        mRecyclerView.setLayoutManager(layoutManager);

        viewModel.getChatMessages(user.getDbID(), hospital.getHospital_id()).observe(this, messages -> {
            chatAdapter.submitList(messages);
        });
        materialToolbar.setTitle(getIntent().getStringExtra("name"));
        //materialToolbar.setOnClickListener();
     /*   materialToolbar.setNavigationOnClickListener(v -> {

        });*/
        inputLayout.setEndIconOnClickListener(v -> {
            Message message = new Message();
            message.setContent(inputEditText.getText().toString());
            message.setSender(user.getDbID());
            message.setHospital(hospital.getHospital_id());
            message.setTime(System.currentTimeMillis());
            message.setRead(false);
            viewModel.sendMessage(message);
            inputEditText.setText("");
        });

    }

}