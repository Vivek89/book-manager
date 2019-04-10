package com.aqr.etf.book.service;

import com.aqr.etf.book.model.Valid;

public abstract class AbstractBookService<T, R> {

    /**
     * This class can have template methods like -
     * PreProcess, Validate, Calculate & PostProcess
     */
    public R applyStrategy(T t) {
        t = preProcess(t);
        Valid v = validate(t);
        if(v == Valid.VALID) {
            R r = compute(t);
            return postProcess(r);
        } else
            return processError(t);
    }

    abstract T preProcess(T t);
    abstract Valid validate(T t);
    abstract R compute(T t);
    abstract R postProcess(R r);
    abstract R processError(T r);
}
