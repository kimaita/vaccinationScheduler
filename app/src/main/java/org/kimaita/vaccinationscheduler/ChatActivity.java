package org.kimaita.vaccinationscheduler;

import static org.kimaita.vaccinationscheduler.Constants.usrDetails;
import static org.kimaita.vaccinationscheduler.Utils.readUserFile;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.kimaita.vaccinationscheduler.adapters.ChatMessageAdapter;
import org.kimaita.vaccinationscheduler.models.ChatMessage;
import org.kimaita.vaccinationscheduler.models.Hospital;
import org.kimaita.vaccinationscheduler.models.Message;
import org.kimaita.vaccinationscheduler.models.User;

import java.io.File;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

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
        viewModel = new ViewModelProvider(this).get(DBViewModel.class);
        materialToolbar = findViewById(R.id.topAppBar);
        inputEditText = findViewById(R.id.textinput_message);
        inputLayout = findViewById(R.id.textinput_layout);
        mRecyclerView = findViewById(R.id.recycler_chat_messages);
        chatAdapter = new ChatMessageAdapter(new ChatMessageAdapter.MessageDiff());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setAdapter(chatAdapter);
        layoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(layoutManager);

        viewModel.getChatMessages(user.getDbID(), hospital.getHospital_id()).observe(this, messages -> {
            chatAdapter.submitList(messages);
        });
        materialToolbar.setTitle(getIntent().getStringExtra("name"));
        //materialToolbar.setOnClickListener();
     /*   materialToolbar.setNavigationOnClickListener(v -> {

        });*/
        inputLayout.setEndIconOnClickListener(v -> {
            if(!inputEditText.getText().toString().isEmpty()) {
                ArrayList<ChatMessage> messages = new ArrayList<>();
                Message message = new Message();
                message.setContent(inputEditText.getText().toString());
                message.setParent(user.getDbID());
                message.setHospital(hospital.getHospital_id());
                message.setTime(System.currentTimeMillis());
                message.setRead(false);
                message.setSender("P");
                messages.add(message);
                viewModel.sendMessage(message);
                viewModel.getChatMessages(user.getDbID(), hospital.getHospital_id()).observe(this, chatMessages -> {
                    chatMessages.add(message);
                });
                inputEditText.setText("");
            } else{
                inputLayout.setBoxStrokeColor(Color.RED);
                inputEditText.requestFocus();
            }
        });

    }

}