package todolist.users;

import ae.*;

@model public class User extends UserModel
{
  private static final class R extends Record
  {
    @id public String googleId;
    @required public String nombre;
    @required public String empresa;
    @required public String rol;
  }
}
