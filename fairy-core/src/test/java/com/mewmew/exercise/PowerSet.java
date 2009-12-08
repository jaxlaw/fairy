package com.mewmew.exercise;

public class PowerSet
{
    private char[] set ;
    private int count = 0;

    public PowerSet(String s)
    {
        set = s.toCharArray();
    }

    public void generate()
    {
        generateR(set, 0, "");
    }

    public void generateR(char a[], int offset, String output)
    {
        for (int i = offset; i < a.length; i++) {
            generateR(a, i+1, output+ a[i]);
        }
        count++;
        System.out.println(output);
    }

    public static void main(String[] args)
    {
        String input = "ABC";
        PowerSet powerSet = new PowerSet(input);
        powerSet.generate();
        System.out.printf("power set count %d = 2^%d -1 is %s\n", powerSet.count, input.length(), powerSet.count == Math.pow(2,input.length()));

    }
}
