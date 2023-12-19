
package com.acme.labs;

import java.util.Collection;
import java.util.Random;

import org.apache.commons.math3.random.RandomGenerator;

public class Utils {
    // Implementing Fisher-Yates shuffle (modern version)
    // reference: http://stackoverflow.com/questions/1519736/random-shuffling-of-an-array
    // reference: http://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle#The_modern_algorithm
    // reference: http://bost.ocks.org/mike/shuffle/
    public static <T> T[]
    shuffleArray(RandomGenerator prng, T[] ar) {
        for (int index, i = ar.length - 1; i > 0; i--) {
            index = prng.nextInt(i + 1);
            T a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }

        return ar;
    }

    public static <T> T[]
    shuffleArray(Random prng, T[] ar) {
        for (int index, i = ar.length - 1; i > 0; i--) {
            index = prng.nextInt(i + 1);
            T a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }

        return ar;
    }

    public static <T> T[]
    shuffleArray(T[] ar) {
        return shuffleArray(new Random(), ar);
    }

    public static int[]
    shuffleIntegerArray(RandomGenerator prng, int[] ar) {
        for (int index, i = ar.length - 1; i > 0; i--) {
            index = prng.nextInt(i + 1);
            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }

        return ar;
    }

    public static int[]
    shuffleIntegerArray(Random prng, int[] ar) {
        for (int index, i = ar.length - 1; i > 0; i--) {
            index = prng.nextInt(i + 1);
            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }

        return ar;
    }

    public static int[]
    shuffleIntegerArray(int[] ar) {
        return shuffleIntegerArray(new Random(), ar);
    }

    // reference: http://stackoverflow.com/questions/960431/how-to-convert-listinteger-to-int-in-java
    public static <T extends Number> int[]
    toIntegerArray(Collection<T> x) {
        int[] ret = new int[x.size()];
        int i = 0;

        for (T e : x) {
            ret[i++] = e.intValue();
        }

        return ret;
    }

    public static <T extends Number> int[]
    toIntegerArray(T[] ar) {
        int[] ret = new int[ar.length];
        int i = 0;

        for (T e : ar) {
            ret[i++] = e.intValue();
        }

        return ret;
    }

    public static <T extends Number> long[]
    toLongArray(Collection<T> x) {
        long[] ret = new long[x.size()];
        int i = 0;

        for (T e : x) {
            ret[i++] = e.longValue();
        }

        return ret;
    }

    public static <T extends Number> long[]
    toLongArray(T[] ar) {
        long[] ret = new long[ar.length];
        int i = 0;

        for (T e : ar) {
            ret[i++] = e.longValue();
        }

        return ret;
    }

    public static <T extends Number> double[]
    toDoubleArray(Collection<T> x) {
        double[] ret = new double[x.size()];
        int i = 0;

        for (T e : x) {
            ret[i++] = e.doubleValue();
        }

        return ret;
    }

    public static <T extends Number> double[]
    toDoubleArray(T[] ar) {
        double[] ret = new double[ar.length];
        int i = 0;

        for (T e : ar) {
            ret[i++] = e.doubleValue();
        }

        return ret;
    }

    public static <T extends Number> int[]
    toShuffledIntArray(RandomGenerator prng, Collection<T> x) {
        return shuffleIntegerArray(prng, toIntegerArray(x));
    }

    public static <T extends Number> int[]
    toShuffledIntArray(RandomGenerator prng, T[] ar) {
        return shuffleIntegerArray(prng, toIntegerArray(ar));
    }

    public static Integer[]
    boxed(int[] ar) {
        Integer[] ret = new Integer[ar.length];
        int i = 0;

        for (int e : ar) {
            ret[i++] = e;
        }

        return ret;
    }

    public static Long[]
    boxed(long[] ar) {
        Long[] ret = new Long[ar.length];
        int i = 0;

        for (long e : ar) {
            ret[i++] = e;
        }

        return ret;
    }

    public static Double[]
    boxed(double[] ar) {
        Double[] ret = new Double[ar.length];
        int i = 0;

        for (double e : ar) {
            ret[i++] = e;
        }

        return ret;
    }
}
