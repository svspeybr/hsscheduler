package org.opta.groupScheduler.domain;

import java.util.ArrayList;
import java.util.List;

public class IndexGenerator {

    private List<Integer> singleton = new ArrayList<>(1);
    public IndexGenerator(){
        this.singleton.add(0);
    }
    public int getNewIndex() {
        singleton.set(0,singleton.get(0)+1);
        return singleton.get(0)+1;
    }
}
