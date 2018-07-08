package com.randomappsinc.randomnumbergeneratorplus.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.activities.MainActivity;
import com.randomappsinc.randomnumbergeneratorplus.activities.SettingsActivity;
import com.randomappsinc.randomnumbergeneratorplus.persistence.PreferencesManager;
import com.randomappsinc.randomnumbergeneratorplus.utils.RandUtils;
import com.randomappsinc.randomnumbergeneratorplus.utils.TextUtils;
import com.randomappsinc.randomnumbergeneratorplus.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DiceFragment extends Fragment {

    public static final String TAG = DiceFragment.class.getSimpleName();

    @BindView(R.id.focal_point) View focalPoint;
    @BindView(R.id.num_dice) EditText numDiceInput;
    @BindView(R.id.num_sides) EditText numSidesInput;
    @BindView(R.id.results_container) View resultsContainer;
    @BindView(R.id.results) TextView results;

    private final TextUtils.SnackbarDisplay snackbarDisplay = new TextUtils.SnackbarDisplay() {
        @Override
        public void showSnackbar(String message) {
            ((MainActivity) getActivity()).showSnackbar(message);
        }
    };

    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dice_page, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        numDiceInput.setText(PreferencesManager.get().getNumDice());
        numSidesInput.setText(PreferencesManager.get().getNumSides());
        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        saveSettings();
    }

    @OnClick(R.id.roll)
    public void roll() {
        if (verifyForm()) {
            if (PreferencesManager.get().shouldPlaySounds()) {
                ((MainActivity) getActivity()).playSound("dice_roll.wav");
            }
            int numDice = Integer.parseInt(numDiceInput.getText().toString());
            int numSides = Integer.parseInt(numSidesInput.getText().toString());

            List<Integer> rolls = RandUtils.getNumbers(
                    1,
                    numSides,
                    numDice,
                    false,
                    new ArrayList<Integer>());
            resultsContainer.setVisibility(View.VISIBLE);
            String rollsText = RandUtils.getDiceResults(rolls);
            UIUtils.animateResults(results, Html.fromHtml(rollsText));
        }
    }

    public boolean verifyForm() {
        UIUtils.hideKeyboard(getActivity());
        focalPoint.requestFocus();

        String numSides = numSidesInput.getText().toString();
        String numDice = numDiceInput.getText().toString();
        try {
            if (Integer.parseInt(numSides) <= 0) {
                snackbarDisplay.showSnackbar(getString(R.string.zero_sides));
                return false;
            } else if (Integer.parseInt(numDice) <= 0) {
                snackbarDisplay.showSnackbar(getString(R.string.zero_dice));
                return false;
            }
        } catch (NumberFormatException exception) {
            snackbarDisplay.showSnackbar(getString(R.string.not_a_number));
            return false;
        }
        return true;
    }

    @OnClick(R.id.copy_results)
    public void copyNumbers() {
        String numbersText = results.getText().toString();
        TextUtils.copyTextToClipboard(numbersText, snackbarDisplay);
    }

    private void saveSettings() {
        PreferencesManager.get().saveDiceSettings(
                numSidesInput.getText().toString(),
                numDiceInput.getText().toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        saveSettings();
        unbinder.unbind();
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
