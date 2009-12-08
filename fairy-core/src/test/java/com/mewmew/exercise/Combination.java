package com.mewmew.exercise;

public class Combination
{
    int count;
    final char[] set;
    private final int choose;


    public Combination(String s, int choose)
    {
        this.choose = choose;
        this.set = s.toCharArray();
    }

    public void cominbate()
    {
        cominbate(set, 0, "");
    }

    public void cominbate(char a[], int offset, String output)
    {
        if (output.length() == choose) {
            System.out.println(output);
            count++;
            return;
        }
        for (int i = offset; i < a.length; i++) {
            char c = a[i];
            cominbate(a, i+1, c + output);
        }
    }

    public static void main(String[] args)
    {
        String input = "ABCDEFGHIJKLMNOPQRS";
        int len = input.length();
        int choose = 5 ;
        Combination p = new Combination(input, choose);
        p.cominbate();
        System.out.printf("%d = %dC%d is %s\n", p.count, len, choose, factorial(len) /(factorial(choose) *factorial(len-choose)));
        int max = Integer.MAX_VALUE;
    }

    public static long factorial(long n)
    {
        if (n==0)return 1;
        return n * factorial(n-1);
    }


}