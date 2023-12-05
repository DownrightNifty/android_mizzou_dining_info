package com.example.myapplication.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        HomeViewModel homeViewModel =
//                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ListView favList = binding.favoritesList;
        String[] sampleFavs = {"1", "2", "3", "4"};
        ArrayAdapter<String> a1 = new ArrayAdapter<String>(this.requireActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                sampleFavs);
        favList.setAdapter(a1);

        ListView nearbyList = binding.nearbyList;
        String[] sampleNearby = {"5", "6", "7", "8"};
        ArrayAdapter<String> a2 = new ArrayAdapter<String>(this.requireActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                sampleNearby);
        nearbyList.setAdapter(a2);

//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}