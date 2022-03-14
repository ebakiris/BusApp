package com.example.busapp.ui.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.busapp.R;

import java.util.ArrayList;


public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    public static final String EXTRA_MESSAGE = "com.example.busapp.ui.gallery.MESSAGE";
    ListView listView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        listView = (ListView) root.findViewById(R.id.listview);
        ArrayList<String> arrayList = new ArrayList<>();

        arrayList.add("Bus No1");
        arrayList.add("Bus No2");
        arrayList.add("Bus No3");
        arrayList.add("Bus No4");
        arrayList.add("Bus No5");
        arrayList.add("Bus No6");
        arrayList.add("Bus No7");
        arrayList.add("Bus No8");
        arrayList.add("Bus No9");
        arrayList.add("Bus No11");
        arrayList.add("Bus No15");
        arrayList.add("Bus No49");

        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, arrayList);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getActivity(), BusRouteActivity.class);
            intent.putExtra(EXTRA_MESSAGE, String.valueOf(position));
            startActivity(intent);
        });

        return root;
    }

}

