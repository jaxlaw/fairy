package com.mewmew.exercise;

import java.util.Set;
import java.util.HashSet;

public class Permutation
{
    int count;
    final Set<Character> set;

    public Permutation(Set<Character> set)
    {
        this.set = set;
    }

    public Permutation(String s)
    {
        set = new HashSet<Character>();
        char[] a = s.toCharArray();
        for (int i = 0; i < a.length; i++) {
            set.add(a[i]);
        }
    }

    public void permute()
    {
        permute(set, "");
    }

    public void permute(Set<Character> a, String output)
    {
        if (a.isEmpty()) {
            System.out.println(output);
            count++;
            return;
        }
        for (Character c : a) {
            Set<Character> r = new HashSet<Character>(a);
            r.remove(c);
            permute(r, c + output);
        }
    }

    public static void main(String[] args)
    {
        Permutation p = new Permutation("ABCD");
        p.permute();
        System.out.printf("%d = %d! is %s\n", p.count, 4, p.count == factorial(4));
    }

    public static int factorial(int n)
    {
        if (n==0)return 1;
        return n * factorial(n-1);
    }


}
