package model.authorization

object Status extends Enumeration {
  type Status = Value
  var Success, Failed = Value
}

class Key(val v: String)

object Authorizer {
  def authorize(key: Key) =
    if (key.v == "123") Status.Success
    else Status.Failed
}