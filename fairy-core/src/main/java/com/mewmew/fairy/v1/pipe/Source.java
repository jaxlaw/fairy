package com.mewmew.fairy.v1.pipe;

import java.util.Iterator;
import java.io.InputStream;

public interface Source<T>
{
    Iterator<T> createIterator(InputStream in);
}
