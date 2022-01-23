package frolova

interface EventStatistic {
    fun incEvent(name: String)
    fun getEventStatisticByName(name: String): Double
    fun getAllEventStatistic(): List<Pair<String, Double>>
    fun printStatistic()
}