package LotSpace;

import net.jini.core.entry.*;

public class LotUser implements Entry{

    public String userName;
    public String hashedPassword;

    public LotUser(){

    }

    // No arg constructor - class to read and write a user object used for logging in
    public LotUser(String userName, String password){
        this.userName = userName;
        this.hashedPassword = password;
    }

    public String returnUserName(){
        return this.userName;
    }

}
