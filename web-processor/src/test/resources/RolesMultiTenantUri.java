package test;

import ae.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@endpoint(admin=true) class Tasks extends TestEndPoint
{
  @GET void index(final HttpServletRequest request, final HttpServletResponse response)
  {
  }

  @multitenant @roles("sysadmin") @GET("{id}") void get(final HttpServletRequest request, final HttpServletResponse response)
  {
  }

  @GET @roles({"sys", "cfg", "other"}) void author(final HttpServletRequest request, final HttpServletResponse response)
  {
  }

  @multitenant @POST void save(final HttpServletRequest request, final HttpServletResponse response)
  {
  }

  @PUT("{id}") void update(final HttpServletRequest request, final HttpServletResponse response)
  {
  }

  @DELETE("{id}") void delete(final HttpServletRequest request, final HttpServletResponse response)
  {
  }
}