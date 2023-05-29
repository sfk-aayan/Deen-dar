package com.example.deen_dar;

import java.lang.*;
import java.util.*;

public class User {
    public String name;
    public String username;
    public String location;
    public Gender gender;
    public String age;
    public String imageUri;
    public String occupation;
    public String height;
    public String phone;
    public ArrayList<String> interests;
 // Get the current user object
        // Replace with your logic to get the current user

        // Call the findMatches method of the User class to obtain the list of matched users
    public User(String name, String username, String location, Gender gender, String age, String occupation, String height, String phone, String imageUri, ArrayList<String> interests) {
        this.name = name;
        this.username = username;
        this.location = location;
        this.gender = gender;
        this.age = age;
        this.interests = interests;
        this.imageUri = imageUri;
        this.occupation = occupation;
        this.height = height;
        this.phone = phone;
    }


    public void showInfo(){
        System.out.println(name);
    }

    // Enum for Gender
    public enum Gender {
        Male,
        Female
    }

    public static ArrayList<User> findMatches(User user, ArrayList<User> all_users) {
        ArrayList<Pair> candidate = new ArrayList<>();
        ArrayList<User> ret = new ArrayList<>();

        //creating candidates for matching
        for (User other_user : all_users) {
            if (other_user == user) continue;
            if (user.gender == other_user.gender) continue;
            int interest_match_cnt = 0;
            for(String his : other_user.interests) {
                for(String mine : user.interests)  {
                    if(his.equals(mine)) {
                        interest_match_cnt++;
                    }
                }
            }
            Pair cur = new Pair(interest_match_cnt, other_user);
            candidate.add(cur);
        }


        //sorting them
        Collections.sort(candidate);

        //taking the best 10, if available, change the want variable as your need
        int want = 10;
        for(int i=0; i<java.lang.Math.min(want, candidate.size()); i++) {
            ret.add(candidate.get(i).user);
        }
        return ret;
    }
}