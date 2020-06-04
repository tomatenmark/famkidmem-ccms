package de.mherrmann.famkidmem.ccms.body;

import de.mherrmann.famkidmem.ccms.item.User;

import java.util.List;

public class ResponseBodyGetUsers extends ResponseBody {

    private List<User> users;

    public ResponseBodyGetUsers(){}

    public ResponseBodyGetUsers(List<User> users){
        super("ok", "Successfully get users");
        this.setUsers(users);
    }

    public ResponseBodyGetUsers(Exception ex){
        super("error", ex.getMessage(), ex);
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
