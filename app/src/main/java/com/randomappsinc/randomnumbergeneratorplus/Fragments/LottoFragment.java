package com.randomappsinc.randomnumbergeneratorplus.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannedString;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.randomnumbergeneratorplus.Activities.MainActivity;
import com.randomappsinc.randomnumbergeneratorplus.Activities.SettingsActivity;
import com.randomappsinc.randomnumbergeneratorplus.Persistence.PreferencesManager;
import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.Utils.RandUtils;
import com.randomappsinc.randomnumbergeneratorplus.Utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class LottoFragment extends Fragment {

    public static final String TAG = LottoFragment.class.getSimpleName();

    @BindView(R.id.lotto_options) Spinner lottoSpinner;
    @BindView(R.id.results_container) View resultsContainer;
    @BindView(R.id.results) TextView results;

    private Unbinder mUnbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lotto_page, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        results.setGravity(Gravity.CENTER_HORIZONTAL);

        String[] lottoOptions = getResources().getStringArray(R.array.lotto_options);
        lottoSpinner.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.spinner_item, lottoOptions));
        return rootView;
    }

    @OnClick(R.id.generate)
    public void generateTickets() {
        if (PreferencesManager.get().shouldPlaySounds()) {
            ((MainActivity) getActivity()).playSound("lotto_scratch.wav");
        }
        SpannedString lottoResults = RandUtils.getLottoResults(lottoSpinner.getSelectedItemPosition());
        resultsContainer.setVisibility(View.VISIBLE);
        results.setText(lottoResults);
    }

    @OnClick(R.id.copy_results)
    public void copyNumbers() {
        String numbersText = results.getText().toString();
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Activity.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(getString(R.string.generated_numbers), numbersText);

        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            ((MainActivity) getActivity()).showSnackbar(getString(R.string.copied_to_clipboard));
        } else {
            ((MainActivity) getActivity()).showSnackbar(getString(R.string.clipboard_fail));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.regular_menu, menu);
        UIUtils.loadMenuIcon(menu, R.id.settings, IoniconsIcons.ion_android_settings);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_left_out, R.anim.slide_left_in);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
