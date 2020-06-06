package de.mherrmann.famkidmem.ccms.body;

public class RequestBodyDeleteUser {

    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object other){
        if( !(other instanceof RequestBodyDeleteUser)){
            return false;
        }
        if(this.username == null){
            return ((RequestBodyDeleteUser) other).username == null;
        }
        return this.username.equals(((RequestBodyDeleteUser) other).username);
    }
}
