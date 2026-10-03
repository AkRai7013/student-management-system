package com.example.ajay.Exception;
//Extend is a keyword give is used to give some powers to the class which is extending the class.
public class StudentNotFoundException extends RuntimeException  {

    public StudentNotFoundException(String message) {
        super(message);
    }
    //Super is also keuword which is used to call the parent class constructor.
    //why are we actually making this class because we want to create our own exception and we want to handle it in our own way.
}
