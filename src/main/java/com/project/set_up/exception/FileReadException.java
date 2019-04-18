package com.project.set_up.exception;

@SuppressWarnings("serial")
public class FileReadException extends Exception 
{ 
    public FileReadException  (String s) 
    { 
        // Call constructor of parent Exception 
        super(s); 
    } 
} 