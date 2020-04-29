package me.trysam.extremewands.util;

public interface Provider<O,I> {

    O provide(I input);

}
