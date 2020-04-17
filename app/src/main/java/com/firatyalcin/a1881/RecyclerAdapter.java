package com.firatyalcin.a1881;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import static com.firatyalcin.a1881.GameFragment.click;
import static com.firatyalcin.a1881.GameFragment.rcells;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.Holder> {

    static public int total = 0, selected_count = 0;
    static Context context;
    static int[] numbers, reqsi; //All numbers and required numbers' indexes
    static int[] selectednumbers; //Numbers that selected by user
    static int[] selectedreqs; //Required numbers that selected by user
    static int[][] resources; //Text colors and cell backgrounds

    public static int getTotal() {
        return total;
    }

    public static void setTotal(int t) {
        total = t;
    }

    public static void setSelected_count(int sc) {
        selected_count = sc;
    }

    public static int getSelected_count() {
        return selected_count;
    }

    RecyclerAdapter(Context c, int[] n, int[] ri){
        context = c;
        numbers = n;
        reqsi = ri;

        selectednumbers = new int[ri.length];
        for (int i = 0; i < ri.length; i++){
            selectednumbers[i] = -1;
        }

        //If user select a number in required numbers we going to store that number to prevent selecting that number again by hint
        //If we don't store the number and just replace that number's index in 'reqsi' array with -1 then when user unselect that number we can't restore it
        selectedreqs = new int[ri.length];
        for (int i = 0; i < ri.length; i++){
            selectedreqs[i] = -1;
        }

        //Default unselected cell resources
        //We need this array just because of the hint button
        //Because when user use a hint, app will select a number in 'reqsi' array and update the adapter so user can see the hint but when adapter updated all the views will turning back their initial states
        resources = new int[numbers.length][2];
        for (int i = 0; i < numbers.length; i++){
            resources[i][0] = context.getResources().getColor(R.color.unselected);
            resources[i][1] = R.drawable.container;
        }
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView number;
        RelativeLayout container;
        public Holder(View v) {
            super(v);
            number = v.findViewById(R.id.number);
            container = v.findViewById(R.id.container);
        }
    }

    @Override
    public RecyclerAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.number.setText(String.valueOf(numbers[position]));

        //For setting hint selections and keeping the cells same when adapter updated
        holder.number.setTextColor(resources[position][0]);
        holder.container.setBackground(context.getDrawable(resources[position][1]));

        holder.itemView.setOnClickListener(v -> {
            if (holder.number.getCurrentTextColor() == context.getResources().getColor(R.color.unselected)){ //Selecting
                if (selected_count < rcells){
                    holder.number.setTextColor(Color.WHITE);
                    holder.container.setBackground(context.getDrawable(R.drawable.container_selected));

                    Select('s', position);

                    //Checking if user select one of the required numbers
                    for (int i = 0; i < reqsi.length; i++){
                        if (position == reqsi[i]){
                            selectedreqs[i] = reqsi[i];
                            reqsi[i] = -1;
                        }
                    }

                    click++;
                }else
                    Toast.makeText(context, context.getString(R.string.maxnumberselect), Toast.LENGTH_SHORT).show();
            }else{ //Unselecting
                holder.number.setTextColor(context.getResources().getColor(R.color.unselected));
                holder.container.setBackground(context.getDrawable(R.drawable.container));

                Select('u', position);

                //Checking if user unselect one of the required numbers
                for (int i = 0; i < reqsi.length; i++){
                    if (position == selectedreqs[i]){
                        reqsi[i] = selectedreqs[i];
                        selectedreqs[i] = -1;
                    }
                }
            }
        });
    }

    static void Hint(){
        if (selected_count == 0){
            Select('s', reqsi[0], true);
            selectedreqs[0] = reqsi[0];
            reqsi[0] = -1;
        }else{
            int b = 0;

            for (int selectednumber : selectednumbers) {
                for (int j = 0; j < reqsi.length; j++) {
                    if (selectednumber == -1 && reqsi[j] != -1) {
                        Select('s', reqsi[j], true);
                        b++;
                        selectedreqs[j] = reqsi[j];
                        reqsi[j] = -1;
                        break;
                    }
                }
                if (b > 0)
                    break;
            }
        }
    }

    static void Select(char type, int i){
        Select(type, i, false);
    }

    static void Select(char type, int i, boolean hint){
        //Storing the resources for setting when adapter updated
        resources[i][0] = (type == 's') ? Color.WHITE : context.getResources().getColor(R.color.unselected);
        resources[i][1] = (type == 's') ? (hint) ? R.drawable.container_selectedbyhint : R.drawable.container_selected : R.drawable.container;

        selectednumbers[(type == 's') ? selected_count : selected_count - 1] = (type == 's') ? i : -1;

        total = (type == 's') ? total + numbers[i] : total - numbers[i];
        selected_count = (type == 's') ? selected_count + 1 : selected_count - 1;

        GameFragment.ct(selected_count);
    }

    @Override
    public int getItemCount() {
        return numbers.length;
    }
}
