package com.fut.desktop.app.extensions;

public final class LongExtensions {
    public static long CalculateBaseId(long resourceId)
    {
        long baseId = resourceId + 3288334336L;
        int version = 0;

        while (baseId > 16777216)
        {
            version++;
            switch (version)
            {
                case 1:
                    baseId -= 1342177280;
                    break;
                case 2:
                    baseId -= 50331648;
                    break;
                default:
                    baseId -= 16777216;
                    break;
            }
        }

        return baseId;
    }
}
