package kade_c.taskforge.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import kade_c.taskforge.R;
import kade_c.taskforge.activities.TaskForgeActivity;

public class AboutFragment extends Fragment {

    private View view;

    final private String linkedinURL = "https://www.linkedin.com/in/christopher-kade-696501a8";
    final private String githubURL = "https://github.com/christopherkade";
    final private String facebookURL = "https://www.facebook.com/Christopher.Kade";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_about, container, false);

        ((TaskForgeActivity) getActivity()).setDrawerState(false);
        ((TaskForgeActivity)getActivity()).displayMenu(false);

        setListeners();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getResources().getString(R.string.aboutTab));
    }

    /**
     * Sets social media link listeners.
     */
    private void setListeners() {
        final ImageView linkedinImg = (ImageView)view.findViewById(R.id.linkdinImg);
        linkedinImg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(linkedinURL));
                startActivity(intent);
            }
        });

        ImageView githubImg = (ImageView)view.findViewById(R.id.githubImg);
        githubImg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(githubURL));
                startActivity(intent);
            }
        });

        ImageView facebookImg = (ImageView)view.findViewById(R.id.facebookImg);
        facebookImg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(facebookURL));
                startActivity(intent);
            }
        });
    }
}
