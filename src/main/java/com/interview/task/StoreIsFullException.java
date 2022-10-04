package com.interview.task;

/**
 * Created by AMT Group <a href="http://www.amt.ru/">http://www.amt.ru</a>
 *
 * @author Mikhail Kovarnev
 * Created at 04.10.2022 18:01
 */
public class StoreIsFullException extends Exception {
    public StoreIsFullException(String message) {
        super(message);
    }
}
