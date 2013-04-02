package org.broadinstitute.sting.queue.util

/**
 * Utilities that try to deeply apply operations to collections, specifically Traversable and Option.
 */
object CollectionUtils {

  /**
   * Loops though a collection running the function f on each value.
   * @param value The value to run f on, or a collection of values for which f should be run on.
   * @param f The function to run on value, or to run on the values within the collection.
   * @return The updated value.
   */
  def updated(value: Any, f: Any => Any): Any = {
    value match {
      case traversable: Traversable[_] => traversable.map(updated(_, f))
      case option: Option[_] => option.map(updated(_, f))
      case x => f(x)
    }
  }

  /**
   * Utility for recursively processing collections.
   * @param value Initial the collection to be processed
   * @param f a function that will be called for each (item, collection) in the initial collection
   */
  def foreach(value: Any, f: (Any, Any) => Unit): Unit = {
    value match {
      case traversable: Traversable[_] =>
        for (item <- traversable) {
          f(item, traversable)
          foreach(item, f)
        }
      case option: Option[_] =>
        for (item <- option) {
          f(item, option)
          foreach(item, f)
        }
      case _ =>
    }
  }

  /**
   * Utility for recursively processing collections.
   * @param value Initial the collection to be processed
   * @param f a function that will be called for each (item, collection) in the initial collection
   */
  def foreach(value: Any, f: (Any) => Unit): Unit = {
    value match {
      case traversable: Traversable[_] => traversable.foreach(f(_))
      case option: Option[_] => option.foreach(f(_))
      case item => f(item)
    }
  }

  /**
   * Removes empty values from collections.
   * @param value The collection to test.
   * @return The value if it is not a collection, otherwise the collection with nulls and empties removed.
   */
  private def filterNotNullOrNotEmpty[T](value: T): T = {
    val newValue = value match {
      case traversable: Traversable[_] => traversable.map(filterNotNullOrNotEmpty(_)).filter(isNotNullOrNotEmpty(_)).asInstanceOf[T]
      case option: Option[_] => option.map(filterNotNullOrNotEmpty(_)).filter(isNotNullOrNotEmpty(_)).asInstanceOf[T]
      case x => x
    }
    newValue
  }


  /**
   * Returns true if the value is null or an empty collection.
   * @param value Value to test for null, or a collection to test if it is empty.
   * @return true if the value is null, or false if the collection is empty, otherwise true.
   */
  def isNullOrEmpty(value: Any): Boolean = !isNotNullOrNotEmpty(value)

  /**
   * Returns false if the value is null or an empty collection.
   * @param value Value to test for null, or a collection to test if it is empty.
   * @return false if the value is null, or false if the collection is empty, otherwise true.
   */
  def isNotNullOrNotEmpty(value: Any): Boolean = {
    val result = value match {
      case traversable: Traversable[_] => !filterNotNullOrNotEmpty(traversable).isEmpty
      case option: Option[_] => !filterNotNullOrNotEmpty(option).isEmpty
      case x => x != null
    }
    result
  }
}
