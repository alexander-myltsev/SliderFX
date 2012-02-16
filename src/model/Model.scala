package model

trait Model {
  def slideNumber: Int

  def slideNumber_=(v: Int)

  def lectureNumber: Int

  def lectureNumber_=(v: Int)
}

class ModelImplementation() extends Model {
  private val _slideNumbersPerLecture = Array(1, 1, 1, 1)

  def slideNumber = _slideNumbersPerLecture(_lectureNumber)

  def slideNumber_=(v: Int) = _slideNumbersPerLecture(_lectureNumber) = v


  private var _lectureNumber = 1

  def lectureNumber = _lectureNumber

  def lectureNumber_=(v: Int) = _lectureNumber = v

}