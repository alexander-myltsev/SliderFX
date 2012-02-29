package model

import controller.Contacts

trait Model {
  def slideNumber: Int

  def slideNumber_=(v: Int)

  def lectureNumber: Int

  def lectureNumber_=(v: Int)

  def contacts: Contacts

  def contacts_=(v: Contacts)
}

class ModelImplementation() extends Model {
  private val _slideNumbersPerLecture = Array(0, 0, 0, 0)

  def slideNumber = _slideNumbersPerLecture(_lectureNumber)

  def slideNumber_=(v: Int) = _slideNumbersPerLecture(_lectureNumber) = v


  private var _lectureNumber = 0

  def lectureNumber = _lectureNumber

  def lectureNumber_=(v: Int) = _lectureNumber = v


  private var _contacts: Contacts = null

  def contacts_=(v: Contacts) = _contacts = v

  def contacts = _contacts
}