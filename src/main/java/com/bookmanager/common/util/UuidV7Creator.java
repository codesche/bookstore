package com.bookmanager.common.util;

import com.github.f4b6a3.uuid.UuidCreator;

public class UuidV7Creator {

    public static String create() {
        return UuidCreator.getTimeOrderedEpoch().toString();
    }

}
