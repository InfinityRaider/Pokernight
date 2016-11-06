package com.infinityraider.pokernight.container.gui;


import com.google.common.base.Predicate;

import javax.annotation.Nullable;

public class PredicateNumeric implements Predicate<String> {
    private static final PredicateNumeric INSTANCE = new PredicateNumeric();

    public static PredicateNumeric getInstance() {
        return INSTANCE;
    }

    private PredicateNumeric() {}

    @Override
    public boolean apply(@Nullable String input) {
        if(input == null) {
            return true;
        }
        for(char character : input.toCharArray()) {
            if(!Character.isDigit(character)) {
                return false;
            }
        }
        return true;
    }
}
