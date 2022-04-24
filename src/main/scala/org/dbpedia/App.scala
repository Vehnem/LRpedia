package org.dbpedia

import org.apache.hadoop.shaded.com.nimbusds.jose.util.StandardCharset
import org.apache.spark.SparkConf
import org.apache.spark.sql.{Dataset, SparkSession}

import java.net.URLEncoder

/**
 * Hello world!
 *
 */
object App {
  def main(args: Array[String]): Unit = {


    val sparkConfig = new SparkConf()
    sparkConfig.setMaster("local[*]")
    sparkConfig.setAppName("lr-dbpedia")
    sparkConfig.setSparkHome("spark-home/")

    val spark = SparkSession.builder().config(sparkConfig).getOrCreate()
    spark.sparkContext.setLogLevel("INFO")

    val sql = spark.sqlContext
    import sql.implicits._
    val ntDs: Dataset[NT] = sql.read.textFile(args(0))
      .map(ntString => NT(ntString))

    def ontolexProperty(name: String) = s"<http://www.w3.org/ns/lemon/ontolex#$name>"

    val cfDs: Dataset[NT] = ntDs.filter(_.p == ontolexProperty("canonicalForm"))
    val wrDs: Dataset[NT] = ntDs.filter(_.p == ontolexProperty("writtenRep"))

    cfDs.as("left").joinWith(
      wrDs.as("right"),
      $"left.o" === $"right.s"
    ).map({
      pair =>
        val wordNetURI = pair._1.s
        val wordNetLabel = pair._2.o
        val dbpediaURI = "<https://lingua.dbpedia.org/str/" + URLEncoder.encode(wordNetLabel.drop(1).dropRight(4), StandardCharset.UTF_8) + ">"
        List(dbpediaURI, "<http://dbpedia.org/ontology/associatedWith>", wordNetURI, ".").mkString(" ")
    }).coalesce(1).write.text(args(1))

    //    println(count)
  }

  case class NT(s: String, p: String, o: String)

  object NT {
    def apply(ntString: String): NT = {
      val ntSplit = ntString.split(" ", 3)
      new NT(ntSplit(0), ntSplit(1), ntSplit(2).dropRight(2))
    }
  }

}
