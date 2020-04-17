package com.firatyalcin.a1881;

import android.os.Bundle;
import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.text.MessageFormat;

public class GameOverFragment extends Fragment {

    int score, level, timne, click, hint, difficulty;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null){
            score = GameOverFragmentArgs.fromBundle(getArguments()).getScore();
            level = GameFragmentArgs.fromBundle(getArguments()).getLevel();
            timne = GameOverFragmentArgs.fromBundle(getArguments()).getTime();
            click = GameOverFragmentArgs.fromBundle(getArguments()).getClick();
            hint = GameOverFragmentArgs.fromBundle(getArguments()).getHint();

            difficulty = GameOverFragmentArgs.fromBundle(getArguments()).getDifficulty();
        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavHostFragment.findNavController(GameOverFragment.this).navigate(R.id.action_GameOverFragment_to_MainFragment);
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gameover, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView Score = view.findViewById(R.id.goscore);
        Score.setText(String.valueOf(score));

        TextView Level = view.findViewById(R.id.golevel);
        Level.setText(MessageFormat.format("{0} {1}", String.valueOf(level), getResources().getStringArray(R.array.gostats)[0]));

        TextView Time = view.findViewById(R.id.gotime);
        Time.setText(MessageFormat.format("{0} {1}", String.valueOf(timne), getResources().getStringArray(R.array.gostats)[1]));

        TextView Click = view.findViewById(R.id.goclick);
        Click.setText(MessageFormat.format("{0} {1}", String.valueOf(click), getResources().getStringArray(R.array.gostats)[2]));

        TextView Hint = view.findViewById(R.id.gohint);
        Hint.setText(MessageFormat.format("{0} {1}", String.valueOf(hint), getResources().getStringArray(R.array.gostats)[3]));


        view.findViewById(R.id.restart).setOnClickListener(view1 -> {
            NavDirections direction = GameOverFragmentDirections.NewGame().setTime(200).setScore(0).setLevel(1).setDifficulty(difficulty);
            NavHostFragment.findNavController(GameOverFragment.this).navigate(direction);
        });

        view.findViewById(R.id.home).setOnClickListener(view1 -> NavHostFragment.findNavController(GameOverFragment.this).navigate(R.id.action_GameOverFragment_to_MainFragment));
    }
}