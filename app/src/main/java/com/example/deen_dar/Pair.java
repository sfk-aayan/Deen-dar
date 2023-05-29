package com.example.deen_dar;

public class Pair implements  Comparable<Pair>{
    public int interest_match_cnt;
    public User user;

    Pair(int interest_cnt, User user_) {
        this.user = user_;
        this.interest_match_cnt = interest_cnt;
    }

    @Override
    public int compareTo(Pair other) {
        return -(this.interest_match_cnt - other.interest_match_cnt);
    }
    public int getCnt(){
        return interest_match_cnt;
    }
}