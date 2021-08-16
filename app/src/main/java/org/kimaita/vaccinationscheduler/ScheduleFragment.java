package org.kimaita.vaccinationscheduler;

import static org.kimaita.vaccinationscheduler.Constants.usrDetails;
import static org.kimaita.vaccinationscheduler.Utils.readUserFile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.kimaita.vaccinationscheduler.databinding.FragmentScheduleBinding;
import org.kimaita.vaccinationscheduler.models.Child;

import java.io.File;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScheduleFragment extends Fragment {

    FragmentScheduleBinding binding;
    DBViewModel viewModel;
    ViewPager2 viewPager;
    TabLayout tabLayout;
    File file;
    int childCount;
    ArrayList<Child> mChildList = new ArrayList<>();

    public ScheduleFragment() {
        // Required empty public constructor
    }

    public static ScheduleFragment newInstance() {
        return new ScheduleFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        file = new File(getContext().getFilesDir(), usrDetails);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentScheduleBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(DBViewModel.class);
        viewPager = binding.pager;
        tabLayout = binding.tabLayout;

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getChildren(readUserFile(file).getDbID()).observe(getViewLifecycleOwner(), children -> {
            childCount = children.size();
            mChildList.addAll(children);
            viewPager.setAdapter(new AppointmentsFragmentsAdapter(this));
            // attaching tab mediator
            new TabLayoutMediator(tabLayout, viewPager,
                    (tab, position) -> tab.setText(children.get(position).getChildName())).attach();

        });
    }

    private class AppointmentsFragmentsAdapter extends FragmentStateAdapter {

        public AppointmentsFragmentsAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {

                return new ChildAppointmentsFragment().newInstance(mChildList.get(position).getChildDBID());

        }

        @Override
        public int getItemCount() {
            return childCount;
        }
    }
}