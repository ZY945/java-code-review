package DesignPattern.all;

import java.util.ArrayList;
import java.util.List;

interface Observer {
    void update(String event);
}

class BlogSubscriber implements Observer {
    private String name;

    public BlogSubscriber(String name) {
        this.name = name;
    }

    public void update(String event) {
        System.out.println(name + " received: " + event);
    }
}

class Blog {
    private List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void publishPost(String post) {
        System.out.println("Publishing: " + post);
        for (Observer observer : observers) {
            observer.update(post);
        }
    }
}

public class ObserverPattern {
    public static void main(String[] args) {
        Blog blog = new Blog();
        Observer sub1 = new BlogSubscriber("Alice");
        Observer sub2 = new BlogSubscriber("Bob");
        blog.addObserver(sub1);
        blog.addObserver(sub2);
        blog.publishPost("New Java Tutorial");
    }
}