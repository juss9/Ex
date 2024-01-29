package org.example;

import org.example.ex2.Api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {
    public static void main(String[] args) {
        //задание 2
        System.out.println("ex.2");
        Api api = new Api();
        api.start();

        //задание 4
//        System.out.println("ex.4");
//        DigitalSignature digitalSignature = new DigitalSignature();
//        digitalSignature.startDS();
    }
}
