package com.firatyalcin.a1881;

import java.util.Random;

public class RNG {

    private static final int s = 1881;
    int totalcell;
    int[] reqs, reqsi;

    int[] generate(int tc, int reqn){

        totalcell = tc * tc;
        reqs = required(reqn);
        reqsi = new int[reqn];

        //final array
        int[] numbers = new int[totalcell];

        //Merging the arrays
        System.arraycopy(reqs, 0, numbers, 0, reqn);
        System.arraycopy(others(totalcell - reqn), 0, numbers, reqn, totalcell - reqn);

        //Shuffling the new array
        for (int i = 0; i < totalcell; i++){
            int randIndex = new Random().nextInt(totalcell);
            int temp = numbers[randIndex];
            numbers[randIndex] = numbers[i];
            numbers[i] = temp;
        }

        //Finding the new index of each required number
        for (int i = 0; i < totalcell; i++){
            for (int j = 0; j < reqn; j++){
                if (numbers[i] == reqs[j])
                    reqsi[j] = i;
            }
        }

        return numbers;
    }

    private int[] required(int a){
        int[] numbers = new int[a];
        int total = 0;

        for(int i = 0; i < a; i++){

            if (i == 0){
                int n = new Random().nextInt(s);
                numbers[i] = (n != s-1) ? n+1 : n;
            }else if (i == a-1)
                numbers[i] = (s-total);
            else
                numbers[i] = new Random().nextInt(s-total);

            total += numbers[i];
        }

        return numbers;
    }

    private int[] others(int a){
        int[] numbers = new int[a];

        for(int i = 0; i < a; i++)
            numbers[i] = new Random().nextInt(s);

        return numbers;
    }
}
