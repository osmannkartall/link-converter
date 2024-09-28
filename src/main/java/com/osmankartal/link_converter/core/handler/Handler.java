package com.osmankartal.link_converter.core.handler;


public interface Handler<I, O> {
    O execute(I command);
}
