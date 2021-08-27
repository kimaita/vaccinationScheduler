package org.kimaita.vaccinationscheduler;

import static org.kimaita.vaccinationscheduler.Constants.usrDetails;
import static org.kimaita.vaccinationscheduler.Utils.readUserFile;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.kimaita.vaccinationscheduler.databinding.FragmentVaccineDetailsBinding;
import org.kimaita.vaccinationscheduler.models.Child;
import org.kimaita.vaccinationscheduler.models.Vaccine;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.TimeZone;


public class VaccineDetailsFragment extends Fragment {

    private static final String ARG_VACCINE_ID = "param1";
    private int vaccineID;
    private Vaccine mVaccine = new Vaccine();
    private FragmentVaccineDetailsBinding binding;
    MaterialTextView textViewDisease, textViewAges, textViewLink, textName;
    LinearLayout layoutAges, layoutDiseases;
    TabLayout mTabLayout;
    ViewPager2 mViewPager;
    DBViewModel viewModel;
    File file;
    int childCount;
    ArrayList<Child> mChildList = new ArrayList<>();

    public VaccineDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        file = new File(getContext().getFilesDir(), usrDetails);
        viewModel = new ViewModelProvider(this).get(DBViewModel.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentVaccineDetailsBinding.inflate(inflater, container, false);

        textViewLink = binding.textLink;
        mTabLayout = binding.tabLayoutVaccineDates;
        mViewPager = binding.pagerVaccineDates;
        textName = binding.textName;
        layoutAges = binding.llAges;
        layoutDiseases = binding.llDiseases;
        vaccineID = VaccineDetailsFragmentArgs.fromBundle(getArguments()).getVaccineID();

        viewModel.getmVaccineDets(vaccineID).observe(getViewLifecycleOwner(), vaccine -> {
            mVaccine = vaccine;
            textName.setText(mVaccine.getVaccineName());
            try {
                JSONArray jsonArrayAges = new JSONArray(mVaccine.getVaccineDates());
                LocalDateTime startDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(0), TimeZone.getDefault().toZoneId());
                for(int i=0;i<jsonArrayAges.length();i++)
                {
                    long age = jsonArrayAges.getLong(i);
                    LocalDateTime endDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(age), TimeZone.getDefault().toZoneId());
                    long weeks = ChronoUnit.WEEKS.between(startDate, endDate);
                    MaterialTextView dynamicTextView = new MaterialTextView(getContext());
                    dynamicTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    dynamicTextView.setTextAppearance(R.style.TextAppearance_MaterialComponents_Subtitle1);
                    dynamicTextView.setText(weeks+"weeks");
                    layoutAges.addView(dynamicTextView);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            textViewLink.setText(mVaccine.getVaccineLink());
            try {
                JSONArray jsonArrayDiseases = new JSONArray(mVaccine.getVaccineDiseases());
                for(int i=0;i<jsonArrayDiseases.length();i++)
                {
                    String disease = jsonArrayDiseases.getString(i);
                    MaterialTextView dynamicTextView = new MaterialTextView(getContext());
                    dynamicTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    dynamicTextView.setTextAppearance(R.style.TextAppearance_MaterialComponents_Subtitle1);
                    dynamicTextView.setText(disease);
                    layoutDiseases.addView(dynamicTextView);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            viewModel.getChildren(readUserFile(file).getDbID()).observe(getViewLifecycleOwner(), children -> {
                childCount = children.size();
                mChildList.addAll(children);
                mViewPager.setAdapter(new VaccineDetailsFragment.VaccineDatesFragmentsAdapter(this));
                // attaching tab mediator
                new TabLayoutMediator(mTabLayout, mViewPager,
                        (tab, position) -> tab.setText(children.get(position).getChildName())).attach();

            });
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private class VaccineDatesFragmentsAdapter extends FragmentStateAdapter {

        public VaccineDatesFragmentsAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return new VaccineDetailsScheduleFragment().newInstance(mChildList.get(position).getChildDBID(), mVaccine.getVaccineDBID());
        }

        @Override
        public int getItemCount() {
            return childCount;
        }
    }
}