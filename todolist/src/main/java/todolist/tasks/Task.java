package todolist.tasks;

import java.util.*;

import ae.*;
import com.google.appengine.api.datastore.*;

@model(kind = "tasks") class Task extends _Task
{
  static class R extends Record
  {
    @id long id;
    @required @indexed String label;
    @required @indexed Boolean status;
    String actor;
  }

  List<Entity> all(final FetchOptions options)
  {
    return selectAll(options).asList();
  }
}
