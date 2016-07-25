package com.example.ericweiner.myapplication;

import java.util.ArrayList;

/**
 * Created by ericweiner on 7/24/16.
 */
public class Contact implements Comparable{
    private String name;
    private ArrayList<String> numbers;


    public Contact(String name, ArrayList<String> numbers) {
        this.name = name;
        this.numbers = numbers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getstrNumbers() {
        StringBuilder sb = new StringBuilder();
        sb.append("Phone Numbers:\n");
        for (int i = 0; i < numbers.size(); i++) {
            String num = numbers.get(i);
            sb.append(num + "\n");
        }
        return sb.toString();
    }

    public ArrayList<String> getNumbers() {
        return numbers;
    }

    public void setNumber(ArrayList<String> numbers) {
        this.numbers = numbers;
    }

    @Override
    public String toString(){
        return name;
    }

    @Override
    public int compareTo(Object o) {
        return this.name.compareTo(o.toString());
    }
}
