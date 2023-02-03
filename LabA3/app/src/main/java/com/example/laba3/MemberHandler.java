package com.example.laba3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MemberHandler {
    List<Map<String, String>> medlemmar;
    List<Member> members;

    public void init(){
        members = new ArrayList<>();
        for (Map<String, String> medlem:medlemmar) {
            String email = medlem.get("epost");
            String name = medlem.get("namn");
            String response = medlem.get("svarade");
            members.add(new Member(email, name, response));
        }
    }

    public Member getMember(int number){
        return members.get(number);
    }

    public List<Member> getMembers(){
        return members;
    }


}
