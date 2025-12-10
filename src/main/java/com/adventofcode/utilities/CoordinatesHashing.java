package com.adventofcode.utilities;

public class CoordinatesHashing {
    // Strong 64-bit mixing function (MurmurHash3 finalizer)
    private static long mix64(long z) {
        z ^= (z >>> 33);
        z *= 0xff51afd7ed558ccdL;
        z ^= (z >>> 33);
        z *= 0xc4ceb9fe1a85ec53L;
        z ^= (z >>> 33);
        return z;
    }

    // Hash a single int vec2
    public static long hashVec2(int x, int y) {
        long h = 0x9E3779B97F4A7C15L; // golden ratio seed
        h ^= mix64(x);
        h = mix64(h);
        h ^= mix64(y);
        h = mix64(h);
        return h;
    }

    // Order-independent hash of two int vec2
    public static long hashUnordered(int ax, int ay, int bx, int by) {
        long ha = hashVec2(ax, ay);
        long hb = hashVec2(bx, by);

        long lo = Math.min(ha, hb);
        long hi = Math.max(ha, hb);

        long h = lo;
        h ^= hi + 0x9E3779B97F4A7C15L + (h << 6) + (h >>> 2); // boost::hash_combine style
        return mix64(h);
    }
}
