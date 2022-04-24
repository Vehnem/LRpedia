package org.dbpedia

import org.apache.spark.SparkConf

object SparkConfig {

  def getConfig(): SparkConf = {

    new SparkConf().setAppName("LRpedia").setMaster("local[*]").setSparkHome("sparkHome")
  }
}
