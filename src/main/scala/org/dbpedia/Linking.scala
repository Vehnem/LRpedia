package org.dbpedia

import org.apache.hadoop.shaded.com.nimbusds.jose.util.StandardCharset
import org.apache.spark.SparkConf
import org.apache.spark.sql.{Dataset, SQLContext, SparkSession}

import java.net.URLEncoder

object Linking {

  def main(args: Array[String]): Unit = {
    
    if (args.length == 2) {
      execute(args)
    } else {
      println("this <INPUT> <OUTPUT>")
    }

  }

  def execute(args: Array[String]): Unit = {
    val sparkConfig = new SparkConf()
    sparkConfig.setMaster("local[*]")
    sparkConfig.setAppName("lr-dbpedia")
    sparkConfig.setSparkHome("spark-home/")
//    sparkConfig.set("hadoop.home.dir","hadoop-home")

    val spark = SparkSession.builder().config(sparkConfig).getOrCreate()
//    spark.sparkContext.setLogLevel("WARN")

    implicit val sqlContext: SQLContext = spark.sqlContext
    tmp(args(0), args(1))
  }

  def tmp(input: String, output: String)(implicit sqlContext: SQLContext): Unit = {

    import sqlContext.implicits._

    val ntDs: Dataset[NT] = sqlContext.read.textFile(input)
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
    }).coalesce(1).write.text(output)

  }

  case class NT(s: String, p: String, o: String)

  object NT {
    def apply(ntString: String): NT = {
      val ntSplit = ntString.split(" ", 3)
      new NT(ntSplit(0), ntSplit(1), ntSplit(2).dropRight(2))
    }
  }
}
