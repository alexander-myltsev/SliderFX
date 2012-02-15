package model

trait Model {
  def slideNumber: Int

  def slideNumber_=(v: Int)


  def lectureNumber: Int

  def lectureNumber_=(v: Int)
}

class ModelImplementation() extends Model {
  private var _slideNumber = 1

  def slideNumber = _slideNumber

  def slideNumber_=(v: Int) = _slideNumber = v


  private var _lectureNumber = 1

  def lectureNumber = _lectureNumber

  def lectureNumber_=(v: Int) = _lectureNumber = v

}