package com.mewmew.exercise;

public class Phone
{
    private static final char mappedChars[][] = {
            {'0'},
            {'1'},
            {'A', 'B', 'C'},
            {'D', 'E', 'F'},
            {'G', 'H', 'I'},
            {'J', 'K', 'L'},
            {'M', 'N', 'O'},
            {'P', 'Q', 'R', 'S'},
            {'T', 'U', 'V'},
            {'X', 'Y', 'Z'}
    };

    public static void generate(int[] digits)
    {
        for (int i=0; i< digits.length; i++) {
            if (digits[i] > 9 || digits[i] < 0) {
                throw new IllegalArgumentException("invalid digit " + digits[i]);
            }
        }
        generate(digits, 0, "");
    }

    public static void generate(int[] digits, int offset, String output)
    {
        if (digits.length == offset) {
            System.out.println(output);
        }
        else {
            int d = digits[offset];
            for (int i = 0; i < mappedChars[d].length; i++) {
                generate(digits, offset + 1, output + mappedChars[d][i]);
            }
        }
    }

    public static void main(String[] args)
    {
        Phone.generate(new int[]{1, 8, 0, 0, 5, 6, 7, 8, 9, 0});
    }
}
