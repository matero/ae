package ae.web;

public final class Name
{
  final String value;

  Name(final String value)
  {
    this.value = value;
  }

  @Override public boolean equals(Object o)
  {
    if (this == o) {
      return true;
    }
    if (o instanceof Name) {
      final Name that = (Name) o;
      return value.equals(that.value);
    }
    return false;
  }

  @Override public int hashCode()
  {
    return value.hashCode();
  }

  @Override public String toString()
  {
    return "Name{'" + value + "'}";
  }
}
