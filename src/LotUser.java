import net.jini.core.entry.*;

public class LotUser implements Entry{

    public String userName;
    public String hashedPassword;

    public LotUser(){

    }

    public LotUser(String userName, String password){
        this.userName = userName;
        this.hashedPassword = password;
    }

    public String returnUserName(){
        return this.userName;
    }

}
