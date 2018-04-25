import java.io.File
import java.nio.file.Paths

class Screens {
    private val memory : Memory = Memory()
    val version = "v1.1"
    val limit = "---------------------------------"
    val eRK = "Enter record's key:"
    val eRV = "Enter record's value:"
    fun startScreen(){
        println(limit)
        println("Not a table $version")
        println("   1 - Show table")
        println("   2 - Add record")
        println("   3 - Delete record")
        println("   4 - Find record")
        println("   0 - Exit")
        when(readLine()!!.split(' ')[0].toInt()){
            1 -> showTableMenu()
            2 -> addRecordMenu()
            3 -> deleteRecordMenu()
            4 -> findRecordMenu()
            0 -> {
                memory.save()
                System.exit(0)
            }
        }
    }
    private fun showTableMenu(){
        println(limit)
        println("   1 - Show all records")
        println("   2 - Show first N records")
        println("   0 - Go back")
        when(readLine()!!.split(' ')[0].toInt()){
            1 -> showAllRecords()
            2 -> showNRecordsMenu()
            0 -> startScreen()
        }
    }
    private fun addRecordMenu(){
        println(limit)
        println(eRK)
        var key = readLine()
        while (key == null) key = readLine()
        if (memory.findByKey(key) != null)
        {
            println("Record with this key exists")
            println("Do you want to rewrite record?")
            if (!yesNo()) {
                goBack()
                return
            }
            else memory.deleteByKey(key)
        }
        println(eRV)
        var value = readLine()
        while (value == null) value = readLine()
        println("You have created record: ($key; $value)")
        println("Do you want to save it?")
        if (yesNo()){
            println("Record saves successfully")
            memory.add(key, value)
            goBack()
        }
        else startScreen()
    }
    private fun deleteRecordMenu(){
        println(limit)
        println("   1 - Delete all")
        println("   2 - Delete by key")
        println("   3 - Delete by value")
        println("   0 - Go back")
        when(readLine()!!.split(' ')[0].toInt()){
            1 -> deleteAllMenu()
            2 -> deleteByKeyMenu()
            3 -> deleteByValueMenu()
            0 -> startScreen()
        }
    }
    private fun findRecordMenu(){
        println(limit)
        println("   1 - Find by key")
        println("   2 - Find by value")
        println("   0 - Go back")
        when(readLine()!!.split(' ')[0].toInt()){
            1 -> findByKeyMenu()
            2 -> findByValueMenu()
            0 -> startScreen()
        }
    }
    private fun showAllRecords(){
        memory.getAll().forEach({k, v -> print("($k; $v) ")})
        println()
        goBack()
    }
    private fun showNRecordsMenu(){
        println("How many records do you need?")
        var count = readLine()!!.split(' ')[0].toInt()
        memory.getAll().forEach({k, v -> if(count-- > 0) print("($k; $v) ")})
        println()
        goBack()
    }
    private fun deleteAllMenu(){
        println("Do you want to delete all records")
        if (yesNo()) {
            println("All records are deleted")
            memory.deleteAll()
            goBack()
        }
        else startScreen()
    }
    private fun deleteByKeyMenu(){
        println(eRK)
        var key = readLine()
        while (key == null) key = readLine()
        val value = memory.findByKey(key)
        if (value == null) {
            println("Record not found")
            goBack()
        }
        else {
            println("Do you really want to delete record ($key; $value)?")
            if (yesNo())
            {
                println("Record ($key; $value) is deleted")
                memory.deleteByKey(key)
                goBack()
            }
            else startScreen()
        }
    }
    private fun deleteByValueMenu(){
        println(eRV)
        var value = readLine()
        while (value == null) value = readLine()
        val key = memory.findByValue(value)
        when {
            key.isEmpty() -> {
                println("Record not found")
                goBack()
            }
            key.size == 1 -> println("Do you really want to delete record (${key[0]}; $value)?")
            else -> println("Do you want to delete ${key.size} records?")
        }
        if(yesNo()){
            memory.deleteByValue(value)
            println("Records are deleted")
            goBack()
        }
        else startScreen()
    }
    private fun findByKeyMenu(){
        println(eRK)
        var key = readLine()
        while (key == null) key = readLine()
        val value = memory.findByKey(key)
        if (value == null)
            println("Record not found")
        else
            println("Found record: ($key; $value)")
        goBack()
    }
    private fun findByValueMenu(){
        println(eRV)
        var value = readLine()
        while (value == null) value = readLine()
        val key = memory.findByValue(value)
        if (key.isEmpty()) {
            println("Record not found")
            goBack()
            startScreen()
        }
        else {
            println("Found ${key.size} records:")
            key.forEach({ print("($it; $value) ") })
            println()
            goBack()
        }

    }
    fun goBack(){
        println("   0 - Go back")
        when (readLine()!!.split(' ')[0].toInt()) {
            0 -> startScreen()
        }
    }
    fun yesNo() : Boolean{
        println("   1 - Yes")
        println("   2 - No")
        when (readLine()!!.split(' ')[0].toInt()) {
            1 -> return true
            2 -> return false
        }
        return false
    }
    init {
        memory.load()
    }
}
class Memory{
    private var memory = HashMap<String, String>()
    val name = "pref.sv"
    fun add(key : String, value : String){
        memory[key] = value
        save()
    }
    fun deleteAll(){
        memory.clear()
        save()
    }
    fun deleteByKey(key: String){
        memory.remove(key)
        save()
    }
    fun deleteByValue(value: String){
        memory.forEach({k, v -> if (v == value) memory.remove(k)})
        save()
    }
    fun findByKey(key : String) : String?{
        return memory[key]
    }
    fun findByValue(value: String) : ArrayList<String>{
        val list = ArrayList<String>()
        memory.forEach({k, v -> if (v == value) list.add(k)})
        return list
    }
    fun getAll() : HashMap<String, String>{
        return memory
    }
    fun load(){
        var a : File = Paths.get(name).toFile()
        a.createNewFile()
        var b = a.readLines()
        b.forEach({
            val c = it.split("<!>")
            memory[c[0]] = c[1]
        })
    }
    fun save(){
        var a : File = Paths.get(name).toFile()
        a.delete()
        a.createNewFile()
        memory.forEach { t, u ->  a.appendText("$t<!>$u\n")}
    }
}
fun main(args : Array<String>){
    val m = Screens()
    m.startScreen()
}