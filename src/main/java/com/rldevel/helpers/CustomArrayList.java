package com.rldevel.helpers;

import java.util.ArrayList;

/**
 * Created by root on 29/12/17.
 */

public class CustomArrayList<T> extends ArrayList {

    public int getLastIndex(){

        return this.size()-1 ;

    }

    public T getLastItem(){

        if (getLastIndex() > -1)
            return (T)this.get(getLastIndex());

        return null;

    }

    public T getFirstItem(){

        return (T) this.get(0);

    }

}

