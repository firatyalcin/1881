package com.firatyalcin.a1881;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.MessageFormat;
import static com.firatyalcin.a1881.RecyclerAdapter.getSelected_count;

public class GameFragment extends Fragment {

    static TextView confirm_text;
    int time = 200, score = 0, level = 1, difficulty = 0;
    int cells, tokens, multiplier;
    static int rcells;
    CountDownTimer timer;
    TextView time_text;
    SharedPreferences scores, token;
    AlertDialog dialog;
    int temptime, passtime, hint;
    static int click = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getContext() != null){ //Preferences
            scores = getContext().getSharedPreferences("Score", Context.MODE_PRIVATE);
            token = getContext().getSharedPreferences("Tokens", Context.MODE_PRIVATE);
            tokens = token.getInt("tokens", 0);
        }


        if (getArguments() != null){ //Arguments
            //For Next Level
            time = Math.min(GameFragmentArgs.fromBundle(getArguments()).getTime(), 999999);
            temptime = time;
            score = GameFragmentArgs.fromBundle(getArguments()).getScore();
            level = GameFragmentArgs.fromBundle(getArguments()).getLevel();
            difficulty = GameFragmentArgs.fromBundle(getArguments()).getDifficulty();

            //For Game Over
            passtime = GameFragmentArgs.fromBundle(getArguments()).getPasstime();
            hint = GameFragmentArgs.fromBundle(getArguments()).getHint();
            click = GameFragmentArgs.fromBundle(getArguments()).getClick();
        }


        //Rules according to difficulty
        //cells^2 = total cells on the screen
        cells = (difficulty == 0) ? 3 : (difficulty == 1) ? 4 : (difficulty == 2) ? 5 : 3;

        //Required cells amount
        if (difficulty == 0)
            rcells = 2;
        else if (difficulty == 1)
            rcells = (level > 0 && level <= 15) ? 2 : 3;
        else if (difficulty == 2)
            rcells = (level > 0 && level <= 5) ? 2 : ((level > 5 && level <= 15) ? 3 : ((level > 15 && level <= 35) ? 4 : 5));

        //Score multiplier
        multiplier = (difficulty == 0) ? 4 : ((difficulty == 1) ? 3 : 2);


        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                dialog = new AlertDialog.Builder(getContext()).create();
                dialog.setTitle(getString(R.string.quitdialogtitle));
                dialog.setMessage(getString(R.string.quitdialogmessage));
                dialog.setButton(Dialog.BUTTON_POSITIVE, getString(R.string.dialogyes), (dialog1, which) -> gameover());
                dialog.setButton(Dialog.BUTTON_NEGATIVE, getString(R.string.dialogno), (dialog1, which) -> {});
                dialog.show();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView score_text = view.findViewById(R.id.score);
        score_text.setText(String.valueOf(score));

        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        RecyclerView.LayoutManager manager = new GridLayoutManager(getContext(), cells);
        recyclerView.setLayoutManager(manager);
        RNG rng = new RNG();
        RecyclerAdapter adapter = new RecyclerAdapter(getContext(), rng.generate(cells, rcells), rng.reqsi);
        recyclerView.setAdapter(adapter);


        time_text = view.findViewById(R.id.time);

        timer = new CountDownTimer(1000 * time, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                if (time > 0){
                    time_text.setText(String.valueOf(time));
                    time--;
                }else
                    timer.cancel();
            }

            @Override
            public void onFinish() {
                time_text.setText(String.valueOf(0));
                if (dialog != null)
                    dialog.cancel();
                gameover();
            }
        };
        timer.start();


        Button confirm_button = view.findViewById(R.id.confirm_button);
        confirm_text = view.findViewById(R.id.confirm_text);
        confirm_text.setText(MessageFormat.format("0/{0}", rcells));

        confirm_button.setOnClickListener(v -> {
            if (getSelected_count() == rcells) {
                if (RecyclerAdapter.getTotal() == 1881) {
                    Clear();
                    NavDirections direction = GameFragmentDirections.NewLevel()
                            .setTime(Math.min(((time / 2) + time), 999999)).setScore(score + ((time * rcells) / multiplier)).setLevel(level+1).setDifficulty(difficulty) //For Next Level
                            .setPasstime(passtime + (temptime-time)).setClick(click).setHint(hint); //For Game Over
                    NavHostFragment.findNavController(GameFragment.this).navigate(direction);
                } else if (RecyclerAdapter.getTotal() > 1881)
                    Toast.makeText(getContext(), R.string.toomuch, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getContext(), R.string.notenough, Toast.LENGTH_SHORT).show();
            }else
                Toast.makeText(getContext(), MessageFormat.format(getString(R.string.selectnumbers), rcells), Toast.LENGTH_SHORT).show();
        });

        ImageView hintview = view.findViewById(R.id.hint);
        hintview.setOnClickListener(v -> {
            if (getSelected_count() < rcells){
                if (tokens > 0){
                    RecyclerAdapter.Hint();
                    adapter.notifyDataSetChanged();

                    tokens--;
                    token.edit().putInt("tokens", tokens).apply();

                    hint++;
                }else
                    Toast.makeText(getContext(), R.string.notoken, Toast.LENGTH_SHORT).show();
            }else
                Toast.makeText(getContext(), R.string.selectcells, Toast.LENGTH_SHORT).show();
        });
    }

    public static void ct(int step){
        confirm_text.setText(MessageFormat.format("{0}/{1}", step, rcells));
    }

    void Clear(){
        timer.cancel();
        RecyclerAdapter.setTotal(0);
        RecyclerAdapter.setSelected_count(0);
    }

    void gameover(){
        if (score > scores.getInt("best", 0))
            scores.edit().putInt("best", score).apply(); //New best score
        Clear();
        NavDirections direction = GameFragmentDirections.GameOver().setScore(score).setLevel(level-1).setTime(passtime + (temptime-time)).setClick(click).setHint(hint).setDifficulty(difficulty);
        NavHostFragment.findNavController(GameFragment.this).navigate(direction);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Integer.parseInt(time_text.getText().toString()) == 0){
            if (dialog != null)
                dialog.cancel();
            gameover();
        }
    }
}
