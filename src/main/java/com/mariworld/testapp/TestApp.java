package com.mariworld.testapp;

import com.mariworld.raw.FileHandler;
import com.mariworld.raw.Person;

public class TestApp {
    public static void main(String[] args){
        try {
            FileHandler fh = new FileHandler("test01.db");
            fh.add("john", 33, "USA", "22-2333", "This is Description");

            fh = new FileHandler("test01.db");
            Person person = fh.readRow(0);
            fh.close(); //읽은후에 파일 꼭 닫아주기!!

            System.out.println(person);

        } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
