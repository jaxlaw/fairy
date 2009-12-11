/*
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
 
  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
*/
package com.mewmew.fairy.v1.esperudf;

public class Bucket
{
    public static long of(long n, long b1)
    {
        return _of(n, b1);
    }

    public static long of(long n, long b1, long b2)
    {
        return _of(n, b1, b2);
    }

    public static long of(long n, long b1, long b2, long b3)
    {
        return _of(n, b1, b2, b3);
    }

    public static long of(long n, long b1, long b2, long b3, long b4)
    {
        return _of(n, b1, b2, b3, b4);
    }

    public static long of(long n, long b1, long b2, long b3, long b4, long b5)
    {
        return _of(n, b1, b2, b3, b4, b5);
    }

    public static long of(long n, long b1, long b2, long b3, long b4, long b5, long b6)
    {
        return _of(n, b1, b2, b3, b4, b5, b6);
    }

    public static long of(long n, long b1, long b2, long b3, long b4, long b5, long b6, long b7)
    {
        return _of(n, b1, b2, b3, b4, b5, b6, b7);
    }

    public static long of(long n, long b1, long b2, long b3, long b4, long b5, long b6, long b7, long b8)
    {
        return _of(n, b1, b2, b3, b4, b5, b6, b7, b8);
    }

    public static long of(long n, long b1, long b2, long b3, long b4, long b5, long b6, long b7, long b8, long b9)
    {
        return _of(n, b1, b2, b3, b4, b5, b6, b7, b8, b9);
    }

    public static long of(long n, long b1, long b2, long b3, long b4, long b5, long b6, long b7, long b8, long b9, long b10)
    {
        return _of(n, b1, b2, b3, b4, b5, b6, b7, b8, b9, b10);
    }

    public static long of(long n, long b1, long b2, long b3, long b4, long b5, long b6, long b7, long b8, long b9, long b10, long b11)
    {
        return _of(n, b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11);
    }

    public static long of(long n, long b1, long b2, long b3, long b4, long b5, long b6, long b7, long b8, long b9, long b10, long b11, long b12)
    {
        return _of(n, b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12);
    }

    public static long of(long n, long b1, long b2, long b3, long b4, long b5, long b6, long b7, long b8, long b9, long b10, long b11, long b12, long b13)
    {
        return _of(n, b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13);
    }

    public static long _of(long n, long... ofs)
    {
        for (int i = 0; i < ofs.length; i++) {
            if (n <= ofs[i]) {
                return ofs[i];
            }
        }
        return ofs[ofs.length - 1] + 1;
    }
}
