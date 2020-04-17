package com.firatyalcin.a1881;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

public class MainFragment extends Fragment {

    int bestScore, tokens = 0, i = 0;
    SharedPreferences score, token;
    View scoreboard;
    TextView bestscore, Tokens;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getContext() != null){
            score = getContext().getSharedPreferences("Score", Context.MODE_PRIVATE);
            bestScore = score.getInt("best", 0);

            token = getContext().getSharedPreferences("Tokens", Context.MODE_PRIVATE);
            tokens = token.getInt("tokens", 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        Tokens = v.findViewById(R.id.tokens);
        Tokens.setText(String.valueOf(tokens));

        //Score Board
        scoreboard = v.findViewById(R.id.scoreboard);
        bestscore = v.findViewById(R.id.bestscore);

        if (bestScore == 0){
            scoreboard.setVisibility(View.GONE);
        }else
            bestscore.setText(String.valueOf(bestScore));

        //Github
        v.findViewById(R.id.git).setOnClickListener(v1 -> {
            Intent git = new Intent();
            git.setAction(Intent.ACTION_VIEW);
            git.setData(Uri.parse("https://github.com/firatyalcin/1881"));
            startActivity(git);
        });

        //Share
        v.findViewById(R.id.share).setOnClickListener(v1 -> {
            Intent share = new Intent();
            share.setAction(Intent.ACTION_SEND);
            share.putExtra(Intent.EXTRA_TEXT, getString(R.string.share));
            share.setType("text/plain");
            startActivity(Intent.createChooser(share, getString(R.string.shr)));
        });

        //Star
        v.findViewById(R.id.star).setOnClickListener(v1 -> {
            try {
                Intent star = new Intent(Intent.ACTION_VIEW);
                star.setData(Uri.parse("market://details?id=com.firatyalcin.a1881"));
                startActivity(star);
            }catch (ActivityNotFoundException e){
                Toast.makeText(getContext(), R.string.notfoundgp, Toast.LENGTH_SHORT).show();
            }
        });

        //Hint
        v.findViewById(R.id.hint_main).setOnClickListener(v1 -> {
            /* When user rewarded
            tokens = token.getInt("tokens", 0) + 1;
            token.edit().putInt("tokens", tokens).apply();
            */
        });

        //Play
        v.findViewById(R.id.play).setOnClickListener(v1 -> {
            Dialog dialog = new Dialog(getContext());
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.difselect);
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            dialog.findViewById(R.id.easy).setOnClickListener(v2 -> { //Easy
                NavDirections direction = MainFragmentDirections.actionMainFragmentToGameFragment().setTime(200).setScore(0).setLevel(1).setDifficulty(0);
                NavHostFragment.findNavController(MainFragment.this).navigate(direction);
                dialog.cancel();
            });
            dialog.findViewById(R.id.med).setOnClickListener(v2 -> { //Medium
                NavDirections direction = MainFragmentDirections.actionMainFragmentToGameFragment().setTime(200).setScore(0).setLevel(1).setDifficulty(1);
                NavHostFragment.findNavController(MainFragment.this).navigate(direction);
                dialog.cancel();
            });
            dialog.findViewById(R.id.hard).setOnClickListener(v2 -> { //Hard
                NavDirections direction = MainFragmentDirections.actionMainFragmentToGameFragment().setTime(200).setScore(0).setLevel(1).setDifficulty(2);
                NavHostFragment.findNavController(MainFragment.this).navigate(direction);
                dialog.cancel();
            });

            dialog.show();
        });

        //1881
        TextView appname = v.findViewById(R.id.appname);
        appname.setOnClickListener(v1 -> AppName(appname));
        appname.setOnLongClickListener(v1 -> {
            appname.setText(getResources().getString(R.string.app_name));
            i = 0;
            return true;
        });
    }

    void AppName(TextView textView){ //A little easter egg
        String[] color = getResources().getStringArray(R.array.colors);

        String s = "<span style='color: #" + color[ng()] + "'>1</span><span style='color: #" + color[ng()] + "'>8</span><span style='color: #" + color[ng()] + "'>8</span><span style='color: #" + color[ng()] + "'>1</span>";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            textView.setText(Html.fromHtml(s, Html.FROM_HTML_MODE_COMPACT));
        else
            textView.setText(Html.fromHtml(s));
        i = i + 3;
    }

    int ng(){
        int temp = i > 3 ? i % 4 : i;
        i++;
        return temp;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (bestScore == 0)
            scoreboard.setVisibility(View.GONE);
        else
            bestscore.setText(String.valueOf(bestScore));
    }
}