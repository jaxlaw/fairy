package com.mewmew.fairy.v1.book;

import com.mewmew.fairy.v1.spell.PipeSpell;
import com.mewmew.fairy.v1.spell.Help;
import com.mewmew.fairy.v1.cli.Param;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Arrays;

import org.apache.commons.codec.binary.Hex;

@Help(desc="compute md5/sha1")
public class MD5 extends PipeSpell
{
    @Param boolean sha1 = false ;

    @Override
    public void process(InputStream in, OutputStream out)
    {
        try {
            in = in instanceof BufferedInputStream ? in : new BufferedInputStream(in);
            PrintWriter pw = new PrintWriter(out, true);
            DigestInputStream din = new DigestInputStream(in, MessageDigest.getInstance(sha1 ? "SHA1" : "MD5"));
            byte[] b = new byte[4096] ;
            int read ;
            while((read=din.read(b))>0);
            pw.println(new String(Hex.encodeHex(din.getMessageDigest().digest())));
            pw.close();

        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String md5(Collection c)
    {
        return hash(c, "MD5");
    }

    public static String sha1(Collection c)
    {
        return hash(c, "SHA1");
    }

    private static String hash(Collection c, String algorithm)
    {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance(algorithm);
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
        for (Object o : c) {
            digest.update(o.toString().getBytes());
        }
        return new String(Hex.encodeHex(digest.digest()));
    }

    public static void main(String[] args)
    {
        System.out.println(sha1(Arrays.asList("a","b")));
        System.out.println(md5(Arrays.asList("a","b")));
    }
}
