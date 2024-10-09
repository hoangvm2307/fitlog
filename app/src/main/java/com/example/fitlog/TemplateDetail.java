package com.example.fitlog;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fitlog.model.Template;

public class TemplateDetail extends Fragment {
    private static final String ARG_TEMPLATE = "template";
    private Template template;

    public TemplateDetail() {
        // Required empty public constructor
    }

    public static TemplateDetail newInstance(Template template) {
        TemplateDetail fragment = new TemplateDetail();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TEMPLATE, template);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            template = getArguments().getParcelable(ARG_TEMPLATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_template_detail, container, false);
    }
}