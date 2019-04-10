package com.aqr.etf.book.generator;

import rx.subjects.ReplaySubject;

public class TestPub {

    public static void main(String[] args) {
        ReplaySubject<Integer> source = ReplaySubject.create();

// It will get 1, 2, 3, 4 and onComplete
        source.subscribe(i -> System.out.println("From-1:" + i));

        source.onNext(1);
        source.onNext(2);
        source.onNext(3);

// It will get 4 and onComplete for second observer also.
        source.subscribe(i -> System.out.println("From -2: " + i));

        source.onNext(4);
        source.onNext(5);
//        source.onComplete();
    }
}
